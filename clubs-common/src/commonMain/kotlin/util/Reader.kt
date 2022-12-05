package dev.gaabriel.clubs.common.util

public data class StringReader(public val content: List<String>) {
    public var index: Int = 0

    public val hasMore: Boolean get() = !isEnd()

    public fun isLast(): Boolean = index + 1 >= content.size

    public fun isEnd(): Boolean = index >= content.size

    public fun peek(): String = content[index]

    public fun peekOrNull(): String? = content.getOrNull(index)

    public fun next(): String {
        val next = content[index]
        index++
        return next
    }

    public fun nextOrNull(): String? {
        val next = content.getOrNull(index) ?: return null
        index++
        return next
    }

    public fun peekRemainingAsList(): List<String> =
        content.takeLast(content.size - index)

    public fun peekRemainingAsListOrNull(): List<String>? =
        content.takeIf { !isEnd() }?.takeLast(content.size - index)

    public fun remainingAsList(): List<String> {
        val remaining = peekRemainingAsList()
        index = content.size - 1
        return remaining
    }

    public fun remainingAsListOrNull(): List<String>? {
        val remaining = peekRemainingAsListOrNull() ?: return null
        index = content.size - 1
        return remaining
    }

    public fun peekRemaining(): String =
        content.takeLast(content.size - index).joinToString(" ")

    public fun peekRemainingOrNull(): String? =
        content.takeIf { !isEnd() }?.takeLast(content.size - index)?.joinToString(" ")

    public fun remaining(): String {
        val remaining = peekRemaining()
        index = content.size - 1
        return remaining
    }

    public fun remainingOrNull(): String? {
        val remaining = peekRemainingOrNull() ?: return null
        index = content.size - 1
        return remaining
    }

    public fun readWhile(scope: (String) -> Boolean): String = buildList {
        while(!isEnd() && scope(peek())) {
            val next = next()
            add(next)
        }
    }.joinToString(" ")

    public fun readWhileOrNull(scope: (String) -> Boolean): String? = readWhile(scope).ifEmpty { null }

    public fun readUntil(scope: (String) -> Boolean): String = buildList {
        while (!isEnd() && !scope(peek())) {
            add(next())
        }
    }.joinToString(" ")

    public fun readUntilOrNull(scope: (String) -> Boolean): String? {
        var found = false
        val result = buildList {
            while (!isEnd()) {
                if (!scope(peek())) {
                    found = true
                    break
                }
                add(next())
            }
        }
        return result.takeIf { found }?.joinToString(" ")
    }

    public fun readUntilInclusive(scope: (String) -> Boolean): String = buildList {
        while (!isEnd()) {
            if (!scope(peek())) {
                add(next())
                break
            }
            add(next())
        }
    }.joinToString(" ")

    public fun readUntilInclusiveOrNull(scope: (String) -> Boolean): String? {
        var found = false
        val result = buildList {
            while (hasMore) {
                if (scope(peek())) {
                    add(next())
                    found = true
                    break
                }
                add(next())
            }
        }
        return result.takeIf { found }?.joinToString(" ")
    }
}