package dev.gaabriel.clubs.common.struct.arguments

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.CommandFailure
import dev.gaabriel.clubs.common.util.StringReader

public data class ArgumentBuilder<S : CommandContext, T : Any?> internal constructor(
    val name: String,
    val type: ArgumentType<T>,
    val command: Command<S>
)

public fun <S : CommandContext, T : Any?> Command<S>.createArgument(
    name: String,
    type: ArgumentType<T>
): ArgumentBuilder<S, T> = ArgumentBuilder(name, type, this)

public interface Argument<S : CommandContext, T> {
    public val name: String
    public val type: ArgumentType<T>
    public val command: Command<S>

    public data class Optional<S : CommandContext, T>(
        override val name: String,
        override val type: ArgumentType<T>,
        override val command: Command<S>
    ): Argument<S, T> {
        override fun get(reader: StringReader): T? {
            if (type.literal && reader.args.isEmpty())
                return null
            return type.parse(reader) ?: throw FailedCommandExecutionException(CommandFailure.MismatchedArgumentType(this))
        }
    }

    public data class Required<S : CommandContext, T>(
        override val name: String,
        override val type: ArgumentType<T>,
        override val command: Command<S>,
    ): Argument<S, T> {
        override fun get(reader: StringReader): T {
            if (type.literal && reader.args.size == 0)
                throw FailedCommandExecutionException(CommandFailure.UnprovidedArgument(this))
            return type.parse(reader) ?: throw FailedCommandExecutionException(CommandFailure.MismatchedArgumentType(this))
        }
    }

    public operator fun get(reader: StringReader): T?
}

public abstract class ArgumentType<T>(
    public val name: String,
    public val literal: Boolean = true,
    public val mustComeLast: Boolean = false
) {
    public object Integer: ArgumentType<Int>("32 bits integer") {
        override fun parse(reader: StringReader): Int? =
            reader.current()?.toIntOrNull()
    }

    public object Double: ArgumentType<kotlin.Double>("64 bits floating points") {
        override fun parse(reader: StringReader): kotlin.Double? =
            reader.current()?.toDoubleOrNull()
    }

    public object Short: ArgumentType<kotlin.Short>("16 bits integer") {
        override fun parse(reader: StringReader): kotlin.Short? =
            reader.current()?.toShortOrNull()
    }

    public object Long: ArgumentType<kotlin.Long>("64 bits integer") {
        override fun parse(reader: StringReader): kotlin.Long? =
            reader.current()?.toLongOrNull()
    }

    public object Float: ArgumentType<kotlin.Long>("32 bits floating point") {
        override fun parse(reader: StringReader): kotlin.Long? =
            reader.current()?.toLongOrNull()
    }

    public sealed class Text(name: String, literal: Boolean = true, mustComeLast: Boolean = false): ArgumentType<String>(name, literal, mustComeLast) {
        public object Word: Text("Word") {
            override fun parse(reader: StringReader): String? = reader.current()
        }
        public object Quote: Text("Quote") {
            override fun parse(reader: StringReader): String? {
                val value = reader.value
                if (!value.startsWith('"')) return null
                return value.substring(1).substringBefore('"')
            }
        }
        public object Greedy: Text("Text", mustComeLast = true) {
            override fun parse(reader: StringReader): String? = reader.value.ifBlank { null }
        }
    }

    public abstract fun parse(reader: StringReader): T?

    public companion object
}

public fun <S : CommandContext, T, O : Argument<S, T>> O.register(): O = apply {
    if (command.arguments.lastOrNull()?.type?.mustComeLast == true)
        error("Greedy arguments must always come last")
    command.arguments.add(this)
}

public fun <S : CommandContext, T> ArgumentBuilder<S, T>.required(): Argument.Required<S, T> =
    Argument.Required(name, type, command).register()

public fun <S : CommandContext, T> ArgumentBuilder<S, T>.optional(): Argument.Optional<S, T> =
    Argument.Optional(name, type, command).register()