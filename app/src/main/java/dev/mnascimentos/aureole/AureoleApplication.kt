package dev.mnascimentos.aureole

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.mnascimentos.aureole.di.appModule
import dev.mnascimentos.aureole.util.WarnLogTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import timber.log.Timber

class AureoleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        runCatching {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        }
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else WarnLogTree())
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                if (BuildConfig.DEBUG) {
                    androidLogger()
                }
                androidContext(this@AureoleApplication)
                modules(appModule)
            }
        }
    }
}
