package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.arguments.Argument

public sealed class CommandResult {
    public object Success: CommandResult()

    public class Failure(public val type: FailureType): CommandResult()
}

public abstract class FailureType {
    public class UnprovidedArgument(public val argument: Argument<*, *>): FailureType()

    public class MismatchedArgumentType(public val argument: Argument<*, *>): FailureType()
}

public fun interface FailureHandler<S : CommandContext> {
    public suspend fun onFailure(context: S, type: FailureType)
}

public class DefaultFailureHandler: FailureHandler<CommandContext> {
    override suspend fun onFailure(context: CommandContext, type: FailureType) {
        context.send(when (type) {
            is FailureType.UnprovidedArgument -> """The argument "${type.argument.name}" is required."""
            is FailureType.MismatchedArgumentType -> """The specified ${type.argument.name} is invalid."""
            else -> "Something went wrong while trying to execute this command"
        })
    }
}
