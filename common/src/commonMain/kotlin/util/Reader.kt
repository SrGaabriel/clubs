package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.CommandContext

public class StringReader(
    public val context: CommandContext,
    public val args: MutableList<String>
) {
    public val value: String get() = args.joinToString(" ")

    public fun current(): String = args.first()

    public fun remove(value: Int) {
        args.removeAll(args.take(value))
    }
}