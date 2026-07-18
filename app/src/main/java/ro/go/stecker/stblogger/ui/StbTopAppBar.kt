package ro.go.stecker.stblogger.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.dialogs.StbDatePickerDialog
import ro.go.stecker.stblogger.ui.screens.discardRed
import kotlin.time.Duration.Companion.milliseconds

private enum class SelectedChip {
    StartStop,
    EndStop,
    Line
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StbTopAppBar(
//    currentScreen: StbScreen,
    title: String = "",
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit = {},
    canSearch: Boolean,
    onSearchFail: (String) -> Unit = {},
    uiState: UiState,
    viewModel: StbViewModel,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current

    if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        ElevatedCard(
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 42.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                BackOrMenuButton(
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = onNavigateBack
                )

                if (canSearch) {
                    Box(
                        modifier = Modifier
                            .animateContentSize()
                            .weight(1f)
                    ) {
                        StbSearchBar(
                            onSearchFail = onSearchFail,
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                AnimatedVisibility(!uiState.isConnected) {
                    Icon(
                        painterResource(R.drawable.cloud_off_24px),
                        tint = discardRed,
                        contentDescription = stringResource(R.string.no_internet),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 42.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp)
            ) {
                BackOrMenuButton(
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = onNavigateBack
                )
            }

            if (canSearch)
                StbSearchBar(
                    onSearchFail = onSearchFail,
                    uiState = uiState,
                    viewModel = viewModel,
                    modifier = Modifier.align(Alignment.Center)
                )

            if(!uiState.isConnected) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cloud_off_24px),
                        tint = discardRed,
                        contentDescription = stringResource(R.string.no_internet),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnyChip() {
    InputChip(
        selected = false,
        onClick = {},
        label = { Text(text = stringResource(R.string.any)) }
    )
}

@Composable
fun InputChipRow(
    list: SnapshotStateList<String>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(list.isEmpty())
            item { AnyChip() }
        items(list) { stop ->
            Box(modifier = Modifier.animateItem()) {
                InputChip(
                    selected = false,
                    onClick = {},
                    label = { Text(text = stop) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_small_24px),
                            contentDescription = null,
                            modifier = Modifier.clickable { list.remove(stop) }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StbSearchBar(
    onSearchFail: (String) -> Unit,
    uiState: UiState,
    viewModel: StbViewModel,
    modifier: Modifier = Modifier
) {
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val coroutineScope = rememberCoroutineScope()

    var searchTrips by remember { mutableStateOf(true) }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var selectedChip by remember { mutableStateOf(SelectedChip.StartStop) }

    val startStops = rememberSaveable { mutableStateListOf<String>() }
    val endStops = rememberSaveable { mutableStateListOf<String>() }
    val lines = rememberSaveable { mutableStateListOf<String>() }
    val dates = rememberSaveable { mutableStateListOf<String>() }

    suspend fun onSearchSuggestions(query: String) {
        when(selectedChip) {
            SelectedChip.Line ->
                suggestions = viewModel.searchLines(query)
            else ->
                suggestions = viewModel.searchStops(query)
        }
    }

    fun onSuggestionPick(suggestion: String) {
        textFieldState.setTextAndPlaceCursorAtEnd("")
        searchTrips = true
        when(selectedChip) {
            SelectedChip.StartStop ->
                if(!startStops.contains(suggestion))
                    startStops.add(suggestion)
            SelectedChip.EndStop ->
                if(!endStops.contains(suggestion))
                    endStops.add(suggestion)
            SelectedChip.Line ->
                if(!lines.contains(suggestion))
                    lines.add(suggestion)
        }

    }

    LaunchedEffect(textFieldState.text) {
        if(textFieldState.text.isNotEmpty()) {
            delay(300.milliseconds)
            searchTrips = false
            onSearchSuggestions(textFieldState.text.toString())
        }
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    coroutineScope.launch {
                        if (searchTrips) {
                            viewModel.showFilteredTrips(
                                true,
                                viewModel.searchTrips(
                                    startStopNames = startStops,
                                    endStopNames = endStops,
                                    lineNames = lines,
                                    dates = dates
                                )
                            )
                            searchBarState.animateToCollapsed()
                        } else {
                            if(suggestions.isEmpty())
                                onSearchFail(textFieldState.text.toString())
                            else {
                                onSuggestionPick(suggestions[0])
                                searchTrips = true
                            }
                        }
                    }
                },
                placeholder = {
                    val text =
                        if(uiState.showFilteredTrips) stringResource(R.string.filter_is_active)
                        else stringResource(R.string.search)
                    Text(text = text, modifier = Modifier.clearAndSetSemantics {})
                },
                leadingIcon = {
                    AnimatedVisibility(
                        visible = searchBarState.currentValue == SearchBarValue.Expanded && !searchBarState.isAnimating,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = { coroutineScope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                painterResource(R.drawable.rounded_arrow_back_24),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.showFilteredTrips(false)
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.close_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    if(showDatePickerDialog)
        StbDatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            onDateSelected = { dates.add(it) }
        )

    SearchBar(
        state = searchBarState,
        inputField = inputField,
        modifier = modifier
    )
    ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
        Column {
            Column (
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row {
                    FilterChip(
                        selected = selectedChip == SelectedChip.StartStop,
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            searchTrips = false
                            selectedChip = SelectedChip.StartStop
                        },
                        label = {
                            Text(text = stringResource(R.string.start_stop))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.home_pin_24px),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InputChipRow(list = startStops)
                }
                Row {
                    FilterChip(
                        selected = selectedChip == SelectedChip.EndStop,
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            searchTrips = false
                            selectedChip = SelectedChip.EndStop
                        },
                        label = {
                            Text(text = stringResource(R.string.end_stop))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.pin_drop_24px),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InputChipRow(list = endStops)
                }
                Row {
                    FilterChip(
                        selected = selectedChip == SelectedChip.Line,
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            searchTrips = false
                            selectedChip = SelectedChip.Line
                        },
                        label = {
                            Text(text = stringResource(R.string.line))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.diagonal_line_24px),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InputChipRow(list = lines)
                }
                Row {
                    AssistChip(
                        onClick = { showDatePickerDialog = true },
                        label = { Text(text = stringResource(R.string.date)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.calendar_month_24px),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.add_24px),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    InputChipRow(list = dates)
                }
            }
            HorizontalDivider()
            LazyColumn {
                items(suggestions) { suggestion ->
                    ListItem(
                        headlineContent = { Text(suggestion) },
                        colors = ListItemDefaults.colors(SearchBarDefaults.colors().containerColor),
                        modifier = Modifier
                            .clickable { onSuggestionPick(suggestion) }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BackOrMenuButton(
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    if (canNavigateBack) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                painterResource(R.drawable.rounded_arrow_back_24),
                contentDescription = stringResource(R.string.back)
            )
        }
    } else {
        IconButton(onClick = { }) {
            Icon(
                painterResource(R.drawable.round_menu_24),
                contentDescription = stringResource(R.string.menu)
            )
        }
    }
}