@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.share2care.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share2care.AuthViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton

@Composable
fun CheckInPage(navController: NavController, authViewModel: AuthViewModel) {
    var phoneNumber by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var nacionalidade by remember { mutableStateOf("") }
    var visitas by remember { mutableStateOf("") }
    var produtosRecolhidos by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleButton(
                        onClick = { navController.popBackStack() },
                        R.drawable.back
                    )
                    Text(
                        text = "Check-In",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Número de telefone e botão Procurar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Telemóvel") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFF34334A),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                    Button(
                        onClick = { /* Implementar lógica de busca */ },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(48.dp)
                    ) {
                        Text("Procurar")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nome
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF34334A),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nacionalidade e Visitas lado a lado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = nacionalidade,
                        onValueChange = { nacionalidade = it },
                        label = { Text("Nacionalidade") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFF34334A),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        enabled = false
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = visitas,
                        onValueChange = { visitas = it },
                        label = { Text("Visitas") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFF34334A),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tabela de comportamento
                Text(
                    text = "Comportamento:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0x5A5881).copy(alpha = 0.4f))
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Comportamento",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentSize(),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Motivo",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentSize(),
                        textAlign = TextAlign.End,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Produtos Recolhidos:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0x5A5881).copy(alpha = 0.4f))
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Produtos",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentSize(),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp
                    )
                }

                // Produtos Recolhidos
                Text(
                    text = "Produtos Recolhidos:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                TextField(
                    value = produtosRecolhidos,
                    onValueChange = { produtosRecolhidos = it },
                    label = { Text("Produtos") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF34334A),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botão de Check-out
                Button(
                    onClick = { navController.navigate("checkout") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A5881))
                ) {
                    Text("Check out", color = Color.White)
                }
            }
        }
    }
}
