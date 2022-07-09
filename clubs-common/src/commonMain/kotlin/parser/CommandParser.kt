package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary

public interface CommandParser {
    public val dictionary: ClubsDictionary

    public fun parse(string: String): CommandCall?
}