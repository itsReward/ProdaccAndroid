package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.BodyTextItalic
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.Red
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.jobcards.viewModels.ReportLoadingState
import com.example.prodacc.ui.jobcards.viewModels.StatusLoadingState
import com.prodacc.data.remote.dao.JobCard
import java.util.UUID

@Composable
fun JobCardDisplayCard(
    jobCard: JobCard,
    reportsMap: Map<UUID, ReportLoadingState>,
    statusMap: Map<UUID, StatusLoadingState>,
    navController: NavController
){
    Card(
        onClick = {
            navController.navigate(
                Route.JobCardDetails.path.replace(
                    "{jobCardId}",
                    jobCard.id.toString()
                )
            )
        },
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardGrey
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
            ) {
                LargeTitleText(name = jobCard.jobCardName)
                if (jobCard.jobCardDeadline != null){

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BodyText(
                            text = "Due: "
                        )

                        FormattedTime(time = jobCard.jobCardDeadline!!)
                    }

                } else {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "No Set Deadline",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Orange,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                }



            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 0.dp),
            ) {

                when (val reportState = reportsMap[jobCard.id] ?: ReportLoadingState.Idle) {
                    is ReportLoadingState.Error -> {
                        BodyTextItalic(text = reportState.message)
                    }
                    is ReportLoadingState.Idle -> {
                        BodyTextItalic(text = "Load Report")
                    }
                    is ReportLoadingState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            CircularProgressIndicator(
                                color = BlueA700,
                                trackColor = Color.Transparent,
                                modifier = Modifier.size(15.dp),
                                strokeWidth = 1.dp
                            )
                            Text(text = "Loading Report...", color = DarkGrey)
                        }
                    }
                    is ReportLoadingState.Success -> {
                        BodyTextItalic(text = reportState.response?.jobReport ?: "${jobCard.serviceAdvisorName}'s report missing")
                    }

                }


            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileAvatar(
                    initials = "${jobCard.serviceAdvisorName.first()}",
                    modifier = Modifier
                        .weight(2f),
                    color = LightGrey,
                    textColor = DarkGrey
                )
                //Text("Progress:", color = DarkGrey, modifier = Modifier.weight(2f))

                when (val status = statusMap[jobCard.id]){
                    is StatusLoadingState.Error -> {
                        Text(text = status.message, color = Orange, fontSize = 12.sp)
                    }
                    is StatusLoadingState.Idle -> {
                        Text(text = "Load Status", color = Orange, fontSize = 10.sp)
                    }
                    is StatusLoadingState.Loading -> {
                        LinearProgressIndicator(
                            color = BlueA700,
                            trackColor = Color.Transparent,
                            modifier = Modifier.height(2.dp)
                        )
                    }
                    is StatusLoadingState.Success -> {
                        if (status.response?.status != null && status.response.status.isNotBlank()) {
                            LinearProgressIndicator(
                                progress = {
                                    when (status.response.status){
                                        "opened" -> 0.15f / 6f
                                        "diagnostics" -> 2f / 6f
                                        "approval" -> 3f / 6f
                                        "work_in_progress" -> 4f / 6f
                                        "testing" -> 5f / 6f
                                        "done" -> 6f / 6f
                                        "frozen" -> 0f
                                        else -> 0f
                                    }
                                },
                                modifier = Modifier
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                color = when (status.response.status){
                                    "done" -> DarkGreen
                                    "frozen" -> Red
                                    else -> BlueA700
                                },
                                trackColor = when (status.response.status) {
                                    "onhold" -> Orange
                                    else -> LightGrey
                                }
                            )
                        } else {
                            Text(text = "No Status", color = Color.Red, fontSize = 10.sp)

                        }

                    }
                    null -> {
                        Text(text = "HUGE ERROR ", color = Color.Red, fontSize = 10.sp)
                    }
                }
            }


        }

    }
}