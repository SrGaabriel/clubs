package io.github.srgaabriel.clubs.common.parser

import io.github.reactivecircus.cache4k.Cache
import io.github.srgaabriel.clubs.common.dictionary.ClubsDictionary
import io.github.srgaabriel.clubs.common.repository.CommandRepository

public interface CommandParser {
    public var cache: Cache<String, CommandCall>?

    public var dictionary: ClubsDictionary
    public var repository: CommandRepository

    public var caseSensitive: Boolean

    public fun parse(prefix: String, string: String): CommandCall?
}