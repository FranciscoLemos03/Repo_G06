package com.example.share2care.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.ui.Components.DynamicButton
import com.example.share2care.R

@Composable
fun InitialPage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value){
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logotemp),
                contentDescription = "Share2Care Logo",
                modifier = Modifier
                    .fillMaxWidth() // Make the logo as wide as the buttons
                    .height(80.dp) // Adjust height to fit the design
                    .padding(bottom = 32.dp)
            )

            // Login Button
            DynamicButton(
                text = "Login",
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth() // Full width
                    .padding(vertical = 8.dp) // Add some vertical spacing
                    .height(56.dp) // Uniform height
            )

            // Register Button
            DynamicButton(
                text = "Register Social Shop",
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier
                    .fillMaxWidth() // Full width
                    .padding(vertical = 8.dp) // Add some vertical spacing
                    .height(56.dp) // Uniform height
            )

            // Visitor Link
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Continue as Guest User",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = { authViewModel.loginAsAnonymous() })
            )
        }
    }
}
