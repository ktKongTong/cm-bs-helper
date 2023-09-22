package io.ktlab.bshelper

import android.os.Bundle
import moe.tlaster.precompose.lifecycle.setContent
import io.ktlab.bshelper.di.ServiceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import moe.tlaster.precompose.lifecycle.PreComposeActivity

class MainActivity : PreComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startKoin {
            modules(ServiceModule.getModules(this@MainActivity))
            androidLogger()
            androidContext(this@MainActivity)
        }
        super.onCreate(savedInstanceState)
        setContent {
            BSHelperApp(name = "Android-BSHelper")
        }
    }
}
