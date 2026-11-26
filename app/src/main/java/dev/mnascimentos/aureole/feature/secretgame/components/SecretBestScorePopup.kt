package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning

/** Centered popup (never a bottom sheet) with the hidden game's best score and how to play it. */
@Composable
fun SecretBestScorePopup(
    bestScore: Int,
    triggerText: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        SecretBestScoreCard(bestScore = bestScore, triggerText = triggerText, modifier = modifier)
    }
}

@Composable
fun SecretBestScoreCard(
    bestScore: Int,
    triggerText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .background(AureoleDS.colors.surfaceVariant)
            .padding(AureoleDS.dimens.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        Icon(
            painter = painterResource(R.drawable.secret_game_ship),
            contentDescription = null,
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.sizeLarge)
        )
        AureoleText(
            text = stringResource(R.string.secret_game_best_score, bestScore),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        )
        AureoleText(
            text = stringResource(
                R.string.secret_game_best_hint,
                triggerText,
                SecretGameTuning.TAPS_TO_OPEN.toString()
            ),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(top = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@AureolePreview
@Composable
private fun SecretBestScorePopupZeroPreview() {
    AureoleLauncherTheme {
        SecretBestScoreCard(bestScore = 0, triggerText = stringResource(R.string.settings_root_thank_you))
    }
}

@AureolePreview
@Composable
private fun SecretBestScorePopupRecordPreview() {
    AureoleLauncherTheme {
        SecretBestScoreCard(bestScore = 290, triggerText = stringResource(R.string.settings_root_thank_you))
    }
}
