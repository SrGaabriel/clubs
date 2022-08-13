package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.repository.CommandRepository
import io.github.reactivecircus.cache4k.Cache

public interface CommandParser {
    public var cache: Cache<String, CommandCall>?

    public var dictionary: ClubsDictionary
    public var repository: CommandRepository

    public var caseSensitive: Boolean

    public fun parse(prefix: String, string: String): CommandCall?
}