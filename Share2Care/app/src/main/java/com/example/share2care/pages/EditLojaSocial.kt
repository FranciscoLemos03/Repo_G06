package com.example.share2care.pages

import android.util.Log
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel

import com.google.firebase.auth.FirebaseAuth
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.clip
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLojaSocial(navController: NavController, authViewModel: AuthViewModel) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>("https://via.placeholder.com/150") }

    val firestoreViewModel = FirestoreViewModel()
    val lojaSocialData by firestoreViewModel.lojaSocialData.observeAsState()
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var isLoading by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri?.toString() // Store the URI of the selected image
        }
    )

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            else -> {
                if (uid != null) {
                    email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                    firestoreViewModel.getLojaSocialDetails(uid)
                }
            }
        }
    }

    LaunchedEffect(lojaSocialData) {
        lojaSocialData?.let {
            name = it.nome
            description = it.descricao
            selectedImageUri = it.imagemUrl ?: "https://via.placeholder.com/150"
            isLoading = false // Data has been loaded
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            // Display a loading indicator while data is being fetched
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Image Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Selecionar Imagem")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    selectedImageUri?.let {
                        // Display the selected image
                        Image(
                            painter = rememberImagePainter(data = it),
                            contentDescription = "Imagem selecionada",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                // Name Field
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

                // Description Field
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

                // Email Field (Read-Only)
                OutlinedTextField(
                    value = email,
                    onValueChange = { /* No-op, read-only */ },
                    label = { Text("Email*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                // Change Password Button
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

                // Update Button
                Button(
                    onClick = {
                        if (uid != null) {
                            firestoreViewModel.uploadImageToFirebase(selectedImageUri.toString()) { imageUrl ->
                                firestoreViewModel.updateLojaSocialDetails(uid, name, description, imageUrl)
                                Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            }
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
}
