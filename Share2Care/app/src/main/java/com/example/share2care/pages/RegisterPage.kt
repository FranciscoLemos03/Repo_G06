package com.example.share2care.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

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
                    .fillMaxWidth()
                    .height(80.dp) // Adjust height
                    .padding(bottom = 32.dp)
            )

            // Access Code Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.background, // Removes border
                    focusedBorderColor = MaterialTheme.colorScheme.secondary, // Pink line when focused
                    cursorColor = MaterialTheme.colorScheme.secondary,             // Pink cursor
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,   // White label when unfocused
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary      // White label when focused
                )
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password*") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.background, // Removes border
                    focusedBorderColor = MaterialTheme.colorScheme.secondary, // Pink line when focused
                    cursorColor = MaterialTheme.colorScheme.secondary,             // Pink cursor
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,   // White label when unfocused
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary      // White label when focused
                )
            )

            // Login Button
            Button(
                onClick = { authViewModel.register(email,password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp)
            ) {
                Text(text = "Register")
            }
        }
    }
}

