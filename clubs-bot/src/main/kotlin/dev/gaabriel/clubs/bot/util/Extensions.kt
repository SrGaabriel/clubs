package dev.gaabriel.clubs.bot.util

import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.struct.Command

public fun command(vararg names: String, builder: Command<BotCommandContext>.() -> Unit): Command<BotCommandContext> =
    Command<BotCommandContext>(names.toList()).apply(builder)