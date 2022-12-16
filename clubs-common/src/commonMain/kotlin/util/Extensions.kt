package io.github.srgaabriel.clubs.common.util

import io.github.srgaabriel.clubs.common.struct.BaseCommandContext
import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandContext

public fun <S : CommandContext<S>> newCommand(vararg names: String, scope: Command<S>.() -> Unit): Command<S> =
    Command<S>(names.toList()).apply(scope)

public fun genericCommand(vararg names: String, scope: Command<*>.() -> Unit): Command<*> =
    Command(names.toList()).apply(scope)

public fun baseCommand(vararg names: String, scope: Command<BaseCommandContext>.() -> Unit): Command<BaseCommandContext> =
    Command<BaseCommandContext>(names.toList()).apply(scope)