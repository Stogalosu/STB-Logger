package ro.go.stecker.stblogger.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.Trip
import ro.go.stecker.stblogger.data.database.entities.getTripType
import ro.go.stecker.stblogger.ui.StbTopAppBar
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(
    onNavigateBack: () -> Unit,
    uiState: UiState,
    viewModel: StbViewModel
) {
    val lineTextState = rememberTextFieldState("")
    var isLineChosen by remember { mutableStateOf(false) }

    val startStopTextState = rememberTextFieldState("")
    var startStop = Pair(0, 0)
    val endStopTextState = rememberTextFieldState("")
    var endStop = Pair(0, 0)

    var lines = listOf<Line>()
    var lineOptions by remember { mutableStateOf(listOf<String>()) }
    var stopsWithDir = listOf<Pair<Stop, Int>>()
    var stopOptions by remember { mutableStateOf(listOf<String>()) }
    var stopOptionsShort by remember { mutableStateOf(listOf<String>()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lines = viewModel.getLines()
        lineOptions = lines.map { it.name }
        lineTextState.setTextAndPlaceCursorAtEnd("")
    }

    Scaffold(
        topBar = {
            StbTopAppBar(
                title = stringResource(R.string.new_trip),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                canSearch = false,
                uiState = uiState
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(32.dp)
                .fillMaxSize()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = stringResource(R.string.choose_line))
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownBox(
                        textFieldState = lineTextState,
                        label = stringResource(R.string.line),
                        options = lineOptions,
                        onOptionChange = { index, line ->
                            coroutineScope.launch {
                                stopsWithDir = viewModel.getStopsOnLine(line)
                                stopOptions =
                                    stopsWithDir.map { "${it.second} - ${it.first.name} (${it.first.id.toInt()})" }
                                startStopTextState.setTextAndPlaceCursorAtEnd("")
                                endStopTextState.setTextAndPlaceCursorAtEnd("")
                                isLineChosen = true
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(
                visible = isLineChosen
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(text = stringResource(R.string.choose_start_stop))
                        Spacer(modifier = Modifier.height(8.dp))
                        DropdownBox(
                            textFieldState = startStopTextState,
                            label = stringResource(R.string.start_stop),
                            options = stopOptions,
                            onOptionChange = { index, stop ->
                                startStop = Pair(
                                    stopsWithDir[index].first.id.toInt(),
                                    stopsWithDir[index].second
                                )
                                stopOptionsShort = stopOptions.subList(index+1, stopOptions.lastIndex)
                                endStopTextState.setTextAndPlaceCursorAtEnd("")
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(text = stringResource(R.string.choose_end_stop))
                        Spacer(modifier = Modifier.height(8.dp))
                        DropdownBox(
                            textFieldState = endStopTextState,
                            label = stringResource(R.string.end_stop),
                            options = stopOptionsShort,
                            onOptionChange = { index, stop ->
                                val goodIndex = stopOptions.indexOf(stop)
                                endStop = Pair(
                                    stopsWithDir[goodIndex].first.id.toInt(),
                                    stopsWithDir[goodIndex].second
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val line = lineTextState.text as String
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val currentDate = LocalDate.now().format(formatter)

                        val trip = Trip(
                            type = getTripType(line),
                            lineId = lines.find { it.name == line }!!.id,
                            startId = startStop.first,
                            startDir = startStop.second,
                            endId = endStop.first,
                            endDir = endStop.second,
                            date = currentDate
                        )
                        viewModel.insertTrip(trip)
                        onNavigateBack()
                    }
                },
                enabled =
                    startStopTextState.text != "" &&
                    endStopTextState.text != "" &&
                    startStopTextState.text != endStopTextState.text,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.done),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBox(
    textFieldState: TextFieldState,
    label: String,
    options: List<String>,
    onOptionChange: (Int, String) -> Unit = { index, line -> }
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = it }
    ) {
        TextField(
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onOptionChange(index, item)
                        textFieldState.setTextAndPlaceCursorAtEnd(item)
                        isDropdownExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}