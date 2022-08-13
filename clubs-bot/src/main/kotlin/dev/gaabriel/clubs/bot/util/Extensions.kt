package dev.gaabriel.clubs.bot.util

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.struct.Command
import io.github.deck.core.event.message.MessageCreateEvent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias PrefixProvider = suspend MessageCreateEvent.() -> String

/**
 * Creates a new command with the provided [names] without registering it
 *
 * @param names command names
 * @param builder command builder
 *
 * @return built command
 */
@OptIn(ExperimentalContracts::class)
public fun command(vararg names: String, builder: Command<BotCommandContext>.() -> Unit): Command<BotCommandContext> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return Command<BotCommandContext>(names.toList()).apply(builder)
}

/**
 * Creates a new command with the provided [names] and registers it
 *
 * @param names command names
 * @param builder command builder
 *
 * @return built command
 */
@OptIn(ExperimentalContracts::class)
public fun BotClubsInstance.create(vararg names: String, builder: Command<BotCommandContext>.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    register(command(names = names, builder = builder))
}