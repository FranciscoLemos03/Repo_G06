package com.example.share2care.pages

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.DynamicButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnouncePage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val creationDate = FieldValue.serverTimestamp()

    val firestoreViewModel = FirestoreViewModel()
    val context = LocalContext.current

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableIntStateOf(0)
    }

    var titulo by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    var meta by remember { mutableStateOf("") }
    var necessidades by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var isValidLink by remember { mutableStateOf(true) }
    val urlRegex = Regex("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-]*)*(\\?[^#]*)?(#.*)?$")
    var requisitos by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<String?>("https://via.placeholder.com/150") }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri?.toString()
        }
    )

    val tipos = listOf("Doação monetária", "Doação de bens", "Notícia", "Voluntariado")

    var dropDownWidth by remember { mutableIntStateOf(0) }




    LaunchedEffect(key1 = authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> Unit
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
                .padding(16.dp),
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
                    text = "Criação do Anúncio",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .onGloballyPositioned { coordinates ->
                        dropDownWidth = coordinates.size.width
                    }
                    .clickable { isDropDownExpanded.value = true }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tipos[itemPosition.intValue],
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Image(
                        painter = painterResource(id = R.drawable.down),
                        contentDescription = "DropDown Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = { isDropDownExpanded.value = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { dropDownWidth.toDp() })
                ) {
                    tipos.forEachIndexed { index, tipo ->
                        DropdownMenuItem(
                            text = { Text(text = tipo) },
                            onClick = {
                                isDropDownExpanded.value = false
                                itemPosition.intValue = index
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título *", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Magenta,
                    unfocusedBorderColor = Color.Magenta,
                )
            )

            when (itemPosition.intValue) {
                0 -> {

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5, // Permite várias linhas
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta,
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = meta,
                        onValueChange = { newText ->
                            if (newText.matches(Regex("^[0-9]*\\.?[0-9]*$"))) {
                                meta = newText
                            }
                        },
                        label = { Text("Meta *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, // Força entrada numérica
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta,
                        )
                    )
                }

                1 -> {

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = necessidades,
                        onValueChange = { necessidades = it },
                        label = { Text("Necessidades *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta,
                        )
                    )
                }

                2 -> {

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta,
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = link,
                        onValueChange = { newLink ->
                            // Valida se o link é válido
                            isValidLink = urlRegex.matches(newLink)
                            link = newLink
                        },
                        label = { Text("Link", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (isValidLink) Color.Magenta else Color.Red,
                            unfocusedBorderColor = if (isValidLink) Color.Magenta else Color.Red,
                        ),
                    )

                    if (!isValidLink && link.isNotEmpty()) {
                        Text(
                            text = "Por favor, insira um link válido.\nEx: google.com ou https://google.com",
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                3 -> {

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = requisitos,
                        onValueChange = { requisitos = it },
                        label = { Text("Requisitos *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Magenta,
                            unfocusedBorderColor = Color.Magenta,
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
                text = "Enviar",
                onClick = {
                    if (uid != null) {
                        firestoreViewModel.uploadAnuncioPhotoToFirebase(selectedImageUri.toString()) { imageUrl ->
                            firestoreViewModel.saveAnnounce(
                                titulo,
                                motivo,
                                meta,
                                necessidades,
                                descricao,
                                link,
                                requisitos,
                                uid,
                                creationDate,
                                itemPosition.intValue,
                                imageUrl
                            )
                            Toast.makeText(
                                context,
                                "Anuncio criado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("announceManagement")
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