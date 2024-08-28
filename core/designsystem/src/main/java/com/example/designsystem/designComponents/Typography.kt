package com.example.designsystem.designComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.designsystem.theme.DarkGrey

@Composable
fun SectionHeading(text: String, modifier: Modifier, textAlign: TextAlign?) {
    Box(modifier = modifier){
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
            color = DarkGrey
        )
    }

}

@Composable
fun LargeTitleText(name: String, color: Color = DarkGrey){
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}

@Composable
fun MediumTitleText(name: String){
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkGrey
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier){
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = DarkGrey,
        modifier = Modifier
    )
}