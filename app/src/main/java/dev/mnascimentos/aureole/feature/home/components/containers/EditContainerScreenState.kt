package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.showsHomeSheetLogo
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetFrame
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetNav
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTipFlow
import dev.mnascimentos.aureole.feature.home.components.containers.model.firstGroupTip
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

@Composable
internal fun rememberGroupSheetNav(params: EditContainerScreenParams): GroupSheetNav = remember {
    GroupSheetNav(params.initialTab).also { start ->
        if (params.initialPage != GroupSheetPage.Tabs) start.open(params.initialPage)
    }
}

@Composable
internal fun rememberGroupTips(params: EditContainerScreenParams): GroupTipFlow {
    val latestParams by rememberUpdatedState(params)
    return remember {
        val first = params.startTip ?: firstGroupTip(params.isNew, params.areTipsSeen)
        GroupTipFlow(first) { latestParams.onTipsSeen() }
    }
}

@Composable
internal fun groupBaseConfig(
    params: EditContainerScreenParams,
    frame: GroupSheetFrame,
    onBackPress: () -> Unit,
    onDismiss: () -> Unit
): AureoleEditOverlayConfig {
    val homeActions = LocalHomeActions.current
    return AureoleEditOverlayConfig(
        title = stringResource(R.string.home_container_edit_title),
        onSettingsClick = { homeActions.onSettingsClick() },
        showHeaderAction = showsHomeSheetLogo(),
        hazeState = params.hazeState,
        isVisible = frame.isVisible,
        animated = true,
        isLeftHanded = frame.isLeftHanded,
        onBackPress = onBackPress,
        onDismiss = onDismiss,
        hasTabBottomBar = true,
        showsDismissFeedback = true,
        contentKey = frame.contentKey
    )
}

/** The pages made of a long list (the folder's app picker) need the room: the sheet opens expanded on them. */
@Composable
internal fun rememberGroupOverlayState(nav: GroupSheetNav, initialPage: GroupSheetPage): AureoleEditOverlayState {
    val state = rememberAureoleEditOverlayState(initialExpanded = initialPage == GroupSheetPage.NewFolder)
    val needsRoom = nav.page == GroupSheetPage.NewFolder
    LaunchedEffect(needsRoom) { state.isExpanded = needsRoom }
    return state
}
