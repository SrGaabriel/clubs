package io.github.srgaabriel.clubs.common.parser

import io.github.srgaabriel.clubs.common.struct.Command
import io.github.srgaabriel.clubs.common.struct.CommandNode
import io.github.srgaabriel.clubs.common.util.ArgumentMap

public data class CommandCall(
    val root: Command<*>,
    val node: CommandNode<*>,
    val arguments: ArgumentMap,
    val rawArguments: List<String>
)