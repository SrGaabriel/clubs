package dev.gaabriel.clubs.bot.impl

import com.deck.common.util.DeckExperimental
import com.deck.core.DeckClient
import com.deck.core.event.message.DeckMessageCreateEvent
import com.deck.core.util.on
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.parser.CommandParser
import kotlinx.coroutines.Job

public class BotCommandListener(
    public val handler: CommandHandler<DeckMessageCreateEvent>,
    public val parser: CommandParser
): CommandListener<DeckClient> {
    @OptIn(DeckExperimental::class)
    override suspend fun start(client: DeckClient): Job = client.on<DeckMessageCreateEvent> {
        val command = parser.parseString(message.content) ?: return@on
        handler.execute(command, this)
    }
}