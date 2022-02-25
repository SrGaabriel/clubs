package dev.gaabriel.clubs.client.impl

import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.StringReader

public class ClientCommandHandler(public val failureHandler: FailureHandler<*>): CommandHandler<DeckMessageCreateEvent> {
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
            context._arguments = parseArguments(context, command.arguments)
            declaration.call(context)
        } catch (exception: FailedCommandExecutionException) {
            failureHandler.onFailure(context, exception.failure)
        }
    }

    private fun parseArguments(context: ClientCommandContext, args: List<String>): List<Any> {
        val reader = StringReader(context, args.toMutableList())
        for (declarationArgument in context.command.arguments) {
            val text = declarationArgument[reader] ?: continue
            if (declarationArgument.type.literal)
                reader.remove(text.toString().split(" ").size)
            reader.history.add(text)
        }
        return reader.history
    }
}