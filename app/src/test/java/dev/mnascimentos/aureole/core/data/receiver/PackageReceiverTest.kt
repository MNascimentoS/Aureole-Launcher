package dev.mnascimentos.aureole.core.data.receiver

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PackageReceiverTest {

    @Test
    fun `onReceive should early return when package name or action is null or self`() {
        val receiver = PackageReceiver()
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Null action
        val intent1 = Intent()
        receiver.onReceive(context, intent1)

        // Null package
        val intent2 = Intent(Intent.ACTION_PACKAGE_ADDED)
        receiver.onReceive(context, intent2)

        // Self package
        val intent3 = Intent(Intent.ACTION_PACKAGE_ADDED).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        receiver.onReceive(context, intent3)
    }
}
