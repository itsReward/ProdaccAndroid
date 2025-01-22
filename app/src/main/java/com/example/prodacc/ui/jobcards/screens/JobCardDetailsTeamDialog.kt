package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.designsystem.designComponents.EmployeeListCategory
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.CardGrey
import com.example.prodacc.ui.jobcards.viewModels.JobCardTechnicianViewModel
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard
import java.util.UUID


@Composable
fun TeamDialog(
    technicians: List<Employee>,
    onDismiss: () -> Unit,
    onAddNewTechnician: (UUID) -> Unit,
    jobCard: JobCard,
    employees: List<EmployeeListCategory>,
    onUpdateServiceAdvisor: (UUID) -> Unit,
    onUpdateSupervisor: (UUID) -> Unit,
    techniciansLoadingState: JobCardTechnicianViewModel.LoadingState,
    techniciansList: List<Employee>
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
                .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .drawBehind {
                        drawLine(
                            color = CardGrey,
                            start = Offset(0F, 80f),
                            end = Offset(size.maxDimension, 80f),
                            strokeWidth = 6f
                        )
                    }
            ) {
                LargeTitleText(name = "Team")

            }

            TeamDialogCard(
                title = "Service Advisor",
                employees = employees,
                initialEmployee = jobCard.serviceAdvisorName,
                onSelectNewEmployee = onUpdateServiceAdvisor
            )
            TeamDialogCard(
                title = "Supervisor",
                employees = employees,
                initialEmployee = jobCard.supervisorName,
                onSelectNewEmployee = onUpdateSupervisor
            )

            TechnicianRow(technicians = technicians, employees = techniciansList, onAddNewTechnician = onAddNewTechnician, techniciansLoadingState = techniciansLoadingState)


            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text(text = "close")
                }
            }
        }
    }
}


@Composable
fun TeamDialogCard(
    title: String,
    employees: List<EmployeeListCategory>,
    initialEmployee: String,
    onSelectNewEmployee: (UUID) -> Unit
) {
    var dropdownMenu by remember {
        mutableStateOf(false)
    }

    Column {
        androidx.compose.material3.Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            color = Color.DarkGray
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Blue50)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = initialEmployee.split(" ")
            ProfileAvatar(initials = initials.first().first().toString() + initials.last().first())
            Text(initialEmployee)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            DropdownMenu(
                expanded = dropdownMenu,
                onDismissRequest = { dropdownMenu = false },
                modifier = Modifier.padding(10.dp)
            ) {
                employees.forEach {
                    Text(text = it.name, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                    it.items.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = "${it.employeeName} ${it.employeeSurname}",
                                color = Color.DarkGray
                            )
                        }, onClick = { onSelectNewEmployee(it.id) })
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TechnicianRow(
    technicians: List<Employee>,
    employees: List<Employee>,
    onAddNewTechnician: (UUID) -> Unit,
    techniciansLoadingState: JobCardTechnicianViewModel.LoadingState
) {
    var dropdownMenu by remember {
        mutableStateOf(false)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediumTitleText("Technicians: ")

            when(SignedInUser.role){
                SignedInUser.Role.Technician -> {}
                else -> {
                    TextButton(onClick = { dropdownMenu = true }) {
                        Text(text = "Add")
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            DropdownMenu(
                expanded = dropdownMenu,
                onDismissRequest = { dropdownMenu = false },
                modifier = Modifier.padding(10.dp).fillMaxWidth()
            ) {
                employees.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${it.employeeName} ${it.employeeSurname}",
                                color = Color.DarkGray
                            )
                        },
                        onClick = {
                            onAddNewTechnician(it.id)
                            dropdownMenu = false
                        }
                    )
                }
            }
        }

        when (techniciansLoadingState) {
            is JobCardTechnicianViewModel.LoadingState.Loading -> {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.Center
                ){
                    LoadingStateColumn(title = "Loading Technicians")
                }

            }
            is JobCardTechnicianViewModel.LoadingState.Error -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = techniciansLoadingState.message)
                }
            }
            else -> {
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    //maxItemsInEachRow = 4,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (technicians.isNotEmpty()){
                        technicians.forEach {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(50.dp)
                                    )
                                    .background(Blue50)
                                    .padding(
                                        start = 10.dp,
                                        end = 30.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    )
                                ,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                ProfileAvatar(initials = it.employeeName.first().toString() + it.employeeSurname.first().toString())
                                Text(text = it.employeeName, color = Color.DarkGray)
                            }

                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(text = "Add Technicians")
                        }

                    }

                }
            }
        }

    }

}