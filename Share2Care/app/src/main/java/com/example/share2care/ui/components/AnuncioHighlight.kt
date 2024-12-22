package com.example.share2care.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.share2care.R

@Composable
fun AnnounceHighlight(){
    Box(
        modifier = Modifier
            .size(350.dp, 200.dp) // Dimensões fixas
            .padding(end = 16.dp) // Padding externo
            .border(
                width = 2.dp, // Contorno branco
                color = Color.White,
                shape = RoundedCornerShape(16.dp) // Cantos arredondados
            )
            .clip(RoundedCornerShape(16.dp)) // Para aplicar o contorno também à imagem
    ) {
        // Imagem de fundo
        Image(
            painter = painterResource(id = R.drawable.imgexemplo),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Ajusta a imagem para preencher o tamanho do Box
            modifier = Modifier.fillMaxSize() // Garante que a imagem ocupe todo o espaço do Box
        )

        // Conteúdo sobre a imagem
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding interno para o conteúdo
            verticalArrangement = Arrangement.SpaceBetween // Organiza os itens no topo e no rodapé
        ) {

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = Color(0xFF595790),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp) // Padding do conteúdo interno
            ) {
                Text(
                    text = "Voluntariado",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Texto no rodapé
            Column {
                Text(
                    text = "Loja Social São Lázaro",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Há 10 minutos",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}