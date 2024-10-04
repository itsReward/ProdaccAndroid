package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.HistorySection
import com.example.designsystem.designComponents.JobStatusFilters
import com.example.designsystem.designComponents.LargeJobCard
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.SectionHeading
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.designComponents.VehiclesDropDown
import com.example.designsystem.theme.vehicleIcon
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.jobcards.viewModels.JobCardViewModel

@Composable
fun JobCardsScreen(
    navController: NavController
) {
    val scroll = rememberScrollState()
    val viewModel = JobCardViewModel()
    var newJobCardDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar("Job Cards"){navController.navigate(Route.Search.path.replace("{title}", "Job Cards"))} },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { newJobCardDialog = !newJobCardDialog },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ) { innerPadding ->

        AnimatedVisibility(visible = newJobCardDialog) {
            Dialog(onDismissRequest = { newJobCardDialog = !newJobCardDialog }) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .wrapContentSize()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                ) {
                    var vehicleExpanded by remember { mutableStateOf(false) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp,top = 20.dp, start = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            LargeTitleText(name = "New Job Card")
                        }

                    }


                    TextField(
                        value = "${viewModel.vehicleState?.color} ${viewModel.vehicleState?.model} ${viewModel.vehicleState?.regNumber}",
                        onValueChange = {},
                        placeholder = {Text(text = "Vehicle")},
                        label = { Text(text = "Vehicle") },
                        readOnly = true,
                        leadingIcon = {
                            Icon(imageVector = vehicleIcon, contentDescription = "Vehicle Icon")
                        },
                        trailingIcon = {
                            IconButton(onClick = { vehicleExpanded = !vehicleExpanded }) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Drop down"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    VehiclesDropDown(
                        expanded = vehicleExpanded,
                        onDismissRequest = { vehicleExpanded = !vehicleExpanded },
                        vehicles = viewModel.vehicles,
                        onVehicleSelected = viewModel::updateVehicle,
                        newVehicle = { navController.navigate(Route.NewVehicle.path) }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                newJobCardDialog = !newJobCardDialog
                            }
                        ){
                            Text(text = "Cancel")

                        }

                        Button(onClick = {
                            navController.navigate(
                                Route.NewJobCard.path.replace(
                                    "vehicleId",
                                    viewModel.vehicleState?.id.toString()
                                )
                            )
                        }) {
                            Text(text = "Proceed")
                        }
                    }


                }

            }
        }



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
                        jobCardName = jobCard.jobCardName.substringAfter(" ", " "),
                        onClick = {
                            navController.navigate(
                                Route.JobCardDetails.path.replace(
                                    "{jobCardId}",
                                    jobCard.id.toString()
                                )
                            )
                        },
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
                        onClick = {
                            navController.navigate(
                                Route.JobCardDetails.path.replace(
                                    "{jobCardId}",
                                    it.id.toString()
                                )
                            )
                        },
                    )
                }
            }


        }

    }
}