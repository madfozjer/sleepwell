package com.example.sleepwell

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.sleepwell.ui.theme.SleepWellTheme
import java.util.Calendar

data class CalendarEntry(
    val number: Int,
    val date: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SleepWellTheme {
                SleepWellApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SleepWellApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val entries = remember { mutableStateListOf<CalendarEntry>() }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen(onAddEntry = { entries.add(it) })
            AppDestinations.CALENDAR -> CalendarScreen(entries = entries)
        }
    }
}

@Composable
fun HomeScreen(
    onAddEntry: (CalendarEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var number by rememberSaveable { mutableIntStateOf(1) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDate by rememberSaveable {
        mutableStateOf("${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}")
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Rate your sleep",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { if (number > 1) number-- },
                enabled = number > 1
            ) {
                Text("-")
            }

            Text(
                text = "$number",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(
                onClick = { if (number < 10) number++ },
                enabled = number < 10
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { datePickerDialog.show() }) {
            Text("Date: $selectedDate")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onAddEntry(CalendarEntry(number, selectedDate))
                Toast.makeText(context, "Added to Calendar!", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("Save to Calendar")
        }
    }
}

@Composable
fun CalendarScreen(
    entries: List<CalendarEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No entries yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Date: ${entry.date}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Number: ${entry.number}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    CALENDAR("Calendar", R.drawable.ic_calendar),
}
