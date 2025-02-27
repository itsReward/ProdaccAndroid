package com.example.designsystem.designComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.Red
import com.prodacc.data.remote.dao.product.Product

@Composable
fun ProductCards(
    product: Product,
    onNavigate: () -> Unit
) {
    val stockColor =
        if (product.inStock > product.healthyNumber) DarkGreen else if (product.inStock == product.healthyNumber) Orange else Red

    Row(
        modifier = Modifier
            .clickable(onClick = onNavigate)
            .fillMaxWidth()
            .padding(20.dp)
            .drawBehind {
                drawLine(
                    color = CardGrey,
                    start = Offset(0f, 0f),
                    end = Offset(100f, 0f),
                    strokeWidth = 1f
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = product.partName, fontWeight = FontWeight.Medium)
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Part No. : ", style = MaterialTheme.typography.labelMedium)
                Text(text = product.partNumber, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "In stock:",
                    style = MaterialTheme.typography.labelMedium,
                    color = stockColor
                )
                Text(
                    text = "${product.inStock}",
                    color = stockColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = "$${product.sellingPrice}", style = MaterialTheme.typography.bodyMedium)
        }

    }
}