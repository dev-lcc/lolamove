package hk.com.lolamove.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import hk.com.lolamove.app.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate() -> MainActivity")

        // Handle the splash screen transition
        installSplashScreen()

        setContentView(R.layout.activity_main)
    }
}