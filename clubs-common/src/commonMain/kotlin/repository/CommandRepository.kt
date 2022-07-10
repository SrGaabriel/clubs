package dev.gaabriel.clubs.common.repository

import dev.gaabriel.clubs.common.struct.Command

public interface CommandRepository {
    public fun register(command: Command<*>)

    public fun search(name: String): Command<*>?

    public fun exclude(command: Command<*>)
}