package wat.psm_lab2_bukalski
// BUKALSKI LAB4
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {

    private lateinit var dao: PersonDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = PersonDB.getPersonDB(applicationContext)
        dao = db.personDao()

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("second/{message}/{message2}") { backStackEntry ->
                    val message =
                        backStackEntry.arguments?.getString("message") ?: "Brak wiadomości"
                    val message2 =
                        backStackEntry.arguments?.getString("message2") ?: "Brak wiadomości"
                    SecondScreen(message, message2, navController)
                }
                composable("third/{message}/{message2}") { backStackEntry ->
                    val message =
                        backStackEntry.arguments?.getString("message") ?: "Brak wiadomości"
                    val message2 =
                        backStackEntry.arguments?.getString("message2") ?: "Brak wiadomości"
                    ThirdScreen(message, message2, navController)
                }
            }

            val people = remember { mutableStateListOf<Person>() }
            val nameFilter = remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    if (dao.getSize() == 0) {
                        val initData = resources.getStringArray(R.array.init_db_content)
                        for (item in initData) {
                            val parts = item.split(":")
                            if (parts.size == 2) {
                                dao.insert(Person(0, parts[0].uppercase(), parts[1].toInt()))
                            }
                        }
                    }
                    val all = dao.getAll()
                    people.clear()
                    people.addAll(all)
                }
            }

            MaterialTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = nameFilter.value,
                        onValueChange = { nameFilter.value = it },
                        label = { Text("Filter by name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val all = dao.getAll()
                                withContext(Dispatchers.Main) {
                                    people.clear()
                                    people.addAll(all)
                                }
                            }
                        }) {
                            Text("Display All")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val list = dao.findByName(nameFilter.value.uppercase())
                                withContext(Dispatchers.Main) {
                                    people.clear()
                                    people.addAll(list)
                                }
                            }
                        }) {
                            Text("Find by Name")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val list = dao.findByAge(20)
                                withContext(Dispatchers.Main) {
                                    people.clear()
                                    people.addAll(list)
                                }
                            }
                        }) {
                            Text("Age > 20")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(people) { person ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = person.name, fontWeight = FontWeight.Bold)
                                    Text(text = "${person.age} lat")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    // Przechowuje tekst wpisany przez użytkownika (imię i nazwisko)
    val imie = remember { mutableStateOf("") }
    val nazwisko = remember { mutableStateOf("") }

    // Kontekst potrzebny do wyświetlania Toastów
    val context = LocalContext.current

    // Główne tło ekranu (Box zajmuje całą powierzchnię)
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        // Kolumna umieszczona centralnie, zawiera cały interfejs
        Column(
            modifier = Modifier.align(alignment = Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nagłówek powitalny
            Text(
                text = "Witaj w aplikacji WiN!\nWszystko i Nic w jednym miejscu!\n\nZaloguj się poniżej:",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )

            // Pole tekstowe na imię
            TextField(
                value = imie.value,
                onValueChange = { newText -> imie.value = newText },
                label = { Text("Wpisz swoje imię") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Pole tekstowe na nazwisko
            TextField(
                value = nazwisko.value,
                onValueChange = { newText -> nazwisko.value = newText },
                label = { Text("Wpisz swoje nazwisko") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Przycisk "Zaloguj"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        // Walidacja imienia
                        if (imie.value.isNullOrEmpty()) {
                            Toast.makeText(context, "Wprowadź poprawne imie.", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }

                        // Walidacja nazwiska
                        if (nazwisko.value.isNullOrEmpty()) {
                            Toast.makeText(
                                context,
                                "Wprowadź poprawne nazwisko.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        } else {
                            // Sukces – przejście do drugiego ekranu z przekazaniem danych
                            Toast.makeText(context, "Poprawnie zalogowano.", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate("second/${imie.value}/${nazwisko.value}")
                        }
                    },
                    modifier = Modifier
                        .padding(top = 32.dp),
                ) {
                    Text("Zaloguj", fontSize = 18.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .padding(top = 32.dp),
                ) {
                    Text("Zarejestruj", fontSize = 18.sp)
                }
            }
        }
    }
}


@Composable
fun SecondScreen(message: String, message2: String, navController: NavHostController) {
    // Blokowanie rotacji ekranu tylko na tym ekranie (na portret)
    val activity = LocalContext.current as? Activity
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Flagi do kontrolowania widoczności danych z sensorów
    val showTemp = rememberSaveable { mutableStateOf(false) }
    val showLight = rememberSaveable { mutableStateOf(false) }
    val showAccel = rememberSaveable { mutableStateOf(false) }

    // Przechowywanie danych z sensorów
    val context = LocalContext.current
    val accelValues = remember { mutableStateOf(FloatArray(3)) }
    val temperatureValue = remember { mutableStateOf<Float?>(null) }
    val lightValue = remember { mutableStateOf(0f) }

    // Obsługa rejestracji i nasłuchu sensorów
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            accelValues.value = it.values.clone()
                        }
                        Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                            temperatureValue.value = it.values[0]
                        }
                        Sensor.TYPE_LIGHT -> {
                            lightValue.value = it.values[0]
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Rejestracja listenera dla wybranych sensorów
        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, tempSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)

        // Usunięcie listenera przy opuszczeniu ekranu
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Tło i główna struktura ekranu
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)) // Jasnoniebieskie tło
    ) {
        // Nagłówek z imieniem i nazwiskiem
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Witaj\n$message $message2!",
                style = TextStyle(
                    fontSize = 36.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )
        }

        // Lista tylko aktywnych tekstów z sensorów
        val visibleSensorTexts = listOfNotNull(
            if (showAccel.value)
                "Akcelerometr:\nX: %.2f, Y: %.2f, Z: %.2f".format(
                    accelValues.value[0],
                    accelValues.value[1],
                    accelValues.value[2]
                ) else null,
            if (showLight.value)
                "Natężenie światła:\n%.2f lx".format(lightValue.value) else null,
            if (showTemp.value)
                temperatureValue.value?.let {
                    "Temperatura:\n %.1f °C".format(it)
                } ?: "Temperatura: niedostępna"
            else null
        )

        // Wyświetlenie aktywnych sensorów w estetycznych boxach
        if (visibleSensorTexts.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    visibleSensorTexts.forEach {
                        SensorBox(it) // Pudełko zawierające opis jednego sensora
                    }
                }
            }
        }

        // Przyciski sterujące widocznością sensorów oraz nawigacją
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("third/${message}/${message2}")
                },
                Modifier.width(230.dp)
            ) {
                Text("Zobacz swoją galerię")
            }
            Button(
                onClick = { showAccel.value = !showAccel.value },
                Modifier.width(230.dp)
            ) {
                Text("Pokaż / ukryj prędkość")
            }
            Button(
                onClick = { showLight.value = !showLight.value },
                Modifier.width(230.dp)
            ) {
                Text("Pokaż / ukryj światło")
            }
            Button(
                onClick = { showTemp.value = !showTemp.value },
                Modifier.width(230.dp)
            ) {
                Text("Pokaż / ukryj temperaturę")
            }
        }

        // Przycisk wylogowania w prawym górnym rogu
        Box(
            Modifier.align(Alignment.TopEnd)
        ) {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        navController.navigate("home") // Powrót do ekranu logowania
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Wyloguj")
                }
            }
        }
    }
}


