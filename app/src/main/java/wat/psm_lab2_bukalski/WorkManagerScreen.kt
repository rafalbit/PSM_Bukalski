package wat.psm_lab2_bukalski

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

@Composable
fun WorkManagerScreen(message: String, message2: String, navController: NavHostController) {
    val context = LocalContext.current

    // Zapamiętujemy workRequest przy pierwszej kompozycji (żeby nie twórczyć go przy każdej rekonstrukcji)
    val workRequest = remember {
        OneTimeWorkRequestBuilder<MyWorker>().build()
    }

    // Pobieramy instancję WorkManagera
    val workManager = WorkManager.getInstance(context)

    // Obserwujemy WorkInfo jako LiveData i konwertujemy do stanu Compose
    val workInfo by workManager.getWorkInfoByIdLiveData(workRequest.id).observeAsState()

    // Pobieramy postęp z WorkInfo (domyślnie 0, jeśli jeszcze nie ustalony)
    val progress = workInfo?.progress?.getInt("progress", 0) ?: 0

    // UI: Wyświetlamy postęp oraz przycisk, który uruchamia zadanie
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Postęp pracy: $progress%", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Enqueue'ujemy zadanie – uruchamia się worker
                workManager.enqueue(workRequest)
            }
        ) {
            Text("Start Work")
        }
        // Przycisk powrotu do poprzedniego ekranu (np. ekranu głównego z sensorami)
        Button(
            onClick = {
                navController.navigate("second/${message}/${message2}")
            },
        ) {
            Text("Powrót")
        }
    }
}
