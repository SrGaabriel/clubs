package dev.gaabriel.clubs.client.impl

import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.struct.arguments.Argument
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.StringReader

public class ClientCommandHandler(public val failureHandler: FailureHandler<*>): CommandHandler<DeckMessageCreateEvent> {
    @Suppress("unchecked_cast")
    override suspend fun execute(command: CommandCall<*>, event: DeckMessageCreateEvent) {
        val declaration = command.command as Command<ClientCommandContext>; failureHandler as FailureHandler<ClientCommandContext>
        val arguments = parseArguments(declaration.arguments, command.arguments)
        val context = ClientCommandContext(
            client = event.client,
            user = event.user,
            team = event.team,
            channel = event.channel,
            arguments = arguments,
            rawArguments = command.arguments
        )
        if (!declaration.requirements(context))
            return
        try {
            declaration.call(context)
        } catch (exception: FailedCommandExecutionException) {
            failureHandler.onFailure(context, exception.failure)
        }
    }

    private fun parseArguments(declarationArguments: Collection<Argument<*, *>>, args: List<String>): List<Any> {
        val arguments: MutableList<Any> = mutableListOf()
        val reader = StringReader(args.toMutableList())
        for (declarationArgument in declarationArguments) {
            val text = declarationArgument[reader] ?: continue
            reader.remove(text.toString().split(" ").size)
            arguments.add(text)
        }
        return arguments
    }
}