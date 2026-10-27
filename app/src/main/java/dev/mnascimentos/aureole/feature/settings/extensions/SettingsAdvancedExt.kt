package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun tryUninstall(context: Context) {
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=${context.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
