package dev.gaabriel.clubs.client.util

import com.deck.core.DeckClient
import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.client.ClientClubsInstance
import dev.gaabriel.clubs.client.impl.ClientCommandHandler
import dev.gaabriel.clubs.client.impl.ClientCommandListener
import dev.gaabriel.clubs.client.text.MarkdownFailureHandler
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.parser.CommandParser
import dev.gaabriel.clubs.common.parser.TextCommandParser
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.repository.MapCommandRepository
import dev.gaabriel.clubs.common.util.DEFAULT_PREFIX
import dev.gaabriel.clubs.common.util.FailureHandler

public class ClientInstanceBuilder {
    public var prefix: String = DEFAULT_PREFIX

    public var repository: CommandRepository = MapCommandRepository()
    public var listener: CommandListener<DeckClient>? = null

    public var failureHandler: FailureHandler<*> = MarkdownFailureHandler()

    public var parser: CommandParser? = null
    public var handler: CommandHandler<DeckMessageCreateEvent>? = null

    public fun build(): ClientClubsInstance =
        ClientClubsInstance(listener ?: ClientCommandListener(
            handler = handler ?: ClientCommandHandler(failureHandler),
            parser = parser ?: TextCommandParser(repository, prefix)
        ), repository)
}