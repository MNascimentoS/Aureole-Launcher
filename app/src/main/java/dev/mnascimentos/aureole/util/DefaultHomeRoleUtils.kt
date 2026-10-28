package dev.mnascimentos.aureole.util

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

object DefaultHomeRoleUtils {

    /** System dialog asking to become the home app, or null when it cannot be shown (API 28, role held). */
    fun createRequestRoleIntent(context: Context): Intent? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) createHomeRoleIntent(context) else null

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createHomeRoleIntent(context: Context): Intent? {
        val roleManager = context.getSystemService(RoleManager::class.java) ?: return null
        val canRequest = roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        return if (canRequest) roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME) else null
    }
}
