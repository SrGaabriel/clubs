package dev.gaabriel.clubs.common.struct

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.exception.CommandParsingException
import dev.gaabriel.clubs.common.util.StringReader

public abstract class ArgumentType<T>(
    public val name: String,
    public val greedy: Boolean = false,
) {
    public abstract fun parse(reader: StringReader, dictionary: ClubsDictionary): T

    public abstract fun isParseable(reader: StringReader): Boolean

    public object Integer: ArgumentType<Int>("32 bits integer") {
        private val regex = Regex("^\\d+\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader, dictionary: ClubsDictionary): Int =
            reader.next().toInt()
    }

    public object Double: ArgumentType<kotlin.Double>("64 bits floating point") {
        private val regex = Regex("^\\d+(\\.\\d+|)\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Double =
            reader.next().toDouble()
    }

    public object Short: ArgumentType<kotlin.Short>("16 bits integer") {
        private val regex = Regex("^\\d+\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Short =
            reader.next().toShort()
    }

    public object Long: ArgumentType<kotlin.Long>("64 bits integer") {
        private val regex = Regex("^\\d+\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Long =
            reader.next().toLong()
    }

    public object Float: ArgumentType<kotlin.Float>("32 bits floating point") {
        private val regex = Regex("^\\d+(\\.\\d+|)\$")

        override fun isParseable(reader: StringReader): Boolean =
            reader.peek() matches regex

        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Float =
            reader.next().toFloat()
    }

    public sealed class Text(name: String, greedy: Boolean = false): ArgumentType<String>(name, greedy) {
        public object Word : Text("Word") {
            override fun isParseable(reader: StringReader): Boolean = !reader.isEnd()

            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String = reader.next()
        }

        public object Quote : Text("Quote") {
            override fun isParseable(reader: StringReader): Boolean =
                reader.peekRemaining().let {
                    it.startsWith('"') && it.count { digit -> digit == '"' } > 1
                }

            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String {
                return reader.readUntilInclusiveOrNull { it.endsWith('"') }?.drop(1)?.dropLast(1)
                    ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.QUOTE_ARGUMENT_NEVER_CLOSED))
            }
        }

        public object Greedy : Text("Text", greedy = true) {
            override fun isParseable(reader: StringReader): Boolean = !reader.isEnd()

            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String = reader.remaining()
        }
    }
}