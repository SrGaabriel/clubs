package io.github.srgaabriel.clubs.common.dictionary

public open class DefaultClubsDictionary: ClubsDictionary {
    private val entries: HashMap<String, (List<String>) -> String> = hashMapOf()

    init {
        entry(ClubsDictionary.UNEXPECTED_ARGUMENT_TYPE) { arguments ->
            val end = arguments.getOrNull(1).let { if (it == null) "it could not be inferred" else "`$it` was found" }
            "A `${arguments.first()}` was expected, but $end."
        }
        entry(ClubsDictionary.QUOTE_ARGUMENT_NEVER_CLOSED) { _ ->
            "A quote was opened but never closed."
        }
        entry(ClubsDictionary.REQUIRED_ARGUMENT_NOT_PROVIDED) { arguments ->
            val start = arguments.getOrNull(1).let { if (it == null) "An argument" else "The argument `$it`" }
            "$start of type `${arguments.first()}` was expected but not provided."
        }
    }

    public var missingEntryMessage: (String) -> String = { key -> "The key `$key` is missing in the `clubs` dictionary." }

    override fun getEntry(key: String, vararg args: String?): String =
        entries[key]?.invoke(args.filterNotNull()) ?: missingEntryMessage(key)

    private fun entry(key: String, scope: (List<String>) -> String) {
        entries[key] = scope
    }
}