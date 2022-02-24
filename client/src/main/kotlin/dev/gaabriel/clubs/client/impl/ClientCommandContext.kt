package dev.gaabriel.clubs.client.impl

import com.deck.common.content.ContentBuilder
import com.deck.core.DeckClient
import com.deck.core.builder.DeckMessageBuilder
import com.deck.core.entity.Message
import com.deck.core.stateless.StatelessTeam
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.channel.StatelessMessageChannel
import com.deck.core.util.sendContent
import com.deck.core.util.sendMessage
import dev.gaabriel.clubs.common.struct.CommandContext

public data class ClientCommandContext(
    val client: DeckClient,
    val user: StatelessUser,
    val team: StatelessTeam?,
    val channel: StatelessMessageChannel,
    override val arguments: List<Any>,
    override val rawArguments: List<String>
): CommandContext {
    override suspend fun send(message: String): Message =
        channel.sendMessage(message)

    public suspend fun send(builder: DeckMessageBuilder.() -> Unit): Message =
        channel.sendMessage(builder)

    public suspend fun sendContent(content: ContentBuilder.() -> Unit): Message =
        channel.sendContent(content)
}