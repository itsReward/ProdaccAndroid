package com.example.designsystem.designComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey

@Composable
fun SectionHeading(text: String, modifier: Modifier, textAlign: TextAlign?) {
    Box(modifier = modifier) {
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
fun SectionLineHeadingSeperator(heading: String) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier
            .background(Color.LightGray)
            .height(2.dp)
            .width(150.dp)) {}
        MediumTitleText(name = heading)
        Box(modifier = Modifier
            .background(Color.LightGray)
            .height(2.dp)
            .width(150.dp)) {}
    }
}


@Composable
fun LargeTitleText(name: String, modifier: Modifier = Modifier, color: Color = DarkGrey) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = modifier
    )
}

@Composable
fun MediumTitleText(name: String, modifier: Modifier = Modifier, color: Color = DarkGrey) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }

}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DarkGrey,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun BodyTextItalic(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = Grey,
        modifier = modifier,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
    )
}