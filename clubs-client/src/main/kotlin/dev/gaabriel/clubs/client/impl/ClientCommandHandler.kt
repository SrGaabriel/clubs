package dev.gaabriel.clubs.client.impl

import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.parser.ArgumentParser
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler

public class ClientCommandHandler(
    public val failureHandler: FailureHandler<*>,
    public val argumentParser: ArgumentParser<ClientCommandContext>
): CommandHandler<DeckMessageCreateEvent> {
    @Suppress("unchecked_cast")
    override suspend fun execute(command: CommandCall<*>, event: DeckMessageCreateEvent) {
        val declaration = command.command as Command<ClientCommandContext>; failureHandler as FailureHandler<ClientCommandContext>
        val context = ClientCommandContext(
            client = event.client,
            user = event.user,
            team = event.team,
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