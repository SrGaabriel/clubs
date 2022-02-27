package dev.gaabriel.clubs.bot.impl

import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.parser.ArgumentParser
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler

public class BotCommandHandler(
    public val failureHandler: FailureHandler<*>,
    public val argumentParser: ArgumentParser<BotCommandContext>
): CommandHandler<DeckMessageCreateEvent> {
    @Suppress("unchecked_cast")
    override suspend fun execute(command: CommandCall<*>, event: DeckMessageCreateEvent) {
        val declaration = command.command as Command<BotCommandContext>; failureHandler as FailureHandler<BotCommandContext>
        val context = BotCommandContext(
            client = event.client,
            user = event.author,
            server = event.server,
            channel = event.channel,
            message = event.message,
            command = declaration,
            rawArguments = command.arguments
        )
        try {
            context._arguments = argumentParser.parseArguments(context, command.arguments)
            declaration.call(context)
        } catch (exception: FailedCommandExecutionException) {
            failureHandler.onFailure(context, exception.failure)
        }
    }
}