package dev.gaabriel.clubs.common.struct

public open class Command<S : CommandContext<S>>(
    public val names: List<String>,
): CommandNode<S> {
    override val children: MutableList<CommandNode<S>> = mutableListOf()
    override var executor: (suspend S.() -> Unit)? = null

    public open var usage: (suspend S.() -> Unit)? = { send("Malformed command") }

    init {
        assert(names.isNotEmpty()) {
            "A command must have at least one name."
        }
    }

    public val officialName: String = names.first()

    public fun usage(scope: suspend S.() -> Unit) {
        this.usage = scope
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Command<*>) return false

        if (names != other.names) return false
        if (children != other.children) return false
        if (executor != other.executor) return false
        if (usage != other.usage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + children.hashCode()
        result = 31 * result + (executor?.hashCode() ?: 0)
        result = 31 * result + (usage?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Command(names=$names)"
    }
}