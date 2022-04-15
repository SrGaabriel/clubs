package dev.gaabriel.clubs.client.text

import com.deck.common.content.Content
import com.deck.common.content.contentBuilder
import dev.gaabriel.clubs.client.impl.ClientCommandContext
import dev.gaabriel.clubs.common.util.FailureHandler
import dev.gaabriel.clubs.common.util.CommandFailure

public class MarkdownFailureHandler: FailureHandler<ClientCommandContext> {
    override suspend fun onFailure(context: ClientCommandContext, failure: CommandFailure) {
        val content: Content = when (failure) {
            is CommandFailure.UnprovidedArgument -> contentBuilder {
                paragraph {
                    + "The required argument "
                    + failure.argument.name.inlineCode()
                    + "(${failure.argument.type.name})".bold()
                    + " was not specified."
                }
            }
            is CommandFailure.MismatchedArgumentType -> contentBuilder {
                paragraph {
                    + "The "
                    + failure.argument.name.inlineCode()
                    + " argument only accepts "
                    + failure.argument.type.name.lowercase()
                    + "s types."
                }
            }
            else -> contentBuilder {
                + "There was an error while trying to execute this command."
            }
        }
        context.send {
            this.content = content
        }
    }
}