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
    /**
     * @return execution time
     */
    public suspend fun execute(call: CommandCall, event: MessageCreateEvent): Long
}

public class DefaultBotCommandHandler(private val clubs: BotClubsInstance): BotCommandHandler {
    @Suppress("unchecked_cast")
    override suspend fun execute(call: CommandCall, event: MessageCreateEvent): Long {
        val root = call.root as Command<BotCommandContext>
        val declaration = call.node as CommandNode<BotCommandContext>
        val context = BotCommandContext(
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
        clubs.logger?.debug { "[Clubs] Now going to command `${context.command.names.first()}`" }
        var exception: Exception? = null
        val executionTime = measureTimeMillis {
            try {
                declaration.executor!!(context)
            } catch (caughtException: Exception) {
                exception = caughtException
            }
        }
        clubs.logger?.info { "[Clubs] Command `${context.command.names.first()}` executed in ${executionTime}ms" }
        event.client.eventService.eventWrappingFlow.emit(CommandExecuteEvent(
            client = event.client,
            barebones = event.barebones,
            clubs = clubs,
            call = call,
            context = context,
            executionTime = executionTime,
            exception = exception
        ))
        return executionTime
    }
}