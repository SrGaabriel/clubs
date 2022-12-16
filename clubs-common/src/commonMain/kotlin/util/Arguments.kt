@file:Suppress("UnusedReceiverParameter", "NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package io.github.srgaabriel.clubs.common.util

import io.github.srgaabriel.clubs.common.struct.ArgumentType
import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandArgumentNode
import io.github.srgaabriel.clubs.common.struct.CommandContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun <S : CommandContext<S>> Command<S>.integer(): ArgumentType<Int> = ArgumentType.Integer

public inline fun <S : CommandContext<S>> Command<S>.short(): ArgumentType<Short> = ArgumentType.Short

public inline fun <S : CommandContext<S>> Command<S>.float(): ArgumentType<Float> = ArgumentType.Float

public inline fun <S : CommandContext<S>> Command<S>.double(): ArgumentType<Double> = ArgumentType.Double

public inline fun <S : CommandContext<S>> Command<S>.long(): ArgumentType<Long> = ArgumentType.Long

public inline fun <S : CommandContext<S>> Command<S>.word(): ArgumentType<String> = ArgumentType.Text.Word

public inline fun <S : CommandContext<S>> Command<S>.quote(): ArgumentType<String> = ArgumentType.Text.Quote

public inline fun <S : CommandContext<S>> Command<S>.phrase(): ArgumentType<String> = ArgumentType.Text.Greedy

// WORKAROUND WARNING
public inline fun <S : CommandContext<S>, F : Any> Command<S>.arguments(
    firstType: ArgumentType<F>,
    crossinline scope: CommandArgumentNode<S, F>.(CommandArgumentNode<S, F>) -> Unit,
) {
    argument(type = firstType) { first ->
        scope(first)
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any> Command<S>.arguments(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    crossinline scope: CommandArgumentNode<S, N>.(CommandArgumentNode<S, F>, CommandArgumentNode<S, N>) -> Unit,
) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    argument(type = firstType) { first ->
        argument(type = secondType) { second ->
            scope(first, second)
        }
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any, R : Any> Command<S>.arguments(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    thirdType: ArgumentType<R>,
    crossinline scope: CommandArgumentNode<S, R>.(CommandArgumentNode<S, F>, CommandArgumentNode<S, N>, CommandArgumentNode<S, R>) -> Unit,
) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    argument(type = firstType) { first ->
        argument(type = secondType) { second ->
            argument(type = thirdType) { third ->
                scope(first, second, third)
            }
        }
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any, R : Any, O : Any> Command<S>.arguments(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    thirdType: ArgumentType<R>,
    fourthType: ArgumentType<O>,
    crossinline scope: CommandArgumentNode<S, O>.(CommandArgumentNode<S, F>, CommandArgumentNode<S, N>, CommandArgumentNode<S, R>, CommandArgumentNode<S, O>) -> Unit,
) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    argument(type = firstType) { first ->
        argument(type = secondType) { second ->
            argument(type = thirdType) { third ->
                argument(type = fourthType) { fourth ->
                    scope(first, second, third, fourth)
                }
            }
        }
    }
}

public inline fun <S : CommandContext<S>, F : Any> Command<S>.executor(
    firstType: ArgumentType<F>,
    crossinline scope: suspend CommandContext<S>.(F) -> Unit
): Unit = arguments(firstType) { first ->
    executor {
        scope(first.infer())
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any> Command<S>.executor(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    crossinline scope: suspend CommandContext<S>.(F, N) -> Unit
): Unit = arguments(firstType, secondType) { first, second ->
    executor {
        scope(first.infer(), second.infer())
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any, R : Any> Command<S>.executor(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    thirdType: ArgumentType<R>,
    crossinline scope: suspend CommandContext<S>.(F, N, R) -> Unit
): Unit = arguments(firstType, secondType, thirdType) { first, second, third ->
    executor {
        scope(first.infer(), second.infer(), third.infer())
    }
}

public inline fun <S : CommandContext<S>, F : Any, N : Any, R : Any, O : Any> Command<S>.executor(
    firstType: ArgumentType<F>,
    secondType: ArgumentType<N>,
    thirdType: ArgumentType<R>,
    fourthType: ArgumentType<O>,
    crossinline scope: suspend CommandContext<S>.(F, N, R, O) -> Unit
): Unit = arguments(firstType, secondType, thirdType, fourthType) { first, second, third, fourth ->
    executor {
        scope(first.infer(), second.infer(), third.infer(), fourth.infer())
    }
}