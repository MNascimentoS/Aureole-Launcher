package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonSheetTab

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceSheetContentScreenshotTest() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.height(AureoleDS.dimens.sizeExtraLarge * 4)) {
            QuickButtonAppearanceSheetContent(
                activeModel = QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = true)),
                onUpdateAppearance = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceColorsTabScreenshotTest() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.height(AureoleDS.dimens.sizeExtraLarge * 4)) {
            QuickButtonAppearanceSheetContent(
                activeModel = QuickButtonModel(
                    appearance = QuickButtonAppearance(
                        iconColor = ThemeColorRef.Role(ThemeColorRole.ACCENT),
                        showLabel = true
                    )
                ),
                onUpdateAppearance = {},
                modifier = Modifier.weight(1f),
                initialTab = QuickButtonSheetTab.COLORS
            )
        }
    }
}
