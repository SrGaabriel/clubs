package dev.gaabriel.clubs.bot.text

import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.FailureType

public class MarkdownFailureHandler: FailureHandler<BotCommandContext> {
    override suspend fun onFailure(context: BotCommandContext, type: FailureType) {
        val content = when (type) {
            is FailureType.UnprovidedArgument -> "The required argument `${type.argument.name}` was not provided."
            is FailureType.MismatchedArgumentType -> "The `${type.argument.name}` argument only accepts `${type.argument.type::class.simpleName ?: "Invalid"}` types."
            else -> "There was an error while trying to execute this command."
        }
        context.send(content)
    }
}