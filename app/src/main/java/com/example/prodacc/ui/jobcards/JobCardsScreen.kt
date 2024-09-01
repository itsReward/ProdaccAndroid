package com.example.prodacc.ui.jobcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.HistorySection
import com.example.designsystem.designComponents.JobStatusFilters
import com.example.designsystem.designComponents.LargeJobCard
import com.example.prodacc.navigation.NavigationBar
import com.example.designsystem.designComponents.SectionHeading
import com.example.designsystem.designComponents.TopBar
import com.example.prodacc.navigation.Route

@Composable
fun JobCardsScreen(
    navController: NavController
) {
    val scroll = rememberScrollState()
    val viewModel = JobCardViewModel()

    Scaffold(
        topBar = { TopBar("Job Cards") },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scroll)
        ) {
            SectionHeading(
                text = "Today's Jobs",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, top = 10.dp),
                textAlign = TextAlign.Start
            )

            JobStatusFilters()


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = 2000.dp)
                    .padding(horizontal = 20.dp)
            ) {

                items(viewModel.jobCards) { jobCard ->
                    LargeJobCard(
                        jobCardName = jobCard.jobCardName.substringAfter(" ",  " "),
                        onClick = { navController.navigate(Route.JobCardDetails.path.replace("{jobCardId}", jobCard.id.toString())) },
                        jobCardDeadline = jobCard.jobCardDeadline
                    )
                }


            }


            HistorySection(
                heading = "History",
                buttonOnClick = {}
            )

            LazyColumn(
                //verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = 2000.dp)
                    .padding(horizontal = 20.dp)
            ) {
                items(viewModel.pastJobCards) {
                    AllJobCardListItem(
                        jobCardName = it.jobCardName,
                        closedDate = it.dateAndTimeClosed,
                        onClick = { navController.navigate(Route.JobCardDetails.path.replace("{jobCardId}", it.id.toString())) },
                    )
                }
            }


        }

    }
}