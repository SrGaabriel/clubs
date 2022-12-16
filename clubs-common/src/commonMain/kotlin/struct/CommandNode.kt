package io.github.srgaabriel.clubs.common.struct

import io.github.srgaabriel.clubs.common.annotation.ClubsDelicateApi
import kotlinx.coroutines.sync.Mutex
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public abstract class CommandNode<S : CommandContext<S>>(public open val name: String?) {
    public val children: MutableList<CommandNode<S>> = mutableListOf()
    private var executor: (suspend S.() -> Unit)? = null

    private var executionMutex: Mutex? = Mutex()
    @PublishedApi
    internal var currentContext: S? = null
    @ClubsDelicateApi
    public var synchronized: Boolean
        get() = executionMutex != null
        set(value) {
            executionMutex = when {
                value && executionMutex != null -> return
                value -> Mutex()
                !value && executionMutex?.isLocked == true -> error("Can't make node async while it is being executed")
                else -> null
            }
        }

    public val isExecutable: Boolean get() = executor != null

    public val delegatedArguments: MutableList<DelegatedArgument<S, *>> = mutableListOf()

    public fun executor(executor: suspend S.() -> Unit) {
        this.executor = executor
    }

    public fun literal(vararg names: String, scope: CommandLiteralNode<S>.() -> Unit) {
        for (name in names) {
            children.add(CommandLiteralNode<S>(name).inheritTraits().apply(scope))
        }
    }

    public fun <T : Any> argument(name: String? = null, type: ArgumentType<T>, scope: CommandArgumentNode<S, T>.(CommandArgumentNode<S, T>) -> Unit) {
        children.add(CommandArgumentNode<S, T>(name, type).inheritTraits().apply { scope(this) })
    }

    public fun <T : Any> requiredArgument(name: String? = null, type: ArgumentType<T>): DelegatedArgument.Required<S, T> {
        val argument = DelegatedArgument.Required<S, T>(name, type)
        delegatedArguments.add(argument)
        return argument
    }

    public fun <T : Any> optionalArgument(name: String? = null, type: ArgumentType<T>): DelegatedArgument.Optional<S, T> {
        val argument = DelegatedArgument.Optional<S, T>(name, type)
        delegatedArguments.add(argument)
        return argument
    }

    public fun selector(options: List<String>, scope: CommandLiteralNode<S>.(String) -> Unit) {
        options.forEach { option ->
            literal(option) {
                scope(option)
            }
        }
    }

    public suspend fun execute(context: S) {
        var caughtException: Throwable? = null

        executionMutex?.lock()
        try {
            currentContext = context
            executor?.let { it(context) }
            currentContext = null
        } catch (throwable: Throwable) {
            caughtException = throwable
        }
        executionMutex?.unlock()
        if (caughtException != null)
            throw caughtException
    }

    public inline operator fun <reified T : Any> DelegatedArgument.Required<S, T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        ReadOnlyProperty { _, _ ->
            val context =
                currentContext ?: error("Tried to delegate argument value while not in a command context")
            context.arguments[this] as T
        }

    public inline operator fun <reified T : Any> DelegatedArgument.Optional<S, T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T?> =
        ReadOnlyProperty { _, _ ->
            val context =
                currentContext ?: error("Tried to delegate argument value while not in a command context")
            val value = context.arguments[this] ?: return@ReadOnlyProperty null
            value as T
        }

    @OptIn(ClubsDelicateApi::class)
    private fun <T : CommandNode<S>> T.inheritTraits() = apply {
        this@inheritTraits.synchronized = this@CommandNode.synchronized
    }
}

public data class CommandLiteralNode<S : CommandContext<S>>(override val name: String): CommandNode<S>(name) {
    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

public class CommandArgumentNode<S : CommandContext<S>, T : Any>(
    name: String?,
    override val type: ArgumentType<T>,
): CommandNode<S>(name), CommandArgument<T> {
    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}