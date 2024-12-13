package com.example.share2care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.share2care.PagesUI.LoginPage
import com.example.share2care.PagesUI.MainPage
import com.example.share2care.ui.theme.AppTheme
import com.example.share2care.ui.theme.Share2CareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                    AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "PaginaInicial"
    ) {
        addMainPage(navController)
        addLoginPage(navController) // Add LoginPage route here
    }
}

fun NavGraphBuilder.addMainPage(navController: NavController) {
    composable("PaginaInicial") {
        MainPage(
            onNavigateToLogin = { navController.navigate("Login") },
            onNavigateToRegister = { navController.navigate("Registo") }
        )
    }
}

fun NavGraphBuilder.addLoginPage(navController: NavController) {
    composable("Login") {
        LoginPage(
            onLoginClick = { /* Handle login logic here */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        AppNavigation()
    }
}
