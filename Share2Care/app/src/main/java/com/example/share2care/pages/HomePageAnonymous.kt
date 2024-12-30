package com.example.share2care.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.Announce
import com.example.share2care.ui.components.AnnounceHighlight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageAnonymous(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    var procurar by remember { mutableStateOf("") }
    var selectedButton by remember { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Voluntariado", "Doação monetária", "Doação de bens", "Noticia")
    val firestoreViewModel = FirestoreViewModel()
    val allAnuncios by firestoreViewModel.allAnuncios.observeAsState(emptyList())
    val filteredAnuncios = allAnuncios.filter { anuncio ->
        (selectedButton == "Todos" || anuncio.tipo == selectedButton) &&
                (procurar.isEmpty() ||
                        anuncio.titulo.contains(procurar, ignoreCase = true) ||
                        anuncio.lojaSocialName.contains(procurar, ignoreCase = true) ||
                        anuncio.tipo.contains(procurar, ignoreCase = true))
    }

    // If user unauthenticated, redirect to login page
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Authenticated -> navController.navigate("home")
            else -> {
                firestoreViewModel.getAllAnunciosWithLojaDetails()
            }
        }
    }

    // Conteúdo principal
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão "Sair" no canto esquerdo
                Button(
                    onClick = {
                        authViewModel.signout()
                    },
                    modifier = Modifier.size(65.dp),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sair),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }



            Spacer(modifier = Modifier.height(50.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(5) {
                    AnnounceHighlight()
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            TextField(
                value = procurar,
                onValueChange = { procurar = it },
                placeholder = { Text("Procurar...", color = Color(0xFFBAB8E7)) },
                singleLine = true,
                shape = RoundedCornerShape(35.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color(0xFF34334A)
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(filters.size) { index ->
                    val filter = filters[index]
                    Button(
                        onClick = { selectedButton = filter },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedButton == filter) Color(0xFF9100C6) else Color(0xFF34334A)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            filter,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            LazyColumn {
                items(filteredAnuncios) { anuncio ->
                    Announce(
                        tipo = anuncio.tipo,
                        titulo = anuncio.titulo,
                        imageUrl = anuncio.imagemUrl,
                        creationDate = anuncio.dataCriacao,
                        lojaSocialName = anuncio.lojaSocialName,
                        imageUrlLojaSocial = anuncio.imageUrlLojaSocial
                    )
                }
            }
        }
    }

}