package io.github.srgaabriel.clubs.common.dictionary

import io.github.srgaabriel.clubs.common.exception.CommandParsingException

public class DefaultErrorHandler: ErrorHandler {
    override fun handle(dictionary: ClubsDictionary, exception: RuntimeException): String? {
        if (exception is CommandParsingException)
            return handleCommandParsingException(dictionary, exception)
        return null
    }

    private fun handleCommandParsingException(dictionary: ClubsDictionary, exception: CommandParsingException): String? {
        return when (exception) {
            is CommandParsingException.RequiredArgumentNotProvided ->
                dictionary.getEntry(ClubsDictionary.REQUIRED_ARGUMENT_NOT_PROVIDED, exception.argument.type.name, exception.argument.name)
            is CommandParsingException.UnexpectedArgumentType ->
                dictionary.getEntry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE, exception.argument.type.name, exception.peek.takeIf { !exception.argument.type.greedy })
            else ->
                null
        }
    }
}