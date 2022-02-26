package dev.gaabriel.clubs.client.util

import dev.gaabriel.clubs.client.impl.ClientCommandContext
import dev.gaabriel.clubs.common.struct.Command

public fun command(vararg names: String, builder: Command<ClientCommandContext>.() -> Unit): Command<ClientCommandContext> =
    Command<ClientCommandContext>(names.toList()).apply(builder)