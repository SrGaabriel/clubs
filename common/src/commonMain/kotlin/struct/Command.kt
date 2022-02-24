package dev.gaabriel.clubs.common.struct

import dev.gaabriel.clubs.common.struct.arguments.Argument
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public data class Command<T : CommandContext>(val names: List<String>) {
    public val metadata: CommandMetadata = CommandMetadata()
    public val subcommands: MutableList<Command<*>> = mutableListOf()

    private var trigger: suspend T.() -> Unit = {}
    public var requirements: suspend T.() -> Boolean = {true}
    private var currentContext: T? = null

    public val arguments: LinkedHashSet<Argument<T, *>> = linkedSetOf()

    public suspend fun call(context: T) {
        currentContext = context
        trigger(context)
    }

    public fun command(vararg names: String, builder: Command<T>.() -> Unit): Unit =
        this.command<T>(names = names, builder = builder)

    @JvmName("typedCommand")
    public fun <G : CommandContext> command(vararg names: String, builder: Command<G>.() -> Unit) {
        subcommands.add(Command<G>(names.toList()).apply(builder))
    }

    public fun runs(executor: suspend T.() -> Unit) {
        trigger = executor
    }

    public fun metadata(vararg entries: Pair<String, Any>): Unit = metadata {
        for (entry in entries) {
            entry.first pair entry.second
        }
    }

    public fun metadata(scope: CommandMetadata.() -> Unit): Unit = metadata.run(scope)

    @Suppress("unchecked_cast")
    public operator fun <V> Argument.Required<T, V>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, V> =
        ReadOnlyProperty { _, _ ->
            currentContext?.arguments?.getOrNull(arguments.indexOf(this)) as? V ?: error("")
        }

    @Suppress("unchecked_cast")
    public operator fun <V> Argument.Optional<T, V>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, V?> =
        ReadOnlyProperty { _, _ ->
            currentContext?.arguments?.getOrNull(arguments.indexOf(this)) as? V ?: error("")
        }
}