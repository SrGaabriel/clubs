package dev.gaabriel.clubs.common.handler

import dev.gaabriel.clubs.common.util.CommandCall

public interface CommandHandler<T : Any> {
    public suspend fun execute(command: CommandCall<*>, event: T)
}