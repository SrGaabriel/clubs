package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandArgumentNode
import dev.gaabriel.clubs.common.struct.CommandNode

public data class CommandCall(
    val root: Command<*>,
    val node: CommandNode<*>,
    val arguments: Map<CommandArgumentNode<*, *>, Any>,
    val rawArguments: List<String>,
)