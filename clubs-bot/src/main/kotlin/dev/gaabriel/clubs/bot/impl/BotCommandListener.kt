package dev.gaabriel.clubs.bot.impl

import io.github.deck.core.DeckClient
import io.github.deck.core.event.message.DeckMessageCreateEvent
import io.github.deck.core.util.on
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.parser.CommandParser
import kotlinx.coroutines.Job

public class BotCommandListener(
    public val handler: CommandHandler<DeckMessageCreateEvent>,
    public val parser: CommandParser
): CommandListener<DeckClient> {
    override suspend fun start(client: DeckClient): Job = client.on<DeckMessageCreateEvent> {
        val command = parser.parseString(message.content) ?: return@on
        handler.execute(command, this)
    }
}