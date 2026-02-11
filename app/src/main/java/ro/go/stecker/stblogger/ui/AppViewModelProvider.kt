package ro.go.stecker.stblogger.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ro.go.stecker.stblogger.StbLoggerApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            StbViewModel(
                databaseRepository = stbLoggerApplication().container.databaseRepository,
                cloudRepository = stbLoggerApplication().container.cloudRepository
            )
        }
    }
}

fun CreationExtras.stbLoggerApplication(): StbLoggerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as StbLoggerApplication)