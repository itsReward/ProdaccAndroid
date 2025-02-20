package com.example.products.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.products.navigation.NavigationBar

@Composable
fun VehiclesListScreen(
    navController: NavController
){
    Scaffold (
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            NavigationBar(navController = navController)
        },
        topBar = {
            Row (
                modifier = Modifier.fillMaxWidth().padding(end = 20.dp, start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
                ){
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Product Vehicles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }

            }
        }
    ) { innerPadding ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }

    }
}