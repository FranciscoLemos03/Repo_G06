package com.example.share2care.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.example.share2care.ui.components.HamburgerButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val firestoreViewModel = FirestoreViewModel()
    val allAnuncios by firestoreViewModel.allAnuncios.observeAsState(emptyList())

    var procurar by remember { mutableStateOf("") }
    var selectedButton by remember { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Voluntariado", "Doação Monetária", "Doação de bens", "Notícias")

    // Estado do menu lateral
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // If user unauthenticated, redirect to login page
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                firestoreViewModel.getAllAnunciosWithLojaDetails()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight() // Garante que o menu ocupe a tela inteira
                    .fillMaxWidth(0.8f)
                    .background(Color(0xFF2F2D43)) // Cor de fundo personalizada
                    .padding(16.dp) // Padding interno, mas sem depender de WindowInsets
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                HamburgerButton("Gestão de Beneficiários e Agregados", {}, R.drawable.userinterface )

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Gestão de Anúncios", {navController.navigate("announceManagement")}, R.drawable.compras )

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Gestão de Check-Ins", {}, R.drawable.cartaovisitas )

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Gestão de Tickets", {}, R.drawable.ticket)

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Gerir Reports", {}, R.drawable.estatisticas )

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Definições da Loja Social", { navController.navigate("editLojaSocial") }, R.drawable.gear )

                Spacer(modifier = Modifier.height(40.dp))

                HamburgerButton("Sair", { authViewModel.signout() }, R.drawable.sair )
            }
        },
    ) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            // Abre o menu lateral
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        modifier = Modifier.size(65.dp),
                        shape = RoundedCornerShape(35.dp)
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.hamburger),
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
                    items(allAnuncios) { anuncio ->
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
}
