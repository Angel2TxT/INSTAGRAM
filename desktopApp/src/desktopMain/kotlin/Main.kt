import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.instaconnect.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GramPulse - Instagram KMP App",
        state = rememberWindowState(width = 440.dp, height = 860.dp)
    ) {
        App()
    }
}
