package io.github.srgaabriel.clubs.common.parser

import io.github.reactivecircus.cache4k.Cache
import io.github.srgaabriel.clubs.common.dictionary.ClubsDictionary
import io.github.srgaabriel.clubs.common.exception.CommandParsingException
import io.github.srgaabriel.clubs.common.repository.CommandRepository
import io.github.srgaabriel.clubs.common.struct.*
import io.github.srgaabriel.clubs.common.util.MutableArgumentMap
import io.github.srgaabriel.clubs.common.util.StringReader
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
        if (root.children.isEmpty() && root.delegatedArguments.isEmpty())
            return CommandCall(root, root, emptyMap(), arguments)
        val stringReader = StringReader(arguments)

        val argumentValues = mutableMapOf<CommandArgument<*>, Any>()
        val node = scanCallAndStoreArguments(root, stringReader, argumentValues) ?: return null
        return CommandCall(root, node, argumentValues, arguments)
    }

    protected fun scanCallAndStoreArguments(command: Command<*>, reader: StringReader, arguments: MutableArgumentMap): CommandNode<*>? {
        if (!reader.hasMore && command.delegatedArguments.filterIsInstance<DelegatedArgument.Required<*, *>>().isNotEmpty()) {
            val firstArgument = command.delegatedArguments.first()
            throw CommandParsingException.RequiredArgumentNotProvided(firstArgument)
        }

        var currentNode: CommandNode<*> = command
        while (reader.hasMore) {
            val matchingLiteral =
                currentNode.children.firstOrNull { it is CommandLiteralNode<*> && it.name.equals(reader.peek(), !caseSensitive) }

            if (matchingLiteral != null) {
                reader.index++
                currentNode = matchingLiteral
                continue
            }

            var argumentIndex = 0
            while (argumentIndex < currentNode.delegatedArguments.size) {
                val argument = currentNode.delegatedArguments[argumentIndex]
                argumentIndex++

                when {
                    argument is DelegatedArgument.Required<*, *> && !reader.hasMore -> {
                        throw CommandParsingException.RequiredArgumentNotProvided(argument)
                    }
                    argument is DelegatedArgument.Optional<*, *> && !reader.hasMore -> {
                        continue
                    }
                    argument is DelegatedArgument.Optional<*, *> && !argument.type.isParseable(reader) -> {
                        if (currentNode.delegatedArguments.size == 1)
                            throw CommandParsingException.UnexpectedArgumentType(argument, reader.peek())
                        continue
                    }
                }

                if (!argument.type.isParseable(reader)) {
                    throw CommandParsingException.UnexpectedArgumentType(argument, reader.peek())
                }
                arguments[argument] = argument.type.parse(reader, dictionary)
            }

            if (currentNode.children.isEmpty())
                break

            val argumentNodes = currentNode.children.filterIsInstance<CommandArgumentNode<*, *>>()
            if (argumentNodes.isEmpty())
                continue

            if (argumentNodes.size == 1) {
                val argumentFound = argumentNodes.first()
                if (!argumentFound.type.isParseable(reader)) {
                    throw CommandParsingException.UnexpectedArgumentType(argumentFound, reader.peek())
                }
                arguments[argumentFound] = argumentFound.type.parse(reader, dictionary)
                currentNode = argumentFound
                continue
            }

            val validArgumentNode = argumentNodes.asSequence().filter { it.type.isParseable(reader) }.maxByOrNull { it.children.size } ?: return null
            arguments[validArgumentNode] = validArgumentNode.type.parse(reader, dictionary)
            currentNode = validArgumentNode
        }
        return currentNode
    }
}