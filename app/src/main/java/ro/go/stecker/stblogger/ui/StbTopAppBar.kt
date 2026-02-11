package ro.go.stecker.stblogger.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StbTopAppBar(
//    currentScreen: StbScreen,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit = {},
    canSearch: Boolean,
    viewModel: StbViewModel,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = { },
                trailingIcon = { },
            )
        }

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
                    SearchBar(
                        state = searchBarState,
                        inputField = inputField,
                    )
                    ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {

                    }
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

            if (canSearch) {
                SearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    modifier = Modifier.align(Alignment.Center)
                )
                ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {

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