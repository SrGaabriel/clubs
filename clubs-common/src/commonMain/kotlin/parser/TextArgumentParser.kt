package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.CommandContext
import dev.gaabriel.clubs.common.util.StringReader

public class TextArgumentParser<S : CommandContext>: ArgumentParser<S> {
    override fun parseArguments(context: S, args: List<String>): List<Any> {
        val reader = StringReader(context, args.toMutableList())
        for (declarationArgument in context.command.arguments) {
            val text = declarationArgument[reader] ?: continue
            if (declarationArgument.type.literal)
                reader.remove(text.toString().split(" ").size)
            reader.history.add(text)
        }
        return reader.history
    }
}