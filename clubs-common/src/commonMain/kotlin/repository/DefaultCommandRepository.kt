package io.github.srgaabriel.clubs.common.repository

import io.github.srgaabriel.clubs.common.struct.Command

public class DefaultCommandRepository: CommandRepository {
    private val commands = mutableMapOf<String, Command<*>>()

    override fun register(command: Command<*>): Unit = command.names.forEach {
        commands[it] = command
    }

    override fun search(name: String, ignoreCase: Boolean): Command<*>? {
        val found = commands[name]
        if (found == null && ignoreCase) {
            return commands[commands.keys.firstOrNull {
                name.lowercase() == it.lowercase()
            }]
        }
        return found
    }

    override fun exclude(command: Command<*>): Unit = command.names.forEach {
        commands.remove(it)
    }
}