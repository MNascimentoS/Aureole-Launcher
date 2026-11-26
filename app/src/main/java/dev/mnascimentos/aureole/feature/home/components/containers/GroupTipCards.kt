package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.tips.AureoleTipCard
import dev.mnascimentos.aureole.composable.tips.model.TipCardContent
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTipFlow

/**
 * The card of the tip [GroupTipFlow.tip] at the top of [tab]'s content (RF-F2), when that tip belongs there:
 * tip 1 opens "Add apps" ([onAddApp]), tip 2 goes to the Layout tab ([onChoosePosition]), tip 3 has no button.
 */
@Composable
internal fun GroupTipCard(
    tips: GroupTipFlow,
    tab: GroupTab,
    onAddApp: () -> Unit,
    onChoosePosition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tip = tips.tip?.takeIf { it.tab == tab } ?: return
    val content = when (tip) {
        GroupTip.FIRST_APP -> TipCardContent(
            step = tip.number,
            stepCount = GroupTip.COUNT,
            title = stringResource(R.string.home_container_tip_first_title),
            text = stringResource(R.string.home_container_tip_first_text),
            onSkip = tips::skip,
            actionLabel = stringResource(R.string.home_container_tip_first_action),
            onAction = onAddApp
        )
        GroupTip.CHOOSE_POSITION -> TipCardContent(
            step = tip.number,
            stepCount = GroupTip.COUNT,
            title = stringResource(R.string.home_container_tip_second_title),
            text = stringResource(R.string.home_container_tip_second_text),
            onSkip = tips::skip,
            actionLabel = stringResource(R.string.home_container_tip_second_action),
            onAction = onChoosePosition
        )
        GroupTip.PICK_POSITION -> TipCardContent(
            step = tip.number,
            stepCount = GroupTip.COUNT,
            title = stringResource(R.string.home_container_tip_third_title),
            text = stringResource(R.string.home_container_tip_third_text),
            onSkip = tips::skip
        )
    }
    AureoleTipCard(content = content, modifier = modifier)
}
