package ro.go.stecker.stblogger.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ro.go.stecker.stblogger.ui.navigation.StbNavHost

@Composable
fun StbApp(navController: NavHostController = rememberNavController()) {
    StbNavHost(navController = navController)
}