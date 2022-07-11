package dev.gaabriel.clubs.bot

import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.bot.impl.BotCommandHandler
import dev.gaabriel.clubs.bot.impl.BotCommandListener
import io.github.deck.core.DeckClient
import dev.gaabriel.clubs.common.ClubsInstance
import dev.gaabriel.clubs.common.abstraction.CommandListener
import dev.gaabriel.clubs.common.dictionary.DefaultClubsDictionary
import dev.gaabriel.clubs.common.parser.DefaultCommandParser
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.repository.DefaultCommandRepository
import dev.gaabriel.clubs.common.struct.Command

public class BotClubsInstance(
    public var listener: CommandListener<DeckClient>,
    public var repository: CommandRepository,
): ClubsInstance<DeckClient, BotCommandContext> {
    override fun start(client: DeckClient) {
        listener.startListening(client)
    }

    override fun register(command: Command<BotCommandContext>) {
        repository.register(command)
    }

    public companion object {
        public operator fun invoke(prefix: String): BotClubsInstance {
            val repository = DefaultCommandRepository()
            return BotClubsInstance(
                listener = BotCommandListener(
                    handler = BotCommandHandler(),
                    prefixFunction = { prefix },
                    parser = DefaultCommandParser(DefaultClubsDictionary(), repository)
                ),
                repository = repository
            )
        }
    }
}