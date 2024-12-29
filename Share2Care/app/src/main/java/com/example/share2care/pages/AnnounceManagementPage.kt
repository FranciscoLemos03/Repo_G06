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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.Announce
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Date

@Composable
fun AnnounceManagementPage(navController: NavController, authViewModel: AuthViewModel) {

    val firestoreViewModel = FirestoreViewModel()
    val authState = authViewModel.authState.observeAsState()
    val anuncios = firestoreViewModel.anuncioData.observeAsState(emptyList())
    val lojaSocialData by firestoreViewModel.lojaSocialData.observeAsState()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var isLoading by remember { mutableStateOf(true) }

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
            isLoading = false // Data has been loaded
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
                    Button(
                        onClick = { navController.navigate("home") },
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

                    Button(
                        onClick = { navController.navigate("announceCreation") },
                        modifier = Modifier.size(65.dp),
                        shape = RoundedCornerShape(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Text(
                    text = "Os Seus Anúncios Ativos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(anuncios.value) { anuncio ->
                        Announce(
                            tipo = anuncio.tipo,
                            titulo = anuncio.titulo,
                            imageUrl = anuncio.imagemUrl,
                            creationDate = anuncio.data_criacao,
                            lojaSocialName = nameLojaSocial,
                            imageUrlLojaSocial = ImageUriLojaSocial
                            )
                    }
                }
            }
        }
    }
}
