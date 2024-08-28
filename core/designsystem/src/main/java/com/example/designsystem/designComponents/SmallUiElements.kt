package com.example.designsystem.designComponents

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Blue50
import com.prodacc.data.remote.dao.Client
import java.util.UUID

@Composable
fun RoundedHeader(title: String) {
    Row(modifier = Modifier.padding(vertical = 5.dp)){
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(
                    RoundedCornerShape(40.dp)
                )
                .border(2.dp, Blue50, RoundedCornerShape(40.dp))
                //.background(Blue50)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Warehouse,
                contentDescription = "Vehicles present in the garage"
            )
            SectionHeading(
                text = title,
                modifier = Modifier,
                textAlign = TextAlign.Start
            )
        }
    }

}

@Composable
fun IconButton(
    onClick : ()-> Unit,
    icon: ImageVector,
    color: Color,

){
    androidx.compose.material3.IconButton(
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContentColor = color,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Icon(icon, "Back Arrow")

    }
}



@Composable
fun ClientDropdown(clients: List<Client>, onClientSelected: (UUID) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val selectedClient = remember { mutableStateOf<UUID?>(null) }

    Column {
        Text(text = "Select a Client")
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            clients.forEach { client ->
                DropdownMenuItem(
                    text = {
                        Text(text = "${client.clientName} ${client.clientSurname}")
                    },
                    onClick = {
                        selectedClient.value = client.id
                        expanded.value = false
                        onClientSelected(client.id)
                    }
                )
            }
        }
    }
}


@Composable
fun CustomTextField(){

}
