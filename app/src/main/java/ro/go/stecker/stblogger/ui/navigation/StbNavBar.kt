package ro.go.stecker.stblogger.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.UiState

@Composable
fun StbNavBar(
    onScreenChange: (StbTab) -> Unit,
    uiState: UiState,
) {
    NavigationBar {
        NavigationBarItem(
            selected = uiState.tab == StbTab.Trips,
            onClick = {
                if(uiState.tab != StbTab.Trips)
                    onScreenChange(StbTab.Trips)
            },
            icon = { Icon(painter = painterResource(R.drawable.trip_24px), contentDescription = null) },
            label =  { Text(text = stringResource(R.string.trips)) }
        )
        NavigationBarItem(
            selected = uiState.tab == StbTab.Lines,
            onClick = {
                if(uiState.tab != StbTab.Lines)
                    onScreenChange(StbTab.Lines)
            },
            icon = { Icon(painter = painterResource(R.drawable.diagonal_line_24px), contentDescription = null) },
            label =  { Text(text = stringResource(R.string.lines)) }
        )
    }
}