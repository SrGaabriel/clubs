package dev.gaabriel.clubs.common.struct

import kotlinx.coroutines.sync.Mutex

public open class Command<S : CommandContext<S>>(
    public val names: List<String>,
): CommandNode<S>(names.first()) {
    public open var usage: (suspend S.() -> Unit)? = null

    internal var executionMutex: Mutex? = Mutex()
    @PublishedApi
    internal var currentContext: S? = null
    public var synchronized: Boolean
        get() = executionMutex != null
        set(value) {
            executionMutex = if (value) Mutex() else null
        }

    override val command: Command<S> get() = this

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