package com.example.designsystem.designComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Orange
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.dao.JobCardStatus
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StepIndicator(
    jobCardStatuses: List<JobCardStatus>,
    role: SignedInUserManager.Role
) {
    var expanded by remember { mutableStateOf(false) }

    fun formatStatusText(status: String): String {
        return if ((role is SignedInUserManager.Role.Technician ||
                    role is SignedInUserManager.Role.Supervisor) &&
            status == "waiting_for_payment") {
            "waiting for client approval"
        } else {
            status
        }
    }

    val currentStep = jobCardStatuses.size - 1
    val maxSteps = jobCardStatuses.size


    AnimatedVisibility(
        visible = expanded,
        enter = slideInVertically(animationSpec = tween(durationMillis = 500)),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 500))
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
            ) {
                (0 until maxSteps).forEachIndexed { index, _ ->

                    val previousStatus = if (index > 0) jobCardStatuses[index - 1] else null
                    val currentStatus = jobCardStatuses[index]

                    val timeSpent: Long = if (previousStatus != null) {
                        Duration.between(previousStatus.createdAt, currentStatus.createdAt).toMinutes()
                    } else {
                        0L
                    }

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
                                    modifier = Modifier.padding(start = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BodyText(
                                        text = "${formatStatusText(jobCardStatuses[index].status)} :"
                                    )
                                    FormattedTime(time = jobCardStatuses[index].createdAt)

                                    DurationText(timeSpentMinutes = timeSpent, color = DarkGrey)


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
        enter = slideInVertically(animationSpec = tween(durationMillis = 500, delayMillis = 500)),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 500))
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(100.dp))
            .clickable { expanded = !expanded }
            .background(Blue50),
            horizontalArrangement = Arrangement.Start) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        when (jobCardStatuses.last().status) {
                            "opened" -> BlueA700
                            "diagnostics" -> Orange
                            "approval" -> Orange
                            "work_in_progress" -> DarkGreen
                            "testing" -> DarkGreen
                            "waiting_for_payment" -> DarkBlue
                            "done" -> DarkGreen
                            else -> Color.Red
                        }
                    )

                    .padding(start = 5.dp ,end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(
                        when (jobCardStatuses.last().status) {
                            "opened" -> 0.1f
                            "diagnostics" -> 0.2f
                            "approval" -> 0.3f
                            "work_in_progress" -> 0.5f
                            "testing" -> 0.6f
                            "waiting_for_payment" -> 0.8f
                            "done" -> 1f
                            else -> 0.1f
                        }
                    )

                , horizontalArrangement = Arrangement.Center

            ) {
                Text(text = formatStatusText(jobCardStatuses.last().status))

            }
        }

    }

}

@Composable
fun DurationText(timeSpentMinutes: Long, color: Color = Color.Black, fontWeight: FontWeight = FontWeight.Bold) {

    val duration = Duration.ofMinutes(timeSpentMinutes)

    val days = duration.toDays()
    val hours = duration.toHoursPart()
    val minutes = duration.toMinutesPart()

    val formattedDuration = buildString {
        if (days > 0) {
            append("$days day${if (days > 1) "s" else ""} ")
        }
        if (hours > 0) {
            append("$hours hr${if (hours > 1) "s" else ""} ")
        }
        if (minutes > 0 || days.toInt() == 0 && hours == 0) { // Show minutes if there are any, or if no days/hours
            append("$minutes min${if (minutes > 1) "s" else ""}")
        }
        if (this.isEmpty()) {
            append("0 min") // Handle zero duration case
        }
    }

    Text(
        text = formattedDuration,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        fontWeight = fontWeight
    )
}


