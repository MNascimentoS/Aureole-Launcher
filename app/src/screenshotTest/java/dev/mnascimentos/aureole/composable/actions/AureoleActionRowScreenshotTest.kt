package dev.mnascimentos.aureole.composable.actions

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
private fun ActionRowCase(isLeftHanded: Boolean, isDestructive: Boolean = false) {
    AureoleLauncherTheme {
        AureoleActionRow(
            params = ActionRowParams("Save", {}, "Restore", {}, isSecondaryDestructive = isDestructive),
            isLeftHanded = isLeftHanded,
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ActionRowRightHandedScreenshotTest() = ActionRowCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ActionRowLeftHandedScreenshotTest() = ActionRowCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun ActionRowDestructiveScreenshotTest() = ActionRowCase(isLeftHanded = false, isDestructive = true)
