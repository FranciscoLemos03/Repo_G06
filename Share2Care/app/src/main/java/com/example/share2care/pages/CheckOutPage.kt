@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.share2care.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton

@Composable
fun CheckOutPage(beneficiarioID: String, nomeInicial: String, navController: NavController, authViewModel: AuthViewModel, firestoreViewModel: FirestoreViewModel = viewModel()) {
    var nome by remember { mutableStateOf(nomeInicial) }
    var motivo by remember { mutableStateOf("") }
    var produtosRecolhidos by remember { mutableStateOf("") }
    var limitar by remember { mutableStateOf(false) }
    var comportamento by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val comportamentos = listOf("Bom", "Mau")
    val context = LocalContext.current

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
            verticalArrangement = Arrangement.Top
        ) {
            // Header com botão de voltar e título
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

            // Campo Nome
            TextField(
                value = nome,
                onValueChange = {},
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF34334A),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown de Comportamento
            Text(
                text = "Comportamento*",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = comportamento,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Comportamento") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF34334A),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    comportamentos.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                comportamento = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Motivo
            if (comportamento != "Bom") {
                TextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF34334A),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox para Limitar?
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = limitar,
                    onCheckedChange = { limitar = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF5A5881),
                        uncheckedColor = Color.Gray
                    )
                )
                Text(
                    text = "Limitar?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Botão "Finalizar"
            Button(
                onClick = {
                    when {
                        comportamento == "Mau" && motivo.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Por favor, preencha o campo Motivo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            firestoreViewModel.saveVisita(beneficiarioID, comportamento, motivo, produtosRecolhidos)
                            firestoreViewModel.updateBeneficiarioLimite(beneficiarioID, limitar)
                            navController.popBackStack()
                            Toast.makeText(
                                context,
                                "CheckOut Finalizado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A5881))
            ) {
                Text("Finalizar", color = Color.White)
            }

        }
    }
}
