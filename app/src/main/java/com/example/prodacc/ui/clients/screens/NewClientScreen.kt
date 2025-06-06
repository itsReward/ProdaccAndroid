package com.example.prodacc.ui.clients.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
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
import com.example.prodacc.ui.clients.viewModels.NewClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClientScreen(
    navController: NavController,
    viewModel: NewClientViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    val scroll = rememberScrollState()
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(visible = true, enter = slideInHorizontally()) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    WebSocketStateIndicator()
                    TopAppBar(
                        title = {
                            LargeTitleText(name = "New Client")
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

                            Button(onClick = {
                                viewModel.saveClient()
                            }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                                Text(text = "Save")
                            }
                        }
                    )
                }

            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(scroll)
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
                            initials = "New",
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
                                value = state.firstName,
                                onValueChange = viewModel::updateFirstName,
                                label = { Text(text = "First Name") },
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
                                value = state.secondName,
                                onValueChange = viewModel::updateSurname,
                                label = { Text(text = "Surname") },
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.gender,
                                    onValueChange = {},
                                    label = { Text(text = "Gender") },
                                    readOnly = true
                                )
                                IconButton(onClick = { viewModel.onGenderToggle() }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "DropDown",
                                        modifier = Modifier.size(50.dp),
                                        tint = Grey
                                    )
                                }
                                DropdownMenu(
                                    expanded = viewModel.genderDropdown.value,
                                    onDismissRequest = { viewModel.onGenderToggle() },
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
                                    for (i in viewModel.genderOptions) {
                                        DropdownMenuItem(
                                            text = { Text(text = i, color = DarkGrey) },
                                            onClick = {
                                                viewModel.updateGender(i)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = if (i == "male") male else female,
                                                    contentDescription = "gender",
                                                    tint = Grey
                                                )
                                            }
                                        )
                                    }
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
                                value = state.phoneNumber,
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
                                value = state.email,
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
                                value = state.address,
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
                                value = state.jobTitle,
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
                                value = state.company,
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

            when (viewModel.saveState.collectAsState().value) {
                is NewClientViewModel.SaveState.Error -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.resetSaveState() },
                        confirmButton = {
                            Button(onClick = { viewModel.saveClient() }) {
                                Text(text = "Try Again")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.resetSaveState() }) {
                                Text(text = "Cancel")
                            }
                        },
                        title = { Text(text = "Error") },
                        text = { Text(text = (viewModel.saveState.collectAsState().value as NewClientViewModel.SaveState.Error).message) }
                    )
                }

                is NewClientViewModel.SaveState.Idle -> {}

                is NewClientViewModel.SaveState.Loading -> {
                    Dialog(onDismissRequest = {}) {
                        LoadingStateColumn(title = "Saving New Client")
                    }
                }

                is NewClientViewModel.SaveState.Success -> {
                    AlertDialog(
                        onDismissRequest = { navController.navigateUp() },
                        confirmButton = {
                            Button(onClick = { navController.navigateUp() }) {
                                Text(text = "Close")
                            }
                        },
                        title = { Text(text = "Saved Successful") },
                        text = { Text(text = "Your changes have been saved") }
                    )
                }

            }

        }
    }


}