package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public fun <T : CommandContext> newCommand(vararg names: String, builder: Command<T>.() -> Unit): Command<T> =
    Command<T>(names.toList()).apply(builder)