@Composable
fun ThirdScreen(message: String, message2: String, navController: NavHostController) {
    // Wczytanie obrazków z zasobów
    val painter = painterResource(id = R.drawable.poro)
    val painter2 = painterResource(id = R.drawable.poro2)
    val painter3 = painterResource(id = R.drawable.poro3)
    val painter4 = painterResource(id = R.drawable.poro4)

    // Wczytanie opisów i tytułów z zasobów string.xml
    val description = stringResource(R.string.poro_w_niegu_na_tle_niegu)
    val title = stringResource(R.string.poro_w_niegu_na_tle_niegu)
    val description2 = stringResource(R.string.poro_w_niegu_na_tle_niegu_ale_innego)
    val title2 = stringResource(R.string.poro_w_niegu_na_tle_niegu_ale_innego)
    val description3 = stringResource(R.string.poro_wcina_porochrupki)
    val title3 = stringResource(R.string.poro_wcina_porochrupki)
    val description4 = stringResource(R.string.boski_poro)
    val title4 = stringResource(R.string.boski_poro)

    // Główna struktura ekranu
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)) // Jasnoniebieskie tło
            .padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Kolumnowy układ zdjęć w rzędach
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Pierwszy rząd obrazków
                Row(modifier = Modifier.height(150.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp)
                    ) {
                        ImageCard(painter, description, title)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ImageCard(painter2, description2, title2)
                    }
                }

                // Drugi rząd obrazków
                Row(modifier = Modifier.height(150.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp)
                    ) {
                        ImageCard(painter3, description3, title3)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ImageCard(painter4, description4, title4)
                    }
                }

                // Trzeci rząd — powtórzenie obrazków
                Row(modifier = Modifier.height(150.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp)
                    ) {
                        ImageCard(painter, description, title)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ImageCard(painter2, description2, title2)
                    }
                }

                // Czwarty rząd — powtórzenie obrazków
                Row(modifier = Modifier.height(150.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp)
                    ) {
                        ImageCard(painter3, description3, title3)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ImageCard(painter4, description4, title4)
                    }
                }
            }
        }

        // Przycisk do powrotu do poprzedniego ekranu
        Button(
            onClick = {
                navController.navigate("second/${message}/${message2}")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Wróć do strony głównej")
        }
    }
}


@Composable
fun ImageCard(
    painter: Painter,
    contentDescription: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    ) {
        Box(modifier = Modifier.height((150.dp))) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 300f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                val offset = Offset(3.0f, 5.0f)
                Text(title, style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        shadow = Shadow(color = Color.Black, offset = offset, blurRadius = 3f))) } } }
}
@Composable
fun SensorBox(value: String) {
    // Komponent karty, która prezentuje pojedynczą wartość z sensora
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.85f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // Tekst z wartością sensora, wyśrodkowany wewnątrz karty
        Text(
            text = value,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontSize = 15.sp, color = Color.Black)
        )
    }
}

