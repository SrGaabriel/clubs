package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.*
import dev.gaabriel.clubs.common.util.StringReader

public class DefaultCommandParser(
    override var caching: Boolean,
    override val dictionary: ClubsDictionary,
    public val repository: CommandRepository
): CommandParser {
    private val cache = mutableMapOf<String, CommandCall>()

    override fun parse(prefix: String, string: String): CommandCall? {
        if (!string.startsWith(prefix))
            return null
        val content = string.substring(prefix.length).trim().ifBlank { return null }
        val cached = cache[content]
        if (cached != null)
            return cached

        val split = content.split(" ")
        val args = split.drop(1).toMutableList()

        val root = repository.search(split.first()) ?: return null
        val call = getCommandCall(root, args) ?: return null
        cache[content] = call
        return call
    }

    private fun getCommandCall(root: Command<*>, arguments: List<String>): CommandCall? {
        if (root.children.isEmpty())
            return CommandCall(root, root, emptyMap(), arguments)
        val stringReader = StringReader(arguments)
        val argumentValues = mutableMapOf<CommandArgumentNode<*, *>, Any>()

        fun scanThroughBranch(root: Command<*>, node: CommandNode<*>, reader: StringReader): CommandNode<*>? {
            if (node.children.isEmpty() || reader.isEnd())
                return node
            val peek = reader.peek()
            val literalWithName = node.children.firstOrNull { it is CommandLiteralNode<*> && it.name.equals(peek, ignoreCase = true) }
            if (literalWithName != null) {
                reader.next()
                return scanThroughBranch(root, literalWithName, reader)
            }

            val argumentNodes = node.children.filterIsInstance<CommandArgumentNode<*, *>>()
            if (argumentNodes.isEmpty())
                return null
            else if (argumentNodes.size == 1) {
                val argumentFound = argumentNodes.first()
                argumentValues[argumentFound] = argumentFound.type.parse(reader, dictionary)
                return scanThroughBranch(root, argumentFound, reader)
            }

            // there have to be at least two arguments
            var desiredOutcome: CommandNode<*>? = null
            val validArgumentNode = argumentNodes.firstOrNull {
                it.type.isParseable(reader) && scanThroughBranch(root, node, reader)?.also { outcome -> desiredOutcome = outcome } != null
            }
            if (desiredOutcome == null || validArgumentNode == null)
                return null

            argumentValues[validArgumentNode] = validArgumentNode.type.parse(reader, dictionary)
            return desiredOutcome
        }
        val node = scanThroughBranch(root, root, stringReader) ?: return null
        return CommandCall(root, node, argumentValues, arguments)
    }
}