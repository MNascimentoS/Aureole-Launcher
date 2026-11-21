package dev.mnascimentos.aureole.util

import android.util.Log
import timber.log.Timber

/**
 * The Timber tree of release builds: a `Timber.DebugTree` that only lets warnings and errors through to logcat (with
 * their cause). Debug builds plant the plain `Timber.DebugTree` instead.
 */
class WarnLogTree : Timber.DebugTree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.WARN
}
