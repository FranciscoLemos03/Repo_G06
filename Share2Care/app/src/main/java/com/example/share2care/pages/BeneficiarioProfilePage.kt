package com.example.share2care.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.BeneficiarioDataField
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.DynamicButton
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BeneficiarioProfilePage(beneficiarioID: String, navController: NavController, authViewModel: AuthViewModel){

    val authState = authViewModel.authState.observeAsState()
    val firestoreViewModel = FirestoreViewModel()
    val beneficiarios = firestoreViewModel.beneficiarioData.observeAsState(emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var telemovel by remember { mutableStateOf("") }
    var nacionalidade by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                if (uid != null) {
                    firestoreViewModel.getSpecificBeneficiarioDetails(beneficiarioID)
                }
            }
        }
    }

    LaunchedEffect(beneficiarios.value) {
        if (beneficiarios.value.isNotEmpty()) {
            nome = beneficiarios.value.first().nome
            telemovel = beneficiarios.value.first().telemovel
            nacionalidade = beneficiarios.value.first().nacionalidade
        } else {
            Log.e("AgregadoDetailsPage", "Não foi possível encontrar esse agregado")
        }
    }

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
                    firestoreViewModel.deleteBeneficiario(beneficiarioID)
                    showDeleteDialog = false
                    Toast.makeText(
                        context,
                        "Beneficiario apagado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate("beneficiariomanagement")
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
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleButton(
                onClick = {
                    navController.navigate("initialbeneficiario")
                },
                R.drawable.back
            )

            Text(
                text = "Perfil Beneficiario",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        BeneficiarioDataField("Telemovel:", telemovel)

        Spacer(modifier = Modifier.height(20.dp))

        BeneficiarioDataField("Nome:", nome)

        Spacer(modifier = Modifier.height(20.dp))

        BeneficiarioDataField("Nacionalidade:", nacionalidade)

        Spacer(modifier = Modifier.height(60.dp))

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
                    navController.navigate("editbeneficiario/${beneficiarioID}")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.width(40.dp))

            DynamicButton(
                text = "Apagar",
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
        }

    }
}