package dev.mnascimentos.aureole.feature.settings.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.extensions.checkDefaultLauncher

class SettingsLifecycleObserver(
    private val viewModel: SettingsViewModel,
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        viewModel.checkDefaultLauncher()
        viewModel.loadSettings()
    }
}
