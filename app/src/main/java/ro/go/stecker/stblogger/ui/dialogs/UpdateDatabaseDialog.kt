package ro.go.stecker.stblogger.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.stblogger.R
import ro.go.stecker.stblogger.ui.StbViewModel

@Composable
fun UpdateDatabaseDialog(
    onDismiss: () -> Unit,
    viewModel: StbViewModel
) {
    var pathUpdateComplete by remember { mutableStateOf(false) }
    var lineUpdateComplete by remember { mutableStateOf(false) }
    var stopUpdateComplete by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val pathJob = coroutineScope.launch {
            viewModel.updatePathDatabase()
        }
        pathJob.invokeOnCompletion { pathUpdateComplete = true }

        val lineJob = coroutineScope.launch {
            viewModel.updateLineDatabase()
        }
        lineJob.invokeOnCompletion { lineUpdateComplete = true }

        val stopJob = coroutineScope.launch {
            viewModel.updateStopDatabase()
        }
        stopJob.invokeOnCompletion { stopUpdateComplete = true }
    }

    LaunchedEffect(pathUpdateComplete, lineUpdateComplete, stopUpdateComplete) {
        if(pathUpdateComplete && lineUpdateComplete && stopUpdateComplete) {
            viewModel.markDatabasesAsUpdated()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.updating_data)) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(!pathUpdateComplete) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.updating_paths))
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.path_update_complete))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(!lineUpdateComplete) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.updating_lines))
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.line_update_complete))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(!stopUpdateComplete) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.updating_stops))
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.stop_update_complete))
                    }
                }
            }
        },
        confirmButton = {}
    )
}