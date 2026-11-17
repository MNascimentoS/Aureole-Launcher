package dev.mnascimentos.aureole.feature.settings.model

import com.google.android.play.core.install.model.InstallStatus

/**
 * What the logo at the top of the Settings root shows about the in-app update (RF-13): nothing, a spinning ring
 * while the check runs (or the download has not started yet), or the real download progress.
 */
sealed interface SettingsUpdateRing {
    data object Hidden : SettingsUpdateRing
    data object Checking : SettingsUpdateRing
    data class Downloading(val fraction: Float) : SettingsUpdateRing
}

/** The ring progress: the download fraction, or null for the spinning ring (and while hidden). */
val SettingsUpdateRing.ringProgress: Float?
    get() = (this as? SettingsUpdateRing.Downloading)?.fraction

/**
 * The ring for an install state of the Play in-app update: the real fraction while it downloads with a known size,
 * spinning while it waits to start or the size is unknown, hidden otherwise (nothing to install, downloaded,
 * installed, failed or canceled).
 */
fun settingsUpdateRingFor(installStatus: Int, bytesDownloaded: Long, totalBytes: Long): SettingsUpdateRing =
    when (installStatus) {
        InstallStatus.DOWNLOADING -> if (totalBytes > 0L) {
            SettingsUpdateRing.Downloading((bytesDownloaded.toFloat() / totalBytes).coerceIn(0f, 1f))
        } else {
            SettingsUpdateRing.Checking
        }
        InstallStatus.PENDING -> SettingsUpdateRing.Checking
        else -> SettingsUpdateRing.Hidden
    }
