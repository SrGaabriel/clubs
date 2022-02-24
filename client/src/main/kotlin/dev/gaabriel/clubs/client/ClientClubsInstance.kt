package dev.gaabriel.clubs.client

import com.deck.core.DeckClient
import dev.gaabriel.clubs.client.util.ClientInstanceBuilder
import dev.gaabriel.clubs.common.ClubsInstance
import dev.gaabriel.clubs.common.handler.CommandListener
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.Command

public class ClientClubsInstance(
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
        public operator fun invoke(prefix: String): ClientClubsInstance = invoke {
            this.prefix = prefix
        }
        public operator fun invoke(builder: ClientInstanceBuilder.() -> Unit = {}): ClientClubsInstance =
            ClientInstanceBuilder().apply(builder).build()
    }
}