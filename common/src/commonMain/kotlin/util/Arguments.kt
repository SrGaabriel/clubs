package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.arguments.Argument
import dev.gaabriel.clubs.common.struct.arguments.ArgumentType
import dev.gaabriel.clubs.common.struct.arguments.createArgument

public fun <S : CommandContext> Command<S>.integer(name: String): Argument.Required<S, Int> =
    createArgument(name, ArgumentType.Integer)

public fun <S : CommandContext> Command<S>.double(name: String): Argument.Required<S, Double> =
    createArgument(name, ArgumentType.Double)

public fun <S : CommandContext> Command<S>.short(name: String): Argument.Required<S, Short> =
    createArgument(name, ArgumentType.Short)

public fun <S : CommandContext> Command<S>.long(name: String): Argument.Required<S, Long> =
    createArgument(name, ArgumentType.Long)

public fun <S : CommandContext> Command<S>.word(name: String): Argument.Required<S, String> =
    createArgument(name, ArgumentType.Text.Word)

public fun <S : CommandContext> Command<S>.quote(name: String): Argument.Required<S, String> =
    createArgument(name, ArgumentType.Text.Quote)

public fun <S : CommandContext> Command<S>.text(name: String): Argument.Required<S, String> =
    createArgument(name, ArgumentType.Text.Greedy)