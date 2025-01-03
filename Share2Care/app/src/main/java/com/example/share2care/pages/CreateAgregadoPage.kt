package com.example.share2care.pages
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.DynamicButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAgregadoPage(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val firestoreviewmodel = FirestoreViewModel()
    val agregados = firestoreviewmodel.agregadoData.observeAsState(emptyList())
    var num_doc by remember { mutableStateOf("MGAG") }
    var isValid by remember { mutableStateOf(true) }
    val creationDate = FieldValue.serverTimestamp()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> Unit
        }
    }

    LaunchedEffect(agregados.value) {
        if (agregados.value.isNotEmpty()) {
            num_doc = agregados.value.first().num_doc
        } else {
            Log.e("AgregadoDetailsPage", "Não foi possível encontrar esse agregado")
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
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
                        navController.navigate("agregadomanagement")
                    },
                    R.drawable.back
                )

                Text(
                    text = "Criar Agregado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.agregado),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = num_doc,
                    onValueChange = {
                        num_doc = it
                        isValid = it.matches(Regex("^MGAG\\d{9}$"))
                    },
                    label = { Text("Número do documento *", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = if (isValid) Color.Magenta else Color.Red,
                        unfocusedBorderColor = if (isValid) Color.Magenta else Color.Red,
                    ),
                    isError = !isValid
                )

                if (!isValid) {
                    Text(
                        text = "Valor inválido, o campo deve conter \"MGAG\" seguido de 9 números",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                DynamicButton(
                    text = "Criar",
                    onClick = {
                        if (isValid) {
                            if (uid != null) {
                                firestoreviewmodel.saveAgregado(num_doc,creationDate,uid)
                            }
                            Toast.makeText(
                                context,
                                "Agregado criado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("agregadomanagement")
                        } else {
                            Toast.makeText(
                                context,
                                "O valor do documento é inválido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(56.dp)
                )
            }
        }
    }
}