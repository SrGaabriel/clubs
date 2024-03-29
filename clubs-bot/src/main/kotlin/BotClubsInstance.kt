package io.github.srgaabriel.clubs.bot

import io.github.reactivecircus.cache4k.Cache
import io.github.srgaabriel.clubs.bot.impl.*
import io.github.srgaabriel.clubs.bot.util.PrefixProvider
import io.github.srgaabriel.clubs.common.ClubsInstance
import io.github.srgaabriel.clubs.common.dictionary.ClubsDictionary
import io.github.srgaabriel.clubs.common.dictionary.DefaultClubsDictionary
import io.github.srgaabriel.clubs.common.dictionary.DefaultErrorHandler
import io.github.srgaabriel.clubs.common.dictionary.ErrorHandler
import io.github.srgaabriel.clubs.common.parser.CommandCall
import io.github.srgaabriel.clubs.common.parser.CommandParser
import io.github.srgaabriel.clubs.common.parser.TextCommandParser
import io.github.srgaabriel.clubs.common.repository.CommandRepository
import io.github.srgaabriel.clubs.common.repository.DefaultCommandRepository
import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandContext
import io.github.srgaabriel.deck.common.log.DeckLogger
import io.github.srgaabriel.deck.core.DeckClient
import io.github.srgaabriel.deck.core.event.message.MessageCreateEvent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class BotClubsInstance: ClubsInstance<DeckClient> {
    public var handler: BotCommandHandler = DefaultBotCommandHandler(this)
    public var listener: BotCommandListener = DefaultBotCommandListener(this)

    public var parser: CommandParser = TextCommandParser(
        dictionary = DefaultClubsDictionary(),
        repository = DefaultCommandRepository()
    )
    public var prefix: PrefixProvider = { DEFAULT_PREFIX }

    public var dictionary: ClubsDictionary by parser::dictionary
    public var repository: CommandRepository by parser::repository
    public var errorHandler: ErrorHandler = DefaultErrorHandler()

    public var caseSensitive: Boolean by parser::caseSensitive
    public var parsingResultsCache: Cache<String, CommandCall>? by parser::cache

    public var logger: DeckLogger? = null

    /**
     * Sets a prefix provider ([MessageCreateEvent] -> [String])
     *
     * @param provider prefix provider
     */
    public fun prefix(provider: PrefixProvider) {
        this.prefix = provider
    }

    /**
     * Defines a cache of parsing results
     *
     * @param cache new parsing results cache, null to disable cache
     */
    public fun setCache(cache: Cache<String, CommandCall>?) {
        parsingResultsCache = cache
    }

    public companion object {
        /**
         * Builds a [BotClubsInstance]. For a default and fixed prefix, specify a [prefix],
         * otherwise the default one ([BotClubsInstance.DEFAULT_PREFIX]) will be used instead.
         *
         * @param prefix a fixed prefix, null for default
         * @param scope builder
         *
         * @return built clubs instance
         */
        public operator fun invoke(prefix: String? = null, scope: BotClubsInstance.() -> Unit): BotClubsInstance =
            clubs(prefix, scope)

        /**
         * Default prefix "!"
         */
        public const val DEFAULT_PREFIX: String = "!"
    }

    override fun setup(client: DeckClient) {
        listener.listen(client)
    }

    override fun <S : CommandContext<S>> register(command: Command<S>): Unit =
        repository.register(command)

    @Suppress("unchecked_cast")
    override fun <S : CommandContext<S>> retrieve(name: String): Command<S>? =
        repository.search(name)?.let { it as? Command<S> }

    override fun <S : CommandContext<S>> exclude(command: Command<S>): Unit =
        repository.exclude(command)
}

/**
 * Builds a [BotClubsInstance]. For a default and fixed prefix, specify a [prefix],
 * otherwise the default one ([BotClubsInstance.DEFAULT_PREFIX]) will be used instead.
 *
 * @param prefix a fixed prefix, null for default
 * @param scope builder
 *
 * @return built clubs instance
 */
@OptIn(ExperimentalContracts::class)
public fun clubs(prefix: String? = null, scope: BotClubsInstance.() -> Unit = {}): BotClubsInstance {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    val clubs = BotClubsInstance()
    if (prefix != null)
        clubs.prefix { prefix }
    clubs.apply(scope)
    return clubs
}