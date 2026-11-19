package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.runtime.saveable.Saver

private const val DRAFT_SEPARATOR = ":"

/**
 * What the background picker inside the Appearance sheet shows: the [draft] being edited, or, before any tap (null),
 * what is saved.
 */
fun shownBackgroundDraft(draft: BackgroundDraft?, isCustomImageSet: Boolean, currentColor: Int): BackgroundDraft =
    draft ?: initialBackgroundDraft(isCustomImageSet, currentColor)

/** Back from the background picker and closing the sheet (null) save the background draft; other pickers do not. */
fun leavingSavesBackground(leaving: SettingsInnerKind?): Boolean =
    leaving == null || leaving == SettingsInnerKind.BACKGROUND

/**
 * The color to save when [leaving] goes away (see [leavingSavesBackground]): the [draft] when it changed (see
 * [backgroundColorToSave]), or null.
 */
fun backgroundColorOnLeaving(
    leaving: SettingsInnerKind?,
    draft: BackgroundDraft?,
    isCustomImageSet: Boolean,
    currentColor: Int
): Int? {
    if (draft == null || !leavingSavesBackground(leaving)) return null
    return backgroundColorToSave(initialBackgroundDraft(isCustomImageSet, currentColor), draft)
}

/**
 * Drag down and tap outside on the background picker bring the Appearance list back instead of closing the sheet
 * (RF-01 of polimento-ui); on the list and on the other pickers they close it.
 */
fun areaDismissReturnsToList(inner: SettingsInnerKind?): Boolean = inner == SettingsInnerKind.BACKGROUND

/** The draft as text: type and color; null (nothing tapped) is not saved. */
fun encodeBackgroundDraft(draft: BackgroundDraft?): String? =
    draft?.let { "${it.type.name}$DRAFT_SEPARATOR${it.color}" }

/** Reads back [encodeBackgroundDraft]; anything it does not know is no draft. */
fun decodeBackgroundDraft(text: String?): BackgroundDraft? {
    val parts = text?.split(DRAFT_SEPARATOR) ?: return null
    val type = BackgroundType.entries.firstOrNull { it.name == parts.firstOrNull() } ?: return null
    val color = parts.getOrNull(1)?.toIntOrNull() ?: return null
    return BackgroundDraft(type = type, color = color)
}

/** Keeps the background draft through a rotation and a trip to the photo picker. */
val BackgroundDraftSaver: Saver<BackgroundDraft?, String> = Saver(
    save = { encodeBackgroundDraft(it) },
    restore = { decodeBackgroundDraft(it) }
)

/**
 * The inputs of the background picker, read from the settings: what is saved, the actions that save it and
 * [onEditPhoto], which opens the photo editor full screen over the sheet.
 */
fun SettingsUiState.toBackgroundParams(actions: SettingsScreenActions, onEditPhoto: () -> Unit): SetBackgroundParams =
    SetBackgroundParams(
        currentColor = manualSeedColor,
        isCustomImageSet = isCustomWallpaperSet,
        onSolidColorSelected = actions.onSelectBackgroundColor,
        onCustomImageClick = actions.onChangeWallpaperClick,
        onDismissRequest = {},
        isLeftHanded = isLeftHandedMode,
        customImagePath = customWallpaperPath,
        adjustments = wallpaperAdjustments,
        onEditPhotoClick = onEditPhoto,
        lockScreen = LockScreenOption(
            checked = applyToLockScreen,
            isAvailable = canSetWallpaper,
            isNoticeSeen = isLockScreenNoticeSeen,
            onTurnOn = actions.onLockScreenTurnOn,
            onTurnOff = actions.onLockScreenTurnOff
        )
    )
