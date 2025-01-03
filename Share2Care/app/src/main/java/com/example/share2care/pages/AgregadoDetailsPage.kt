package com.example.share2care.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.DynamicButton
import com.example.share2care.ui.components.TableComponent
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

@Composable
fun AgregadoDetailsPage(AgregadoID: String, navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val firestoreViewModel = FirestoreViewModel()
    val agregados = firestoreViewModel.agregadoData.observeAsState(emptyList())
    val beneficiarios = firestoreViewModel.beneficiarioData.observeAsState(emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var agregado_num by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var ticketToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                if (uid != null) {
                    firestoreViewModel.getSpecificAgregadoDetails(AgregadoID)
                    firestoreViewModel.getBeneficiariosByAgregado(AgregadoID)
                }
            }
        }
    }

    LaunchedEffect(agregados.value) {
        if (agregados.value.isNotEmpty()) {
            agregado_num = agregados.value.first().num_doc
        } else {
            Log.e("AgregadoDetailsPage", "Não foi possível encontrar esse agregado")
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
                    Text(text = "Tem certeza de que deseja apagar o agregado?\nNota: Todos os beneficiarios deste agregado serão apagados também")
                },
                confirmButton = {
                    TextButton(onClick = {
                        ticketToDelete?.let { id ->
                            firestoreViewModel.deleteAgregado(id)
                        }
                        showDeleteDialog = false
                        Toast.makeText(
                            context,
                            "Agregado e Beneficiarios apagados com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("agregadomanagement")
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
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
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
                            navController.navigate("agregadomanagement")
                        },
                        R.drawable.back
                    )

                    CircleButton(
                        onClick = {
                            navController.navigate("createbeneficiario/${AgregadoID}")
                        },
                        R.drawable.add
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Agregado: ${agregado_num}",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(Color(0xFF9100C6))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Beneficiários:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0x5A5881).copy(alpha = 0.4f))
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Nome",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )

                Text(
                    text = "Telemóvel",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )

                Text(
                    text = "Nacionalidade",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(beneficiarios.value) { beneficiario ->
                    TableComponent(
                        "",
                        Date(),
                        onClick = {
                            navController.navigate("beneficiario/${beneficiario.id}")
                        },
                        "beneficiario",
                        beneficiario.nome,
                        beneficiario.telemovel,
                        beneficiario.nacionalidade
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DynamicButton(
                    text = "Atualizar",
                    onClick = {
                        navController.navigate("updateagregado/${AgregadoID}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                DynamicButton(
                    text = "Apagar",
                    onClick = {
                        ticketToDelete = AgregadoID
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )
            }
        }
    }
}
