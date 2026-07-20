package ro.go.stecker.stblogger.ui.screens

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.ui.StbViewModel

@Composable
fun LinesScreen(
    innerPadding: PaddingValues,
    viewModel: StbViewModel
) {
    var lines by remember { mutableStateOf(listOf<Line>()) }

    LaunchedEffect(Unit) {
        lines = viewModel.getLines()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(256.dp),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(lines) { line ->
            LineItem(line)
        }
    }
}

@Composable
fun LineItem(
    line: Line
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Row(
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
        }
    }
}