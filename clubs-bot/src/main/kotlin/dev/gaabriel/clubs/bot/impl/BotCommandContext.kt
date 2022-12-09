package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.struct.CommandNode
import dev.gaabriel.clubs.common.util.ArgumentMap
import io.github.deck.common.EmbedBuilder
import io.github.deck.common.util.GenericId
import io.github.deck.core.DeckClient
import io.github.deck.core.entity.Message
import io.github.deck.core.event.message.MessageCreateEvent
import io.github.deck.core.stateless.StatelessServer
import io.github.deck.core.stateless.StatelessUser
import io.github.deck.core.stateless.channel.StatelessMessageChannel
import io.github.deck.core.util.*
import io.github.deck.rest.builder.SendMessageRequestBuilder
import java.util.*

public open class BotCommandContext(
    public val client: DeckClient,
    public val event: MessageCreateEvent,
    public val userId: GenericId,
    public val serverId: GenericId?,
    public val channelId: UUID,
    public val message: Message,
    override val command: Command<BotCommandContext>,
    override val node: CommandNode<BotCommandContext>,
    override val arguments: ArgumentMap,
    override val rawArguments: List<String>
): CommandContext<BotCommandContext> {
    public val user: StatelessUser by lazy { StatelessUser(client, userId) }
    public val server: StatelessServer? by lazy { serverId?.let { StatelessServer(client, it) } }
    public val channel: StatelessMessageChannel by lazy { StatelessMessageChannel(client, channelId, serverId) }

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
        this.message.sendReplyWithEmbed(content, embed)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BotCommandContext

        if (client != other.client) return false
        if (event != other.event) return false
        if (userId != other.userId) return false
        if (serverId != other.serverId) return false
        if (channelId != other.channelId) return false
        if (message != other.message) return false
        if (command != other.command) return false
        if (node != other.node) return false
        if (arguments != other.arguments) return false
        if (rawArguments != other.rawArguments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = client.hashCode()
        result = 31 * result + event.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + (serverId?.hashCode() ?: 0)
        result = 31 * result + channelId.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + node.hashCode()
        result = 31 * result + arguments.hashCode()
        result = 31 * result + rawArguments.hashCode()
        return result
    }

    override fun toString(): String {
        return "BotCommandContext(client=$client, event=$event, userId='$userId', serverId=$serverId, channelId=$channelId, message=$message, command=$command, node=$node, arguments=$arguments, rawArguments=$rawArguments)"
    }
}