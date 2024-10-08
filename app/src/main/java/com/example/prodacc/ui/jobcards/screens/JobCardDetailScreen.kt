package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.designsystem.designComponents.DateTimePickerTextField
import com.example.designsystem.designComponents.DisabledTextField
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.StepIndicator
import com.example.designsystem.designComponents.Timesheets
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.checklistIcon
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel
import java.time.LocalTime


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCardDetailScreen(
    navController: NavController, jobCardId: String
) {
    val viewModel = JobCardDetailsViewModel(job = jobCardId)
    val scroll = rememberScrollState()
    val statusScroll = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .systemBarsPadding()

    ) {
        TopBar(
            jobCardName = viewModel.jobCard.jobCardName,
            navController = navController,
            onClickPeople = { showDialog = !showDialog },
            onClickDelete = {}
        )
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = !showDialog }) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LargeTitleText(name = "Team")

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardGrey)
                            .height(2.dp)
                    ) {

                    }
                    DisabledTextField(
                        label = "Service Advisor",
                        text = viewModel.jobCard.serviceAdvisorName
                    )
                    DisabledTextField(label = "Supervisor", text = viewModel.jobCard.supervisorName)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MediumTitleText("Technicians: ")
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "Add")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { showDialog = !showDialog }) {
                            Text(text = "close")
                        }
                    }
                }
            }
        }


        //content


        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .animateContentSize()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .horizontalScroll(statusScroll),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(
                    jobCardStatuses = viewModel.jobCardStatusList.subList(0, 3)
                )
            }


            /*Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {}) {
                    Text(text = "Vehicle")

                }
                Button(onClick = {}) {
                    Text(text = "Client")

                }
                Row(
                    modifier = Modifier
                        .size(200.dp, 50.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Grey)

                ) {

                }
            }*/


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(CardGrey)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)

                ) {

                    Row {
                        DisabledTextField(
                            label = "Vehicle",
                            text = viewModel.jobCard.vehicleName,
                            modifier = Modifier.weight(2f)
                        )
                        DisabledTextField(
                            label = "Client",
                            text = viewModel.jobCard.clientName.toString(),
                            modifier = Modifier.weight(2f)
                        )

                    }

                    Row {
                        DisabledTextField(
                            label = "JobCard No.",
                            text = viewModel.jobCard.jobCardNumber.toString(),
                            modifier = Modifier
                        )
                    }


                }
            }


            DateTimePickerTextField(
                value = viewModel.jobCard.dateAndTimeIn,
                onValueChange = { viewModel.updateDateAndTimeIn(it) },
                label = "Date In",
                modifier = Modifier.fillMaxWidth()
            )

            DateTimePickerTextField(
                value = viewModel.jobCard.jobCardDeadline,
                onValueChange = { viewModel.updateJobCardDeadline(it) },
                label = "Deadline",
                modifier = Modifier.fillMaxWidth()
            )


            DateTimePickerTextField(
                value = viewModel.jobCard.dateAndTimeClosed,
                onValueChange = { newDateTime -> viewModel.updateDateAndTimeClosed(newDateTime) },
                label = "Date Closed",
                modifier = Modifier.fillMaxWidth()
            )


            DateTimePickerTextField(
                value = viewModel.jobCard.dateAndTimeFrozen,
                onValueChange = { newDateTime -> viewModel.updateDateAndTimeFrozen(newDateTime) },
                label = "Date Frozen",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )




            Row(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey,
                    )
                ) {
                    Text(text = "State Checklist")

                }
            }

            OutlinedTextField(
                value = "",
                onValueChange = {it},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = {
                    Text(
                        text = "Service Advisor Report"
                    )
                }
            )
            OutlinedTextField(
                value = "",
                onValueChange = {it},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = {
                    Text(
                        text = "Diagnostics Report"
                    )
                }
            )

            DateTimePickerTextField(
                value = viewModel.jobCard.estimatedTimeOfCompletion,
                onValueChange = { viewModel.updateEstimatedTimeOfCompletion(it) },
                label = "E.T.C.",
                modifier = Modifier.fillMaxWidth()
            )

            Column (
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                MediumTitleText(name = "Timesheets/work done ")

                Timesheets(
                    title = "Fixing Breaks",
                    startTime = LocalTime.of(12, 21),
                    endTime = null,
                    profileInitials = "RM"
                )
                Timesheets(
                    title = "Wheel Alignment",
                    startTime = LocalTime.of(12, 21),
                    endTime = null,
                    profileInitials = "MJ"
                )
                Timesheets(
                    title = "Engine Overhaul",
                    startTime = LocalTime.of(12, 21),
                    endTime = null,
                    profileInitials = "PA"
                )
                Timesheets(
                    title = "Minor service",
                    startTime = LocalTime.of(12, 21),
                    endTime = null,
                    profileInitials = "RM"
                )
            }


            Row(
                modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(imageVector = checklistIcon, contentDescription = "Checklist icon")
                    Text(text = "Checklists", modifier = Modifier.padding(start = 5.dp))
                }

                Button(
                    onClick = {},
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey,
                    )
                ) {
                   // Icon(imageVector = checklistIcon, contentDescription = "")
                    Text(text = "Control")

                }

                Button(
                    onClick = {},
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey,
                    )
                ) {
                    Text(text = "Service ")

                }


            }


        }


    }
}

