
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.jogamp.common.os.Platform
import io.ktlab.bshelper.di.AppModule
import io.ktlab.bshelper.ui.BSHelperApp
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform.startKoin
import kotlin.system.exitProcess

fun main() {
    startKoin(AppModule.getModules(), Level.INFO)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "BSHelper",
            state = WindowState(size = DpSize(1440.dp, 768.dp)),
        ) {
            BSHelperApp()
        }
    }
    exitProcess(0)
}
