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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
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

    var passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val firestoreViewModel = FirestoreViewModel()

    LaunchedEffect(authState.value){
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
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
                    .height(80.dp)
                    .padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !isEmailValid(it)
                },
                label = { Text("Email*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = if (emailError) Color.Red else  MaterialTheme.colorScheme.background, // Removes border
                    focusedBorderColor = if (emailError) Color.Red else  MaterialTheme.colorScheme.secondary, // Pink line when focused
                    cursorColor = MaterialTheme.colorScheme.secondary,             // Pink cursor
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,   // White label when unfocused
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary      // White label when focused
                )
            )
            if (emailError) {
                Text(
                    text = "Email inválido",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                )
            }

            //Password Button
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = !isPasswordValid(it)
                },
                label = { Text("Palavra-passe*") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.visibility else R.drawable.visibilityoff),
                            contentDescription = if (passwordVisible) "Esconder Palavra passe" else "Mostrar Palavra Passe"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = if (passwordError) Color.Red else MaterialTheme.colorScheme.background,
                    focusedBorderColor = if (passwordError) Color.Red else MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            if (passwordError) {
                Text(
                    text = "A password deve ter uma maiúscula, um caractere especial e no mínimo 6 caracteres.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                )
            }

            // Login Button
            Button(
                onClick = {

                    if (emailError || passwordError || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Registo Invalido",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        authViewModel.register(email, password, firestoreViewModel, navController)

                    }
              },
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

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    return password.length >= 6 && hasUpperCase && hasSpecialChar
}

