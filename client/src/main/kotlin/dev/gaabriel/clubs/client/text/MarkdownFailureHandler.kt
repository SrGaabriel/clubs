package dev.gaabriel.clubs.client.text

import com.deck.common.content.Content
import com.deck.common.content.contentBuilder
import dev.gaabriel.clubs.client.impl.ClientCommandContext
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.FailureType

public class MarkdownFailureHandler: FailureHandler<ClientCommandContext> {
    override suspend fun onFailure(context: ClientCommandContext, type: FailureType) {
        val content: Content = when (type) {
            is FailureType.UnprovidedArgument -> contentBuilder {
                paragraph {
                    + "The required argument "
                    + type.argument.name.inlineCode()
                    + " was not specified."
                }
            }
            is FailureType.MismatchedArgumentType -> contentBuilder {
                paragraph {
                    + "The "
                    + type.argument.name.inlineCode()
                    + " argument only accepts "
                    + (type.argument.type::class.simpleName ?: "Invalid").inlineCode()
                    + " types."
                }
            }
            else -> contentBuilder {
                paragraph {
                    + "There was an error while trying to execute this command."
                }
            }
        }
        context.send {
            this.content = content
        }
    }
}