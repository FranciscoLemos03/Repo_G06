@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.share2care.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.share2care.AuthViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.FirestoreViewModel

@Composable
fun CheckInPage(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    firestoreViewModel: FirestoreViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var nacionalidade by remember { mutableStateOf("") }
    var beneficiarioID by remember { mutableStateOf("") }
    var visitas by remember { mutableStateOf("") }
    val beneficiarioData by firestoreViewModel.beneficiarioData.observeAsState(emptyList())
    val context = LocalContext.current
    var isValidPhoneNumber by remember { mutableStateOf(true) }
    var visitasDetalhes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var produtosRecolhidosDetalhes by remember { mutableStateOf<List<String>>(emptyList()) }
    var showLimitDialog by remember { mutableStateOf(false) }


    LaunchedEffect(beneficiarioData) {
        if (beneficiarioData.isNotEmpty()) {
            val beneficiario = beneficiarioData[0]
            nome = beneficiario.nome
            beneficiarioID = beneficiario.id
            nacionalidade = beneficiario.nacionalidade
            firestoreViewModel.getNumeroDeVisitas(beneficiarioID) { numeroDeVisitas ->
                visitas = numeroDeVisitas.toString()
            }
            firestoreViewModel.getVisitasByBeneficiario(beneficiarioID) { comportamentos, produtos ->
                visitasDetalhes = comportamentos
                produtosRecolhidosDetalhes = produtos
            }

        } else if (phoneNumber.isNotEmpty()) {
            Toast.makeText(context, "Não existe esse usuário", Toast.LENGTH_SHORT).show()
        }
    }



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
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            isValidPhoneNumber = it.matches(Regex("^[2|9]\\d{8}\$"))
                        },
                        label = { Text("Telemóvel *", color = Color.White) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (isValidPhoneNumber) Color.Magenta else Color.Red,
                            unfocusedBorderColor = if (isValidPhoneNumber) Color.Magenta else Color.Red,
                            containerColor = Color(0xFF34334A)
                        ),
                        isError = !isValidPhoneNumber
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (isValidPhoneNumber) {
                                firestoreViewModel.getBeneficiarioByTelemovel(phoneNumber)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Por favor, insira um número de telemóvel válido.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .height(48.dp)
                    ) {
                        Text("Procurar")
                    }
                }

                if (!isValidPhoneNumber) {
                    Text(
                        text = "Número inválido! Deve ter 9 dígitos e começar por 9 ou 2.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
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
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
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
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Comportamento:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Cabeçalho da tabela
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

                Spacer(modifier = Modifier.height(10.dp))

                // Adiciona altura limitada à lista
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Limita a altura máxima
                    ) {
                        items(visitasDetalhes) { visita ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color(0x000000).copy(alpha = 0.4f))
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = visita.first, // Comportamento
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Start,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = visita.second, // Motivo
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.End,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Divider(color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tabela de Produtos Recolhidos
                Text(
                    text = "Produtos Recolhidos:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Cabeçalho da tabela
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0x5A5881).copy(alpha = 0.4f))
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Produtos Recolhidos",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentSize(),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Adiciona altura limitada à lista
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Limita a altura máxima
                    ) {
                        items(produtosRecolhidosDetalhes) { produto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color(0x000000).copy(alpha = 0.4f))
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = produto, // Produto recolhido
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Start,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Divider(color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (showLimitDialog) {
                    AlertDialog(
                        onDismissRequest = { showLimitDialog = false },
                        title = { Text("Atenção") },
                        text = { Text("O beneficiário já ultrapassou o limite permitido e não pode fazer check-in.") },
                        confirmButton = {
                            TextButton(onClick = { showLimitDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
                // Botão de Check-out
                Button(
                    onClick = {
                        when {
                            phoneNumber.isBlank() -> {
                                // Exibe Toast se o número de telemóvel não estiver preenchido
                                Toast.makeText(context, "Tem de preencher o campo do Telemóvel.", Toast.LENGTH_SHORT).show()
                            }
                            beneficiarioData.isEmpty() -> {
                                // Exibe Toast se não houver beneficiário válido após a busca
                                Toast.makeText(context, "Por favor, procure um beneficiário válido antes de continuar.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val beneficiario = beneficiarioData[0]
                                if (beneficiario.limite) {
                                    // Mostra o alerta se o limite estiver true
                                    showLimitDialog = true
                                } else {
                                    // Navega para o check-out se não houver limite
                                    navController.navigate("checkout/${beneficiario.id}/${beneficiario.nome}")
                                }
                            }
                        }
                    },
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
