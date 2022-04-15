package dev.gaabriel.clubs.bot.text

import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.CommandFailure

public class MarkdownFailureHandler: FailureHandler<BotCommandContext> {
    override suspend fun onFailure(context: BotCommandContext, failure: CommandFailure) {
        val content = when (failure) {
            is CommandFailure.UnprovidedArgument -> "The required argument `${failure.argument.name}` **(${failure.argument.type.name})** was not provided."
            is CommandFailure.MismatchedArgumentType -> "The `${failure.argument.name}` argument only accepts ${failure.argument.type.name.lowercase()}s."
            else -> "There was an error while trying to execute this command."
        }
        context.send(content)
    }
}