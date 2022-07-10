package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.common.parser.CommandCall
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandNode
import io.github.deck.core.event.message.MessageCreateEvent
import io.github.deck.core.util.sendMessage

public class BotCommandHandler {
    @Suppress("unchecked_cast")
    public suspend fun execute(call: CommandCall, event: MessageCreateEvent) {
        val root = call.root as Command<BotCommandContext>
        val declaration = call.node as CommandNode<BotCommandContext>
        val context = BotCommandContext(
            client = event.client,
            event = event,
            userId = event.authorId,
            serverId = event.serverId,
            channelId = event.channelId,
            message = event.message,
            command = call.root as Command<BotCommandContext>,
            node = call.node as CommandNode<BotCommandContext>,
            arguments = call.arguments as Map<CommandArgumentNode<BotCommandContext, *>, Any>,
            rawArguments = call.rawArguments
        )
        if (declaration.executor == null) {
            root.usage?.let { context.channel.sendMessage(it(context)) }
            return
        }
        declaration.executor!!(context)
    }
}