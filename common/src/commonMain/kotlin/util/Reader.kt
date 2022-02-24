package dev.gaabriel.clubs.common.util

public class StringReader(public val args: MutableList<String>) {
    public val value: String get() = args.joinToString(" ")

    public fun current(): String = args.first()

    public fun remove(value: Int) {
        args.removeAll(args.take(value))
    }
}