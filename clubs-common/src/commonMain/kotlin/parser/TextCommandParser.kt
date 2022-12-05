package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.exception.CommandParsingException
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.*
import dev.gaabriel.clubs.common.util.StringReader
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

public open class TextCommandParser(
    override var dictionary: ClubsDictionary,
    override var repository: CommandRepository
): CommandParser {
    override var cache: Cache<String, CommandCall>? = Cache.Builder()
        .maximumCacheSize(200)
        .expireAfterAccess(30.minutes)
        .build()

    override var caseSensitive: Boolean = false

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

        val (node, argumentValues) = scanCallAndStoreArguments(root, stringReader) ?: return null
        return CommandCall(root, node, argumentValues, arguments)
    }

    protected fun scanCallAndStoreArguments(command: Command<*>, reader: StringReader): Pair<CommandNode<*>, Map<CommandArgumentNode<*, *>, Any>>? {
        val arguments = mutableMapOf<CommandArgumentNode<*, *>, Any>()

        var currentNode: CommandNode<*> = command
        while (reader.hasMore && currentNode.children.isNotEmpty()) {
            val matchingLiteral =
                currentNode.children.firstOrNull { it is CommandLiteralNode<*> && it.name.equals(reader.peek(), !caseSensitive) }

            if (matchingLiteral != null) {
                reader.index++
                currentNode = matchingLiteral
                continue
            }

            val argumentNodes = currentNode.children.filterIsInstance<CommandArgumentNode<*, *>>()
            if (argumentNodes.size == 1) {
                val argumentFound = argumentNodes.first()
                if (!argumentFound.type.isParseable(reader)) {
                    throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, argumentFound.type.name, reader.peek()))
                }
                arguments[argumentFound] = argumentFound.type.parse(reader, dictionary)
                currentNode = argumentFound
                continue
            }

            val validArgumentNode = argumentNodes.firstOrNull { it.type.isParseable(reader) } ?: return null
            arguments[validArgumentNode] = validArgumentNode.type.parse(reader, dictionary)
            currentNode = validArgumentNode
        }
        return currentNode to arguments
    }
}