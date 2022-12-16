package io.github.srgaabriel.clubs.bot.event

import io.github.srgaabriel.clubs.bot.BotClubsInstance
import io.github.srgaabriel.clubs.common.parser.CommandCall
import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandContext
import io.github.srgaabriel.clubs.common.struct.CommandNode
import io.github.srgaabriel.clubs.common.util.ArgumentMap
import io.github.srgaabriel.deck.core.DeckClient
import io.github.srgaabriel.deck.core.event.DeckEvent
import io.github.srgaabriel.deck.gateway.event.GatewayEvent

public data class CommandExecuteEvent(
    override val client: DeckClient,
    override val barebones: GatewayEvent,
    public val clubs: BotClubsInstance,
    public val call: CommandCall,
    public val context: CommandContext<*>,
    public val executionTime: Long,
    public val exception: Exception?
) : DeckEvent {
    public val command: Command<*> = call.root
    public val node: CommandNode<*> = call.node
    public val arguments: ArgumentMap = call.arguments
    public val rawArguments: List<String> = call.rawArguments

    public var logException: Boolean = true
}