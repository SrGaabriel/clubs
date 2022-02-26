package dev.gaabriel.clubs.common.struct

public interface CommandContext {
    public val command: Command<out CommandContext>
    public val arguments: List<Any>
    public val rawArguments: List<String>

    public suspend fun send(message: String): Any

    public suspend fun reply(message: String): Any = send(message)
}