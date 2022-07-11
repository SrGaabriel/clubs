package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.common.abstraction.CommandListener
import dev.gaabriel.clubs.common.exception.CommandParsingException
import io.github.deck.core.DeckClient
import io.github.deck.core.util.on
import dev.gaabriel.clubs.common.parser.CommandParser
import io.github.deck.core.event.message.MessageCreateEvent
import io.github.deck.core.util.sendMessage
import kotlinx.coroutines.Job

public class BotCommandListener(
    public var prefixFunction: MessageCreateEvent.() -> String,
    public val parser: CommandParser,
    public val handler: BotCommandHandler,
): CommandListener<DeckClient> {
    override fun startListening(client: DeckClient): Job = client.on<MessageCreateEvent> {
        val call = try {
             parser.parse(prefixFunction(), message.content) ?: return@on
        } catch (exception: CommandParsingException) {
            channel.sendMessage(exception.guildedMessage)
            return@on
        }
        handler.execute(call, this)
    }
}