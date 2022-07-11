package dev.gaabriel.clubs.common

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public interface ClubsInstance<C : Any, S : CommandContext<S>> {
    public fun start(client: C)

    public fun register(command: Command<S>)
}