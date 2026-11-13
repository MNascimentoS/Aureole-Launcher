package dev.mnascimentos.aureole.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Runs one job at a time: starting a new one cancels the previous, so only the latest request wins. */
class LatestJobRunner(private val scope: CoroutineScope) {
    private var current: Job? = null

    fun launch(block: suspend () -> Unit): Job {
        current?.cancel()
        return scope.launch { block() }.also { current = it }
    }

    /** Drops the running job, if any, so a result that is no longer wanted never applies. */
    fun cancel() {
        current?.cancel()
        current = null
    }
}
