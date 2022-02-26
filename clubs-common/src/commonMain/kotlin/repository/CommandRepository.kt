package dev.gaabriel.clubs.common.repository

import dev.gaabriel.clubs.common.struct.Command

public interface CommandRepository {
    public fun register(command: Command<*>)

    public fun retrieve(name: String): Command<*>?

    public fun delete(name: String)
}