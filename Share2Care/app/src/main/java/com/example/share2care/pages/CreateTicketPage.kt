package com.example.share2care.pages

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketPage(
    navController: NavController,
    authViewModel: AuthViewModel,
    announceId: String
) {

    val firestoreViewModel = FirestoreViewModel()
    val anuncioData by firestoreViewModel.anuncioData.observeAsState()
    val firestore = FirebaseFirestore.getInstance()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val imageHeight = (screenHeight * 0.5f).dp
    val componentHeight = (screenHeight * 0.6f).dp
    var anuncio by remember { mutableStateOf<FirestoreViewModel.AllAnuncios?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // dados vaga voluntariado
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(true) }
    val emailRegex = Regex("^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$")
    var motivo by remember { mutableStateOf("") }

    // dados agendamento
    var listabens by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var condicao by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    val creationDate = FieldValue.serverTimestamp()

    var selectedImageUri by remember { mutableStateOf<String?>("https://via.placeholder.com/150") }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri?.toString()
        }
    )

    LaunchedEffect(announceId) {
        firestore.collection("anuncios").document(announceId).get()
            .addOnSuccessListener { document ->
                val anuncioData = document.toObject(FirestoreViewModel.AllAnuncios::class.java)
                    ?.copy(id = announceId)

                if (anuncioData != null) {
                    val lojaSocialId = document.getString("loja_social_id")

                    if (lojaSocialId != null) {
                        firestore.collection("lojaSocial").document(lojaSocialId).get()
                            .addOnSuccessListener { lojaSocialDoc ->
                                val lojaSocialName =
                                    lojaSocialDoc.getString("nome") ?: "Loja desconhecida"
                                val imageUrlLojaSocial = lojaSocialDoc.getString("imagemUrl")

                                anuncio = anuncioData.copy(
                                    lojaSocialName = lojaSocialName,
                                    imageUrlLojaSocial = imageUrlLojaSocial
                                )
                                isLoading = false
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    } else {
                        anuncio = anuncioData
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        anuncio?.let { anuncioDetails ->

            when (anuncioDetails.tipo) {
                "Voluntariado" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("announceDetails/${anuncioDetails.id}")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFB65500),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    OutlinedTextField(
                                        value = nome,
                                        onValueChange = { nome = it },
                                        label = { Text("Nome", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { newEmail ->
                                            isValidEmail = emailRegex.matches(newEmail)
                                            email = newEmail
                                        },
                                        label = { Text("Email", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = if (isValidEmail) Color.Magenta else Color.Red,
                                            unfocusedBorderColor = if (isValidEmail) Color.Magenta else Color.Red,
                                        ),
                                    )

                                    if (!isValidEmail && email.isNotEmpty()) {
                                        Text(
                                            text = "Por favor, insira um email válido.\nEx: exemplo@dominio.com",
                                            color = Color.Red,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = motivo,
                                        onValueChange = { motivo = it },
                                        label = { Text("Porque deveriamos escolher-te?", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 5,
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                }
                            }
                        }

                        Button(
                            onClick = {
                                firestoreViewModel.saveTicket(nome, email, motivo, listabens, quantidade, condicao, descricao, anuncioDetails.id, anuncioDetails.tipo, creationDate, "", anuncioDetails.titulo)
                                Toast.makeText(
                                    context,
                                    "Ticket criado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("announceDetails/${anuncioDetails.id}")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Enviar", color = Color.White)
                        }
                    }

                } "Doação de bens" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("announceDetails/${anuncioDetails.id}")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF5CA8FF),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    OutlinedTextField(
                                        value = listabens,
                                        onValueChange = { listabens = it },
                                        label = { Text("Lista de bens", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 5,
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = quantidade,
                                        onValueChange = { quantidade = it },
                                        label = { Text("Quantidade", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = condicao,
                                        onValueChange = { condicao = it },
                                        label = { Text("Condição", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = descricao,
                                        onValueChange = { descricao = it },
                                        label = { Text("Descrição do bem", color = Color.White) },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 5,
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Magenta,
                                            unfocusedBorderColor = Color.Magenta,
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

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

                                }
                            }
                        }

                        Button(
                            onClick = {
                                firestoreViewModel.uploadBensPhotoToFirebase(selectedImageUri.toString()) { imageUrl ->
                                    firestoreViewModel.saveTicket(
                                        nome,
                                        email,
                                        motivo,
                                        listabens,
                                        quantidade,
                                        condicao,
                                        descricao,
                                        anuncioDetails.id,
                                        anuncioDetails.tipo,
                                        creationDate,
                                        imageUrl,
                                        anuncioDetails.titulo
                                    )
                                    Toast.makeText(
                                        context,
                                        "Ticket criado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("announceDetails/${anuncioDetails.id}")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Enviar", color = Color.White)
                        }
                    }

                }
            }
        }
    }
}
