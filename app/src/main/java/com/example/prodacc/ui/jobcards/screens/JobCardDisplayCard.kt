package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.BodyTextItalic
import com.example.designsystem.designComponents.DurationText
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.IconButton
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.BlueA60
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.DarkOrange
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGreen
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.LightOrange
import com.example.designsystem.theme.LightRed
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.Red
import com.example.designsystem.theme.chat
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.jobcards.viewModels.ReportLoadingState
import com.example.prodacc.ui.jobcards.viewModels.StatusLoadingState
import com.prodacc.data.remote.dao.JobCard
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun JobCardDisplayCard(
    jobCard: JobCard,
    reportsMap: Map<UUID, ReportLoadingState>,
    statusMap: Map<UUID, StatusLoadingState>,
    navController: NavController
) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (jobCard.priority && jobCard.dateAndTimeClosed == null) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100))
                                .background(LightOrange)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(text = "priority")
                        }
                    }
                    LargeTitleText(name = jobCard.jobCardName)

                }

                Spacer(modifier = Modifier.height(2.dp))
                if (jobCard.jobCardDeadline != null && (jobCard.jobCardDeadline)!! > LocalDateTime.now()
                        .toLocalDate().atStartOfDay() && jobCard.dateAndTimeClosed == null
                ) {

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BodyText(
                            text = "Due: "
                        )

                        FormattedTime(time = jobCard.jobCardDeadline!!)
                    }

                } else if (jobCard.jobCardDeadline != null && (jobCard.jobCardDeadline)!! < LocalDateTime.now() && jobCard.dateAndTimeClosed == null) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Overdue by: ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        DurationText(
                            timeSpentMinutes = Duration.between(
                                jobCard.jobCardDeadline,
                                LocalDateTime.now()
                            ).toMinutes()
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        FormattedTime(time = jobCard.jobCardDeadline!!)
                    }

                } else if (jobCard.dateAndTimeFrozen == null && jobCard.dateAndTimeClosed == null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "No Set Deadline",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Orange,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                } else if (jobCard.dateAndTimeFrozen != null && jobCard.dateAndTimeClosed == null) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BodyText(
                            text = "Frozen: "
                        )

                        FormattedTime(time = jobCard.dateAndTimeFrozen!!)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BodyText(
                            text = "Closed: "
                        )

                        FormattedTime(time = jobCard.dateAndTimeClosed!!)

                        if (jobCard.dateAndTimeClosed!! > jobCard.jobCardDeadline) {
                            Spacer(modifier = Modifier.width(5.dp))
                            /*Text(text = "over due by: ", color = Color.Red, fontWeight = FontWeight.Medium)
                            DurationText(timeSpentMinutes = Duration.between(jobCard.jobCardDeadline, jobCard.dateAndTimeClosed!!).toMinutes())*/
                        }
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
                        BodyTextItalic(
                            text = reportState.response?.jobReport
                                ?: "${jobCard.serviceAdvisorName}'s report missing"
                        )
                    }

                }


            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(2f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        initials = "${jobCard.serviceAdvisorName.first()}",
                        color = LightGrey,
                        textColor = DarkGrey
                    )

                    IconButton(onClick = { }, icon = chat, color = Grey)
                }

                //Text("Progress:", color = DarkGrey, modifier = Modifier.weight(2f))

                when (val status = statusMap[jobCard.id]) {
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
                            modifier = Modifier
                                .height(5.dp)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }

                    is StatusLoadingState.Success -> {
                        if (status.response?.status != null && status.response.status.isNotBlank()) {
                            if (jobCard.dateAndTimeClosed != null && jobCard.jobCardDeadline != null) {
                                Row(
                                    modifier = Modifier
                                        //.shadow(5.dp, RoundedCornerShape(20.dp))
                                        .clip(RoundedCornerShape(100))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (jobCard.dateAndTimeClosed!! < jobCard.jobCardDeadline) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Done tick",
                                            tint = if (jobCard.dateAndTimeClosed!! < jobCard.jobCardDeadline) BlueA700 else Orange
                                        )
                                        Text(
                                            text = "on time",
                                            color = if (jobCard.dateAndTimeClosed!! < jobCard.jobCardDeadline) BlueA700 else Orange,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100))
                                                .background(Color.White)
                                                .padding(horizontal = 5.dp)
                                        ) {
                                            DurationText(
                                                timeSpentMinutes = Duration.between(
                                                    jobCard.dateAndTimeClosed,
                                                    jobCard.jobCardDeadline
                                                ).toMinutes(),
                                                fontWeight = FontWeight.Medium,
                                                color = Grey
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Done tick",
                                            tint = Red
                                        )
                                        Text(
                                            text = "late",
                                            color = Red,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100))
                                                .background(Color.White)
                                                .padding(horizontal = 5.dp)
                                        ) {
                                            DurationText(
                                                timeSpentMinutes = Duration.between(
                                                    jobCard.jobCardDeadline,
                                                    jobCard.dateAndTimeClosed
                                                ).toMinutes(),
                                                fontWeight = FontWeight.Medium,
                                                color = Grey
                                            )
                                        }
                                    }

                                }
                            } else {
                                LinearProgressIndicator(
                                    progress = {
                                        when (status.response.status) {
                                            "opened" -> 0.15f / 6f
                                            "diagnostics" -> 1f / 6f
                                            "approval" -> 2f / 6f
                                            "work_in_progress" -> 3f / 6f
                                            "testing" -> 4f / 6f
                                            "waiting_for_payment" -> 5f / 6f
                                            "done" -> 6f / 6f
                                            "frozen" -> 0f
                                            else -> 0f
                                        }
                                    },
                                    modifier = Modifier
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    color = when (status.response.status) {
                                        "done" -> DarkGreen
                                        "frozen" -> Red
                                        else -> BlueA700
                                    },
                                    trackColor = when (status.response.status) {
                                        "onhold" -> Orange
                                        else -> LightGrey
                                    }
                                )
                            }

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