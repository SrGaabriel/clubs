package dev.gaabriel.clubs.common

import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.Command

public interface ClubsInstance<C : Any> {
    public var listener: CommandListener<C>
    public var repository: CommandRepository

    public suspend fun start(client: C)

    public suspend fun register(command: Command<*>)
}