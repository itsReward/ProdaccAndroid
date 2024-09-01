package com.example.prodacc.ui.jobcards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.example.designsystem.designComponents.DisabledTextField
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.StepIndicator
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.CardGrey
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCardDetailScreen(
    navController: NavController, jobCardId: String
) {
    val viewModel = JobCardDetailsViewModel(job = jobCardId)
    val scroll = rememberScrollState()
    val statusScroll = rememberScrollState()
    var showDialog by remember{ mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()

    ) {
        TopBar(jobCardName = viewModel.jobCard.jobCardName, navController = navController, onClickPeople = {showDialog = !showDialog}, onClickDelete = {})

        if (showDialog){
            Dialog(onDismissRequest = { showDialog = !showDialog }) {
                Column (
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
                    ){
                        LargeTitleText(name = "Team")

                    }

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardGrey)
                            .height(2.dp)
                    ){

                    }
                    DisabledTextField(label = "Service Advisor", text = viewModel.jobCard.serviceAdvisorName)
                    DisabledTextField(label = "Supervisor", text = viewModel.jobCard.supervisorName)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        MediumTitleText("Technicians: ")
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "Add")
                        }
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        Button(onClick = {showDialog = !showDialog}) {
                            Text(text = "close")
                        }
                    }

                }

            }
        }

        Column(
            modifier = Modifier.verticalScroll(scroll).animateContentSize()
        ) {
            Row (
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .horizontalScroll(statusScroll),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                StepIndicator(
                    jobCardStatuses = viewModel.jobCardStatusList.subList(0,3)
                )
            }



            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
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
                            label = "JobCard No.",
                            text = viewModel.jobCard.jobCardNumber.toString(),
                            modifier = Modifier.weight(2f)
                        )
                        DisabledTextField(
                            label = "Vehicle",
                            text = viewModel.jobCard.vehicleName,
                            modifier = Modifier.weight(2f)
                        )
                    }



                    FlowRow {
                        DisabledTextField(
                            label = "Date In",
                            text = "${viewModel.jobCard.dateAndTimeIn.dayOfMonth} " +
                                    "${viewModel.jobCard.dateAndTimeIn.month} " +
                                    "${viewModel.jobCard.dateAndTimeIn.hour}:" +
                                    "${viewModel.jobCard.dateAndTimeIn.minute}",
                            modifier = Modifier.weight(2f)
                        )
                        DisabledTextField(
                            label = "E.T.C.",
                            text = "${viewModel.jobCard.estimatedTimeOfCompletion.dayOfMonth} " +
                                    "${viewModel.jobCard.estimatedTimeOfCompletion.month} " +
                                    "${viewModel.jobCard.estimatedTimeOfCompletion.hour}:" +
                                    "${viewModel.jobCard.estimatedTimeOfCompletion.minute}",
                            modifier = Modifier.weight(2f)
                        )
                    }

                    Row {

                        DisabledTextField(
                            label = "Deadline",
                            text = "${viewModel.jobCard.jobCardDeadline.dayOfMonth} " +
                                    "${viewModel.jobCard.jobCardDeadline.month} " +
                                    "${viewModel.jobCard.jobCardDeadline.hour}:" +
                                    "${viewModel.jobCard.jobCardDeadline.minute}",
                            modifier = Modifier.weight(2f)
                        )
                        if (viewModel.jobCard.dateAndTimeClosed != null) {
                            DisabledTextField(
                                label = "Date Closed",
                                text = "${viewModel.jobCard.dateAndTimeClosed!!.dayOfMonth} " +
                                        "${viewModel.jobCard.dateAndTimeClosed!!.month} " +
                                        "${viewModel.jobCard.dateAndTimeClosed!!.hour}:" +
                                        "${viewModel.jobCard.dateAndTimeClosed!!.minute}",
                                modifier = Modifier.weight(2f)
                            )
                        }

                    }

                    Row {
                        if (viewModel.jobCard.dateAndTimeFrozen != null) {
                            DisabledTextField(
                                label = "Date Frozen",
                                text = "${viewModel.jobCard.dateAndTimeFrozen!!.dayOfMonth} " +
                                        "${viewModel.jobCard.dateAndTimeFrozen!!.month} " +
                                        "${viewModel.jobCard.dateAndTimeFrozen!!.hour}:" +
                                        "${viewModel.jobCard.dateAndTimeFrozen!!.minute}",
                                modifier = Modifier.weight(2f)
                            )
                        }

                    }


                }
            }


        }










    }
}

