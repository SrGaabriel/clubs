package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.util.CommandCall

public interface CommandParser {
    public fun parseString(string: String): CommandCall<*>?
}