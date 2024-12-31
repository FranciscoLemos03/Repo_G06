package com.example.share2care.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton (onClick: () -> Unit, img: Int){
    Button(
        onClick = { onClick() },
        modifier = Modifier.size(65.dp),
        shape = RoundedCornerShape(35.dp)
    ) {
        Icon(
            painter = painterResource(img),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}