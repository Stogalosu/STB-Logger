package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Stop
import ro.go.stecker.stblogger.data.database.entities.getTerminusStops
import ro.go.stecker.stblogger.ui.StbViewModel
import ro.go.stecker.stblogger.ui.UiState
import ro.go.stecker.stblogger.ui.navigation.StbTab

@Composable
fun LinesScreen(
    onLineClick: (String) -> Unit,
    innerPadding: PaddingValues,
    uiState: UiState,
    viewModel: StbViewModel
) {
    var lines by remember { mutableStateOf(listOf<Line>()) }

    LaunchedEffect(Unit) {
        viewModel.changeTab(StbTab.Lines)
        lines = viewModel.getLines()
    }

    if(!uiState.showFilteredLines || uiState.filteredLines.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(256.dp),
            flingBehavior = ScrollableDefaults.flingBehavior(),
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (!uiState.showFilteredLines)
                items(lines) { line ->
                    LineItem(
                        line = line,
                        onLineClick = onLineClick,
                        viewModel = viewModel
                    )
                }
            else
                items(uiState.filteredLines) { line ->
                    LineItem(
                        line = line,
                        onLineClick = onLineClick,
                        viewModel = viewModel
                    )
                }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.no_lines_found),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LineItem(
    line: Line,
    onLineClick: (String) -> Unit,
    viewModel: StbViewModel
) {
    var terminusStops by remember { mutableStateOf(Pair(Stop(), Stop())) }

    LaunchedEffect(Unit) {
        terminusStops = line.getTerminusStops(viewModel)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable(onClick = { onLineClick(line.name) })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = line.type.color)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 0.dp, end = 8.dp, bottom = 0.dp, start = 0.dp)
                ) {
                    Icon(
                        painter = painterResource(line.type.icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = line.name)
                }
            }
            Spacer(modifier = Modifier.width(32.dp))
            Text(text = "${terminusStops.first.name} - ${terminusStops.second.name}")
        }
    }
}