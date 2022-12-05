package dev.gaabriel.clubs.common.dictionary

public interface ClubsDictionary {
    public fun getEntry(key: String, vararg args: String): String

    public companion object {
        public const val UNEXPECTED_ARGUMENT_TYPE: String = "unexpected-argument-type"

        public const val QUOTE_ARGUMENT_NEVER_CLOSED: String = "quote-never-closed"

        public const val REQUIRED_ARGUMENT_NOT_PROVIDED: String = "required-argument-not-provided"
    }
}