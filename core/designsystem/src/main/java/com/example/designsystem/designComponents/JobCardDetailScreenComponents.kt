package com.example.designsystem.designComponents

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.BlueA700
import com.prodacc.data.remote.dao.JobCardStatus
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StepIndicator(
    jobCardStatuses: List<JobCardStatus>
) {
    var expanded by remember { mutableStateOf(false) }

    val currentStep = jobCardStatuses.size - 1
    val maxSteps = 6


    AnimatedVisibility(
        visible = expanded,
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
            ) {
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
                                        text = "${jobCardStatuses[index].status} : ${jobCardStatuses[index].createdAt.hour}:" + "${jobCardStatuses[index].createdAt.minute}  - " + "${jobCardStatuses[index].createdAt.dayOfMonth} " + "${jobCardStatuses[index].createdAt.month} " + "${jobCardStatuses[index].createdAt.year}"
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
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = !expanded }) {
            Spacer(modifier = Modifier.width(20.dp))

            Icon(
                Icons.Default.Garage, "", Modifier.size(25.dp), BlueA700
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
            MediumTitleText(
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

@OptIn(ExperimentalMaterial3Api::class)
object SelectableDates : SelectableDates {
    // Blocks Sunday and from being selected.
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dayOfWeek =
                Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate().dayOfWeek
            dayOfWeek != DayOfWeek.SUNDAY
        } else {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = utcTimeMillis
            calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY && calendar[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY
        }
    }

    // Allow selecting dates from year 2024 forward.
    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerTextField(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime) -> Unit,
    label: String,
    modifier: Modifier = Modifier
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
        OutlinedTextField(value = value?.format(formatter) ?: "",
            onValueChange = { },
            label = { Text(label, color = Color.DarkGray) },
            readOnly = true,
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date and time",
                    modifier = Modifier.clickable { showDialog = true })
            },
            modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    AlertDialog(onDismissRequest = onDismiss, title = {

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            LargeTitleText(name = "Select Date and Time")
        }

    }, text = {



        Column(
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier.height(500.dp)
            ) { page ->
                when (page) {
                    0 -> TimePickerContent(initialTime = selectedTime,
                        onTimeSelected = { selectedTime = it })

                    1 -> DatePickerContent(initialDate = selectedDate,
                        onDateSelected = { selectedDate = it })
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                MediumTitleText(name = "Swipe ${if (pagerState.currentPage == 0) "left" else "right"} to select ${if (pagerState.currentPage == 0) "date" else "time"}")
                Icon(
                    imageVector = if (pagerState.currentPage == 0) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                    contentDescription = "direction side"
                )
            }
        }
    }, confirmButton = {
        Button(onClick = { onDateTimeSelected(selectedDate, selectedTime) }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel", color = Color.DarkGray)
        }
    }, modifier = Modifier.wrapContentSize()
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

    TimePicker(
        state = timePickerState, modifier = Modifier.fillMaxSize()
    )

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
fun Timesheets(
    title: String,
    startTime: LocalTime,
    endTime: LocalTime?
){
    Row (
        modifier = Modifier.fillMaxWidth().background(Blue50).padding(horizontal = 10.dp, vertical = 5.dp).height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = title, modifier = Modifier.weight(2f), color = Color.DarkGray)
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "${startTime.hour}:${startTime.minute}", color = Color.DarkGray)
            Text(text = if (endTime != null) "${endTime.hour}:${endTime.minute}" else "not entered", color = Color.DarkGray)
        }
    }
}