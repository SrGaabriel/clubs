package dev.gaabriel.clubs.bot.impl

import io.github.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.StringReader

public class BotCommandHandler(public val failureHandler: FailureHandler<*>): CommandHandler<DeckMessageCreateEvent> {
    @Suppress("unchecked_cast")
    override suspend fun execute(command: CommandCall<*>, event: DeckMessageCreateEvent) {
        val declaration = command.command as Command<BotCommandContext>; failureHandler as FailureHandler<BotCommandContext>
        val context = BotCommandContext(
            client = event.client,
            event = event,
            userId = event.authorId,
            serverId = event.serverId,
            channelId = event.channelId,
            message = event.message,
            command = declaration,
            rawArguments = command.arguments
        )
        try {
            context._arguments = parseArguments(context, command.arguments)
            declaration.call(context)
        } catch (exception: FailedCommandExecutionException) {
            failureHandler.onFailure(context, exception.failure)
        }
    }

    private fun parseArguments(context: BotCommandContext, args: List<String>): List<Any> {
        val reader = StringReader(context, args.toMutableList())
        for (declarationArgument in context.command.arguments) {
            val text = declarationArgument[reader] ?: continue
            reader.remove(text.toString().split(" ").size)
            reader.history.add(text)
        }
        return reader.history
    }
}