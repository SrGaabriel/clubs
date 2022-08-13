package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.common.exception.CommandParsingException
import io.github.deck.core.DeckClient
import io.github.deck.core.event.message.MessageCreateEvent
import io.github.deck.core.util.sendMessage
import kotlinx.coroutines.Job

public interface BotCommandListener {
    public fun listen(client: DeckClient): Job
}

public class DefaultBotCommandListener(private val clubs: BotClubsInstance): BotCommandListener {
    override fun listen(client: DeckClient): Job = client.on<MessageCreateEvent> {
        val call = try {
             clubs.parser.parse(clubs.prefix(this), message.content) ?: return@on
        } catch (exception: CommandParsingException) {
            channel.sendMessage(exception.guildedMessage)
            return@on
        }
        clubs.handler.execute(call, this)
    }
}