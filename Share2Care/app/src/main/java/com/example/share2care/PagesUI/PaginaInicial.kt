package com.example.share2care.PagesUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.share2care.DynamicButton
import com.example.share2care.R

@Composable
fun MainPage(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit) {
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
                painter = painterResource(id = R.drawable.logotemp), // Replace with your image name
                contentDescription = "Share2Care Logo",
                modifier = Modifier
                    .fillMaxWidth() // Make the logo as wide as the buttons
                    .height(80.dp) // Adjust height to fit the design
                    .padding(bottom = 32.dp)
            )

            // Login Button
            DynamicButton(
                text = "Entrar",
                onClick = { onNavigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth() // Full width
                    .padding(vertical = 8.dp) // Add some vertical spacing
                    .height(56.dp) // Uniform height
            )

            // Register Button
            DynamicButton(
                text = "Registar Loja Social",
                onClick = { onNavigateToRegister() },
                modifier = Modifier
                    .fillMaxWidth() // Full width
                    .padding(vertical = 8.dp) // Add some vertical spacing
                    .height(56.dp) // Uniform height
            )

            // Visitor Link
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Continuar como visitante",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = { onNavigateToLogin() })
            )
        }
    }
}
