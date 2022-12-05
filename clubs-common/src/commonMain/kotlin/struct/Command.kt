package dev.gaabriel.clubs.common.struct

public open class Command<S : CommandContext<S>>(
    public val names: List<String>,
): CommandNode<S>(names.first()) {
    public open var usage: (suspend S.() -> Unit)? = null

    init {
        assert(names.isNotEmpty()) {
            "A command must have at least one name."
        }
    }

    public val officialName: String = names.first()

    public fun usage(scope: suspend S.() -> Unit) {
        this.usage = scope
    }

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "Command(names=$names)"
    }


}