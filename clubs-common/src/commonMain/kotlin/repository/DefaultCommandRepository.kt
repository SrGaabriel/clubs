package dev.gaabriel.clubs.common.repository

import dev.gaabriel.clubs.common.struct.Command

public class DefaultCommandRepository: CommandRepository {
    private val commands = mutableMapOf<String, Command<*>>()

    override fun register(command: Command<*>): Unit = command.names.forEach {
        commands[it] = command
    }

    override fun search(name: String): Command<*>? =
        commands[name]

    override fun exclude(command: Command<*>): Unit = command.names.forEach {
        commands.remove(it)
    }
}