package com.example.share2care.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.share2care.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnnounceForAdmin(tipo: String, titulo: String, imageUrl: String?, creationDate: Date, lojaSocialName: String, imageUrlLojaSocial: String?, onClick: () -> Unit, Edit: () -> Unit, Delete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B29))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Image(
                rememberAsyncImagePainter(imageUrl),
                contentDescription = "Imagem do Anúncio",
                modifier = Modifier
                    .fillMaxHeight()
                    .height(100.dp)
                    .width(100.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tipo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E),
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        Button(
                            onClick = { Edit() },
                            modifier = Modifier,
                            shape = RoundedCornerShape(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1B29)),
                            contentPadding = PaddingValues(0.dp) // Remove padding extra ao redor do conteúdo
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.escrever),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp) // Ajusta o tamanho do ícone
                            )
                        }

                        Button(
                            onClick = { Delete() },
                            modifier = Modifier,
                            shape = RoundedCornerShape(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1B29)),
                            contentPadding = PaddingValues(0.dp) // Remove padding extra ao redor do conteúdo
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.trash),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp) // Ajusta o tamanho do ícone
                            )
                        }
                    }


                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        rememberAsyncImagePainter(imageUrlLojaSocial),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = lojaSocialName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(creationDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}





