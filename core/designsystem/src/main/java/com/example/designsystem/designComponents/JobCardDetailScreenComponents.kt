package com.example.designsystem.designComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.example.designsystem.theme.BlueA700
import com.prodacc.data.remote.dao.JobCardStatus

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StepIndicator(
    jobCardStatuses: List<JobCardStatus>
) {
    var expanded by remember { mutableStateOf(false) }

    val currentStep = jobCardStatuses.size-1
    val maxSteps = 6


    AnimatedVisibility (
        visible =  expanded,
        /*enter = slideInHorizontally(animationSpec = tween(durationMillis = 500)) + fadeIn(),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 10)) + fadeOut(animationSpec = tween(durationMillis = 400))*/
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .animateEnterExit(enter = fadeIn(), exit = fadeOut()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                MediumTitleText(name = "JobCard Status")
            }


            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            ){
                (0 until maxSteps).forEachIndexed { index, _ ->
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding()
                    ) {


                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(25.dp)
                        ) {
                            if (index == 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                            } else {
                                Row(
                                    modifier = Modifier
                                        .background(if (index <= currentStep) BlueA700 else Color.LightGray)
                                        .height(80.dp)
                                        .width(2.dp)
                                        .offset(y = 10.dp)
                                ) {

                                }
                            }

                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        shape = CircleShape,
                                        color = if (index <= currentStep) BlueA700 else Color.LightGray
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {
                                Row(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(if (index <= currentStep) BlueA700 else Color.LightGray),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center

                                ) {
                                    Text(text = (index + 1).toString(), fontSize = 10.sp)
                                }

                            }
                            if (index <= jobCardStatuses.size - 1) {
                                Row(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    BodyText(
                                        text = "${jobCardStatuses[index].status} : ${jobCardStatuses[index].createdAt.hour}:" +
                                                "${jobCardStatuses[index].createdAt.minute}  - " +
                                                "${jobCardStatuses[index].createdAt.dayOfMonth} " +
                                                "${jobCardStatuses[index].createdAt.month} " +
                                                "${jobCardStatuses[index].createdAt.year}"
                                    )
                                }

                            }

                        }


                    }
                }
            }

            Button(onClick = { expanded = !expanded }, modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "collapse")
            }
        }
    }
    AnimatedVisibility(
        visible = !expanded,
  /*      enter = slideInHorizontally(animationSpec = tween(durationMillis = 500)),
        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 500))*/
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Spacer(modifier = Modifier.width(20.dp))

            Icon(
                Icons.Default.Garage,
                "",
                Modifier.size(25.dp),
                BlueA700
            )

            (0 until maxSteps).forEachIndexed { index, _ ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .background(if (index <= currentStep) BlueA700 else Color.LightGray)
                                .height(2.dp)
                                .width(70.dp)
                                .offset(y = 10.dp)
                        ) {

                        }
                    }

                    Row(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                shape = CircleShape,
                                color = if (index <= currentStep) BlueA700 else Color.LightGray
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {
                        Row(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(CircleShape)
                                .background(if (index <= currentStep) BlueA700 else Color.LightGray),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {
                            Text(text = (index + 1).toString(), fontSize = 10.sp)
                        }

                    }

                }


            }
            Text(
                text = jobCardStatuses.last().status,
                //fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(50.dp))
        }
    }

}


@Composable
fun TopBar(
    jobCardName: String,
    navController: NavController,
    onClickPeople: () -> Unit,
    onClickDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomEnd = 30.dp))
            .background(Color.Blue)
            .wrapContentSize()
            .systemBarsPadding()
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Row(
            modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(
                onClick = { navController.navigateUp() },
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                color = Color.White
            )
            LargeTitleText(
                name = jobCardName, color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .weight(2f)
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(
                onClick = onClickPeople, icon = Icons.Default.People, color = Color.White
            )
            IconButton(
                onClick = onClickDelete, icon = Icons.Filled.Delete, color = Color.White
            )
        }


    }
}