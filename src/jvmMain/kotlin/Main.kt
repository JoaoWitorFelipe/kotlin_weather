import adapters.WeatherAdapter
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import httpClient.WeatherHttpClient
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import localStorage.FileLocalStorage
import java.awt.RenderingHints
import java.awt.image.BufferedImage


private val json = Json { ignoreUnknownKeys = true }

fun main() = application {
    val openDialog = remember { mutableStateOf(false) }
    val localStorage = remember { FileLocalStorage() }
    val weatherHttpClient = remember { WeatherHttpClient() }
    val credentialValue = remember { mutableStateOf(TextFieldValue(localStorage.get("api_weather_key"))) }
    var trayIcon = remember { mutableStateOf(TrayIcon("")) }

    Tray(
        icon = trayIcon.value,
        onAction = {
            openDialog.value = true
        }
    )

    if (openDialog.value) {
        Dialog(onCloseRequest = {
            openDialog.value = false
        }, title = "Bem vindo ao Weather!") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Adicione sua chave da API aqui https://openweathermap.org/api"
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(credentialValue.value.text, { newText: String ->
                    credentialValue.value = credentialValue.value.copy(newText)
                })

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        localStorage.save("api_weather_key", credentialValue.value.text)

                        runBlocking {
                            val response = async {
                                weatherHttpClient.get("weather?lat=-28.4808&lon=-49.0094&appid=${localStorage.get("api_weather_key")}&&units=metric")
                            }

                            val adapter = json.decodeFromString<WeatherAdapter>(response.await())
                            println("my temp 2 3: ${adapter.main.temp}")
                            trayIcon.value = TrayIcon(adapter.main.temp.toInt().toString())

                        }
                    }
                ) {
                    Text("Salvar")
                }
            }

        }
    }

}

class TrayIcon(private val value: String) : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {

        val width = 24
        val height = 32

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = image.createGraphics()

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.color = java.awt.Color.BLACK
        g2d.fillRect(0, 0, width, height)

        g2d.color = java.awt.Color.WHITE
        val textWidth = g2d.fontMetrics.stringWidth(value)
        val textHeight = g2d.fontMetrics.height
        val textX = (width - textWidth) / 2
        val textY = (height - textHeight) / 2 + g2d.fontMetrics.ascent
        g2d.drawString(value, textX, textY)

        g2d.dispose()

        drawImage(
            image = image.toComposeImageBitmap()
        )
    }
}