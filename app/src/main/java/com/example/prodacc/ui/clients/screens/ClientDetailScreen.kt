package com.example.prodacc.ui.clients.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ClientVehicleRow
import com.example.designsystem.designComponents.DisplayTextField
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.companyIcon
import com.example.designsystem.theme.errorIcon
import com.example.designsystem.theme.workIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.clients.viewModels.ClientDetailsViewModel
import com.prodacc.data.SignedInUserManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClientDetailScreen(
    navController: NavController,
    viewModel: ClientDetailsViewModel = hiltViewModel()
) {
    val client = viewModel.client.collectAsState().value

    Scaffold(
        topBar = {
            Column(modifier =Modifier.statusBarsPadding()) {
                WebSocketStateIndicator(modifier = Modifier)
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

                        when (viewModel.currentUserRole.collectAsState().value){
                            is SignedInUserManager.Role.Admin -> {
                                IconButton(onClick = {
                                    navController.navigate(Route.EditClient.path.replace("{clientId}", client!!.id.toString()))
                                }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                }

                                IconButton(onClick = {viewModel.toggleDeleteClientConfirmation()}) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                            is SignedInUserManager.Role.ServiceAdvisor -> {
                                IconButton(onClick = {
                                    navController.navigate(Route.EditClient.path.replace("{clientId}", client!!.id.toString()))
                                }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                }
                            }
                            else -> {}
                        }


                    }
                )
            }

        }
    ) { innerPadding ->

        when(viewModel.loadState.collectAsState().value){
            is ClientDetailsViewModel.LoadState.Error -> {
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
                        Text(text = (viewModel.loadState.collectAsState().value as ClientDetailsViewModel.LoadState.Error).message)
                        Button(onClick = { viewModel.refreshClient() }) {
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            is ClientDetailsViewModel.LoadState.Idle -> {
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
                        Text(text = "Load Client")
                        Button(onClick = { viewModel.refreshClient() }) {
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            is ClientDetailsViewModel.LoadState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                        CircularProgressIndicator(
                            color = BlueA700,
                            trackColor = Color.Transparent
                        )
                        Text(text = "Loading Client...")
                    }
                }
            }
            is ClientDetailsViewModel.LoadState.Success -> {
                if (client != null){
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
                                    initials = "${client.clientName.first()}${client.clientSurname.first()}",
                                    size = 120.dp,
                                    textSize = 40.sp
                                )
                            }
                            LargeTitleText(name = " (${if (client.gender == "male") "Mr" else "Mrs"}) ${client.clientName} ${client.clientSurname} ")
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
                                Row(
                                    modifier = Modifier.padding(bottom = 5.dp)
                                ) {
                                    MediumTitleText(name = "Contact Details:")
                                }


                                when (viewModel.currentUserRole.collectAsState().value){
                                    SignedInUserManager.Role.Supervisor -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Not Authorised"
                                            )
                                            Text(text = "Not Authorised ")
                                        }

                                    }
                                    SignedInUserManager.Role.Technician -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Not Authorised"
                                            )
                                            Text(text = "Not Authorised ")
                                        }
                                    }
                                    else -> {
                                        DisplayTextField(
                                            icon = Icons.Outlined.Phone,
                                            label = "Phone",
                                            text = client.phone,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                        DisplayTextField(
                                            icon = Icons.Outlined.Email,
                                            label = "Email",
                                            text = client.email,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                        DisplayTextField(
                                            icon = Icons.Outlined.LocationOn,
                                            label = "Address",
                                            text = client.address,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }





                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CardGrey)
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(bottom = 5.dp)
                                ) {
                                    MediumTitleText(name = "Professional Details:")
                                }


                                when (viewModel.currentUserRole.collectAsState().value){
                                    SignedInUserManager.Role.Supervisor -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Not Authorised"
                                            )
                                            Text(text = "Not Authorised ")
                                        }
                                    }
                                    SignedInUserManager.Role.Technician -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Not Authorised"
                                            )
                                            Text(text = "Not Authorised ")
                                        }
                                    }
                                    else -> {
                                        DisplayTextField(
                                            icon = workIcon,
                                            label = "Job Title",
                                            text = client.jobTitle
                                        )

                                        DisplayTextField(
                                            icon = companyIcon,
                                            label = "Company",
                                            text = client.company
                                        )

                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                }



                            }

                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 50.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {


                            }

                            Row (
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                MediumTitleText(name = "Vehicles", modifier = Modifier.padding(start = 20.dp, bottom = 10.dp))

                                when(viewModel.currentUserRole.collectAsState().value){
                                    SignedInUserManager.Role.Supervisor -> {}
                                    SignedInUserManager.Role.Technician -> {}
                                    else -> {
                                        IconButton(onClick = {navController.navigate(Route.NewVehicle.path)}){
                                            Icon(
                                                imageVector = Icons.Default.AddCircle,
                                                contentDescription = "Add new Vehicle",
                                                tint = Color.DarkGray
                                            )
                                        }
                                    }
                                }


                            }


                            FlowRow (
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                client.vehicles.forEach {
                                    ClientVehicleRow(vehicle = it) {
                                        navController.navigate(Route.VehicleDetails.path.replace("{vehicleId}", it.id.toString()))
                                    }
                                }
                            }
                        }


                    }
                } else {
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
                            Text(text = "Client is null")
                            Button(onClick = { viewModel.refreshClient() }) {
                                Text(text = "Refresh")
                            }
                        }
                    }
                }
            }
        }

        when(viewModel.deleteState.collectAsState().value){
            is ClientDetailsViewModel.DeleteState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.refreshDeleteState() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteClient() }) {
                            Text("Try Again")
                        }
                    },
                    title = {Text(text = "Error")},
                    text = {Text(text = (viewModel.deleteState.collectAsState().value as ClientDetailsViewModel.DeleteState.Error).message)},
                    icon = { Icon(imageVector = errorIcon, contentDescription = "Error Icon", tint = DarkGrey) },
                    dismissButton = {
                        Button(onClick = { viewModel.refreshDeleteState() }){
                            Text(text = "Dismiss")
                        }
                    }
                )
            }
            is ClientDetailsViewModel.DeleteState.Idle -> {}
            is ClientDetailsViewModel.DeleteState.Loading -> {
                LoadingStateColumn(title = "Deleting Client ${client?.clientName} ${client?.clientName} and their vehicles")
            }
            is ClientDetailsViewModel.DeleteState.Success -> {
                LaunchedEffect(Unit) {
                    delay(1500L)
                    navController.navigateUp()
                }
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "Client Successfully deleted")
                }
            }
        }

        when (viewModel.deleteClientConfirmation.collectAsState().value){
            true -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetDeleteClientConfirmation() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteClient() }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.resetDeleteClientConfirmation() }) {
                            Text("Cancel")
                        }
                    },
                    title = {Text(text = "Delete Client")},
                    text = {Text(text = "Are you sure you want to delete this client?")},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Client",
                            tint = DarkGrey
                        )
                    }

                )
            }
            false -> {}
        }



    }
    
}
