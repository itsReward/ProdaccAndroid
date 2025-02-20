package com.example.prodacc.ui.clients.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.IdleStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.contactDetails
import com.example.designsystem.theme.female
import com.example.designsystem.theme.male
import com.example.designsystem.theme.workIcon
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.clients.viewModels.EditClientDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientDetailScreen(
    navController: NavController,
    viewModel: EditClientDetailsViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val client = viewModel.client.collectAsState().value



    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                WebSocketStateIndicator(modifier = Modifier)
                TopAppBar(
                    title = {
                        LargeTitleText(name = "Edit Client")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Navigate Back"
                            )
                        }
                    },
                    actions = {

                        Button(onClick = { viewModel.saveClientDetails() }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                            Text(text = "Save")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        when(viewModel.loadingState.collectAsState().value){
            is EditClientDetailsViewModel.LoadingState.Error -> {
                ErrorStateColumn(
                    title = "Error",
                    buttonOnClick = { viewModel.refreshClient() },
                    buttonText = "Refresh"
                )
            }
            is EditClientDetailsViewModel.LoadingState.Idle -> {
                IdleStateColumn(
                    title = "Load Client",
                    buttonOnClick = { viewModel.refreshClient() },
                    buttonText = "Load"
                )
            }
            is EditClientDetailsViewModel.LoadingState.Loading -> {
                LoadingStateColumn(title = "Loading Clients...")
            }
            is EditClientDetailsViewModel.LoadingState.Success -> {

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
                                initials = if (client != null) "${client.clientName.first()}${client.clientSurname.first()}" else "New",
                                size = 120.dp,
                                textSize = 40.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {


                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "",
                                modifier = Modifier.padding(10.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = client?.clientName?: "",
                                    onValueChange = viewModel::updateFirstName,
                                    label = { Text(text = "First Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words
                                    )
                                )
                                OutlinedTextField(
                                    value = client?.clientSurname?: "",
                                    onValueChange = viewModel::updateSurname,
                                    label = { Text(text = "Surname") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = client?.gender?: "",
                                        onValueChange = {},
                                        label = { Text(text = "Gender") },
                                        readOnly = true
                                    )
                                    IconButton(onClick = { viewModel.toggleGender() }) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "DropDown",
                                            modifier = Modifier.size(50.dp),
                                            tint = Grey
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = viewModel.dropGenderToggle.value,
                                        onDismissRequest = { viewModel.toggleGender() },
                                        properties = PopupProperties(),
                                    ) {
                                        Text(
                                            text = "Select Gender",
                                            color = DarkGrey,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 10.dp
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(2.dp)
                                                .background(
                                                    LightGrey
                                                )
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = "male", color = DarkGrey) },
                                            onClick = {
                                                viewModel.updateGender("male")
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = male,
                                                    contentDescription = "male",
                                                    tint = Grey
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = "female", color = DarkGrey) },
                                            onClick = {
                                                viewModel.updateGender("female")
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = female,
                                                    contentDescription = "Female",
                                                    tint = Grey
                                                )
                                            }
                                        )
                                    }
                                }





                            }


                        }



                        Row {
                            Icon(
                                imageVector = contactDetails,
                                contentDescription = "Contact Details Icon",
                                modifier = Modifier.padding(10.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = client?.phone?: "",
                                    onValueChange = viewModel::updatePhone,
                                    label = { Text(text = "Phone Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Phone
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                                OutlinedTextField(
                                    value = client?.email?: "",
                                    onValueChange = viewModel::updateEmail,
                                    label = { Text(text = "Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Email
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                                OutlinedTextField(
                                    value = client?.address?: "",
                                    onValueChange = viewModel::updateAddress,
                                    label = { Text(text = "Home Address") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )


                            }
                        }

                        Row {
                            Icon(
                                imageVector = workIcon,
                                contentDescription = "Work Icon",
                                modifier = Modifier.padding(10.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = client?.jobTitle?: "",
                                    onValueChange = viewModel::updateJobTitle,
                                    label = { Text(text = "Job Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                                OutlinedTextField(
                                    value = client?.company?: "",
                                    onValueChange = viewModel::updateCompany,
                                    label = { Text(text = "Company") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}