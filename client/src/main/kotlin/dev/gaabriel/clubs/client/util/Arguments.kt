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
            println(context.command.arguments.filter { it.type == User }.size)
            val mention = context.content.mentions
                .filter { it.mentionType == RawMentionType.USER }
                .getOrNull(reader.history.filterIsInstance<StatelessUser>().size)
                ?: return null
            return StatelessUser(context.client, mention.id.content)
        }
    }
}

public fun Command<ClientCommandContext>.user(name: String): Argument.Required<ClientCommandContext, StatelessUser> =
    createArgument(name, ClientArgumentType.User)