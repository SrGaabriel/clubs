package dev.gaabriel.clubs.common.struct.arguments

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureType
import dev.gaabriel.clubs.common.util.StringReader

public fun <S : CommandContext, T : Any?> Command<S>.createArgument(
    name: String,
    type: ArgumentType<T>
): Argument.Required<S, T> {
    if (arguments.lastOrNull()?.type == ArgumentType.Text.Greedy)
        error("Greedy arguments must always come last")
    val argument = Argument.Required(name, type, this)
    arguments.add(argument)
    return argument
}

public interface Argument<S : CommandContext, T> {
    public val name: String
    public val type: ArgumentType<T>
    public val command: Command<S>

    public data class Optional<S : CommandContext, T>(
        override val name: String,
        override val type: ArgumentType<T>,
        override val command: Command<S>,
    ): Argument<S, T> {
        override fun get(reader: StringReader): T? {
            return type.parse(reader) ?: throw FailedCommandExecutionException(FailureType.MismatchedArgumentType(this))
        }
    }

    public data class Required<S : CommandContext, T>(
        override val name: String,
        override val type: ArgumentType<T>,
        override val command: Command<S>,
    ): Argument<S, T> {
        override fun get(reader: StringReader): T {
            if (reader.args.size == 0)
                throw FailedCommandExecutionException(FailureType.UnprovidedArgument(this))
            return type.parse(reader) ?: throw FailedCommandExecutionException(FailureType.MismatchedArgumentType(this))
        }
    }

    public operator fun get(reader: StringReader): T?
}

public abstract class ArgumentType<T> {
    public object Integer: ArgumentType<Int>() {
        override fun parse(reader: StringReader): Int? =
            reader.current().toIntOrNull()
    }

    public object Double: ArgumentType<kotlin.Double>() {
        override fun parse(reader: StringReader): kotlin.Double? =
            reader.current().toDoubleOrNull()
    }

    public object Short: ArgumentType<kotlin.Short>() {
        override fun parse(reader: StringReader): kotlin.Short? =
            reader.current().toShortOrNull()
    }

    public object Long: ArgumentType<kotlin.Long>() {
        override fun parse(reader: StringReader): kotlin.Long? =
            reader.current().toLongOrNull()
    }

    public sealed class Text: ArgumentType<String>() {
        public object Word: Text() {
            override fun parse(reader: StringReader): String = reader.current()
        }
        public object Quote: Text() {
            override fun parse(reader: StringReader): String? {
                val value = reader.value
                if (!value.startsWith('"')) return null
                return value.substring(1).substringBefore('"').also { println(it) }
            }
        }
        public object Greedy: Text() {
            override fun parse(reader: StringReader): String = reader.value
        }
    }

    public abstract fun parse(reader: StringReader): T?

    public companion object
}

public fun <S : CommandContext, T> Argument.Optional<S, T>.required(): Argument.Required<S, T> =
    Argument.Required(name, type, command)

public fun <S : CommandContext, T> Argument.Required<S, T>.optional(): Argument.Optional<S, T> =
    Argument.Optional(name, type, command)