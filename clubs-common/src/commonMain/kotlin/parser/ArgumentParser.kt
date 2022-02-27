package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.CommandContext

public interface ArgumentParser<S : CommandContext> {
    public fun parseArguments(context: S, args: List<String>): List<Any>
}