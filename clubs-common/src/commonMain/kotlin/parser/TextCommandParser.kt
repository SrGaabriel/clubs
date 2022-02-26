package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.util.CommandCall

public class TextCommandParser(
    public val repository: CommandRepository,
    public val prefix: String
): CommandParser {
    override fun parseString(string: String): CommandCall<*>? {
        if (!string.startsWith(prefix))
            return null
        val content = string.substring(prefix.length).trim().split(" ").ifEmpty { return null }
        val args = content.drop(1).toMutableList()

        val command = repository
            .retrieve(content.first())
            ?.let { base: Command<*> -> reformulate(base, args) }
            ?: return null
        return CommandCall(command, args)
    }

    private tailrec fun reformulate(command: Command<*>, arguments: MutableList<String>): Command<*> {
        val argument = arguments.firstOrNull() ?: return command
        val match = repository.retrieve(argument)
            ?: command.subcommands.firstOrNull { argument in it.names }
            ?: return command

        arguments.remove(arguments.first())
        return reformulate(match, arguments)
    }
}