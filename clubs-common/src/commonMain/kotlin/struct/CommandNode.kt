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

    public fun <T : Any> delegateArgument(name: String? = null, type: ArgumentType<T>, command: Command<S>): DelegatedArgument.Required<S, T> {
        val argumentNode = CommandArgumentNode(command, name, type)
        workspace.children.add(argumentNode)
        workspace.workingArgument = argumentNode
        workingArgument = argumentNode
        return DelegatedArgument.Required(command, argumentNode)
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
            context.arguments[this.node] as T
        }
}

public data class CommandLiteralNode<S : CommandContext<S>>(override val command: Command<S>, override val name: String)
    : CommandNode<S>(name)

public class CommandArgumentNode<S : CommandContext<S>, T : Any>(
    override val command: Command<S>,
    name: String?,
    public val type: ArgumentType<T>,
): CommandNode<S>(name) {
    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

public sealed class DelegatedArgument<S : CommandContext<S>, T : Any>(public val command: Command<S>, public val node: CommandArgumentNode<S, T>) {
    public class Required<S : CommandContext<S>, T : Any>(command: Command<S>, node: CommandArgumentNode<S, T>): DelegatedArgument<S, T>(command, node)
}