package dev.gaabriel.clubs.common.repository

import dev.gaabriel.clubs.common.struct.Command

public class MapCommandRepository: CommandRepository {
    public val map: MutableMap<List<String>, Command<*>> = mutableMapOf()

    override fun register(command: Command<*>) {
        map[command.names] = command
    }

    override fun retrieve(name: String): Command<*>? {
        return map[map.keys.firstOrNull { name in it }]
    }

    override fun delete(name: String) {
        map.remove(retrieve(name)?.names)
    }
}