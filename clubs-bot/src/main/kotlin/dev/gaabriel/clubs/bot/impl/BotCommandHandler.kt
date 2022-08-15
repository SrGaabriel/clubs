package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.bot.event.CommandExecuteEvent
import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandNode
import io.github.deck.common.log.debug
import io.github.deck.common.log.info
import io.github.deck.core.event.message.MessageCreateEvent
import kotlin.system.measureTimeMillis

public fun interface BotCommandHandler {
    public suspend fun execute(call: CommandCall, event: MessageCreateEvent)
}

public class DefaultBotCommandHandler(private val clubs: BotClubsInstance): BotCommandHandler {
    @Suppress("unchecked_cast")
    override suspend fun execute(call: CommandCall, event: MessageCreateEvent) {
        val root = call.root as Command<BotCommandContext>
        val declaration = call.node as CommandNode<BotCommandContext>
        val context = BotCommandContext(
            client = event.client,
            event = event,
            userId = event.authorId,
            serverId = event.serverId,
            channelId = event.channelId,
            message = event.message,
            command = root,
            node = declaration,
            arguments = call.arguments as Map<CommandArgumentNode<BotCommandContext, *>, Any>,
            rawArguments = call.rawArguments
        )
        clubs.logger?.debug { "[Clubs] Now proceeding to execute command `${context.command.officialName}`" }
        if (declaration.executor == null) {
            root.usage?.invoke(context)
            clubs.logger?.debug { "[Clubs] The node from `${context.command.officialName}` that was called is not executable (missing an executor)" }
        }
        var exception: Exception? = null
        val executionTime = measureTimeMillis {
            try {
                declaration.executor?.invoke(context)
            } catch (caughtException: Exception) {
                exception = caughtException
            }
        }
        clubs.logger?.info { "[Clubs] Command `${context.command.officialName}` executed in ${executionTime}ms" }
        event.client.eventService.eventWrappingFlow.emit(CommandExecuteEvent(
            client = event.client,
            barebones = event.barebones,
            clubs = clubs,
            call = call,
            context = context,
            executionTime = executionTime,
            exception = exception
        ))
    }
}