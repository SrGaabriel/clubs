package io.github.srgaabriel.clubs.common.dictionary

public fun interface ErrorHandler {
    public fun handle(dictionary: ClubsDictionary, exception: RuntimeException): String?
}