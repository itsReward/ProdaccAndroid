package com.example.designsystem.designComponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.DarkGrey
import com.example.navigation.Route
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.ClientVehicle

@Composable
fun ClientListCard(
    client: Client,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileAvatar(initials = "${client.clientName.first()}${client.clientSurname.first()}")
            BodyText(text = "${client.clientName} ${client.clientSurname}")
        }
        Text(text = "${client.vehicle.size}", color = DarkGrey)

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorisedList(
    categories: List<ListCategory>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier
    ) {
        categories.forEach { category ->
            stickyHeader {
                CategoryHeader(text = category.name)
            }
            items(category.items) { client ->
                ClientListCard(
                    client = client,
                    onClick = {
                        navController.navigate(
                            Route.ClientDetails.path.replace(
                                "{clientId}",
                                client.id.toString()
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.background(Color.White)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = DarkGrey

        )
    }

}


@Composable
fun ClientVehicleRow(
    vehicle: ClientVehicle,
    onClick: () -> Unit
){
    Row (
        modifier = Modifier
            .wrapContentSize().clip(RoundedCornerShape(5.dp)).background(Blue50)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 20.dp)

    ){
        MediumTitleText(name = "${vehicle.make} ${vehicle.model}")
    }
}
   
data class ListCategory(
    val name: String,
    val items: List<Client>
)

