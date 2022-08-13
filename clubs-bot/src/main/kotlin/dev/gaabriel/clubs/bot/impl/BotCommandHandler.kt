package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandNode
import dev.gaabriel.clubs.common.util.ClubsLogger
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
            command = call.root as Command<BotCommandContext>,
            node = call.node as CommandNode<BotCommandContext>,
            arguments = call.arguments as Map<CommandArgumentNode<BotCommandContext, *>, Any>,
            rawArguments = call.rawArguments
        )
        if (declaration.executor == null) {
            root.usage?.invoke(context)
            return
        }
        clubs.logger?.log(ClubsLogger.LogLevel.Debug, "Now going to command `${context.command.names.first()}`")
        val executionTime = measureTimeMillis {
            declaration.executor!!(context)
        }
        clubs.logger?.log(ClubsLogger.LogLevel.Info, "Command `${context.command.names.first()}` executed in ${executionTime}ms")
    }
}