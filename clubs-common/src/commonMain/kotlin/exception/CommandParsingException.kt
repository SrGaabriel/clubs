package dev.gaabriel.clubs.common.exception

import dev.gaabriel.clubs.common.dictionary.ClubsDictionary
import dev.gaabriel.clubs.common.struct.CommandArgument

public open class CommandParsingException(public val guildedMessage: String): RuntimeException() {
    public class RequiredArgumentNotProvided(dictionary: ClubsDictionary, argument: CommandArgument<*>):
        CommandParsingException(dictionary.getEntry(ClubsDictionary.REQUIRED_ARGUMENT_NOT_PROVIDED, argument.type.name, argument.name))

    public class UnexpectedArgumentType(dictionary: ClubsDictionary, argument: CommandArgument<*>, peek: String):
        CommandParsingException(dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, argument.type.name, peek.takeIf { !argument.type.greedy }))

    override fun toString(): String {
        return "CommandParsingException(guildedMessage='$guildedMessage')"
    }
}