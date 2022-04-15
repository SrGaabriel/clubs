package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.arguments.ArgumentBuilder
import dev.gaabriel.clubs.common.struct.arguments.ArgumentType
import dev.gaabriel.clubs.common.struct.arguments.createArgument

public fun <S : CommandContext> Command<S>.integer(name: String): ArgumentBuilder<S, Int> =
    createArgument(name, ArgumentType.Integer)

public fun <S : CommandContext> Command<S>.double(name: String): ArgumentBuilder<S, Double> =
    createArgument(name, ArgumentType.Double)

public fun <S : CommandContext> Command<S>.short(name: String): ArgumentBuilder<S, Short> =
    createArgument(name, ArgumentType.Short)

public fun <S : CommandContext> Command<S>.long(name: String): ArgumentBuilder<S, Long> =
    createArgument(name, ArgumentType.Long)

public fun <S : CommandContext> Command<S>.word(name: String): ArgumentBuilder<S, String> =
    createArgument(name, ArgumentType.Text.Word)

public fun <S : CommandContext> Command<S>.quote(name: String): ArgumentBuilder<S, String> =
    createArgument(name, ArgumentType.Text.Quote)

public fun <S : CommandContext> Command<S>.text(name: String): ArgumentBuilder<S, String> =
    createArgument(name, ArgumentType.Text.Greedy)