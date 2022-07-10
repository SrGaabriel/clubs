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
            println("Now scanning through another node ($node)")
            if (node.children.isEmpty() || reader.isEnd())
                return node
            println("Node confirmed to have children.")
            val peek = reader.peek()
            val literalWithName = node.children.firstOrNull { it is CommandLiteralNode<*> && it.name.equals(peek, ignoreCase = true) }
            println("Literal with name for node is: $literalWithName")
            if (literalWithName != null) {
                reader.next()
                println("There is a literal with name $peek after all!")
                return scanThroughBranch(root, literalWithName, reader)
            }
            println("There isn't a literal with name $peek")

            println("Checking for argument nodes for $peek!")
            val argumentNodes = node.children.filterIsInstance<CommandArgumentNode<*, *>>()
            if (argumentNodes.isEmpty())
                return null
            else if (argumentNodes.size == 1) {
                println("There was only one possible argument for $peek!")
                val argumentFound = argumentNodes.first()
                argumentValues[argumentFound] = argumentFound.type.parse(reader, dictionary)
                return scanThroughBranch(root, argumentFound, reader)
            }
            println("Multiple possible arguments found for $peek!")

            // there have to be at least two arguments
            var desiredOutcome: CommandNode<*>? = null
            val validArgumentNode = argumentNodes.firstOrNull {
                it.type.isParseable(reader) && scanThroughBranch(root, node, reader)?.also { outcome -> desiredOutcome = outcome } != null
            }
            println("Not any argument was found! :(")
            if (desiredOutcome == null || validArgumentNode == null)
                return null

            argumentValues[validArgumentNode] = validArgumentNode.type.parse(reader, dictionary)
            return desiredOutcome
        }
        val node = scanThroughBranch(root, root, stringReader) ?: return null
        return CommandCall(root, node, argumentValues, arguments)
    }
}