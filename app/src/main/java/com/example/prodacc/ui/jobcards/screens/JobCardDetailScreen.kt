package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.ControlChecklist
import com.example.designsystem.designComponents.DateTimePickerTextField
import com.example.designsystem.designComponents.DisabledTextField
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.NewTimeSheet
import com.example.designsystem.designComponents.ServiceChecklist
import com.example.designsystem.designComponents.StateChecklist
import com.example.designsystem.designComponents.StepIndicator
import com.example.designsystem.designComponents.TeamDialog
import com.example.designsystem.designComponents.Timesheets
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.checklistIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeesViewModel
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JobCardDetailScreen(
    navController: NavController, jobCardId: String
) {
    val viewModel = JobCardDetailsViewModel(job = jobCardId)
    val employeesViewModel = EmployeesViewModel()

    val scroll = rememberScrollState()
    val statusScroll = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }


    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    //checklist dialogs
    var showStateChecklistDialog by remember {
        mutableStateOf(false)
    }
    var showControlChecklistDialog by remember {
        mutableStateOf(false)
    }
    var showServiceChecklistDialog by remember {
        mutableStateOf(false)
    }


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .navigationBarsPadding()

    ) {
        TopBar(
            jobCardName = viewModel.jobCard.jobCardName,
            navController = navController,
            onClickPeople = { showDialog = !showDialog },
            onClickDelete = {}
        )
        if (showDialog) {
            TeamDialog(
                onDismiss = { showDialog = !showDialog },
                onAddNewTechnician = { showDialog = !showDialog },
                jobCard = viewModel.jobCard,
                employees = employeesViewModel.employees,
                onUpdateSupervisor = viewModel::updateSupervisor,
                onUpdateServiceAdvisor = viewModel::updateServiceAdvisor
            )
        }


        //content


        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .animateContentSize()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .animateContentSize()
                //.horizontalScroll(statusScroll)
                ,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(
                    jobCardStatuses = viewModel.jobCardStatusList.subList(0, 4)
                )

            }

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(CardGrey)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)

                ) {

                    Row {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(2f)
                        ) {
                            MediumTitleText(name = "Vehicle : ")
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .border(2.dp, LightGrey, RoundedCornerShape(100.dp))
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable(onClick = {
                                        navController.navigate(
                                            Route.VehicleDetails.path.replace(
                                                "{vehicleId}",
                                                viewModel.jobCard.vehicleId.toString()
                                            )
                                        )
                                    })
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                BodyText(text = viewModel.jobCard.vehicleName)

                            }


                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(2f)
                        ) {
                            MediumTitleText(name = "Client : ")
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .border(2.dp, LightGrey, RoundedCornerShape(100.dp))
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable(onClick = {
                                        navController.navigate(
                                            Route.ClientDetails.path.replace(
                                                "{clientId}",
                                                viewModel.jobCard.clientId.toString()
                                            )
                                        )
                                    })
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                BodyText(text = viewModel.jobCard.clientName.substringAfter(" "))

                            }


                        }
                    }


                    Row {
                        DisabledTextField(
                            label = "JobCard No.",
                            text = viewModel.jobCard.jobCardNumber.toString(),
                            modifier = Modifier
                        )
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
                    value = viewModel.jobCard.dateAndTimeFrozen,
                    onValueChange = { newDateTime -> viewModel.updateDateAndTimeFrozen(newDateTime) },
                    label = "Date Frozen",
                    modifier = Modifier
                        .fillMaxWidth()
                )


            }


            Button(
                onClick = {
                    showStateChecklistDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Grey,
                )
            ) {
                Text(text = "State Checklist")

            }


            Column {
                OutlinedTextField(
                    value = "",
                    onValueChange = { it },
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
                    onValueChange = { it },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }





            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MediumTitleText(name = "Timesheets/work done ")
                    TextButton(
                        onClick = {
                            showBottomSheet = true
                        }
                    ) {
                        Text(text = "Add")
                    }
                }

                viewModel.timesheets.forEach {
                    Timesheets(
                        it
                    )
                }
            }


            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = checklistIcon, contentDescription = "Checklist icon")
                    MediumTitleText(name = "Checklists", modifier = Modifier.padding(start = 5.dp))
                }

                Row {
                    Button(
                        onClick = {
                            showControlChecklistDialog = true
                        },
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Grey,
                        )
                    ) {
                        // Icon(imageVector = checklistIcon, contentDescription = "")
                        Text(text = "Control")

                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            showServiceChecklistDialog = true
                        },
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Grey,
                        )
                    ) {
                        Text(text = "Service ")

                    }
                }


            }

            OutlinedTextField(
                value = "",
                onValueChange = { it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = {
                    Text(
                        text = "Control Report"
                    )
                }
            )

            DateTimePickerTextField(
                value = viewModel.jobCard.dateAndTimeClosed,
                onValueChange = { newDateTime -> viewModel.updateDateAndTimeClosed(newDateTime) },
                label = "Date Closed",
                modifier = Modifier.fillMaxWidth()
            )


        }


    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            // Sheet content

            NewTimeSheet(
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                saveSheet = {}
            )
        }
    }

    AnimatedVisibility(
        visible = showStateChecklistDialog,
        enter = androidx.compose.animation.slideInVertically(),
        exit = slideOutVertically()
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 0.dp, vertical = 20.dp)
                .systemBarsPadding()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                StateChecklist(jobCardName = viewModel.jobCard.jobCardName, onClose = { showStateChecklistDialog = false })

            }


        }
    }



    AnimatedVisibility(
        visible = showServiceChecklistDialog,
        enter = androidx.compose.animation.slideInVertically(),
        exit = slideOutVertically()
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 0.dp, vertical = 20.dp)
                .systemBarsPadding()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                ServiceChecklist(jobCardName = viewModel.jobCard.jobCardName, onClose = { showServiceChecklistDialog = false })

            }


        }

    }

    AnimatedVisibility(
        visible = showControlChecklistDialog,
        enter = slideInVertically(animationSpec = tween(durationMillis = 500, easing = EaseIn )),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 500, easing = EaseOut ))
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 0.dp, vertical = 20.dp)
                .systemBarsPadding()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                ControlChecklist(jobCardName = viewModel.jobCard.jobCardName, onClose = { showControlChecklistDialog = false })

            }


        }

    }

}

