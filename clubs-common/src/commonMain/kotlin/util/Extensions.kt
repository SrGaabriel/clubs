package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public fun <S : CommandContext<S>> newCommand(vararg names: String, scope: Command<S>.() -> Unit): Command<S> =
    Command<S>(names.toList()).apply(scope)

public fun genericCommand(vararg names: String, scope: Command<*>.() -> Unit): Command<*> =
    Command(names.toList()).apply(scope)