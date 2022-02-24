package dev.gaabriel.clubs.common.util

import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.struct.CommandContext

public data class CommandCall<S: CommandContext>(
    val command: Command<S>,
    val arguments: List<String>
)