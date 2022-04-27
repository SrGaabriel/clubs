package dev.gaabriel.clubs.bot.impl

import io.github.deck.core.DeckClient
import io.github.deck.core.entity.Message
import io.github.deck.core.stateless.StatelessServer
import io.github.deck.core.stateless.StatelessUser
import io.github.deck.core.stateless.channel.StatelessMessageChannel
import io.github.deck.rest.builder.SendMessageRequestBuilder
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import io.github.deck.common.EmbedBuilder
import io.github.deck.common.util.GenericId
import io.github.deck.core.event.message.DeckMessageCreateEvent
import io.github.deck.core.util.*
import java.util.UUID

public data class BotCommandContext(
    val client: DeckClient,
    val event: DeckMessageCreateEvent,
    val userId: GenericId,
    val serverId: GenericId?,
    val channelId: UUID,
    val message: Message,
    override val command: Command<BotCommandContext>,
    override val rawArguments: List<String>
): CommandContext {
    val user: StatelessUser by lazy { StatelessUser(client, userId) }
    val server: StatelessServer? by lazy { serverId?.let { StatelessServer(client, it) } }
    val channel: StatelessMessageChannel by lazy { StatelessMessageChannel(client, channelId, serverId) }

    internal var _arguments: List<Any> = listOf()
    override val arguments: List<Any> get() = _arguments

    public val content: String get() = message.content

    override suspend fun send(message: String): Message =
        channel.sendMessage(message)

    public suspend fun send(builder: SendMessageRequestBuilder.() -> Unit): Message =
        channel.sendMessage(builder)

    public suspend fun sendEmbed(content: String? = null, embed: EmbedBuilder.() -> Unit): Message =
        channel.sendEmbed(content, embed)

    override suspend fun reply(message: String): Any =
        this.message.sendReply(message)

    public suspend fun reply(builder: SendMessageRequestBuilder.() -> Unit): Message =
        this.message.sendReply(builder)

    public suspend fun replyEmbed(content: String? = null, embed: EmbedBuilder.() -> Unit): Message =
        this.message.replyWithEmbed(content, embed)
}