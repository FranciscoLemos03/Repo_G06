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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.DynamicButton
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBeneficiarioPage(AgregadoID: String, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestoreViewModel = FirestoreViewModel()
    val context = LocalContext.current

    var nacionalidade by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var telemovel by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }


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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("agregado/${AgregadoID}") },
                    modifier = Modifier.size(65.dp),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Text(
                    text = "Criação de Beneficiário",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = telemovel,
                onValueChange = {
                    telemovel = it
                    isValid = it.matches(Regex("^[2|9]\\d{8}\$"))
                },
                label = { Text("Telemovel *", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (isValid) Color.Magenta else Color.Red,
                    unfocusedBorderColor = if (isValid) Color.Magenta else Color.Red,
                ),
                isError = !isValid
            )

            if (!isValid) {
                Text(
                    text = "Valor inválido, o campo deve conter um número de telefone válido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome *", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Magenta,
                    unfocusedBorderColor = Color.Magenta,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nacionalidade,
                onValueChange = { nacionalidade = it },
                label = { Text("Nacionalidade *", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Magenta,
                    unfocusedBorderColor = Color.Magenta,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            DynamicButton(
                text = "Enviar",
                onClick = {

                    if (isValid) {

                        firestoreViewModel.saveBeneficiario(telemovel, nome, nacionalidade, AgregadoID)
                        Toast.makeText(
                            context,
                            "Beneficiario criado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("agregado/${AgregadoID}")

                    } else {
                        Toast.makeText(
                            context,
                            "telemovel em formato inválido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },


                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

        }
    }
}