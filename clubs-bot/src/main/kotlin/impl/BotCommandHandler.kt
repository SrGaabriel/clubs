package io.github.srgaabriel.clubs.bot.impl

import io.github.srgaabriel.clubs.bot.BotClubsInstance
import io.github.srgaabriel.clubs.bot.event.CommandExecuteEvent
import io.github.srgaabriel.clubs.common.parser.CommandCall
import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandNode
import io.github.srgaabriel.deck.common.log.debug
import io.github.srgaabriel.deck.common.log.error
import io.github.srgaabriel.deck.common.log.info
import io.github.srgaabriel.deck.core.event.message.MessageCreateEvent
import kotlin.system.measureTimeMillis

public interface BotCommandHandler {
    public suspend fun execute(call: CommandCall, event: MessageCreateEvent)

    public suspend fun execute(call: CommandCall, context: BotCommandContext)
}

public class DefaultBotCommandHandler(private val clubs: BotClubsInstance): BotCommandHandler {
    @Suppress("unchecked_cast")
    public var contextBuilder: (CommandCall, MessageCreateEvent) -> BotCommandContext? = { call, event ->
        BotCommandContext(
            client = event.client,
            event = event,
            userId = event.authorId,
            serverId = event.serverId,
            channelId = event.channelId,
            message = event.message,
            command = call.root as Command<BotCommandContext>,
            node = call.node as CommandNode<BotCommandContext>,
            arguments = call.arguments,
            rawArguments = call.rawArguments
        )
    }

    override suspend fun execute(call: CommandCall, event: MessageCreateEvent) {
        execute(call, contextBuilder(call, event) ?: return)
    }

    override suspend fun execute(call: CommandCall, context: BotCommandContext) {
        val executionLog = "[Clubs] Now proceeding to execute command `${context.command.officialName}` in node `${context.node.name ?: "<unnamed>"}`"
        clubs.logger?.debug { executionLog }
        if (!context.node.isExecutable) {
            context.command.usage?.invoke(context)
            clubs.logger?.debug { "[Clubs] The node from `${context.command.officialName}` that was called is not executable (missing an executor)" }
            return
        }
        var exception: Exception? = null
        val executionTime = measureTimeMillis {
            try {
                context.node.execute(context)
            } catch (caughtException: Exception) {
                exception = caughtException
            }
        }
        clubs.logger?.info { "[Clubs] Command `${context.command.officialName}` executed in ${executionTime}ms" }

        val executionEvent = CommandExecuteEvent(
            client = context.event.client,
            barebones = context.event.barebones,
            clubs = clubs,
            call = call,
            context = context,
            executionTime = executionTime,
            exception = exception
        )
        executionEvent.client.eventService.eventWrappingFlow.emit(executionEvent)
        if (executionEvent.logException && exception != null) {
            clubs.logger?.error(exception) {}
        }
    }
}