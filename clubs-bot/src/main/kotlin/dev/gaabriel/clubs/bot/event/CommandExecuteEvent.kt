package dev.gaabriel.clubs.bot.event

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgument
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.CommandNode
import io.github.deck.core.DeckClient
import io.github.deck.core.event.DeckEvent
import io.github.deck.gateway.event.GatewayEvent

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
    public val arguments: Map<CommandArgument<*, *>, Any> = call.arguments
    public val rawArguments: List<String> = call.rawArguments

    public var logException: Boolean = true
}