package com.example.share2care.pages
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share2care.AuthState
import com.example.share2care.AuthViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import com.example.share2care.ui.components.DynamicButton

@Composable
fun InitialBeneficiarioPage(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value){
        when(authState.value){
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            is AuthState.Unauthenticated -> navController.navigate("initial")
            is AuthState.Anonymous -> navController.navigate("homeAnonymous")
            else -> Unit
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
                        navController.navigate("home")
                    },
                    R.drawable.back
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Gestão de Beneficiarios e Agregados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Image(
                    painter = painterResource(id = R.drawable.beneficiariomanagement),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(bottom = 8.dp)
                )

                DynamicButton(
                    text = "Gestão de Beneficiários",
                    onClick = {
                        navController.navigate("beneficiariomanagement")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(56.dp)
                )

                DynamicButton(
                    text = "Gestão de Agregados",
                    onClick = {
                        navController.navigate("agregadomanagement")
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