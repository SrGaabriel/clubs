package dev.gaabriel.clubs.bot.impl

import com.deck.core.DeckClient
import com.deck.core.entity.Message
import com.deck.core.stateless.StatelessMember
import com.deck.core.stateless.StatelessServer
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.channel.StatelessMessageChannel
import com.deck.core.util.StatelessMember
import com.deck.core.util.sendMessage
import com.deck.core.util.sendReply
import com.deck.rest.builder.SendMessageRequestBuilder
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public open class BotCommandContext(
    public val client: DeckClient,
    public val user: StatelessUser,
    public val server: StatelessServer?,
    public val channel: StatelessMessageChannel,
    public val message: Message,
    override val command: Command<BotCommandContext>,
    override val rawArguments: List<String>
): CommandContext {
    internal var _arguments: List<Any> = listOf()
    override val arguments: List<Any> get() = _arguments

    public val content: String get() = message.content
    public val member: StatelessMember? by lazy { server?.let { StatelessMember(client, user.id, it) } }

    override suspend fun send(message: String): Message =
        channel.sendMessage(message)

    public suspend fun send(builder: SendMessageRequestBuilder.() -> Unit): Message =
        channel.sendMessage(builder)

    override suspend fun reply(message: String): Message =
        this.message.sendReply(message)

    public suspend fun reply(builder: SendMessageRequestBuilder.() -> Unit): Message =
        this.message.sendReply(builder)
}