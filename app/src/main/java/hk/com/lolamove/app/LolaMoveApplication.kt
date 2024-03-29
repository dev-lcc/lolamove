package hk.com.lolamove.app

import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hk.com.lolamove.app.di.ViewModelModules
import hk.com.lolamove.data.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import timber.log.Timber

class LolaMoveApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val buildIsDebug = BuildConfig.DEBUG

        // Init Koin Dependency here...
        startKoin {
            androidContext(applicationContext)
            loadKoinModules(ViewModelModules.build())
            loadKoinModules(
                DataModule(
                    restApiUrlEndPoint = BuildConfig.REST_API_URL_ENDPOINT,
                    isDebug = BuildConfig.DEBUG,
                ).build()
            )
        }

        // Facebook STETHO
        if (buildIsDebug) {
            Stetho.initializeWithDefaults(applicationContext)
        }

        // Firebase Crashlytics
        val crashlytics = FirebaseCrashlytics.getInstance()
        // Integrate with Timber Log
        Timber.plant(
            if (buildIsDebug) Timber.DebugTree()
            else CrashReportingTree(crashlytics)
        )

        Timber.d("onCreate() -> LolaMoveApplication")

    }

    /**
     * Timber Tree
     */
    class CrashReportingTree(
        private val crashlytics: FirebaseCrashlytics
    ) : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

            crashlytics.setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
            tag?.let { crashlytics.setCustomKey(CRASHLYTICS_KEY_TAG, it) }
            crashlytics.setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

            if (priority in listOf(Log.ASSERT, Log.DEBUG, Log.INFO, Log.VERBOSE, Log.WARN)) {
                return
            }

            val _err = t ?: Throwable(message)
            crashlytics.log("E/TAG: $message")
            crashlytics.recordException(_err)

        }

        companion object {
            const val CRASHLYTICS_KEY_PRIORITY = "priority"
            const val CRASHLYTICS_KEY_TAG = "tag"
            const val CRASHLYTICS_KEY_MESSAGE = "message"
        }

    }

    companion object {
        private val TAG = LolaMoveApplication::class.java.simpleName
    }

}