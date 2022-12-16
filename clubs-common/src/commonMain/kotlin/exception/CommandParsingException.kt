package io.github.srgaabriel.clubs.common.exception

import io.github.srgaabriel.clubs.common.dictionary.ClubsDictionary
import io.github.srgaabriel.clubs.common.struct.CommandArgument

public open class CommandParsingException(public val key: String): RuntimeException() {
    public class RequiredArgumentNotProvided(public val argument: CommandArgument<*>):
        CommandParsingException(ClubsDictionary.REQUIRED_ARGUMENT_NOT_PROVIDED)

    public class UnexpectedArgumentType(public val argument: CommandArgument<*>, public val peek: String):
        CommandParsingException(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE)

    override fun toString(): String {
        return "CommandParsingException(key='$key')"
    }
}