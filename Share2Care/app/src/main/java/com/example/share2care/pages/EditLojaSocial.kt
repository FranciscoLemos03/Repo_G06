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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLojaSocial(navController: NavController, authViewModel: AuthViewModel) {

    var name by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val firestoreViewModel = FirestoreViewModel()

    val lojaSocialData by firestoreViewModel.lojaSocialData.observeAsState()

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val uid = FirebaseAuth.getInstance().currentUser?.uid


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            else -> {
                if (uid != null) {
                    email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                    firestoreViewModel.getLojaSocialDetails(uid)
                }
                Unit
            }
        }
    }

    LaunchedEffect(lojaSocialData) {
        // Preencher as variaveis com os dados do Firestore
        lojaSocialData?.let {
            name = it.nome
            description = it.descricao
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            // Campo de email - somente leitura
            OutlinedTextField(
                value = email,
                onValueChange = { /* Não faz nada, é somente leitura */ },
                label = { Text("Email*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = false,  // Impede a edição
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),  // cor do texto quando não está focado
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,  // cor do texto quando está focado
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground) // Força a cor branca do texto
            )


            // Mudar password
            Button(
                onClick = {
                    authViewModel.sendPasswordResetEmail(
                        email = email,
                        onSuccess = {
                            Toast.makeText(context, "E-mail enviado para redefinir a senha!", Toast.LENGTH_SHORT).show()
                            authViewModel.signout()
                        },
                        onFailure = { errorMessage ->
                            Toast.makeText(context, "Erro: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp)
            ) {
                Text(text = "Mudar Password")
            }

            // Login Button
            Button(
                onClick = {
                    if (uid != null) {
                        firestoreViewModel.updateLojaSocialDetails(uid, name, description)
                        Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")
                    } else {
                        Toast.makeText(context, "Erro ao obter ID da Loja Social", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp)
            ) {
                Text(text = "Alterar")
            }
        }
    }
}