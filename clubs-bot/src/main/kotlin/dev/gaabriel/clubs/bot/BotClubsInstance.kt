package dev.gaabriel.clubs.bot

import io.github.deck.core.DeckClient
import dev.gaabriel.clubs.bot.util.BotInstanceBuilder
import dev.gaabriel.clubs.common.ClubsInstance
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.Command

public class BotClubsInstance(
    override var listener: CommandListener<DeckClient>,
    override var repository: CommandRepository
): ClubsInstance<DeckClient> {
    override suspend fun start(client: DeckClient) {
        listener.start(client)
    }

    override suspend fun register(command: Command<*>) {
        repository.register(command)
    }

    public companion object {
        public operator fun invoke(prefix: String): BotClubsInstance = invoke {
            this.prefix = prefix
        }
        public operator fun invoke(builder: BotInstanceBuilder.() -> Unit = {}): BotClubsInstance =
            BotInstanceBuilder().apply(builder).build()
    }
}