package dev.gaabriel.clubs.bot.event

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandNode
import dev.gaabriel.clubs.common.util.ArgumentMap
import io.github.deck.core.DeckClient
import io.github.deck.core.event.DeckEvent
import io.github.deck.gateway.event.GatewayEvent

public data class CommandParseEvent(
    override val client: DeckClient,
    override val barebones: GatewayEvent,
    public val clubs: BotClubsInstance,
    public val call: CommandCall,
) : DeckEvent {
    public var proceed: Boolean = true

    public val command: Command<*> = call.root
    public val node: CommandNode<*> = call.node
    public val arguments: ArgumentMap = call.arguments
    public val rawArguments: List<String> = call.rawArguments

    public fun interrupt() {
        proceed = false
    }
}