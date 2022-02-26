package dev.gaabriel.clubs.client.impl

import com.deck.common.content.Content
import com.deck.common.content.ContentBuilder
import com.deck.core.DeckClient
import com.deck.core.builder.DeckMessageBuilder
import com.deck.core.entity.Message
import com.deck.core.stateless.StatelessTeam
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.channel.StatelessMessageChannel
import com.deck.core.util.sendContent
import com.deck.core.util.sendMessage
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public data class ClientCommandContext(
    val client: DeckClient,
    val user: StatelessUser,
    val team: StatelessTeam?,
    val channel: StatelessMessageChannel,
    val message: Message,
    override val command: Command<ClientCommandContext>,
    override val rawArguments: List<String>
): CommandContext {
    internal var _arguments: List<Any> = listOf()
    override val arguments: List<Any> get() = _arguments

    public val content: Content get() = message.content

    override suspend fun send(message: String): Message =
        channel.sendMessage(message)

    public suspend fun send(builder: DeckMessageBuilder.() -> Unit): Message =
        channel.sendMessage(builder)

    public suspend fun sendContent(content: ContentBuilder.() -> Unit): Message =
        channel.sendContent(content)

    override suspend fun reply(message: String): Message =
        this.message.sendReply { content { + message } }

    public suspend fun reply(builder: DeckMessageBuilder.() -> Unit): Message =
        this.message.sendReply(builder)

    public suspend fun replyContent(content: ContentBuilder.() -> Unit): Message =
        this.message.sendReply { this.content(content) }
}