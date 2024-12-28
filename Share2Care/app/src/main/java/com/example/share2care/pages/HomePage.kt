package com.example.share2care.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.share2care.R
import com.example.share2care.ui.components.Announce
import com.example.share2care.ui.components.AnnounceHighlight
import com.example.share2care.ui.components.HamburgerButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

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
            else -> Unit
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                    .background(Color(0xFF2F2D43))
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        HamburgerButton(
                            text = "Gestão de Beneficiários e Agregados",
                            onClick = {},
                            img = R.drawable.userinterface
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Gestão de Anúncios",
                            onClick = { navController.navigate("announceManagement") },
                            img = R.drawable.compras
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Gestão de Check-Ins",
                            onClick = {},
                            img = R.drawable.cartaovisitas
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Gestão de Tickets",
                            onClick = {},
                            img = R.drawable.ticket
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Gerir Reports",
                            onClick = {},
                            img = R.drawable.estatisticas
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Definições da Loja Social",
                            onClick = { navController.navigate("editLojaSocial") },
                            img = R.drawable.gear
                        )
                    }

                    item { Spacer(modifier = Modifier.height(28.dp)) }

                    item {
                        HamburgerButton(
                            text = "Sair",
                            onClick = { authViewModel.signout() },
                            img = R.drawable.sair
                        )
                    }
                }
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
                    items(5) {
                        Announce()
                    }
                }
            }
        }
    }
}
