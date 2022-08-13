package dev.gaabriel.clubs.common.struct

public interface CommandNode<S : CommandContext<S>> {
    public val children: MutableList<CommandNode<S>>
    public var executor: (suspend S.() -> Unit)?

    public fun executor(executor: suspend S.() -> Unit) {
        this.executor = executor
    }

    public fun literal(vararg names: String, scope: CommandLiteralNode<S>.() -> Unit) {
        children.add(CommandLiteralNode<S>(names.toList()).apply(scope))
    }

    public fun <T : Any> argument(name: String, type: ArgumentType<T>, scope: CommandArgumentNode<S, T>.(CommandArgumentNode<S, T>) -> Unit) {
        children.add(CommandArgumentNode<S, T>(name, type).apply { scope(this) })
    }
}

public data class CommandLiteralNode<S : CommandContext<S>>(val names: List<String>): CommandNode<S> {
    override val children: MutableList<CommandNode<S>> = mutableListOf()
    override var executor: (suspend S.() -> Unit)? = null
}

public data class CommandArgumentNode<S : CommandContext<S>, T : Any>(
    public val name: String,
    public val type: ArgumentType<T>,
): CommandNode<S> {
    override val children: MutableList<CommandNode<S>> = mutableListOf()
    override var executor: (suspend S.() -> Unit)? = null
}