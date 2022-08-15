package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandLiteralNode
import dev.gaabriel.clubs.common.struct.CommandNode
import dev.gaabriel.clubs.common.util.StringReader
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

public open class TextCommandParser(
    override var dictionary: ClubsDictionary,
    override var repository: CommandRepository
): CommandParser {
    override var cache: Cache<String, CommandCall>? = Cache.Builder()
        .maximumCacheSize(200)
        .expireAfterAccess(30.minutes)
        .build()

    override var caseSensitive: Boolean = false

    @OptIn(ExperimentalTime::class)
    override fun parse(prefix: String, string: String): CommandCall? {
        if (!string.startsWith(prefix))
            return null
        val content = string.substring(prefix.length).trim().ifBlank { return null }
        val cached = cache?.get(content)
        if (cached != null)
            return cached

        val split = content.split(" ")
        val root = repository.search(
            name = split.first(),
            ignoreCase = !caseSensitive
        ) ?: return null
        val args = split.drop(1).toMutableList()
        val call: CommandCall = getCommandCall(root, args) ?: return null

        cache?.put(content, call)
        return call
    }

    protected fun getCommandCall(root: Command<*>, arguments: List<String>): CommandCall? {
        if (root.children.isEmpty())
            return CommandCall(root, root, emptyMap(), arguments)
        val stringReader = StringReader(arguments)
        val argumentValues = mutableMapOf<CommandArgumentNode<*, *>, Any>()

        fun scanThroughBranch(root: Command<*>, node: CommandNode<*>, reader: StringReader): CommandNode<*>? {
            if (node.children.isEmpty() || reader.isEnd())
                return node
            val peek = reader.peek()
            val literals = node.children.asSequence().filterIsInstance<CommandLiteralNode<*>>()
            val matchingLiteral = literals.firstOrNull { peek in it.names }
                ?: if (!caseSensitive) (literals.firstOrNull { peek.lowercase() in it.names.map { name -> name.lowercase() } }) else null

            if (matchingLiteral != null) {
                reader.next()
                return scanThroughBranch(root, matchingLiteral, reader)
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