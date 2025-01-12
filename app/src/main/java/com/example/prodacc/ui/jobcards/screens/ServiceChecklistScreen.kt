package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.IconButton
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.prodacc.ui.jobcards.viewModels.ServiceChecklistViewModel
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.ServiceChecklist
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ServiceChecklistSection(
    loadingState: ServiceChecklistViewModel.ServiceChecklistLoadingState,
    savingState: ServiceChecklistViewModel.SaveState,
    serviceChecklist: ServiceChecklist?,
    onRefreshChecklist: () -> Unit,
    onSaveServiceChecklist: (Map<String, String>) -> Unit,
    resetSaveState: () -> Unit,
    onClose: () -> Unit
) {
    when (loadingState) {
        is ServiceChecklistViewModel.ServiceChecklistLoadingState.Error -> {
            ErrorStateColumn(
                title = loadingState.message,
                buttonOnClick = onRefreshChecklist,
                buttonText = "Refresh"
            )
        }

        is ServiceChecklistViewModel.ServiceChecklistLoadingState.Loading -> {
            LoadingStateColumn(title = "Loading Checklist")
        }

        else -> {
            when (savingState) {
                is ServiceChecklistViewModel.SaveState.Error -> {
                    ErrorStateColumn(
                        title = savingState.message,
                        buttonOnClick = resetSaveState,
                        buttonText = "Refresh"
                    )
                }

                ServiceChecklistViewModel.SaveState.Saving -> {
                    LoadingStateColumn(title = "Saving Checklist")
                }

                else -> {
                    ServiceChecklistSectionContent(
                        existingChecklist = serviceChecklist,
                        onClose = onClose,
                        onSave = { checklist -> onSaveServiceChecklist(checklist) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceChecklistSectionContent(
    existingChecklist: ServiceChecklist?,
    onClose: () -> Unit,
    onSave: (Map<String, String>) -> Unit
) {
    val scrollState = rememberScrollState()
    val options = listOf("OK", "Rectified", "Not Authorised")

    val checklistItems = mutableMapOf<String, MutableState<String>>().apply {
        put("brakes", mutableStateOf(existingChecklist?.checklist?.get("brakes") ?: "OK"))
        put("lights", mutableStateOf(existingChecklist?.checklist?.get("lights") ?: "OK"))
        put("wipers", mutableStateOf(existingChecklist?.checklist?.get("wipers") ?: "OK"))
        put(
            "continuosBeltAndPulleys",
            mutableStateOf(existingChecklist?.checklist?.get("continuosBeltAndPulleys") ?: "OK")
        )
        put("hooters", mutableStateOf(existingChecklist?.checklist?.get("hooters") ?: "OK"))
        put("battery", mutableStateOf(existingChecklist?.checklist?.get("battery") ?: "OK"))
        put(
            "airConDustFilter",
            mutableStateOf(existingChecklist?.checklist?.get("airConDustFilter") ?: "OK")
        )
        put("rearDiff", mutableStateOf(existingChecklist?.checklist?.get("rearDiff") ?: "OK"))
        put("gearBoxOil", mutableStateOf(existingChecklist?.checklist?.get("gearBoxOil") ?: "OK"))
        put(
            "powerSteeringFluid",
            mutableStateOf(existingChecklist?.checklist?.get("powerSteeringFluid") ?: "OK")
        )
        put("coolant", mutableStateOf(existingChecklist?.checklist?.get("coolant") ?: "OK"))
        put(
            "tyrePressure",
            mutableStateOf(existingChecklist?.checklist?.get("tyrePressure") ?: "OK")
        )
        put("clock", mutableStateOf(existingChecklist?.checklist?.get("clock") ?: "OK"))
        put(
            "coolantPressureTest",
            mutableStateOf(existingChecklist?.checklist?.get("coolantPressureTest") ?: "OK")
        )
    }

    val date = remember { mutableStateOf(existingChecklist?.created ?: LocalDateTime.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${existingChecklist?.jobCardName ?: "New"} Service Checklist",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        color = Color.DarkGray
                    )

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (existingChecklist!= null){
                            Row(
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                BodyText(text = "Updated on: ")
                                FormattedTime(time = existingChecklist.created)
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                BodyText(text = "Created on: ")
                                FormattedTime(time = LocalDateTime.now())
                                BodyText(text = " by ${existingChecklist?.technicianName ?: SignedInUser.user!!.employeeName } ${SignedInUser.user!!.employeeSurname}")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Inspection Items Section
                ChecklistSection(
                    title = "Inspection Items",
                    items = checklistItems.filterKeys { it in inspectionItems },
                    options = options
                )
            }


            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                /*TextButton(onClick = onClose) {
                    Text("Cancel")
                }*/
                Button(
                    onClick = {
                        val checklistData = checklistItems.mapValues { it.value.value }
                        onSave(checklistData)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (existingChecklist==null)"Save" else "Update")
                }
            }
        }
    }
}


private val inspectionItems = setOf(
    "brakes", "lights", "wipers", "continuosBeltAndPulleys",
    "hooters", "battery", "airConDustFilter",
    "rearDiff", "gearBoxOil", "powerSteeringFluid", "coolant", "tyrePressure",
    "clock", "coolantPressureTest"
)
