package wat.psm_lab2_bukalski

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun ShakeCounter() {
    // Pobranie kontekstu aplikacji
    val context = LocalContext.current

    // Pamiętany stan liczby wykrytych wstrząsów
    val shakeCount = remember { mutableStateOf(0) }
    // Flaga, czy pomiar trwa
    val isCounting = remember { mutableStateOf(false) }
    // Tekst z podaną przez użytkownika wartością czasu trwania pomiaru w sekundach
    val durationText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pole do wprowadzenia czasu pomiaru przez użytkownika
        TextField(
            value = durationText.value,
            onValueChange = { durationText.value = it },
            label = { Text("Czas pomiaru (sekundy)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Przycisk uruchamiający pomiar
        Button(
            onClick = {
                // Konwersja wpisanego czasu na liczbę
                val duration = durationText.value.toIntOrNull()

                // Walidacja wejścia – czas musi być dodatni
                if (duration == null || duration <= 0) {
                    Toast.makeText(context, "Podaj poprawny czas (w sekundach)", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Rozpoczęcie pomiaru — reset licznika i ustawienie flagi
                isCounting.value = true
                shakeCount.value = 0

                // Uzyskanie dostępu do akcelerometru
                val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

                // Próg wykrycia wstrząsu
                val stepThreshold = 11.0f
                var lastMagnitude = 0f

                // Utworzenie listenera dla zdarzeń akcelerometru
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (!isCounting.value) return

                        event?.let {
                            val x = it.values[0]
                            val y = it.values[1]
                            val z = it.values[2]
                            // Obliczenie wartości wektora przyspieszenia
                            val magnitude = sqrt(x * x + y * y + z * z)
                            // Obliczenie różnicy między bieżącym a poprzednim pomiarem
                            val delta = abs(magnitude - lastMagnitude)

                            // Jeżeli zmiana przekracza ustalony próg, uznajemy, że nastąpił wstrząs
                            if (delta > stepThreshold) {
                                shakeCount.value += 1
                            }

                            lastMagnitude = magnitude
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }

                // Rejestracja listenera akcelerometru
                sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)

                // Rozpoczęcie liczenia czasu za pomocą Coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    // Czekamy zadany czas trwania pomiaru
                    delay(duration * 1000L)
                    withContext(Dispatchers.Main) {
                        // Zakończenie pomiaru – wyłączenie flagi i odrejestrowanie listenera
                        isCounting.value = false
                        sensorManager.unregisterListener(listener)
                        // Wyświetlenie wyniku przy użyciu Toast
                        Toast.makeText(context, "Wykryto ${shakeCount.value} wstrząsów", Toast.LENGTH_LONG).show()
                    }
                }

            },
            enabled = !isCounting.value, // Przycisk aktywny tylko, gdy pomiar nie trwa
        ) {
            Text("Rozpocznij liczenie wstrząsów")
        }

        // Wyświetlanie bieżącej liczby wykrytych wstrząsów w trakcie pomiaru
        if (isCounting.value) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Wstrząsy: ${shakeCount.value}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
