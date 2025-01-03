package com.example.share2care.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.share2care.ui.components.AnnounceForAdmin
import com.example.share2care.ui.components.CircleButton

@Composable
fun AnnounceManagementPage(navController: NavController, authViewModel: AuthViewModel) {

    val firestoreViewModel = FirestoreViewModel()
    val authState = authViewModel.authState.observeAsState()
    val anuncios = firestoreViewModel.anuncioData.observeAsState(emptyList())
    val lojaSocialData by firestoreViewModel.lojaSocialData.observeAsState()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var anuncioToDelete by remember { mutableStateOf<String?>(null) }

    var nameLojaSocial by remember { mutableStateOf("") }
    var ImageUriLojaSocial by remember { mutableStateOf<String?>("https://via.placeholder.com/150") }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                if(uid != null) {
                    firestoreViewModel.getAnunciosByLojaSocialId(uid)
                    firestoreViewModel.getLojaSocialDetails(uid)
                }
            }
        }
    }

    LaunchedEffect(lojaSocialData) {
        lojaSocialData?.let {
            nameLojaSocial = it.nome
            ImageUriLojaSocial = it.imagemUrl ?: "https://via.placeholder.com/150"
            isLoading = false // booleano para validar se os dados já foram carregados
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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

                    CircleButton (
                        onClick = {
                            navController.navigate("home")
                        },
                        R.drawable.back
                    )

                    CircleButton (
                        onClick = {
                            navController.navigate("announceCreation")
                        },
                        R.drawable.add
                    )

                }

                Text(
                    text = "Os Seus Anúncios Ativos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        title = {
                            Text(text = "Confirmar Exclusão")
                        },
                        text = {
                            Text(text = "Tem certeza de que deseja apagar este anúncio?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                anuncioToDelete?.let { id ->
                                    firestoreViewModel.deleteAnuncio(id)
                                }
                                showDeleteDialog = false
                            }) {
                                Text("Sim")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false // Fecha o diálogo sem excluir
                            }) {
                                Text("Não")
                            }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {

                    items(anuncios.value) { anuncio ->
                        AnnounceForAdmin(
                            tipo = anuncio.tipo,
                            titulo = anuncio.titulo,
                            imageUrl = anuncio.imagemUrl,
                            creationDate = anuncio.data_criacao,
                            lojaSocialName = nameLojaSocial,
                            imageUrlLojaSocial = ImageUriLojaSocial,
                            onClick = {
                                navController.navigate("announceDetails/${anuncio.id}")
                            },
                            Edit = {
                                navController.navigate("editAnuncio/${anuncio.id}")
                            },
                            Delete = {
                                anuncioToDelete = anuncio.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}
