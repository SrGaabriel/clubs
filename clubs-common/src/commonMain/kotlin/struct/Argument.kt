package dev.gaabriel.clubs.common.struct

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.exception.CommandParsingException
import dev.gaabriel.clubs.common.util.StringReader

public abstract class ArgumentType<T>(
    public val name: String,
    public val greedy: Boolean = false,
) {
    public abstract fun parse(reader: StringReader, dictionary: ClubsDictionary): T

    public object Integer: ArgumentType<Int>("32 bits integer") {
        override fun parse(reader: StringReader, dictionary: ClubsDictionary): Int =
            reader.next().let { it.toIntOrNull() ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, it)) }
    }

    public object Double: ArgumentType<kotlin.Double>("64 bits floating point") {
        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Double =
            reader.next().let { it.toDoubleOrNull() ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, it)) }
    }

    public object Short: ArgumentType<kotlin.Short>("16 bits integer") {
        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Short =
            reader.next().let { it.toShortOrNull() ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, it)) }
    }

    public object Long: ArgumentType<kotlin.Long>("64 bits integer") {
        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Long =
            reader.next().let { it.toLongOrNull() ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, it)) }
    }

    public object Float: ArgumentType<kotlin.Float>("32 bits floating point") {
        override fun parse(reader: StringReader, dictionary: ClubsDictionary): kotlin.Float =
            reader.next().let { it.toFloatOrNull() ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, it)) } }

    public sealed class Text(name: String, greedy: Boolean = false): ArgumentType<String>(name, greedy) {
        public object Word : Text("Word") {
            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String = reader.next()
        }

        public object Quote : Text("Quote", greedy = true) {
            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String {
                val peek = reader.peek()
                if (!peek.startsWith('"')) throw CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, name, peek))
                return reader.readUntilInclusiveOrNull { it.endsWith('"') }?.drop(1)?.dropLast(1)
                    ?: throw CommandParsingException(dictionary.getEntry(ClubsDictionary.QUOTE_ARGUMENT_NEVER_CLOSED))
            }
        }

        public object Greedy : Text("Text", greedy = true) {
            override fun parse(reader: StringReader, dictionary: ClubsDictionary): String = reader.remaining()
        }
    }
}