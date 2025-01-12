package com.example.designsystem.designComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.dialog.Dialog
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.DarkOrange
import com.example.designsystem.theme.Green
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGreen
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.LightOrange
import com.example.designsystem.theme.Red
import com.example.designsystem.theme.generateRandomColor
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.JobCardReport
import java.time.LocalDateTime

@Composable
fun LargeJobCard(
    onClick: () -> Unit,
    jobCard: JobCard,
    reports: List<JobCardReport>
) {
    Card(
        onClick = onClick,
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
                if (jobCard.jobCardDeadline != null) {
                    BodyText(
                        text = "Due: ${
                            jobCard.jobCardDeadline!!.dayOfWeek.toString().lowercase()
                                .replaceFirstChar { it.uppercase() }
                        } ${jobCard.jobCardDeadline!!.hour}:${jobCard.jobCardDeadline!!.minute}",
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        BodyText(
                            text = "No Set Deadline",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                }


            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 0.dp),
            ) {
                BodyTextItalic(text = "Check the breaks, Check the tires, Steering need attention Perform major service")
            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileAvatar(initials = "AT", modifier = Modifier.weight(2f))
                //Text("Progress:", color = DarkGrey, modifier = Modifier.weight(2f))
                LinearProgressIndicator(progress = { 3f / 5f }, modifier = Modifier.weight(2f))
            }


        }

    }

}


@Composable
fun ProfileAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textSize: TextUnit = 12.sp,
    color: Color = generateRandomColor(),
    textColor: Color = Color.White
) {

    Row(modifier = modifier) {


        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(size)
                .background(color)
            //.border(0.25.dp, Grey, CircleShape)
            ,
            contentAlignment = Alignment.Center
        ) {
            /*Image(
                painter = painterResource(id = R.drawable.profile_avatar),
                contentDescription = "Profile Icon",
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Grey),
                modifier = Modifier.size(20.dp)

            )*/
            Text(text = initials, color = textColor, fontSize = textSize)

        }
    }

}



@Composable
fun HistorySection(
    sectionIcon: ImageVector = Icons.Filled.History,
    heading: String,
    buttonOnClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 25.dp, top = 20.dp, bottom = 0.dp, end = 10.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = sectionIcon,
                contentDescription = "History",
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                text = heading,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = DarkGrey
            )


        }

        Row(
            modifier = Modifier
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "View more")
            IconButton(onClick = buttonOnClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Localized description"
                )
            }
        }


    }

}

@Composable
fun AllJobCardListItem(
    jobCardName: String, closedDate: LocalDateTime?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            //.background(Blue50)
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawLine(
                    color = LightGrey,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = jobCardName,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Closed: ${
                if (closedDate != null) {
                    "${closedDate.dayOfWeek} ${closedDate.month} ${closedDate.hour}:${closedDate.minute}"
                } else {
                    "null"
                }
            } ",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LoadingStateColumn(
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = BlueA700,
                trackColor = Color.Transparent
            )
            Text(text = title)
        }
    }

}

@Composable
fun ErrorStateColumn(
    title: String,
    buttonOnClick: () -> Unit,
    buttonText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = title, color = Color.DarkGray)
            Button(onClick = buttonOnClick) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun IdleStateColumn(
    title: String,
    buttonOnClick: () -> Unit,
    buttonText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = title)
            Button(onClick = buttonOnClick) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun DeleteStateError(
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        Button(onClick = onClick) {
            Text(text = "Okay")
        }
    }
}


@Composable
fun DeleteConfirmation(
    title: String,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    var showDialogState by remember {
        mutableStateOf(true)
    }
    Dialog(
        showDialog = showDialogState,
        onDismissRequest = { showDialogState = !showDialogState },
        modifier = Modifier
            .background(Color.White)
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    showDialogState = !showDialogState
                    run { onCancel }
                }) {
                    Text(text = "Cancel")
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Red)
                ) {
                    Text(text = "Delete")
                }

            }

        }
    }

}