@Composable
fun DateTimePickerTextField(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(value?.toLocalDate() ?: LocalDate.now()) }
    var selectedTime by remember {
        mutableStateOf(
            value?.toLocalTime() ?: java.time.LocalTime.now()
        )
    }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

    Column(modifier = modifier) {
        TextField(value = value?.format(formatter) ?: "",
            onValueChange = { },
            leadingIcon = {
                Text(
                    "$label :", color = Color.DarkGray, modifier = Modifier.padding(start = 15.dp)
                )
            },
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date and time",
                    modifier = Modifier
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (enabled){
                        showDialog = true
                    }
                },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = CardGrey,
                focusedContainerColor = CardGrey,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledTextColor = Color.DarkGray,
                disabledContainerColor = CardGrey,
                disabledIndicatorColor = CardGrey,
                disabledTrailingIconColor = Color.DarkGray
            )
        )

        if (showDialog) {
            DateTimePickerDialog(initialDate = selectedDate,
                initialTime = selectedTime,
                onDateTimeSelected = { date, time ->
                    selectedDate = date
                    selectedTime = time
                    onValueChange(LocalDateTime.of(date, time))
                    showDialog = false
                },
                onDismiss = { showDialog = false })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateTimePickerDialog(
    initialDate: LocalDate,
    initialTime: java.time.LocalTime,
    onDateTimeSelected: (LocalDate, java.time.LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf(initialTime) }

    val pagerState = rememberPagerState(initialPage = 0) { 2 }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                MediumTitleText(name = "Select Date and Time")
            }

        },
        text = {


            Column {
                HorizontalPager(
                    state = pagerState, modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                ) { page ->
                    when (page) {
                        0 -> TimePickerContent(
                            initialTime = selectedTime,
                            onTimeSelected = { selectedTime = it })

                        1 -> DatePickerContent(
                            initialDate = selectedDate,
                            onDateSelected = { selectedDate = it })
                    }


                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage == 1){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "direction side"
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    BodyText("Swipe to select ${if (pagerState.currentPage == 0) "date" else "time"}")
                    Spacer(modifier = Modifier.width(20.dp))
                    if (pagerState.currentPage == 0){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "direction side"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDateTimeSelected(selectedDate, selectedTime) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.DarkGray)
            }
        },
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerContent(
    initialTime: java.time.LocalTime, onTimeSelected: (java.time.LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour, initialMinute = initialTime.minute
    )

    Column {
        TimePicker(
            state = timePickerState,
            modifier = Modifier.fillMaxSize(),
            colors = TimePickerDefaults.colors(
                containerColor = BlueA700,
                timeSelectorSelectedContainerColor = BlueA700,
                timeSelectorSelectedContentColor = Color.White,
                periodSelectorSelectedContentColor = Color.White,
                periodSelectorSelectedContainerColor = BlueA700,
                timeSelectorUnselectedContainerColor = Blue50,
                timeSelectorUnselectedContentColor = Color.DarkGray,
                periodSelectorUnselectedContentColor = Color.DarkGray,
                periodSelectorUnselectedContainerColor = Blue50,
                clockDialColor = Blue50
            )
        )
    }


    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeSelected(java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerContent(
    initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay().toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    )

    DatePicker(
        state = datePickerState, showModeToggle = false, modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            val selectedDate =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC).toLocalDate()
            onDateSelected(selectedDate)
        }
    }
}



@Composable
fun FormattedTimeDisplay(
    clockIn: LocalDateTime,
    clockOut: LocalDateTime?,
    currentDate: LocalDate = LocalDate.now()
) {
    val formattedText = remember(clockIn, clockOut) {
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val clockInText = when (clockIn.toLocalDate()) {
            currentDate -> clockIn.format(timeFormatter)
            else -> "${clockIn.format(dateFormatter)} ${clockIn.format(timeFormatter)}"
        }

        if (clockOut != null) {
            val clockOutText = when (clockOut.toLocalDate()) {
                currentDate -> clockOut.format(timeFormatter)
                else -> "${clockOut.format(dateFormatter)} ${clockOut.format(timeFormatter)}"
            }

            // Calculate duration
            val duration = Duration.between(clockIn, clockOut)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60

            val durationText = when {
                hours > 0 -> "${hours}h ${minutes}m"
                else -> "${minutes}m"
            }

            "$clockInText - $clockOutText : $durationText"
        } else {
            "$clockInText - Clock Out"
        }
    }

    Text(
        text = formattedText,
        color = if (clockOut != null) Color.DarkGray else Orange,
        fontWeight = if (clockOut != null) FontWeight.Normal else FontWeight.Bold
    )
}

@Composable
fun FormattedTime(
    modifier: Modifier = Modifier,
    color: Color = Color.DarkGray,
    time: LocalDateTime,
    style: TextStyle = MaterialTheme.typography.bodyLarge
){

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val timeDisplay = when (time.toLocalDate()) {
        LocalDate.now() -> time.format(timeFormatter)
        else -> "${time.format(dateFormatter)} ${time.format(timeFormatter)}"
    }

    BodyText(
        text = timeDisplay,
        color = color,
        modifier = modifier,
        style = style
    )
}




