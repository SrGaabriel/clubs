package dev.gaabriel.clubs.bot.impl

import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.bot.event.CommandParseEvent
import dev.gaabriel.clubs.common.exception.CommandParsingException
import dev.gaabriel.clubs.common.parser.CommandCall
import io.github.deck.common.log.debug
import io.github.deck.common.log.info
import io.github.deck.core.DeckClient
import io.github.deck.core.event.message.MessageCreateEvent
import io.github.deck.core.util.sendMessage
import kotlinx.coroutines.Job
import kotlin.system.measureTimeMillis

public interface BotCommandListener {
    public fun listen(client: DeckClient): Job
}

public class DefaultBotCommandListener(private val clubs: BotClubsInstance): BotCommandListener {
    override fun listen(client: DeckClient): Job = client.on<MessageCreateEvent> {
        val parsingTime: Long
        val executionTime: Long
        val overallProcessDuration = measureTimeMillis {
            val call = try {
                val call: CommandCall
                parsingTime = measureTimeMillis {
                    call = clubs.parser.parse(clubs.prefix(this), message.content) ?: return@on
                }
                clubs.logger?.debug { "[Clubs] Command call for ${call.root.officialName} parsed in ${parsingTime}ms" }
                call
            } catch (exception: CommandParsingException) {
                channel.sendMessage(exception.guildedMessage)
                return@on
            }
            val event = CommandParseEvent(
                client = client,
                barebones = barebones,
                clubs = clubs,
                call = call
            )
            client.eventService.eventWrappingFlow.emit(event)
            if (event.proceed)
                executionTime = measureTimeMillis {
                    clubs.handler.execute(call, this)
                }
            else
                return@on
        }
        clubs.logger?.info { "[Clubs] Total command cost was ${overallProcessDuration}ms (Parsing: ${parsingTime}ms | Execution: ${executionTime}ms | Others: ${overallProcessDuration - parsingTime - executionTime}ms)" }
    }
}