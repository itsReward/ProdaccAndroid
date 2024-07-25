package com.example.prodacc.ui.jobcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.designsystem.designComponents.NavigationBar
import com.example.designsystem.designComponents.TopBar

@Composable
fun JobCardsScreen(
    navController: NavController
){
    Scaffold(
        topBar = {TopBar("Job Cards")},
        bottomBar = { NavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("JobCards Screen")
        }
    }
}