package dev.mnascimentos.aureole.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IntentUtilsTest {

    @Test
    fun `openAppInfo should start activity for package`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        IntentUtils.openAppInfo(context, "com.example.test")
    }

    @Test
    fun `uninstallApp should attempt uninstall`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        IntentUtils.uninstallApp(context, "com.example.test")
    }

    @Test
    fun `expandNotificationShade should attempt expand without crash`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        IntentUtils.expandNotificationShade(context)
    }
}
