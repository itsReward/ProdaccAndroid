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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.designsystem.designComponents.OptionDropdown
import com.example.prodacc.ui.jobcards.viewModels.ControlChecklistViewModel
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.dao.ControlChecklist
import com.prodacc.data.remote.dao.Employee
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun ControlChecklistSection(
    loadingState: ControlChecklistViewModel.ControlChecklistLoadingState,
    savingState: ControlChecklistViewModel.SaveState,
    controlChecklist: ControlChecklist?,
    onRefreshChecklist: () -> Unit,
    onSaveControlChecklist: (Map<String, String>) -> Unit,
    resetSaveState: () -> Unit,
    onClose: () -> Unit,
    role: SignedInUserManager.Role,
    signedInEmployee: Employee
) {
    when (loadingState) {
        is ControlChecklistViewModel.ControlChecklistLoadingState.Loading -> {
            LoadingStateColumn(title = "Loading Checklist")
        }
        is ControlChecklistViewModel.ControlChecklistLoadingState.Error -> {
            ErrorStateColumn(
                title = loadingState.message,
                buttonOnClick = onRefreshChecklist,
                buttonText = "Refresh"
            )
        }
        else -> {
            when (savingState){
                is ControlChecklistViewModel.SaveState.Error -> {
                    ErrorStateColumn(
                        title = savingState.message,
                        buttonOnClick = resetSaveState,
                        buttonText = "Refresh"
                    )
                }
                ControlChecklistViewModel.SaveState.Saving -> {
                    LoadingStateColumn(title = "Saving Checklist")
                }
                else -> {

                    if (controlChecklist == null && role != SignedInUserManager.Role.Technician && role != SignedInUserManager.Role.Admin){
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            IconButton(
                                onClick = onClose,
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                color = Color.DarkGray
                            )
                            Text(text = "Technician has not created control checklist")
                        }
                    } else {
                        ControlChecklistSectionContent(
                            existingChecklist = controlChecklist,
                            onClose = onClose,
                            onSave = { checklist -> onSaveControlChecklist(checklist) },
                            enabled = when(role){
                                SignedInUserManager.Role.Admin -> true
                                SignedInUserManager.Role.Technician -> true
                                else -> {
                                    false
                                }
                            },
                            signedInEmployee = signedInEmployee
                        )
                    }


                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlChecklistSectionContent(
    existingChecklist: ControlChecklist?,
    onClose: () -> Unit,
    onSave: (Map<String, String>) -> Unit,
    enabled: Boolean,
    signedInEmployee: Employee
) {
    val scrollState = rememberScrollState()
    val options = listOf("OK", "Not Okay", "Action Taken")
    val itemOptions = listOf("None", "Present")

    // Initialize state based on existing checklist or defaults
    val checklistItems = remember {
        mutableStateMapOf<String, MutableState<String>>().apply {
            // Inspection Items
            put("oilLevel", mutableStateOf(existingChecklist?.checklist?.get("oilLevel") ?: "OK"))
            put("coolant", mutableStateOf(existingChecklist?.checklist?.get("coolant") ?: "OK"))
            put("steeringFluidLevel", mutableStateOf(existingChecklist?.checklist?.get("steeringFluidLevel") ?: "OK"))
            put("batteryCondition", mutableStateOf(existingChecklist?.checklist?.get("batteryCondition") ?: "OK"))
            put("batteryTerminals", mutableStateOf(existingChecklist?.checklist?.get("batteryTerminals") ?: "OK"))
            put("batteryWarningLight", mutableStateOf(existingChecklist?.checklist?.get("batteryWarningLight") ?: "OK"))
            put("wipers", mutableStateOf(existingChecklist?.checklist?.get("wipers") ?: "OK"))
            put("lights", mutableStateOf(existingChecklist?.checklist?.get("lights") ?: "OK"))
            put("indicators", mutableStateOf(existingChecklist?.checklist?.get("indicators") ?: "OK"))
            put("hooters", mutableStateOf(existingChecklist?.checklist?.get("hooters") ?: "OK"))
            put("tyrePressure", mutableStateOf(existingChecklist?.checklist?.get("tyrePressure") ?: "OK"))
            put("radio", mutableStateOf(existingChecklist?.checklist?.get("radio") ?: "OK"))
            put("acOperation", mutableStateOf(existingChecklist?.checklist?.get("acOperation") ?: "OK"))
            put("malfunctionOnCluster", mutableStateOf(existingChecklist?.checklist?.get("malfunctionOnCluster") ?: "OK"))
            put("engineCheckLight", mutableStateOf(existingChecklist?.checklist?.get("engineCheckLight") ?: "OK"))
            put("usedPartsStoredInBoot", mutableStateOf(existingChecklist?.checklist?.get("usedPartsStoredInBoot") ?: "OK"))
            put("wheelsTorqueing", mutableStateOf(existingChecklist?.checklist?.get("wheelsTorqueing") ?: "OK"))
            put("engineCovers", mutableStateOf(existingChecklist?.checklist?.get("engineCovers") ?: "OK"))
            put("sumpCovers", mutableStateOf(existingChecklist?.checklist?.get("sumpCovers") ?: "OK"))


            put("windowSwitch", mutableStateOf(existingChecklist?.checklist?.get("windowSwitch") ?: "OK"))
            put("belts", mutableStateOf(existingChecklist?.checklist?.get("belts") ?: "OK"))
            put("mirrors", mutableStateOf(existingChecklist?.checklist?.get("mirrors") ?: "OK"))
            put("brakes", mutableStateOf(existingChecklist?.checklist?.get("brakes") ?: "OK"))
            put("seatBelts", mutableStateOf(existingChecklist?.checklist?.get("seatBelts") ?: "OK"))
            put("airDucts", mutableStateOf(existingChecklist?.checklist?.get("airDucts") ?: "OK"))
            put("tyres", mutableStateOf(existingChecklist?.checklist?.get("tyres") ?: "OK"))
            put("anyOther", mutableStateOf(existingChecklist?.checklist?.get("anyOther") ?: "OK"))

            // Items
            put("spareWheel", mutableStateOf(existingChecklist?.checklist?.get("spareWheel") ?: "None"))
            put("spanners", mutableStateOf(existingChecklist?.checklist?.get("spanners") ?: "None"))
            put("jack", mutableStateOf(existingChecklist?.checklist?.get("jack") ?: "None"))
            put("fireExtinguisher", mutableStateOf(existingChecklist?.checklist?.get("fireExtinguisher") ?: "None"))
            put("triangle", mutableStateOf(existingChecklist?.checklist?.get("triangle") ?: "None"))
            put("radios", mutableStateOf(existingChecklist?.checklist?.get("radios") ?: "None"))
            put("cds", mutableStateOf(existingChecklist?.checklist?.get("cds") ?: "None"))
            put("reflector", mutableStateOf(existingChecklist?.checklist?.get("reflector") ?: "None"))
            put("firstAidKit", mutableStateOf(existingChecklist?.checklist?.get("firstAidKit") ?: "None"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${existingChecklist?.jobCardName ?: "New"} Control Checklist",
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
                .padding(horizontal = 16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
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
                            BodyText(text = " by ${signedInEmployee.employeeName } ${signedInEmployee.employeeSurname}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Inspection Items Section
            ChecklistSection(
                title = "Inspection Items",
                items = checklistItems.filterKeys { it in inspectionItems },
                options = options
            )

            // Items Section
            ChecklistSection(
                title = "Items",
                items = checklistItems.filterKeys { it in itemsList },
                options = itemOptions
            )

            // Action Buttons
            if (enabled){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val checklistData = checklistItems.mapValues { it.value.value }
                            onSave(checklistData)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save")
                    }
                }
            }

        }
    }
}

@Composable
fun ChecklistSection(
    title: String,
    items: Map<String, MutableState<String>>,
    options: List<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.entries.take(items.size / 2).forEach { (key, state) ->
                    OptionDropdown(
                        label = key.toDisplayName(),
                        initialOption = state.value,
                        options = options
                    ).also { state.value = it }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.entries.drop(items.size / 2).forEach { (key, state) ->
                    OptionDropdown(
                        label = key.toDisplayName(),
                        initialOption = state.value,
                        options = options
                    ).also { state.value = it }
                }
            }
        }
    }
}

private fun String.toDisplayName(): String {
    return buildString {
        this@toDisplayName.forEach { char ->
            if (char.isUpperCase() && isNotEmpty()) {
                append(' ')
            }
            append(char)
        }
    }.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

private val inspectionItems = setOf(
    "oilLevel", "coolant", "steeringFluidLevel", "batteryCondition",
    "batteryTerminals", "batteryWarningLight", "wipers", "lights",
    "indicators", "hooters", "tyrePressure", "radio", "acOperation",
    "malfunctionOnCluster", "engineCheckLight", "usedPartsStoredInBoot",
    "wheelsTorqueing", "engineCovers", "sumpCovers"
)

private val itemsList = setOf(
    "spareWheel", "spanners", "jack", "fireExtinguisher", "triangle",
    "radios", "cds", "reflector", "firstAidKit", "windowSwitch",
    "belts", "mirrors", "brakes", "seatBelts", "airDucts", "tyres",
    "anyOther"
)