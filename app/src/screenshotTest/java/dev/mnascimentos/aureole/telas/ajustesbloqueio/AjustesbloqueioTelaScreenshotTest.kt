package dev.mnascimentos.aureole.telas.ajustesbloqueio

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.LockScreenOption
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.fixtures.SettingsSheetCase

/** Sheet de fundo com "Usar também na tela de bloqueio" ligado. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() {
    val option = LockScreenOption(checked = true)
    SettingsSheetCase(
        sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND),
        uiState = SettingsUiState(
            applyToLockScreen = option.checked,
            canSetWallpaper = option.isAvailable,
            isLockScreenNoticeSeen = option.isNoticeSeen
        )
    )
}
