package dev.gaabriel.clubs.client.util

import com.deck.common.content.node.Node
import com.deck.common.entity.RawMentionType
import com.deck.common.util.DeckObsoleteApi
import com.deck.common.util.mapToBuiltin
import com.deck.core.stateless.StatelessRole
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.generic.GenericStatelessChannel
import com.deck.core.util.StatelessChannel
import com.deck.core.util.StatelessRole
import com.deck.core.util.StatelessUser
import dev.gaabriel.clubs.client.impl.ClientCommandContext
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.arguments.Argument
import dev.gaabriel.clubs.common.struct.arguments.ArgumentType
import dev.gaabriel.clubs.common.struct.arguments.createArgument
import dev.gaabriel.clubs.common.util.StringReader
import kotlinx.serialization.json.int

public abstract class ClientArgumentType<T>(literal: Boolean): ArgumentType<T>(literal) {
    public object User : ClientArgumentType<StatelessUser>(false) {
        override fun parse(reader: StringReader): StatelessUser? {
            val context = reader.context as ClientCommandContext
            val mention = context.content.mentions
                .filter { it.mentionType == RawMentionType.USER }
                .getOrNull(reader.history.filterIsInstance<StatelessUser>().size)
                ?: return null
            return StatelessUser(context.client, mention.id.content)
        }
    }
    public object Role : ClientArgumentType<StatelessRole>(false) {
        override fun parse(reader: StringReader): StatelessRole? {
            val context = reader.context as ClientCommandContext
            val mention = context.content.mentions
                .filter { it.mentionType == RawMentionType.ROLE }
                .getOrNull(reader.history.filterIsInstance<StatelessRole>().size)
                ?: return null
            return StatelessRole(context.client, mention.id.int, context.team!!)
        }
    }
    public object Channel : ClientArgumentType<GenericStatelessChannel>(false) {
        @OptIn(DeckObsoleteApi::class)
        override fun parse(reader: StringReader): GenericStatelessChannel? {
            val context = reader.context as ClientCommandContext
            val mention = context.content.paragraphs
                .flatMap { it.data.children }
                .filterIsInstance<Node.Mention.Channel>()
                .getOrNull(reader.history.filterIsInstance<GenericStatelessChannel>().size)
                ?: return null
            return StatelessChannel(context.client, mention.id.mapToBuiltin(), context.team)
        }
    }
}

public fun Command<ClientCommandContext>.user(name: String): Argument.Required<ClientCommandContext, StatelessUser> =
    createArgument(name, ClientArgumentType.User)

public fun Command<ClientCommandContext>.role(name: String): Argument.Required<ClientCommandContext, StatelessRole> =
    createArgument(name, ClientArgumentType.Role)

public fun Command<ClientCommandContext>.channel(name: String): Argument.Required<ClientCommandContext, GenericStatelessChannel> =
    createArgument(name, ClientArgumentType.Channel)