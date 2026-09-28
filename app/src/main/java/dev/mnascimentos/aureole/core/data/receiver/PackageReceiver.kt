package dev.mnascimentos.aureole.core.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        val action = intent.action
        if (packageName.isNullOrEmpty() || action.isNullOrEmpty() || packageName == context.packageName) {
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appRepository = AppRepository.getInstance(context)
                when (action) {
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        if (!isReplacing) {
                            appRepository.removePackage(packageName)
                        }
                    }

                    Intent.ACTION_PACKAGE_ADDED,
                    Intent.ACTION_PACKAGE_CHANGED,
                    Intent.ACTION_PACKAGE_REPLACED -> {
                        appRepository.syncPackage(packageName)
                    }

                    else -> {}
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
