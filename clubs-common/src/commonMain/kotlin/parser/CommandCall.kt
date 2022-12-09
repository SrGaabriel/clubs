package dev.gaabriel.clubs.common.parser

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandNode
import dev.gaabriel.clubs.common.util.ArgumentMap

public data class CommandCall(
    val root: Command<*>,
    val node: CommandNode<*>,
    val arguments: ArgumentMap,
    val rawArguments: List<String>
)