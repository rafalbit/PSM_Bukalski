package wat.psm_lab2_bukalski
// BUKALSKI LAB5
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

//lab6
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import wat.psm_lab2_bukalski.appModule

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        // lab 6
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("second/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    SecondScreen(message, message2, navController)
                }
                composable("second") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    SecondScreen(message, message2, navController)
                }
                composable("third/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    ThirdScreen(message, message2, navController)
                }
                // lab 5
                composable("thread/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    ThreadScreen(message, message2, navController)
                }
                composable("workmanager/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    WorkManagerScreen(message, message2, navController)
                }
            }
        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController) {
    // lab 6
    val viewModel: PersonViewModel = koinViewModel()

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
                        // Pobierz dane z pól tekstowych i przekształć na wielkie litery
                        val name = imie.value.trim().uppercase()
                        val surname = nazwisko.value.trim().uppercase()

                        // Sprawdź, czy imię nie jest puste
                        if (name.isEmpty()) {
                            Toast.makeText(context, "Wprowadź imię", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Uruchom zapytanie do bazy danych w tle
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = viewModel.getUserByFullName(name, surname) // Wyszukaj użytkownika po imieniu i nazwisku

                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    // Jeśli użytkownik istnieje – przejdź do ekranu głównego
                                    navController.navigate("second/${user.name}/${user.surname}")
                                } else {
                                    // W przeciwnym razie pokaż komunikat
                                    Toast.makeText(
                                        context,
                                        "Użytkownik nie istnieje!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 32.dp),
                ) {
                    Text("Zaloguj", fontSize = 18.sp) // Tekst na przycisku
                }
            }

            // Przycisk "Zarejestruj"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        // Pobierz i przekształć dane wejściowe do wersji wielkimi literami
                        val name = imie.value.trim().uppercase()
                        val surname = nazwisko.value.trim().uppercase()

                        // Walidacja – brak imienia
                        if (name.isEmpty()) {
                            Toast.makeText(context, "Wprowadź imię", Toast.LENGTH_SHORT).show()
                            return@Button
                        } else {
                            // Walidacja – brak nazwiska
                            if (surname.isEmpty()) {
                                Toast.makeText(context, "Wprowadź nazwisko", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        }

                        // Sprawdzenie, czy użytkownik już istnieje w bazie
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = viewModel.getUserByFullName(name, surname)

                            if (user != null) {
                                // Jeśli już istnieje – informacja
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Użytkownik już istnieje!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Jeśli nie istnieje – zapisz nowego użytkownika
                                viewModel.addPerson(name, surname)

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Zarejestrowano jako $name $surname",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Przejdź do kolejnego ekranu po rejestracji
                                    navController.navigate("second/$name/$surname")
                                }
                            }
                        }
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
fun SecondScreen(
    message: String,
    message2: String,
    navController: NavHostController
) {
    val viewModel: PersonViewModel = koinViewModel()

    // Blokowanie rotacji ekranu tylko na tym ekranie (na portret)

    val activity = LocalContext.current as? Activity
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Przechowywanie danych z sensorów
    val context = LocalContext.current
    val accelValues = remember { mutableStateOf(FloatArray(3)) }
    val temperatureValue = remember { mutableStateOf<Float?>(null) }
    val lightValue = remember { mutableStateOf(0f) }

    // Lab 4
    val showList = remember { mutableStateOf(false) } // kontrola widoczności
    val isFiltering = remember { mutableStateOf(true) }


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

//lab4
        val people = remember { mutableStateListOf<Person>() }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                viewModel.loadAll { result ->
                    people.clear()
                    people.addAll(result)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // lab 5 ----------------------------------
                ShakeCounter()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Button(onClick = { navController.navigate("thread/${message}/${message2}") }) {
                        Text("Czasomierz")
                    }

                    Button(onClick = { navController.navigate("workmanager/${message}/${message2}") }) {
                        Text("Praca")
                    }
                }

                //-----------------------------------------------------
                @Composable
                fun PersonDetailsScreen(
                    person: Person,
                    viewModel: PersonViewModel,
                    onBack: () -> Unit
                ) {
                    var newName by remember { mutableStateOf(person.name) }
                    var newSurname by remember { mutableStateOf(person.surname) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Edytuj lub usuń osobę",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Imię") })
                        TextField(
                            value = newSurname,
                            onValueChange = { newSurname = it },
                            label = { Text("Nazwisko") })

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                viewModel.updatePerson(
                                    person.copy(
                                        name = newName,
                                        surname = newSurname
                                    )
                                )
                                onBack() // Wracamy do listy
                            }) {
                                Text("Zapisz")
                            }

                            Button(onClick = {
                                viewModel.deletePerson(person)
                                onBack() // Wracamy do listy
                            }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                                Text("Usuń")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            onBack
                            isFiltering.value = false}) {
                            Text("Powrót")
                        }
                    }
                }

                @Composable
                fun PersonDropdownMenu(persons: List<Person>, viewModel: PersonViewModel) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedPerson by remember { mutableStateOf<Person?>(null) }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val searchQuery = remember { mutableStateOf("") }
                        val people = remember { mutableStateListOf<Person>() }

                        LaunchedEffect(Unit) {
                            viewModel.loadAll { result ->
                                people.clear()
                                people.addAll(result)
                            }
                        }


                        TextField(
                            value = searchQuery.value,
                            onValueChange = { searchQuery.value = it },
                            label = { Text("Wpisz imię, nazwisko lub oba") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.search(searchQuery.value.trim().uppercase()) { result ->
                                    people.clear()
                                    people.addAll(result)
                                    showList.value = true
                                    isFiltering.value = true
                                }
                            }
                        }) {
                            Text("Filtruj")
                        }

                        if (showList.value) {
                            LazyColumn {
                                items(people.take(4)) { person ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "${person.name} ${person.surname}")
                                        }
                                    }
                                }
                            }
                        }


                        Button(onClick = {
                            expanded = !expanded
                            showList.value = false  // ukrywaj listę po kliknięciu przycisku
                            isFiltering.value = true
                        }) {
                            Text(selectedPerson?.name ?: "Wybierz osobę")

                        }
                        if (selectedPerson == null) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                persons.forEach { person ->
                                    DropdownMenuItem(
                                        onClick = { selectedPerson = person },
                                        text = { Text("${person.name} ${person.surname}") },
                                        trailingIcon = {
                                            Row {
                                                IconButton(onClick = { viewModel.updatePerson(person) }) {
                                                    @androidx.compose.runtime.Composable {
                                                        Icon(
                                                            Icons.Filled.Edit,
                                                            contentDescription = "Edytuj"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            // Po wybraniu osoby, pokaż ekran edycji/usuwania
                            PersonDetailsScreen(selectedPerson!!, viewModel) {
                                selectedPerson = null
                            }
                        }
                    }
                }

                PersonDropdownMenu(persons = people, viewModel = viewModel)
            }
        }

        // Przyciski sterujące widocznością sensorów oraz nawigacją
        if (!isFiltering.value) {
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
                    onClick = {
                        val (x, y, z) = accelValues.value
                        Toast.makeText(
                            context,
                            "Prędkość (akcelerometr):\nX: %.2f, Y: %.2f, Z: %.2f".format(x, y, z),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    Modifier.width(230.dp)
                ) {
                    Text("Pokaż prędkość")
                }

                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Natężenie światła: %.2f lx".format(lightValue.value),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    Modifier.width(230.dp)
                ) {
                    Text("Pokaż światło")
                }

                Button(
                    onClick = {
                        val temp = temperatureValue.value
                        val msg = if (temp != null) {
                            "Temperatura: %.1f °C".format(temp)
                        } else {
                            "Temperatura: niedostępna"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    Modifier.width(230.dp)
                ) {
                    Text("Pokaż temperaturę")
                }
            }
        }

        // Przycisk wylogowania w prawym górnym rogu
        Box(
            Modifier.align(Alignment.TopEnd)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {
                Button(
                    onClick = {
                        isFiltering.value = isFiltering.value.not()
                    },
                    modifier = Modifier.padding(16.dp),

                    ) {
                    Text("Opcje")
                }
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
                Text(
                    title, style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        shadow = Shadow(color = Color.Black, offset = offset, blurRadius = 3f)
                    )
                )
            }
        }
    }
}

//@Composable
//fun SensorBox(value: String) {
//    // Komponent karty, która prezentuje pojedynczą wartość z sensora
//    Card(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth(0.85f),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        // Tekst z wartością sensora, wyśrodkowany wewnątrz karty
//        Text(
//            text = value,
//            modifier = Modifier.padding(8.dp),
//            style = TextStyle(fontSize = 15.sp, color = Color.Black)
//        )
//    }
//}

