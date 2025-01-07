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
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton

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
            selectedImageUri = uri?.toString()
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
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top navigation button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(horizontal = 16.dp, vertical = 20.dp) // Padding do topo de 20.dp
                    .align(Alignment.TopStart) // Align to the top start of the screen
            ) {
                CircleButton(
                    onClick = {
                        navController.navigate("home")
                    },
                    R.drawable.back
                )
            }

            // Center content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center), // Align to the center of the screen
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
            }

            // Bottom buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp) // Padding do fundo de 20.dp
                    .align(Alignment.BottomCenter), // Align to the bottom of the screen
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        .height(56.dp)
                ) {
                    Text(text = "Mudar Password")
                }

                Button(
                    onClick = {
                        if (uid != null) {
                            if (selectedImageUri == lojaSocialData?.imagemUrl) {
                                firestoreViewModel.updateLojaSocialDetails(uid, name, description, selectedImageUri!!)
                                Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            } else {
                                firestoreViewModel.uploadLojaSocialImageToFirebase(selectedImageUri.toString()) { imageUrl ->
                                    firestoreViewModel.updateLojaSocialDetails(uid, name, description, imageUrl)
                                    Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home")
                                }
                            }
                        } else {
                            Toast.makeText(context, "Erro ao obter ID da Loja Social", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Alterar")
                }
            }
        }
    }
}
