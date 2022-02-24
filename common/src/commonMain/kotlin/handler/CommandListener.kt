package dev.gaabriel.clubs.common.handler

import kotlinx.coroutines.Job

public interface CommandListener<C : Any> {
    public suspend fun start(client: C): Job
}