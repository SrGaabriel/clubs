package dev.gaabriel.clubs.common.struct

public interface CommandArgument<S : CommandContext<S>, T : Any> {
    public val name: String?
    public val type: ArgumentType<T>
}

public sealed class DelegatedArgument<S : CommandContext<S>, T : Any>(
    override val name: String?,
    override val type: ArgumentType<T>,
): CommandArgument<S, T> {
    public class Required<S : CommandContext<S>, T : Any>(
        name: String?,
        type: ArgumentType<T>
    ): DelegatedArgument<S, T>(name, type)

    public class Optional<S : CommandContext<S>, T : Any>(
        name: String?,
        type: ArgumentType<T>
    ): DelegatedArgument<S, T>(name, type)
}