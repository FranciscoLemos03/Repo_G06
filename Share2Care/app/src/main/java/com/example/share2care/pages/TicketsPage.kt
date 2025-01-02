package com.example.share2care.pages

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.TicketValidation

@Composable
fun TicketsPage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val firestoreViewModel = FirestoreViewModel()
    val ticketData by firestoreViewModel.ticketData.observeAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var ticketToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                firestoreViewModel.getTicketDetails()
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        color = MaterialTheme.colorScheme.background
    ) {

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                title = {
                    Text(text = "Confirmar Exclusão")
                },
                text = {
                    Text(text = "Tem certeza de que deseja recusar este ticket?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        ticketToDelete?.let { id ->
                                firestoreViewModel.deleteTicket(id)
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Sim")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                    }) {
                        Text("Não")
                    }
                }
            )
        }

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
                CircleButton(
                    onClick = {
                        navController.navigate("home")
                    },
                    R.drawable.back
                )

                Text(
                    text = "Gestão de tickets",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ticketData?.let { tickets ->
                    items(tickets) { ticket ->
                        TicketValidation(
                            titulo = ticket.anuncio_titulo,
                            listaBens = ticket.listaBens,
                            quantidade = ticket.quantidade,
                            condicao = ticket.condicao,
                            imageUrl = ticket.imagemUrl,
                            nome = ticket.nome,
                            email = ticket.email,
                            motivo = ticket.motivo,
                            tipoTicket = ticket.tipo,
                            recusar = {
                                ticketToDelete = ticket.id
                                showDeleteDialog = true
                            },
                            aceitar = {
                                firestoreViewModel.acceptTicket(ticket.id, ticket.nome, ticket.email, ticket.motivo, ticket.listaBens, ticket.quantidade, ticket.condicao, ticket.descricao, ticket.anuncio_id, ticket.tipo, ticket.creation_date, ticket.imagemUrl, ticket.anuncio_titulo)
                            },
                            redirecionar = {
                                navController.navigate("announceDetails/${ticket.anuncio_id}")
                            }
                        )
                    }
                }
            }

        }
    }
}