package dev.gaabriel.clubs.common.struct

import kotlinx.coroutines.sync.withLock
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public abstract class CommandNode<S : CommandContext<S>>(public open val name: String?) {
    public val children: MutableList<CommandNode<S>> = mutableListOf()
    private var executor: (suspend S.() -> Unit)? = null

    private var workingArgument: CommandArgumentNode<S, *>? = null
    @PublishedApi
    internal val workspace: CommandNode<S> get() = workingArgument ?: this

    public val isExecutable: Boolean get() = workspace.executor != null

    public abstract val command: Command<S>
    public val delegatedArguments: MutableList<DelegatedArgument<S, *>> = mutableListOf()

    public fun executor(executor: suspend S.() -> Unit) {
        workspace.executor = executor
    }

    public fun literal(vararg names: String, scope: CommandLiteralNode<S>.() -> Unit) {
        for (name in names) {
            workspace.children.add(CommandLiteralNode(command, name).apply(scope))
        }
    }

    public fun <T : Any> argument(name: String? = null, type: ArgumentType<T>, scope: CommandArgumentNode<S, T>.(CommandArgumentNode<S, T>) -> Unit) {
        workspace.children.add(CommandArgumentNode(command, name, type).apply { scope(this) })
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
            workspace.literal(option) {
                scope(option)
            }
        }
    }

    public suspend fun execute(context: S) {
        var caughtException: Throwable? = null
        command.executionMutex?.withLock {
            command.currentContext = context
            try {
                workspace.executor?.let { it(context) }
            } catch (throwable: Throwable) {
                caughtException = throwable
            }
            command.currentContext = null
        }
        if (caughtException != null) throw caughtException!!
    }

    public inline operator fun <reified T : Any> DelegatedArgument.Required<S, T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        ReadOnlyProperty { _, _ ->
            val context =
                command.currentContext ?: error("Tried to delegate argument value while not in a command context")
            context.arguments[this] as T
        }

    public inline operator fun <reified T : Any> DelegatedArgument.Optional<S, T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T?> =
        ReadOnlyProperty { _, _ ->
            val context =
                command.currentContext ?: error("Tried to delegate argument value while not in a command context")
            val value = context.arguments[this] ?: return@ReadOnlyProperty null
            value as T
        }
}

public data class CommandLiteralNode<S : CommandContext<S>>(override val command: Command<S>, override val name: String)
    : CommandNode<S>(name)

public class CommandArgumentNode<S : CommandContext<S>, T : Any>(
    override val command: Command<S>,
    name: String?,
    override val type: ArgumentType<T>,
): CommandNode<S>(name), CommandArgument<S, T> {
    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}