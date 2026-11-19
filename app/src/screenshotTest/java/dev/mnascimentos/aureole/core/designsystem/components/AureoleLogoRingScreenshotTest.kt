package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleLogoRingScreenshotTest() {
    AureoleLauncherTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
            AureoleLogoRing(progress = 0f)
            AureoleLogoRing(progress = 0.5f)
            AureoleLogoRing(progress = 1f, modifier = Modifier.size(72.dp))
            // Previews draw a still ring: the indeterminate arc at its resting angle.
            AureoleLogoRing(progress = null)
        }
    }
}
