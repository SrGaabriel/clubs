package dev.gaabriel.clubs.common.dictionary

public open class DefaultClubsDictionary: ClubsDictionary {
    private val entries: HashMap<String, String> = hashMapOf(
        ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE to "A `{0}` was expected, but `{1}` was found.",
        ClubsDictionary.QUOTE_ARGUMENT_NEVER_CLOSED to "A quote was opened but never closed.",
        ClubsDictionary.REQUIRED_ARGUMENT_NOT_PROVIDED to "An argument of type `{0}` was expected but not provided.",
    )

    public var missingEntryMessage: (String) -> String = { key -> "The key `$key` is missing in the `clubs` dictionary." }

    override fun getEntry(key: String, vararg args: String): String {
        return format((entries[key] ?: return missingEntryMessage(key)), args)
    }

    private fun format(text: String, args: Array<out String>): String {
        var result: String = text
        args.forEachIndexed { index, argument ->
            result = result.replace("{$index}", argument)
        }
        return result
    }
}