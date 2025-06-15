package wat.psm_lab2_bukalski.threadsFun

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController

@Composable
fun ThreadScreen(message: String, message2: String, navController: NavHostController) {
    var input by remember { mutableStateOf("") }        // dane wejściowe od użytkownika (czas w sekundach)
    var result by remember { mutableStateOf(0) }        // aktualna liczba sekund (wynik działania wątku)
    var isRunning by remember { mutableStateOf(false) } // flaga określająca, czy licznik działa

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Czasomierz", style = MaterialTheme.typography.headlineSmall)

        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Podaj liczbę (np. 5)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Przycisk uruchamiający odliczanie w osobnym wątku
        Button(
            onClick = {
                val value = input.toIntOrNull() ?: return@Button // walidacja wejścia (czy jest liczbą)
                isRunning = true
                result = 0

                // Urochomienie wątku, który wykonuje pętlę przez `value` sekund
                Thread {
                    for (i in 1..value) {
                        Thread.sleep(1000) // symulacja upływu 1 sekundy

                        // aktualizacja interfejsu z wątku głównego (UI) przez Handler
                        Handler(Looper.getMainLooper()).post {
                            result = i                          // aktualizujemy wynik
                            if (i == value) isRunning = false   // zatrzymanie licznika po zakończeniu
                        }
                    }
                }.start()
            },
            enabled = !isRunning,               // przycisk aktywny tylko, gdy licznik NIE działa
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start")
        }

        // Wyświetlenie aktualnego czasu (zliczanych sekund)
        Text(
            text = "Czas: ${result} sek.",
            style = MaterialTheme.typography.titleMedium
        )

        // Przycisk powrotu do poprzedniego ekranu
        Button(
            onClick = {
                navController.navigate("second/${message}/${message2}")
            },
        ) {
            Text("Powrót")
        }
    }
}
