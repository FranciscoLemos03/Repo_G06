package com.example.share2care.PagesUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.share2care.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
<<<<<<< Updated upstream:Share2Care/app/src/main/java/com/example/share2care/PagesUI/Login.kt
fun LoginPage(onLoginClick: () -> Unit) {
    var accessCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
=======
fun LoginPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value){
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
>>>>>>> Stashed changes:Share2Care/app/src/main/java/com/example/share2care/pages/LoginPage.kt

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                value = accessCode,
                onValueChange = { accessCode = it },
                label = { Text("Código de acesso*") },
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
                label = { Text("Palavra-passe*") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.visibility else R.drawable.visibilityoff),
                            contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )


            // Login Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp)
            ) {
                Text(text = "LOGIN")
            }
        }
    }
}
