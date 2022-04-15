package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.CommandContext

public class StringReader(
    public val context: CommandContext,
    public val args: MutableList<String>
) {
    public val value: String get() = args.joinToString(" ")
    public val history: MutableList<Any> = mutableListOf()

    public fun current(): String? = args.firstOrNull()

    public fun remove(value: Int) {
        args.removeAll(args.take(value))
    }
}