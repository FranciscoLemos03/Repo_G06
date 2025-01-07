package com.example.share2care.pages

import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.AnnounceForAdmin
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.TableComponent
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregadoManagementPage(navController: NavController, authViewModel: AuthViewModel, firestoreViewModel: FirestoreViewModel = viewModel()){


    val authState = authViewModel.authState.observeAsState()
    var procurar by remember { mutableStateOf("") }
    val agregados = firestoreViewModel.agregadoData.observeAsState(emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val filteredAgregados = remember(procurar, agregados.value) {
        agregados.value.filter { it.num_doc.contains(procurar, ignoreCase = true) }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> {
                if(uid != null) {
                    firestoreViewModel.getAgregadosByLojaSocialId(uid)
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

            Column (
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

                    CircleButton(
                        onClick = {
                            navController.navigate("createagregado")
                        },
                        R.drawable.add
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
                    text = "Gestão de agregados:",
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
                    text = "Agregado",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )

                Text(
                    text = "Data criação",
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

                items(filteredAgregados) { agregados ->
                    TableComponent(
                        agregados.num_doc,
                        agregados.data_criacao,
                        onClick = {
                            navController.navigate("agregado/${agregados.id}")
                        },
                        "agregado",
                        "",
                        "",
                        ""
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

            }


        }
    }
}