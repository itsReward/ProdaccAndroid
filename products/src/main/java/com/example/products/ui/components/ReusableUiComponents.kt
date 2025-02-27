package com.example.products.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey

@Composable
fun LoadingComposable(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = BlueA700,
            trackColor = CardGrey,
            strokeWidth = 2.dp,
            modifier = Modifier.size(20.dp)

        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "Loading")
    }
}

@Composable
fun ErrorComposable(
    text: String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(5.dp))
        TextButton(onClick = onClick) {
            Text(text = "Refresh")
        }
    }

}
