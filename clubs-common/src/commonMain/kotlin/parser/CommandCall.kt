package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgument
import dev.gaabriel.clubs.common.struct.CommandNode

public data class CommandCall(
    val root: Command<*>,
    val node: CommandNode<*>,
    val arguments: Map<CommandArgument<*, *>, Any>,
    val rawArguments: List<String>
)