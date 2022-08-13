package dev.gaabriel.clubs.bot.util

import dev.gaabriel.clubs.common.util.ClubsLogger
import io.github.deck.common.log.DeckLogger
import io.github.deck.common.log.LoggingLevel

public fun DeckLogger.wrap(
    enabledLogLevels: MutableSet<ClubsLogger.LogLevel> = ClubsLogger.LogLevel.values().toMutableSet()
): ClubsLogger = object: ClubsLogger {
    override var enabledLogLevels: MutableSet<ClubsLogger.LogLevel> = enabledLogLevels

    override fun log(level: ClubsLogger.LogLevel, message: String, exception: Throwable?) {
        if (level !in enabledLogLevels)
            return
        this@wrap.log(level.wrap(), { message }, exception)
    }
}

public fun ClubsLogger.LogLevel.wrap(): LoggingLevel = when (this) {
    ClubsLogger.LogLevel.Info -> LoggingLevel.Info
    ClubsLogger.LogLevel.Debug -> LoggingLevel.Debug
    ClubsLogger.LogLevel.Warning -> LoggingLevel.Warning
    ClubsLogger.LogLevel.Error -> LoggingLevel.Error
}