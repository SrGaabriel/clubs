package dev.gaabriel.clubs.bot.util

import io.github.deck.core.DeckClient
import io.github.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.bot.impl.BotCommandHandler
import dev.gaabriel.clubs.bot.impl.BotCommandListener
import dev.gaabriel.clubs.bot.text.MarkdownFailureHandler
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.parser.CommandParser
import dev.gaabriel.clubs.common.parser.TextCommandParser
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.repository.MapCommandRepository
import dev.gaabriel.clubs.common.util.DEFAULT_PREFIX
import dev.gaabriel.clubs.common.util.FailureHandler

public class BotInstanceBuilder {
    public var prefix: String = DEFAULT_PREFIX

    public var repository: CommandRepository = MapCommandRepository()
    public var listener: CommandListener<DeckClient>? = null

    public var failureHandler: FailureHandler<*> = MarkdownFailureHandler()

    public var parser: CommandParser? = null
    public var handler: CommandHandler<DeckMessageCreateEvent>? = null

    public fun build(): BotClubsInstance =
        BotClubsInstance(listener ?: BotCommandListener(
            handler = handler ?: BotCommandHandler(failureHandler),
            parser = parser ?: TextCommandParser(repository, prefix)
        ), repository)
}