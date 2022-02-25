package dev.gaabriel.clubs.client.util

import com.deck.common.entity.RawMentionType
import com.deck.core.stateless.StatelessUser
import com.deck.core.util.StatelessUser
import dev.gaabriel.clubs.client.impl.ClientCommandContext
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.arguments.Argument
import dev.gaabriel.clubs.common.struct.arguments.ArgumentType
import dev.gaabriel.clubs.common.struct.arguments.createArgument
import dev.gaabriel.clubs.common.util.StringReader

public abstract class ClientArgumentType<T>(literal: Boolean): ArgumentType<T>(literal) {
    public object User : ClientArgumentType<StatelessUser>(false) {
        override fun parse(reader: StringReader): StatelessUser? {
            val context = reader.context as ClientCommandContext
            val mention = context.content.mentions
                .filter { it.mentionType == RawMentionType.USER }
                .getOrNull(context.command.arguments.filterIsInstance<User>().indexOf(this) + 1)
                ?: return null
            return StatelessUser(context.client, mention.id.content)
        }
    }
}

public fun Command<ClientCommandContext>.user(name: String): Argument.Required<ClientCommandContext, StatelessUser> =
    createArgument(name, ClientArgumentType.User)