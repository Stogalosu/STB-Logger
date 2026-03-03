package ro.go.stecker.stblogger.ui.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.UiState

@Composable
fun UpdateStopDatabaseDialog(
    onDismissRequest: () -> Unit,
    onNoInternet: () -> Unit,
    updateDatabase: (onFinish: () -> Unit) -> Unit,
    uiState: UiState
) {
    var isUpdating by remember { mutableStateOf(false) }

    if(!isUpdating)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(painterResource(R.drawable.refresh_24px), contentDescription = stringResource(R.string.update_stop_data)) },
            title = { Text(stringResource(R.string.update_stop_data)) },
            text = { Text(stringResource(R.string.update_stop_data_dialog)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if(uiState.isConnected) {
                            updateDatabase {
                                isUpdating = false
                                onDismissRequest()
                            }
                            isUpdating = true
                        } else {
                            onDismissRequest()
                            onNoInternet()
                        }
                    }
                ) {
                    Text(stringResource(R.string.update))
                }
            }
        )
    else
        AlertDialog(
            onDismissRequest = {},
            icon = { CircularProgressIndicator(modifier = Modifier.size(30.dp)) },
            title = { Text(stringResource(R.string.update_stop_data)) },
            text = {},
            confirmButton = {}
        )
}