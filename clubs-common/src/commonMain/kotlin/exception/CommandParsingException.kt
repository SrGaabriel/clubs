package dev.gaabriel.clubs.common.exception

public data class CommandParsingException(val guildedMessage: String): RuntimeException()