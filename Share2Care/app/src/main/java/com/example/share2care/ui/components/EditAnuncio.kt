package com.example.share2care.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAnuncioPage(
    navController: NavController,
    authViewModel: AuthViewModel,
    announceId: String
) {

    val firestoreViewModel = FirestoreViewModel()
    val anuncioData by firestoreViewModel.anuncioData.observeAsState()
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    var tipo by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }
    var necessidades by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var lojaSocial by remember { mutableStateOf("") }
    var dataCriacao by remember { mutableStateOf<Date?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>("https://via.placeholder.com/150")}
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri?.toString()
        }
    )

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                firestoreViewModel.getAnuncioDetails(announceId)
            }
        }
    }

    LaunchedEffect(anuncioData) {
        anuncioData?.firstOrNull()?.let { anuncio ->
            tipo = anuncio.tipo
            titulo = anuncio.titulo
            motivo = anuncio.motivo
            meta = anuncio.meta
            necessidades = anuncio.necessidades
            descricao = anuncio.descricao
            link = anuncio.link
            requisitos = anuncio.requisitos
            selectedImageUri = anuncio.imagemUrl ?: "https://via.placeholder.com/150"
            lojaSocial = anuncio.lojaSocial
            dataCriacao = anuncio.data_criacao
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("announceManagement") },
                    modifier = Modifier.size(65.dp),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Text(
                    text = "Edição do Anúncio",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título *", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Magenta,
                    unfocusedBorderColor = Color.Magenta
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (tipo) {
                "Doação monetária" -> {

                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = meta,
                        onValueChange = { meta = it },
                        label = { Text("Meta", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )
                }

                "Doação de bens" -> {

                    OutlinedTextField(
                        value = necessidades,
                        onValueChange = { necessidades = it },
                        label = { Text("Necessidades", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )
                }

                "Notícia" -> {

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Link", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )
                }

                "Voluntariado" -> {

                    OutlinedTextField(
                        value = requisitos,
                        onValueChange = { requisitos = it },
                        label = { Text("Requisitos", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta
                        )
                    )

                }
            }

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

            Spacer(modifier = Modifier.height(16.dp))

            DynamicButton(
                text = "Salvar Alterações",
                onClick = {
                    if (uid != null) {
                        firestoreViewModel.uploadAnuncioPhotoToFirebase(selectedImageUri.toString()) { imageUrl ->
                            firestoreViewModel.updateAnuncioDetails(announceId, titulo, motivo, meta, necessidades, descricao, link, requisitos, uid, dataCriacao, tipo, imageUrl)
                            Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        }
                    } else {
                        Toast.makeText(context, "Erro ao obter ID da Loja Social", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}
