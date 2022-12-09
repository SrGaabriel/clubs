package dev.gaabriel.clubs.common.struct

import dev.gaabriel.clubs.common.util.ArgumentMap

public interface CommandContext<S : CommandContext<S>> {
    public val command: Command<S>
    public val node: CommandNode<S>
    public val arguments: ArgumentMap
    public val rawArguments: List<String>

    public suspend fun send(message: String): Any

    public suspend fun reply(message: String): Any = send(message)

    @Suppress("unchecked_cast")
    public fun <T : Any> CommandArgumentNode<S, T>.infer(): T =
        (arguments[this] ?: error("Error, argument not provided")) as? T ?: error("Tried to infer argument with wrong type")
}