package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary

public interface CommandParser {
    public var caching: Boolean
    public val dictionary: ClubsDictionary

    public fun parse(prefix: String, string: String): CommandCall?
}