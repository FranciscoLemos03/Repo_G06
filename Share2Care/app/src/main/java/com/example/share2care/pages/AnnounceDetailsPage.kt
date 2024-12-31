package com.example.share2care.pages

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.share2care.FirestoreViewModel
import com.example.share2care.R
import com.example.share2care.ui.components.CircleButton
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun AnnounceDetailsPage(announceId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val imageHeight = (screenHeight * 0.5f).dp
    val componentHeight = (screenHeight * 0.6f).dp
    var valorDoado by remember { mutableStateOf(0) }
    var anuncio by remember { mutableStateOf<FirestoreViewModel.AllAnuncios?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(announceId) {
        firestore.collection("anuncios").document(announceId).get()
            .addOnSuccessListener { document ->
                val anuncioData = document.toObject(FirestoreViewModel.AllAnuncios::class.java)?.copy(id = announceId)

                if (anuncioData != null) {
                    val lojaSocialId = document.getString("loja_social_id")

                    if (lojaSocialId != null) {
                        firestore.collection("lojaSocial").document(lojaSocialId).get()
                            .addOnSuccessListener { lojaSocialDoc ->
                                val lojaSocialName = lojaSocialDoc.getString("nome") ?: "Loja desconhecida"
                                val imageUrlLojaSocial = lojaSocialDoc.getString("imagemUrl")

                                anuncio = anuncioData.copy(
                                    lojaSocialName = lojaSocialName,
                                    imageUrlLojaSocial = imageUrlLojaSocial
                                )
                                isLoading = false
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    } else {
                        anuncio = anuncioData
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        anuncio?.let { anuncioDetails ->

            when (anuncioDetails.tipo) {
                "Voluntariado" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("home")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFB65500),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Text(
                                        text = "Requisitos:",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = anuncioDetails.requisitos.trimIndent(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate("createTicket/${anuncioDetails.id}")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Candidatar", color = Color.White)
                        }
                    }

                }
                "Noticia" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("home")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF5E5790),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Text(
                                        text = anuncioDetails.descricao.trimIndent(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val link = anuncioDetails.link

                                val validlink = if (!link.startsWith("https://")) {
                                    "https://$link"
                                } else {
                                    link
                                }

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validlink))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Ver Mais", color = Color.White)
                        }
                    }

                }
                "Doação de bens" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("home")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF5CA8FF),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Text(
                                        text = "Necessidades:",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = anuncioDetails.necessidades.trimIndent(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate("createTicket/${anuncioDetails.id}")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Agendar Doação", color = Color.White)
                        }
                    }

                }
                "Doação monetária" -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = 300f
                                    )
                                )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(anuncioDetails.imagemUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 16.dp)
                        ) {
                            CircleButton(
                                onClick = {
                                    navController.navigate("home")
                                },
                                R.drawable.back
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF28A745),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = anuncioDetails.tipo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = anuncioDetails.titulo,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF1F1E2C),
                                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                    )
                                    .height(componentHeight)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            rememberAsyncImagePainter(anuncioDetails.imageUrlLojaSocial),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = anuncioDetails.lojaSocialName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Text(
                                        text = anuncioDetails.motivo.trimIndent(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = buildAnnotatedString {
                                                append("Angariado ")
                                                withStyle(style = SpanStyle(color = Color(0xFF28A745))) {
                                                    append(valorDoado.toString() + "€")
                                                }
                                                append(" de ")
                                                withStyle(style = SpanStyle(color = Color(0xFF28A745))) {
                                                    append(anuncioDetails.meta + "€")
                                                }
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White,
                                            lineHeight = 20.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        val percentagem = (valorDoado*100)/anuncioDetails.meta.toInt()

                                        Text(
                                            text = "$percentagem%",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF28A745),
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .border(2.dp, Color(0xFF28A745), RoundedCornerShape(15.dp))
                                            .background(Color(0xFF1F1E2C)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        val percentagem = (valorDoado * 100) / anuncioDetails.meta.toInt()
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(percentagem / 100f)
                                                .clip(RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                                                .background(Color(0xFF28A745))
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {

                                        items(listOf(1, 5, 10, 20, anuncioDetails.meta.toInt())) { value ->
                                            Button(
                                                onClick = { valorDoado = valorDoado + value },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                                            ) {
                                                Text(
                                                    text = "${value}€",
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                /*TODO*/
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Fazer doação monetária", color = Color(0xFF1F1E2C))
                        }
                    }

                }
            }


        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Anúncio não encontrado")
            }
        }
    }
}
