package io.ktlab.bshelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.di.AppModule
import io.ktlab.bshelper.ui.BSHelperApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

private val logger = KotlinLogging.logger {  }
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(AppModule.getModules())
        }
        try {
            setContent{
                BSHelperApp()
            }
        }catch (e:Exception) {
            logger.error { e.stackTraceToString() }
        }
    }
}
