package dev.gaabriel.clubs.common.struct

public data class Command<S : CommandContext<S>>(
    public val names: List<String>,
): CommandNode<S> {
    override val children: MutableList<CommandNode<S>> = mutableListOf()
    override var executor: (suspend S.() -> Unit)? = null

    var usage: (S.() -> String)? = { "Malformed command" }

    public fun usage(scope: S.() -> String) {
        this.usage = scope
    }
}