@file:Suppress("UnusedReceiverParameter")

package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.ArgumentType
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public fun <S : CommandContext<S>> Command<S>.integer(): ArgumentType<Int> = ArgumentType.Integer

public fun <S : CommandContext<S>> Command<S>.short(): ArgumentType<Short> = ArgumentType.Short

public fun <S : CommandContext<S>> Command<S>.float(): ArgumentType<Float> = ArgumentType.Float

public fun <S : CommandContext<S>> Command<S>.double(): ArgumentType<Double> = ArgumentType.Double

public fun <S : CommandContext<S>> Command<S>.long(): ArgumentType<Long> = ArgumentType.Long

public fun <S : CommandContext<S>> Command<S>.word(): ArgumentType<String> = ArgumentType.Text.Word

public fun <S : CommandContext<S>> Command<S>.quote(): ArgumentType<String> = ArgumentType.Text.Quote

public fun <S : CommandContext<S>> Command<S>.phrase(): ArgumentType<String> = ArgumentType.Text.Greedy