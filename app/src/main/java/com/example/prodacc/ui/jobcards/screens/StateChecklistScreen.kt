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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.IconButton
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.OptionDropdown
import com.example.designsystem.theme.DarkRed
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.Red
import com.example.prodacc.ui.jobcards.viewModels.StateChecklistViewModel
import com.prodacc.data.remote.dao.StateChecklist
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun StateChecklistSection(
    loadingState: StateChecklistViewModel.StateChecklistLoadingState,
    savingState: StateChecklistViewModel.SaveState,
    stateChecklist: StateChecklist?,
    onRefreshChecklist: () -> Unit,
    onSaveStateChecklist: (Map<String, String>) -> Unit,
    resetSaveState: () -> Unit,
    onClose: () -> Unit,
    fuelLevelIn: String,
    fuelLevelOut: String,
    onFuelLevelInChange: (String) -> Unit = {},
    onFuelLevelOutChange: (String) -> Unit = {},
    millageIn: String,
    millageOut: String,
    onMillageInChange: (String) -> Unit = {},
    onMillageOutChange: (String) -> Unit = {},

) {
    when (loadingState) {
        is StateChecklistViewModel.StateChecklistLoadingState.Error -> {
            ErrorStateColumn(
                title = loadingState.message,
                buttonOnClick = onRefreshChecklist,
                buttonText = "Refresh"
            )
        }

        is StateChecklistViewModel.StateChecklistLoadingState.Loading -> {
            LoadingStateColumn(title = "Loading Checklist")
        }

        else -> {
            when (savingState) {
                is StateChecklistViewModel.SaveState.Error -> {
                    ErrorStateColumn(
                        title = savingState.message,
                        buttonOnClick = resetSaveState,
                        buttonText = "Refresh"
                    )
                }

                StateChecklistViewModel.SaveState.Saving -> {
                    LoadingStateColumn(title = "Saving Checklist")
                }

                else -> {
                    StateChecklistSectionContent(
                        existingChecklist = stateChecklist,
                        onClose = onClose,
                        onSave = { checklist -> onSaveStateChecklist(checklist) },
                        fuelLevelIn = fuelLevelIn,
                        fuelLevelOut = fuelLevelOut,
                        onFuelLevelInChange = onFuelLevelInChange,
                        onFuelLevelOutChange = onFuelLevelOutChange,
                        millageIn = millageIn,
                        millageOut = millageOut,
                        onMillageInChange = onMillageInChange,
                        onMillageOutChange = onMillageOutChange,
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateChecklistSectionContent(
    existingChecklist: StateChecklist?,
    onClose: () -> Unit,
    onSave: (Map<String, String>) -> Unit,
    fuelLevelIn: String,
    fuelLevelOut: String,
    onFuelLevelInChange: (String) -> Unit = {},
    onFuelLevelOutChange: (String) -> Unit = {},
    millageIn: String,
    millageOut: String,
    onMillageInChange: (String) -> Unit = {},
    onMillageOutChange: (String) -> Unit = {},
) {

    val scrollState = rememberScrollState()
    val options = listOf("OK", "Missing", "Faulty")
    val fuelLevelOptions = listOf("Full", "3 Quarters", "Half", "Quarter", "Below Quarter", "Empty")

    val date = remember { mutableStateOf(existingChecklist?.created ?: LocalDateTime.now()) }


    val checklistItems = mutableMapOf<String, MutableState<String>>().apply {
        // Mileage and Fuel Level
        put("millageIn", mutableStateOf(existingChecklist?.checklist?.get("millageIn") ?: ""))
        put("millageOut", mutableStateOf(existingChecklist?.checklist?.get("millageOut") ?: ""))
        put("fuelLevelIn", mutableStateOf(existingChecklist?.checklist?.get("fuelLevelIn") ?: "------"))
        put("fuelLevelOut", mutableStateOf(existingChecklist?.checklist?.get("fuelLevelOut") ?: "------"))

        // Tools
        put("wheelSpanner", mutableStateOf(existingChecklist?.checklist?.get("wheelSpanner") ?: options[0]))
        put("jack", mutableStateOf(existingChecklist?.checklist?.get("jack") ?: options[0]))
        put("triangle", mutableStateOf(existingChecklist?.checklist?.get("triangle") ?: options[0]))
        put("firstAidKit", mutableStateOf(existingChecklist?.checklist?.get("firstAidKit") ?: options[0]))
        put("spanners", mutableStateOf(existingChecklist?.checklist?.get("spanners") ?: options[0]))
        put("fireExtinguisher", mutableStateOf(existingChecklist?.checklist?.get("fireExtinguisher") ?: options[0]))

        // Wheels and Tires
        put("wheelStuds", mutableStateOf(existingChecklist?.checklist?.get("wheelStuds") ?: options[0]))
        put("spareWheel", mutableStateOf(existingChecklist?.checklist?.get("spareWheel") ?: options[0]))
        put("size", mutableStateOf(existingChecklist?.checklist?.get("size") ?: ""))
        put("rightFrontWheel", mutableStateOf(existingChecklist?.checklist?.get("rightFrontWheel") ?: options[0]))
        put("leftFrontWheel", mutableStateOf(existingChecklist?.checklist?.get("leftFrontWheel") ?: options[0]))
        put("rightRearWheel", mutableStateOf(existingChecklist?.checklist?.get("rightRearWheel") ?: options[0]))
        put("leftRearWheel", mutableStateOf(existingChecklist?.checklist?.get("leftRearWheel") ?: options[0]))
        put("lockNuts", mutableStateOf(existingChecklist?.checklist?.get("lockNuts") ?: options[0]))
        put("hubCaps", mutableStateOf(existingChecklist?.checklist?.get("hubCaps") ?: options[0]))

        // Audio Equipment
        put("radioTape", mutableStateOf(existingChecklist?.checklist?.get("radioTape") ?: options[0]))
        put("make", mutableStateOf(existingChecklist?.checklist?.get("make") ?: ""))
        put("cd", mutableStateOf(existingChecklist?.checklist?.get("cd") ?: options[0]))
        put("cdShuttle", mutableStateOf(existingChecklist?.checklist?.get("cdShuttle") ?: options[0]))
        put("modulator", mutableStateOf(existingChecklist?.checklist?.get("modulator") ?: options[0]))
        put("usb", mutableStateOf(existingChecklist?.checklist?.get("usb") ?: options[0]))

        // Lights
        put("rightFrontLight", mutableStateOf(existingChecklist?.checklist?.get("rightFrontLight") ?: options[0]))
        put("leftFrontLight", mutableStateOf(existingChecklist?.checklist?.get("leftFrontLight") ?: options[0]))
        put("interiorLight", mutableStateOf(existingChecklist?.checklist?.get("interiorLight") ?: options[0]))
        put("fogLight", mutableStateOf(existingChecklist?.checklist?.get("fogLight") ?: options[0]))
        put("rightRearLight", mutableStateOf(existingChecklist?.checklist?.get("rightRearLight") ?: options[0]))
        put("leftRearLight", mutableStateOf(existingChecklist?.checklist?.get("leftRearLight") ?: options[0]))
        put("bootLight", mutableStateOf(existingChecklist?.checklist?.get("bootLight") ?: options[0]))
        put("bonnetLight", mutableStateOf(existingChecklist?.checklist?.get("bonnetLight") ?: options[0]))

        // Additional
        put("bootHandle", mutableStateOf(existingChecklist?.checklist?.get("bootHandle") ?: options[0]))
        put("bootMat", mutableStateOf(existingChecklist?.checklist?.get("bootMat") ?: options[0]))
        put("sunRoof", mutableStateOf(existingChecklist?.checklist?.get("sunRoof") ?: options[0]))
        put("engineCovers", mutableStateOf(existingChecklist?.checklist?.get("engineCovers") ?: options[0]))
        put("airDucts", mutableStateOf(existingChecklist?.checklist?.get("airDucts") ?: options[0]))

        // Roller Blinds
        put("rearWindScreen", mutableStateOf(existingChecklist?.checklist?.get("rearWindScreen") ?: options[0]))
        put("boot", mutableStateOf(existingChecklist?.checklist?.get("boot") ?: options[0]))
        put("engineCover", mutableStateOf(existingChecklist?.checklist?.get("engineCover") ?: options[0]))
        put("bonnetLiner", mutableStateOf(existingChecklist?.checklist?.get("bonnetLiner") ?: options[0]))
        put("rightFrontDoorTrim", mutableStateOf(existingChecklist?.checklist?.get("rightFrontDoorTrim") ?: options[0]))
        put("leftFrontDoorTrim", mutableStateOf(existingChecklist?.checklist?.get("leftFrontDoorTrim") ?: options[0]))
        put("rightRearDoorTrim", mutableStateOf(existingChecklist?.checklist?.get("rightRearDoorTrim") ?: options[0]))
        put("leftRearDoorTrim", mutableStateOf(existingChecklist?.checklist?.get("leftRearDoorTrim") ?: options[0]))
        put("rightFrontSunVisor", mutableStateOf(existingChecklist?.checklist?.get("rightFrontSunVisor") ?: options[0]))
        put("leftFrontSunVisor", mutableStateOf(existingChecklist?.checklist?.get("leftFrontSunVisor") ?: options[0]))
        put("overMats", mutableStateOf(existingChecklist?.checklist?.get("overMats") ?: options[0]))
        put("gloveCompartment", mutableStateOf(existingChecklist?.checklist?.get("gloveCompartment") ?: options[0]))
        put("mudflaps", mutableStateOf(existingChecklist?.checklist?.get("mudflaps") ?: options[0]))

        // Glass
        put("windscreen", mutableStateOf(existingChecklist?.checklist?.get("windscreen") ?: options[0]))
        put("rearScreen", mutableStateOf(existingChecklist?.checklist?.get("rearScreen") ?: options[0]))
        put("rightFrontGlass", mutableStateOf(existingChecklist?.checklist?.get("rightFrontGlass") ?: options[0]))
        put("leftFrontGlass", mutableStateOf(existingChecklist?.checklist?.get("leftFrontGlass") ?: options[0]))
        put("rightRearGlass", mutableStateOf(existingChecklist?.checklist?.get("rightRearGlass") ?: options[0]))
        put("leftRearGlass", mutableStateOf(existingChecklist?.checklist?.get("leftRearGlass") ?: options[0]))
        put("interiorMirror", mutableStateOf(existingChecklist?.checklist?.get("interiorMirror") ?: options[0]))
        put("rightDoorMirror", mutableStateOf(existingChecklist?.checklist?.get("rightDoorMirror") ?: options[0]))
        put("leftDoorMirror", mutableStateOf(existingChecklist?.checklist?.get("leftDoorMirror") ?: options[0]))
        put("wipers", mutableStateOf(existingChecklist?.checklist?.get("wipers") ?: options[0]))

        // Interior
        put("centralLocking", mutableStateOf(existingChecklist?.checklist?.get("centralLocking") ?: options[0]))
        put("dashBoard", mutableStateOf(existingChecklist?.checklist?.get("dashBoard") ?: options[0]))
        put("instrumentCluster", mutableStateOf(existingChecklist?.checklist?.get("instrumentCluster") ?: options[0]))
        put("windowWinders", mutableStateOf(existingChecklist?.checklist?.get("windowWinders") ?: options[0]))
        put("cigaretteLighters", mutableStateOf(existingChecklist?.checklist?.get("cigaretteLighters") ?: options[0]))
        put("mats", mutableStateOf(existingChecklist?.checklist?.get("mats") ?: options[0]))
        put("ownersManual", mutableStateOf(existingChecklist?.checklist?.get("ownersManual") ?: options[0]))

        // Star
        put("bootStar", mutableStateOf(existingChecklist?.checklist?.get("bootStar") ?: options[0]))
        put("bonnetStar", mutableStateOf(existingChecklist?.checklist?.get("bonnetStar") ?: options[0]))
        put("grillStar", mutableStateOf(existingChecklist?.checklist?.get("grillStar") ?: options[0]))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "${existingChecklist?.jobCardName ?: "New"} State Checklist",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.DarkGray
                        )

                    }

                },
                navigationIcon = {
                    IconButton(onClick = onClose, icon = Icons.AutoMirrored.Filled.ArrowBack, color = Color.DarkGray )

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
                .padding(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (existingChecklist!= null){
                        Row(
                            verticalAlignment = Alignment.Bottom
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
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(20.dp))



            Text(
                text = "Millage and Fuel Level",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.DarkGray
            )
            Column (
                modifier = Modifier.fillMaxWidth()
            ){
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TextField(
                        value = millageIn,
                        onValueChange = { onMillageInChange(it) },
                        leadingIcon = {
                            Text(
                                text = "Millage In:",
                                color = if (millageIn == "") Red else Color.DarkGray,
                                fontWeight = if (millageIn == "") FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(2f),
                        placeholder = { Text(text = "- - - - - -", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())},
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.End),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(
                            onDone = KeyboardActions.Default.onDone
                        )
                    )

                    Column (
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        OptionDropdown(
                            label = "Fuel Level In",
                            initialOption = fuelLevelIn,
                            options = fuelLevelOptions
                        ).also { newValue -> onFuelLevelInChange(newValue) }
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TextField(
                        value = millageOut,
                        onValueChange = { onMillageOutChange(it) },
                        leadingIcon = {
                            Text(
                                text = "Millage Out: ",
                                color = if (millageOut == "") Red else Color.DarkGray,
                                fontWeight = if (millageOut == "") FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(2f),
                        placeholder = { Text(text = "- - - - - -", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())},
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.End),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Column (
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ){

                        OptionDropdown(
                            label = "Fuel Level Out",
                            initialOption = fuelLevelOut,
                            options = fuelLevelOptions
                        ).also { newValue -> onFuelLevelOutChange(newValue) }
                    }
                }



            }


            Spacer(modifier = Modifier.height(20.dp))

            // Tools
            ChecklistSection(
                title = "Tools",
                items = checklistItems.filterKeys { it in toolsItems },
                options = options
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Wheels and Tires
            ChecklistSection(
                title = "Wheels and Tires",
                items = checklistItems.filterKeys { it in wheelsAndTiresItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Equipment
            ChecklistSection(
                title = "Audio Equipment",
                items = checklistItems.filterKeys { it in audioEquipmentItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lights
            ChecklistSection(
                title = "Lights",
                items = checklistItems.filterKeys { it in lightsItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Additional
            ChecklistSection(
                title = "Additional",
                items = checklistItems.filterKeys { it in additionalItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Roller Blinds
            ChecklistSection(
                title = "Roller Blinds",
                items = checklistItems.filterKeys { it in rollerBlindsItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Glass
            ChecklistSection(
                title = "Glass",
                items = checklistItems.filterKeys { it in glassItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Interior
            ChecklistSection(
                title = "Interior",
                items = checklistItems.filterKeys { it in interiorItems },
                options = options
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Star
            ChecklistSection(
                title = "Star",
                items = checklistItems.filterKeys { it in starItems },
                options = options
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
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





// Tools
private val toolsItems = setOf(
    "wheelSpanner", "jack", "triangle", "firstAidKit", "spanners", "fireExtinguisher"
)

// Wheels and Tires
private val wheelsAndTiresItems = setOf(
    "wheelStuds", "spareWheel", "size", "rightFrontWheel", "leftFrontWheel",
    "rightRearWheel", "leftRearWheel", "lockNuts", "hubCaps"
)

// Audio Equipment
private val audioEquipmentItems = setOf(
    "radioTape", "make", "cd", "cdShuttle", "modulator", "usb"
)

// Lights
private val lightsItems = setOf(
    "rightFrontLight", "leftFrontLight", "interiorLight", "fogLight",
    "rightRearLight", "leftRearLight", "bootLight", "bonnetLight"
)

// Additional
private val additionalItems = setOf(
    "bootHandle", "bootMat", "sunRoof", "engineCovers", "airDucts"
)

// Roller Blinds
private val rollerBlindsItems = setOf(
    "rearWindScreen", "boot", "engineCover", "bonnetLiner",
    "rightFrontDoorTrim", "leftFrontDoorTrim", "rightRearDoorTrim",
    "leftRearDoorTrim", "rightFrontSunVisor", "leftFrontSunVisor",
    "overMats", "gloveCompartment", "mudflaps"
)

// Glass
private val glassItems = setOf(
    "windscreen", "rearScreen", "rightFrontGlass", "leftFrontGlass",
    "rightRearGlass", "leftRearGlass", "interiorMirror",
    "rightDoorMirror", "leftDoorMirror", "wipers"
)

// Interior
private val interiorItems = setOf(
    "centralLocking", "dashBoard", "instrumentCluster", "windowWinders",
    "cigaretteLighters", "mats", "ownersManual"
)

// Star
private val starItems = setOf(
    "bootStar", "bonnetStar", "grillStar"
)

