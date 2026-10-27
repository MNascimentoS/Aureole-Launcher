package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class SettingsAdvancedExtTest {

    @Test
    fun `tryUninstall should launch market intent for app package`() {
        val context = RuntimeEnvironment.getApplication()
        tryUninstall(context)

        val shadowApp = shadowOf(context)
        val nextIntent = shadowApp.nextStartedActivity
        assertNotNull(nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("market://details?id=${context.packageName}", nextIntent.dataString)
    }

    @Test
    fun `tryUninstall should fallback to web url when market intent throws ActivityNotFoundException`() {
        val baseContext = RuntimeEnvironment.getApplication()
        var startedIntent: Intent? = null

        val throwingContext = object : ContextWrapper(baseContext) {
            private var firstCall = true
            override fun startActivity(intent: Intent?) {
                if (firstCall) {
                    firstCall = false
                    throw ActivityNotFoundException("Market not found")
                } else {
                    startedIntent = intent
                    super.startActivity(intent)
                }
            }
        }

        tryUninstall(throwingContext)

        assertNotNull(startedIntent)
        assertEquals(Intent.ACTION_VIEW, startedIntent?.action)
        assertEquals(
            "https://play.google.com/store/apps/details?id=${baseContext.packageName}",
            startedIntent?.dataString
        )
    }
}
