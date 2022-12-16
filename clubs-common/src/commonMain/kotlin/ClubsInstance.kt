package io.github.srgaabriel.clubs.common

import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandContext

public interface ClubsInstance<C : Any> {
    /**
     * Setups this club instance within the provided [client], meaning
     * it'll start to listen to incoming commands.
     *
     * @param client client to inhibit
     */
    public fun setup(client: C)

    /**
     * Registers the provided [command]
     *
     * @param command command to be registered
     */
    public fun <S : CommandContext<S>> register(command: Command<S>)

    /**
     * Searches for the first command with the provided [name]
     *
     * @param name command name
     *
     * @return found command, null if none
     */
    public fun <S : CommandContext<S>> retrieve(name: String): Command<S>?

    /**
     * Unregisters the provided [command]
     *
     * @param command command to be excluded
     */
    public fun <S : CommandContext<S>> exclude(command: Command<S>)
}