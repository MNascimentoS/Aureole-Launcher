package dev.mnascimentos.aureole.feature.settings.model

/**
 * What a color change can overwrite, kept to bring it back with "Undo".
 *
 * @property themeName Name of the selected palette ("Custom" after a manual color).
 * @property seedColor The manual seed color.
 * @property customWallpaperPath Path of the custom image when one is the background, null otherwise.
 */
data class SeedSnapshot(
    val themeName: String,
    val seedColor: Int,
    val customWallpaperPath: String?
)

fun SettingsUiState.toSeedSnapshot(): SeedSnapshot = SeedSnapshot(
    themeName = selectedThemeName,
    seedColor = manualSeedColor,
    customWallpaperPath = customWallpaperPath?.takeIf { isCustomWallpaperSet && it.isNotEmpty() }
)

/** State after putting the palette and color of [snapshot] back; the wallpaper fields follow [keepWallpaper]. */
fun SettingsUiState.withSeedSnapshot(snapshot: SeedSnapshot, keepWallpaper: Boolean): SettingsUiState {
    val path = snapshot.customWallpaperPath.takeIf { keepWallpaper }
    return copy(
        selectedThemeName = snapshot.themeName,
        manualSeedColor = snapshot.seedColor,
        isCustomWallpaperSet = path != null,
        customWallpaperPath = path
    )
}
