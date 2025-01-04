package com.example.share2care.pages

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.share2care.ui.components.TableComponent
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiariosManagementPage(navController: NavController, authViewModel: AuthViewModel){

    val authState = authViewModel.authState.observeAsState()
    val firestoreViewModel = FirestoreViewModel()
    var procurar by remember { mutableStateOf("") }
    val beneficiario = firestoreViewModel.beneficiarioData.observeAsState(emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val filteredAgregados = remember(procurar, beneficiario.value) {
        beneficiario.value.filter {
            it.nome.contains(procurar, ignoreCase = true) ||
            it.telemovel.contains(procurar, ignoreCase = true) ||
            it.nacionalidade.contains(procurar, ignoreCase = true) }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                if(uid != null) {
                    firestoreViewModel.getBeneficiariosFromLojaSocial(uid)
                }
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
                            navController.navigate("initialbeneficiario")
                        },
                        R.drawable.back
                    )

                }

                Spacer(modifier = Modifier.height(40.dp))

                TextField(
                    value = procurar,
                    onValueChange = { procurar = it },
                    placeholder = { Text("Procurar...", color = Color(0xFFBAB8E7)) },
                    singleLine = true,
                    shape = RoundedCornerShape(35.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color(0xFF34334A)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Gestão de beneficiários:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0x5A5881).copy(alpha = 0.4f))
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nome",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )

                Text(
                    text = "Telemóvel",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                )

                Text(
                    text = "Nacionalidade",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                items(filteredAgregados) { beneficiario ->
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
        }
    }
    }