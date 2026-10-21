package dev.mnascimentos.aureole

import android.app.Application
import dev.mnascimentos.aureole.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import timber.log.Timber

class AureoleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
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
