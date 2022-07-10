package dev.gaabriel.clubs.common.abstraction

import kotlinx.coroutines.Job

public fun interface CommandListener<C : Any> {
    public fun startListening(client: C): Job
}