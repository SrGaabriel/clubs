package io.github.srgaabriel.clubs.common.repository

import io.github.srgaabriel.clubs.common.struct.Command

public interface CommandRepository {
    public fun register(command: Command<*>)

    public fun search(name: String, ignoreCase: Boolean = true): Command<*>?

    public fun exclude(command: Command<*>)
}