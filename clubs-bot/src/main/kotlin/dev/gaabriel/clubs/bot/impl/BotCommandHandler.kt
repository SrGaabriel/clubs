package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.bot.event.CommandExecuteEvent
import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandNode
import io.github.deck.common.log.debug
import io.github.deck.common.log.error
import io.github.deck.common.log.info
import io.github.deck.core.event.message.MessageCreateEvent
import kotlin.system.measureTimeMillis

public fun interface BotCommandHandler {
    public suspend fun execute(call: CommandCall, event: MessageCreateEvent)
}

@Suppress("unchecked_cast")
public class DefaultBotCommandHandler(private val clubs: BotClubsInstance): BotCommandHandler {
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
            arguments = call.arguments as Map<CommandArgumentNode<BotCommandContext, *>, Any>,
            rawArguments = call.rawArguments
        )
    }

    override suspend fun execute(call: CommandCall, event: MessageCreateEvent) {
        val context = contextBuilder(call, event) ?: return

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
            client = event.client,
            barebones = event.barebones,
            clubs = clubs,
            call = call,
            context = context,
            executionTime = executionTime,
            exception = exception
        )
        executionEvent.client.eventService.eventWrappingFlow.emit(executionEvent)
        if (executionEvent.logException) {
            clubs.logger?.error(exception) {}
        }
    }
}