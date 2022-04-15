package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.arguments.Argument

public sealed class CommandResult {
    public object Success: CommandResult()

    public class Failure(public val type: CommandFailure): CommandResult()
}

public abstract class CommandFailure {
    public class UnprovidedArgument(public val argument: Argument<*, *>): CommandFailure()

    public class MismatchedArgumentType(public val argument: Argument<*, *>): CommandFailure()
}

public fun interface FailureHandler<S : CommandContext> {
    public suspend fun onFailure(context: S, failure: CommandFailure)
}

public class DefaultFailureHandler: FailureHandler<CommandContext> {
    override suspend fun onFailure(context: CommandContext, failure: CommandFailure) {
        val content = when (failure) {
            is CommandFailure.UnprovidedArgument -> """The argument "${failure.argument.name}" is required."""
            is CommandFailure.MismatchedArgumentType -> """The specified ${failure.argument.name} is invalid."""
            else -> "Something went wrong while trying to execute this command"
        }
        context.reply(content)
    }
}
