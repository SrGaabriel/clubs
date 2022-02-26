package dev.gaabriel.clubs.bot.impl

import com.deck.core.DeckClient
import com.deck.core.entity.Message
import com.deck.core.stateless.StatelessServer
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.channel.StatelessMessageChannel
import com.deck.core.util.sendMessage
import com.deck.core.util.sendReply
import com.deck.rest.builder.SendMessageRequestBuilder
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public data class BotCommandContext(
    val client: DeckClient,
    val user: StatelessUser,
    val server: StatelessServer?,
    val channel: StatelessMessageChannel,
    val message: Message,
    override val command: Command<BotCommandContext>,
    override val rawArguments: List<String>
): CommandContext {
    internal var _arguments: List<Any> = listOf()
    override val arguments: List<Any> get() = _arguments

    public val content: String get() = message.content

    override suspend fun send(message: String): Message =
        channel.sendMessage(message)

    public suspend fun send(builder: SendMessageRequestBuilder.() -> Unit): Message =
        channel.sendMessage(builder)

    override suspend fun reply(message: String): Any =
        this.message.sendReply(message)

    public suspend fun reply(builder: SendMessageRequestBuilder.() -> Unit): Message =
        this.message.sendReply(builder)
}