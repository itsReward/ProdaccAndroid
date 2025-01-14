package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.example.designsystem.designComponents.BodyTextItalic
import com.example.designsystem.designComponents.DateTimePickerTextField
import com.example.designsystem.designComponents.FormattedTimeDisplay
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.BlueA700
import com.example.prodacc.ui.jobcards.viewModels.TimeSheetsViewModel
import com.prodacc.data.remote.dao.Timesheet
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun Timesheets(
    timeSheet: Timesheet?,
    onClick: (Timesheet) -> Unit,
) {
    val currentDateTime = LocalDate.now()

    if (timeSheet != null){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onClick(timeSheet) })
                .background(Blue50)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(initials = timeSheet.technicianName.first().toString())
            Text(
                text = timeSheet.sheetTitle,
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 5.dp),
                color = Color.DarkGray
            )
            FormattedTimeDisplay(
                clockIn = timeSheet.clockInDateAndTime,
                clockOut = timeSheet.clockOutDateAndTime,
                currentDate = currentDateTime
            )
        }


    } else{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blue50)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = "Failed to load timesheet")
        }
    }


}

@Composable
fun TimeSheetDialog(
    timeSheet: Timesheet,
    timeSheetDialogVisibility: Boolean,
    isTimesheetEdited: Boolean,
    onDialogDismiss: () -> Unit,
    clockOut: (LocalDateTime) -> Unit,
    onReportChange: (String) -> Unit,
    onSave: () -> Unit
){
    AnimatedVisibility(visible = timeSheetDialogVisibility) {
        Dialog(onDismissRequest = onDialogDismiss) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(15.dp)
                    )
                    .background(Color.White)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MediumTitleText(name = timeSheet.sheetTitle)
                    BodyTextItalic(text = "by ${timeSheet.technicianName}")
                }

                OutlinedTextField(value = timeSheet.sheetTitle,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Title", color = Color.DarkGray) })
                DateTimePickerTextField(
                    value = timeSheet.clockInDateAndTime, onValueChange = {}, label = "Start"
                )

                DateTimePickerTextField(
                    value = timeSheet.clockOutDateAndTime, onValueChange = clockOut, label = "Stop"
                )
                OutlinedTextField(
                    value = timeSheet.report,
                    onValueChange = { onReportChange(it) },
                    label = { Text(text = "Timesheet report") },
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (isTimesheetEdited){
                        Button(onClick = onSave) {
                            Text(text = "Save")
                        }
                    } else {
                        Button(onClick = onDialogDismiss) {
                            Text(text = "Dismiss")
                        }
                    }

                }
            }
        }

    }
}