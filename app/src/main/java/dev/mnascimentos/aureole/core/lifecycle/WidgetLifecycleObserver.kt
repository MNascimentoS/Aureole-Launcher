package dev.mnascimentos.aureole.core.lifecycle

import android.appwidget.AppWidgetHost
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class WidgetLifecycleObserver(
    private val appWidgetHost: AppWidgetHost,
    private val onWasInBackgroundChanged: (Boolean) -> Unit,
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        appWidgetHost.startListening()
    }

    override fun onStop(owner: LifecycleOwner) {
        onWasInBackgroundChanged(true)
        appWidgetHost.stopListening()
    }
}
