package hk.com.lolamove.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hk.com.lolamove.app.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate() -> MainActivity")
        setContentView(R.layout.activity_main)
    }
}