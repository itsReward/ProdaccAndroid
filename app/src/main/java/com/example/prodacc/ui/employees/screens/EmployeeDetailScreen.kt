package com.example.prodacc.ui.employees.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.DisplayTextField
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.companyIcon
import com.example.designsystem.theme.workIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    navController: NavController,
    employeeId: String
) {
    val viewModel = EmployeeDetailsViewModel(employeeId = employeeId)
    val employee = viewModel.state.value.employee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.EditEmployee.path.replace("{employeeId}", employee.id.toString()))
                    }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ProfileAvatar(
                        initials = "${employee.employeeName.first()}${employee.employeeSurname.first()}",
                        size = 120.dp,
                        textSize = 40.sp
                    )
                }
                LargeTitleText(name = " ${employee.employeeName} ${employee.employeeSurname} ")
                DisplayTextField(
                    icon = Icons.Outlined.Star,
                    label = "Rating",
                    text = employee.rating.toString(),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardGrey)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.Start
                ) {


                    DisplayTextField(
                        icon = Icons.Outlined.Phone,
                        label = "Phone",
                        text = employee.phoneNumber,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    DisplayTextField(
                        icon = Icons.Outlined.LocationOn,
                        label = "Address",
                        text = employee.homeAddress,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )



                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardGrey)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    DisplayTextField(
                        icon = workIcon,
                        label = "Job Title",
                        text = employee.employeeRole
                    )

                    DisplayTextField(
                        icon = companyIcon,
                        label = "Department",
                        text = employee.employeeDepartment
                    )

                }

                /*Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier
                        .background(LightGrey)
                        .height(2.dp)
                        .fillMaxWidth())

                }*/

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    MediumTitleText(name = "JobCards")

                }

                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(viewModel.jobCards){ jobCards ->
                        if (jobCards != null) {
                            AllJobCardListItem(
                                jobCardName = jobCards.jobCardName,
                                closedDate = jobCards.dateAndTimeClosed,
                                onClick = { navController.navigate(Route.JobCardDetails.path.replace("{jobCardId}", jobCards.id.toString())) }

                            )
                        }
                    }
                }
            }


        }
    }
}