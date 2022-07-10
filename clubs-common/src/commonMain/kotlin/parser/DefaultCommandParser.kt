package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.*
import dev.gaabriel.clubs.common.util.StringReader

public class DefaultCommandParser(
    override val dictionary: ClubsDictionary,
    public val prefix: String,
    public val repository: CommandRepository
): CommandParser {
    override fun parse(string: String): CommandCall? {
        if (!string.startsWith(prefix))
            return null
        val content = string.substring(prefix.length).trim().split(" ").ifEmpty { return null }
        val args = content.drop(1).toMutableList()

        val root = repository.search(content.first()) ?: return null
        return getCommandCall(root, args)
    }

    private fun getCommandCall(root: Command<*>, arguments: List<String>): CommandCall? {
        if (root.children.isEmpty())
            return CommandCall(root, root, emptyMap(), arguments)
        val stringReader = StringReader(arguments)
        val argumentValues = mutableMapOf<CommandArgumentNode<*, *>, Any>()

        fun scanThroughBranch(root: Command<*>, node: CommandNode<*>, reader: StringReader): CommandNode<*>? {
            if (node.children.isEmpty() || arguments.isEmpty()) {
                return node
            }
            val next = reader.nextOrNull() ?: return node
            val literalWithName = node.children
                .firstOrNull { it is CommandLiteralNode<*> && it.name.equals(next, ignoreCase = true) }

            if (literalWithName != null)
                return scanThroughBranch(root, literalWithName, reader) ?: node
            val argumentsInBranch = node.children.filterIsInstance<CommandArgumentNode<*, *>>()
            if (argumentsInBranch.isEmpty())
                return null

            // it has to be an argument
            var expectingOutcome: CommandNode<*>? = null
            var expectingArgumentBranch: CommandArgumentNode<*, *>? = null
            for (argumentBranch in argumentsInBranch) {
                val successfulOutcome = scanThroughBranch(root, argumentBranch, reader)
                if (successfulOutcome != null) {
                    expectingOutcome = successfulOutcome
                    expectingArgumentBranch = argumentBranch
                    break
                }
            }
            if (expectingOutcome == null)
                return node

            reader.index--
            argumentValues[expectingArgumentBranch!!] = expectingArgumentBranch.type.parse(reader.clone(), dictionary)
            return expectingOutcome
        }
        val node = scanThroughBranch(root, root, stringReader) ?: return null
        return CommandCall(root, node, argumentValues, arguments)
    }
}