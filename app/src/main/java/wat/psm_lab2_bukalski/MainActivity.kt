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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight


class MainActivity : ComponentActivity() {

    private lateinit var dao: PersonDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = PersonDB.getPersonDB(applicationContext)
        dao = db.personDao()

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController, dao)
                }
                composable("second/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    SecondScreen(message, message2, dao, navController)
                }
                composable("third/{message}/{message2}") { backStackEntry ->
                    val message = backStackEntry.arguments?.getString("message") ?: "Brak"
                    val message2 = backStackEntry.arguments?.getString("message2") ?: "Brak"
                    ThirdScreen(message, message2, navController)
                }
            }
        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController, dao: PersonDao) {
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
                        val name = imie.value.trim().uppercase()
                        val surname = nazwisko.value.trim().uppercase()

                        if (name.isEmpty()) {
                            Toast.makeText(context, "Wprowadź imię", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            val user = dao.getByFullName(name, surname)
//                            val imie = dao.getByNameExact(name)
//                            val nazwisko = dao.getBySurnameExact(surname)

                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    //Toast.makeText(context, "Zalogowano jako ${imie.name}", Toast.LENGTH_SHORT).show()
                                    navController.navigate("second/${user.name}/${user.surname}")
                                } else {
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
                    Text("Zaloguj", fontSize = 18.sp)
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
                        val name = imie.value.trim().uppercase()
                        val surname = nazwisko.value.trim().uppercase()

                        if (name.isEmpty()) {
                            Toast.makeText(context, "Wprowadź imię", Toast.LENGTH_SHORT).show()
                            return@Button
                        } else {
                            if (surname.isEmpty()) {
                                Toast.makeText(context, "Wprowadź nazwisko", Toast.LENGTH_SHORT)
                                    .show()
                                return@Button
                            }
                        }


                        CoroutineScope(Dispatchers.IO).launch {
                            val user = dao.getByFullName(name, surname)
                            if (user != null) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Użytkownik już istnieje!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                dao.insert(Person(0, name, surname)) // domyślny wiek np. 18

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Zarejestrowano jako $user",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
    dao: PersonDao,
    navController: NavHostController
) {
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
//lab4
        val people = remember { mutableStateListOf<Person>() }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val all = dao.getAll()
                withContext(Dispatchers.Main) {
                    people.clear()
                    people.addAll(all)
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
                var visibleItems by remember { mutableStateOf(3) } // Początkowo widoczne tylko 3 osoby
                var isExpanded by remember { mutableStateOf(false) }

                @Composable
                fun PersonDetailsScreen(person: Person, viewModel: PersonViewModel, onBack: () -> Unit) {
                    var newName by remember { mutableStateOf(person.name) }
                    var newSurname by remember { mutableStateOf(person.surname) }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Edytuj lub usuń osobę", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        TextField(value = newName, onValueChange = { newName = it }, label = { Text("Imię") })
                        TextField(value = newSurname, onValueChange = { newSurname = it }, label = { Text("Nazwisko") })

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                viewModel.updatePerson(person.copy(name = newName, surname = newSurname))
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

                        Button(onClick = onBack) {
                            Text("Powrót")
                        }
                    }
                }

                @Composable
                fun PersonDropdownMenu(persons: List<Person>, viewModel: PersonViewModel) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedPerson by remember { mutableStateOf<Person?>(null) }
                    var editPerson by remember { mutableStateOf<Person?>(null) }
                    var searchQuery by remember { mutableStateOf("") }
                    val filteredPersons = persons.filter { person ->
                        person.name.contains(searchQuery, ignoreCase = true) ||
                                person.surname.contains(searchQuery, ignoreCase = true)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Szukaj osoby") },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )

                        Button(onClick = { expanded = !expanded }) {
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
//                                                IconButton(onClick = { viewModel.deletePerson(person) }) {
//                                                    Icon(
//                                                        Icons.Default.Delete,
//                                                        contentDescription = "Usuń"
//                                                    )
//                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        } else {
                            // Po wybraniu osoby, pokaż ekran edycji/usuwania
                            PersonDetailsScreen(selectedPerson!!, viewModel) { selectedPerson = null }
                        }
                    }
                }


                @Composable
                fun showEditDialog(person: Person, viewModel: PersonViewModel, onDismiss: () -> Unit) {
                    var newName by remember { mutableStateOf(person.name) }
                    var newSurname by remember { mutableStateOf(person.surname) }

                    AlertDialog(
                        onDismissRequest = onDismiss,
                        title = { Text("Edytuj osobę") },
                        text = {
                            Column {
                                TextField(value = newName, onValueChange = { newName = it }, label = { Text("Imię") })
                                TextField(value = newSurname, onValueChange = { newSurname = it }, label = { Text("Nazwisko") })
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.updatePerson(person.copy(name = newName, surname = newSurname))
                                onDismiss()
                            }) {
                                Text("Zapisz")
                            }
                        },
                        dismissButton = {
                            Button(onClick = onDismiss) {
                                Text("Anuluj")
                            }
                        }
                    )
                }


                PersonDropdownMenu(persons = people, viewModel = PersonViewModel(dao))
            }

        }


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

