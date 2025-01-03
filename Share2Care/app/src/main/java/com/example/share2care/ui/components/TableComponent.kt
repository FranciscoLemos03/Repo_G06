package com.example.share2care.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TableComponent(numDoc: String, creationDate: Date, onClick: () -> Unit, tipo: String, nome: String, telemovel: String, nacionalidade: String){

    if(tipo == "agregado") {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0x000000).copy(alpha = 0.4f))
                .padding(vertical = 20.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = numDoc,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )

            Text(
                text = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "PT")).format(creationDate),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                textAlign = TextAlign.End,
                fontSize = 16.sp
            )
        }

    } else if(tipo == "beneficiario") {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0x000000).copy(alpha = 0.4f))
                .padding(vertical = 20.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = nome,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )

            Text(
                text = telemovel,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )

            Text(
                text = nacionalidade,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )
        }

    }
}