package com.example.share2care.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.rememberAsyncImagePainter
import com.example.share2care.R
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun TicketValidation(titulo: String?, listaBens: String?, quantidade: String?, condicao: String?, imageUrl: String? , nome: String? , email: String? , motivo: String? , tipoTicket: String, recusar: () -> Unit, aceitar: () -> Unit ,redirecionar: () -> Unit) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val squareSize = 60.dp
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1, -sizePx to 2)
    val height = if (tipoTicket == "Bens") { 520.dp } else { 400.dp }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                    .background(Color.Red)
                    .clickable {
                        recusar()
                    },
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                    .background(Color.Green)
                    .clickable {
                        aceitar()
                    },
            )
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        swipeableState.offset.value.roundToInt(), 0
                    )
                }
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(height)
                .background(Color(0xFF1C1B29))
                .border(2.dp, Color.White, RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (tipoTicket == "Bens") {
                    Image(
                        painter = painterResource(id = R.drawable.donate),
                        contentDescription = "Imagem",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else if (tipoTicket == "Voluntário") {
                    Image(
                        painter = painterResource(id = R.drawable.voluntario),
                        contentDescription = "Imagem",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (titulo != null) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (tipoTicket == "Bens") {
                        Text(
                            text = "Lista de bens: ${listaBens}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Quantidade: ${quantidade}", color = Color.White, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Condição: ${condicao}", color = Color.White, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Image(
                            rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Imagem",
                            modifier = Modifier
                                .sizeIn(maxWidth = 150.dp, maxHeight = 200.dp)
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        )
                    } else if (tipoTicket == "Voluntario") {
                        Text(
                            text = "Voluntariado: ${nome}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Email: ${email}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Motivo: ${motivo}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

            }
            Button(
                onClick = {
                    redirecionar()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(text = "Ver Anuncio", color = Color.White)
            }
        }
    }
}


