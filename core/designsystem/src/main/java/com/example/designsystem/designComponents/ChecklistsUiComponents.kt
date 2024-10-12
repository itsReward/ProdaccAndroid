package com.example.designsystem.designComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun StateChecklist(
    jobCardName: String,
    onClose: () -> Unit
) {

    val scrollState = rememberScrollState()

    val options = listOf("OK", "MISSING", "FAULTY")
    val fuelLevelOptions = listOf("Full", "3 Quarters", "Half", "Quarter", "Below Quarter", "Empty")

    // Mileage and Fuel Level
    val millageIn = remember { mutableStateOf("") }
    val millageOut = remember { mutableStateOf("") }
    val fuelLevelIn = remember { mutableStateOf(fuelLevelOptions[0]) }
    val fuelLevelOut = remember { mutableStateOf(fuelLevelOptions[0]) }

    // Tools
    val wheelSpanner = remember { mutableStateOf(options[0]) }
    val jack = remember { mutableStateOf(options[0]) }
    val triangle = remember { mutableStateOf(options[0]) }
    val firstAidKit = remember { mutableStateOf(options[0]) }
    val spanners = remember { mutableStateOf(options[0]) }
    val fireExtinguisher = remember { mutableStateOf(options[0]) }

    // Wheels and Tires
    val wheelStuds = remember { mutableStateOf(options[0]) }
    val spareWheel = remember { mutableStateOf(options[0]) }
    val size = remember { mutableStateOf("") }
    val rightFrontWheel = remember { mutableStateOf(options[0]) }
    val leftFrontWheel = remember { mutableStateOf(options[0]) }
    val rightRearWheel = remember { mutableStateOf(options[0]) }
    val leftRearWheel = remember { mutableStateOf(options[0]) }
    val lockNuts = remember { mutableStateOf(options[0]) }
    val hubCaps = remember { mutableStateOf(options[0]) }

    // Audio Equipment
    val radioTape = remember { mutableStateOf(options[0]) }
    val make = remember { mutableStateOf("") }
    val cd = remember { mutableStateOf(options[0]) }
    val cdShuttle = remember { mutableStateOf(options[0]) }
    val modulator = remember { mutableStateOf(options[0]) }
    val usb = remember { mutableStateOf(options[0]) }

    // Lights
    val rightFrontLight = remember { mutableStateOf(options[0]) }
    val leftFrontLight = remember { mutableStateOf(options[0]) }
    val interiorLight = remember { mutableStateOf(options[0]) }
    val fogLight = remember { mutableStateOf(options[0]) }
    val rightRearLight = remember { mutableStateOf(options[0]) }
    val leftRearLight = remember { mutableStateOf(options[0]) }
    val bootLight = remember { mutableStateOf(options[0]) }
    val bonnetLight = remember { mutableStateOf(options[0]) }

    // Additional
    val bootHandle = remember { mutableStateOf(options[0]) }
    val bootMat = remember { mutableStateOf(options[0]) }
    val sunRoof = remember { mutableStateOf(options[0]) }
    val engineCovers = remember { mutableStateOf(options[0]) }
    val airDucts = remember { mutableStateOf(options[0]) }

    // Roller Blinds
    val rearWindScreen = remember { mutableStateOf(options[0]) }
    val boot = remember { mutableStateOf(options[0]) }
    val engineCover = remember { mutableStateOf(options[0]) }
    val bonnetLiner = remember { mutableStateOf(options[0]) }
    val rightFrontDoorTrim = remember { mutableStateOf(options[0]) }
    val leftFrontDoorTrim = remember { mutableStateOf(options[0]) }
    val rightRearDoorTrim = remember { mutableStateOf(options[0]) }
    val leftRearDoorTrim = remember { mutableStateOf(options[0]) }
    val rightFrontSunVisor = remember { mutableStateOf(options[0]) }
    val leftFrontSunVisor = remember { mutableStateOf(options[0]) }
    val overMats = remember { mutableStateOf(options[0]) }
    val gloveCompartment = remember { mutableStateOf(options[0]) }
    val mudflaps = remember { mutableStateOf(options[0]) }

    // Glass
    val windscreen = remember { mutableStateOf(options[0]) }
    val rearScreen = remember { mutableStateOf(options[0]) }
    val rightFrontGlass = remember { mutableStateOf(options[0]) }
    val leftFrontGlass = remember { mutableStateOf(options[0]) }
    val rightRearGlass = remember { mutableStateOf(options[0]) }
    val leftRearGlass = remember { mutableStateOf(options[0]) }
    val interiorMirror = remember { mutableStateOf(options[0]) }
    val rightDoorMirror = remember { mutableStateOf(options[0]) }
    val leftDoorMirror = remember { mutableStateOf(options[0]) }
    val wipers = remember { mutableStateOf(options[0]) }

    // Interior
    val centralLocking = remember { mutableStateOf(options[0]) }
    val dashBoard = remember { mutableStateOf(options[0]) }
    val instrumentCluster = remember { mutableStateOf(options[0]) }
    val windowWinders = remember { mutableStateOf(options[0]) }
    val cigaretteLighters = remember { mutableStateOf(options[0]) }
    val mats = remember { mutableStateOf(options[0]) }
    val ownersManual = remember { mutableStateOf(options[0]) }

    // Star
    val bootStar = remember { mutableStateOf(options[0]) }
    val bonnetStar = remember { mutableStateOf(options[0]) }
    val grillStar = remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LargeTitleText("$jobCardName State Checklist")
            IconButton(onClick = onClose, icon = Icons.Default.Close, color = Color.DarkGray)
        }


        // Mileage and Fuel Level Section
        MediumTitleText("Mileage and Fuel Level")
        OptionDropdown(
            label = "Millage In",
            initialOption = millageIn.value,
            options = listOf("Enter Mileage")
        ) { newValue -> millageIn.value }
        OptionDropdown(
            label = "Millage Out",
            initialOption = millageOut.value,
            options = listOf("Enter Mileage")
        ) { newValue -> millageOut.value }
        OptionDropdown(
            label = "Fuel Level In",
            initialOption = fuelLevelIn.value,
            options = fuelLevelOptions
        ) { newValue -> fuelLevelIn.value }
        OptionDropdown(
            label = "Fuel Level Out",
            initialOption = fuelLevelOut.value,
            options = fuelLevelOptions
        ) { newValue -> fuelLevelOut.value }

        // Tools Section
        MediumTitleText("Tools")
        OptionDropdown(
            label = "Wheel Spanner",
            initialOption = wheelSpanner.value,
            options = options
        ) { newValue -> wheelSpanner.value = newValue }
        OptionDropdown(
            label = "Jack",
            initialOption = jack.value,
            options = options
        ) { newValue -> jack.value = newValue }
        OptionDropdown(
            label = "Triangle",
            initialOption = triangle.value,
            options = options
        ) { newValue -> triangle.value = newValue }
        OptionDropdown(
            label = "First Aid Kit",
            initialOption = firstAidKit.value,
            options = options
        ) { newValue -> firstAidKit.value = newValue }
        OptionDropdown(
            label = "Spanners",
            initialOption = spanners.value,
            options = options
        ) { newValue -> spanners.value = newValue }
        OptionDropdown(
            label = "Fire Extinguisher",
            initialOption = fireExtinguisher.value,
            options = options
        ) { newValue -> fireExtinguisher.value = newValue }

        // Wheels and Tires Section
        MediumTitleText("Wheels and Tires")
        OptionDropdown(
            label = "Wheel Studs",
            initialOption = wheelStuds.value,
            options = options
        ) { newValue -> wheelStuds.value = newValue }
        OptionDropdown(
            label = "Spare Wheel",
            initialOption = spareWheel.value,
            options = options
        ) { newValue -> spareWheel.value = newValue }
        OptionDropdown(
            label = "Size",
            initialOption = size.value,
            options = listOf("Enter Size")
        ) { newValue -> size.value = newValue }
        OptionDropdown(
            label = "Right Front Wheel",
            initialOption = rightFrontWheel.value,
            options = options
        ) { newValue -> rightFrontWheel.value = newValue }
        OptionDropdown(
            label = "Left Front Wheel",
            initialOption = leftFrontWheel.value,
            options = options
        ) { newValue -> leftFrontWheel.value = newValue }
        OptionDropdown(
            label = "Right Rear Wheel",
            initialOption = rightRearWheel.value,
            options = options
        ) { newValue -> rightRearWheel.value = newValue }
        OptionDropdown(
            label = "Left Rear Wheel",
            initialOption = leftRearWheel.value,
            options = options
        ) { newValue -> leftRearWheel.value = newValue }
        OptionDropdown(
            label = "Lock Nuts",
            initialOption = lockNuts.value,
            options = options
        ) { newValue -> lockNuts.value = newValue }
        OptionDropdown(
            label = "Hub Caps",
            initialOption = hubCaps.value,
            options = options
        ) { newValue -> hubCaps.value = newValue }

        // Continue similarly for Audio Equipment, Lights, Additional, Roller Blinds, Glass, Interior, and Star sections.

        // Audio Equipment Section
        MediumTitleText("Audio Equipment")
        OptionDropdown(
            label = "Radio Tape",
            initialOption = radioTape.value,
            options = options
        ) { newValue -> radioTape.value = newValue }
        OptionDropdown(
            label = "Make",
            initialOption = make.value,
            options = listOf("Enter Make")
        ) { newValue -> make.value = newValue }
        OptionDropdown(
            label = "CD",
            initialOption = cd.value,
            options = options
        ) { newValue -> cd.value = newValue }
        OptionDropdown(
            label = "CD Shuttle",
            initialOption = cdShuttle.value,
            options = options
        ) { newValue -> cdShuttle.value = newValue }
        OptionDropdown(
            label = "Modulator",
            initialOption = modulator.value,
            options = options
        ) { newValue -> modulator.value = newValue }
        OptionDropdown(
            label = "USB",
            initialOption = usb.value,
            options = options
        ) { newValue -> usb.value = newValue }


        // Lights Section
        MediumTitleText("Lights")
        OptionDropdown(
            label = "Right Front Light",
            initialOption = rightFrontLight.value,
            options = options
        ) { newValue -> rightFrontLight.value = newValue }
        OptionDropdown(
            label = "Left Front Light",
            initialOption = leftFrontLight.value,
            options = options
        ) { newValue -> leftFrontLight.value = newValue }
        OptionDropdown(
            label = "Interior Light",
            initialOption = interiorLight.value,
            options = options
        ) { newValue -> interiorLight.value = newValue }
        OptionDropdown(
            label = "Fog Light",
            initialOption = fogLight.value,
            options = options
        ) { newValue -> fogLight.value = newValue }
        OptionDropdown(
            label = "Right Rear Light",
            initialOption = rightRearLight.value,
            options = options
        ) { newValue -> rightRearLight.value = newValue }
        OptionDropdown(
            label = "Left Rear Light",
            initialOption = leftRearLight.value,
            options = options
        ) { newValue -> leftRearLight.value = newValue }
        OptionDropdown(
            label = "Boot Light",
            initialOption = bootLight.value,
            options = options
        ) { newValue -> bootLight.value = newValue }
        OptionDropdown(
            label = "Bonnet Light",
            initialOption = bonnetLight.value,
            options = options
        ) { newValue -> bonnetLight.value = newValue }

        // Additional Section
        MediumTitleText("Additional")
        OptionDropdown(
            label = "Boot Handle",
            initialOption = bootHandle.value,
            options = options
        ) { newValue -> bootHandle.value = newValue }
        OptionDropdown(
            label = "Boot Mat",
            initialOption = bootMat.value,
            options = options
        ) { newValue -> bootMat.value = newValue }
        OptionDropdown(
            label = "Sun Roof",
            initialOption = sunRoof.value,
            options = options
        ) { newValue -> sunRoof.value = newValue }
        OptionDropdown(
            label = "Engine Covers",
            initialOption = engineCovers.value,
            options = options
        ) { newValue -> engineCovers.value = newValue }
        OptionDropdown(
            label = "Air Ducts",
            initialOption = airDucts.value,
            options = options
        ) { newValue -> airDucts.value = newValue }

// Roller Blinds Section
        MediumTitleText("Roller Blinds")
        OptionDropdown(
            label = "Rear Wind Screen",
            initialOption = rearWindScreen.value,
            options = options
        ) { newValue -> rearWindScreen.value = newValue }
        OptionDropdown(
            label = "Boot",
            initialOption = boot.value,
            options = options
        ) { newValue -> boot.value = newValue }
        OptionDropdown(
            label = "Engine Cover",
            initialOption = engineCover.value,
            options = options
        ) { newValue -> engineCover.value = newValue }
        OptionDropdown(
            label = "Bonnet Liner",
            initialOption = bonnetLiner.value,
            options = options
        ) { newValue -> bonnetLiner.value = newValue }
        OptionDropdown(
            label = "Right Front Door Trim",
            initialOption = rightFrontDoorTrim.value,
            options = options
        ) { newValue -> rightFrontDoorTrim.value = newValue }
        OptionDropdown(
            label = "Left Front Door Trim",
            initialOption = leftFrontDoorTrim.value,
            options = options
        ) { newValue -> leftFrontDoorTrim.value = newValue }
        OptionDropdown(
            label = "Right Rear Door Trim",
            initialOption = rightRearDoorTrim.value,
            options = options
        ) { newValue -> rightRearDoorTrim.value = newValue }
        OptionDropdown(
            label = "Left Rear Door Trim",
            initialOption = leftRearDoorTrim.value,
            options = options
        ) { newValue -> leftRearDoorTrim.value = newValue }
        OptionDropdown(
            label = "Right Front Sun Visor",
            initialOption = rightFrontSunVisor.value,
            options = options
        ) { newValue -> rightFrontSunVisor.value = newValue }
        OptionDropdown(
            label = "Left Front Sun Visor",
            initialOption = leftFrontSunVisor.value,
            options = options
        ) { newValue -> leftFrontSunVisor.value = newValue }
        OptionDropdown(
            label = "Over Mats",
            initialOption = overMats.value,
            options = options
        ) { newValue -> overMats.value = newValue }
        OptionDropdown(
            label = "Glove Compartment",
            initialOption = gloveCompartment.value,
            options = options
        ) { newValue -> gloveCompartment.value = newValue }
        OptionDropdown(
            label = "Mudflaps",
            initialOption = mudflaps.value,
            options = options
        ) { newValue -> mudflaps.value = newValue }


        // Glass Section
        MediumTitleText("Glass")
        OptionDropdown(
            label = "Windscreen",
            initialOption = windscreen.value,
            options = options
        ) { newValue -> windscreen.value = newValue }
        OptionDropdown(
            label = "Rear Screen",
            initialOption = rearScreen.value,
            options = options
        ) { newValue -> rearScreen.value = newValue }
        OptionDropdown(
            label = "Right Front Glass",
            initialOption = rightFrontGlass.value,
            options = options
        ) { newValue -> rightFrontGlass.value = newValue }
        OptionDropdown(
            label = "Left Front Glass",
            initialOption = leftFrontGlass.value,
            options = options
        ) { newValue -> leftFrontGlass.value = newValue }
        OptionDropdown(
            label = "Right Rear Glass",
            initialOption = rightRearGlass.value,
            options = options
        ) { newValue -> rightRearGlass.value = newValue }
        OptionDropdown(
            label = "Left Rear Glass",
            initialOption = leftRearGlass.value,
            options = options
        ) { newValue -> leftRearGlass.value = newValue }
        OptionDropdown(
            label = "Interior Mirror",
            initialOption = interiorMirror.value,
            options = options
        ) { newValue -> interiorMirror.value = newValue }
        OptionDropdown(
            label = "Right Door Mirror",
            initialOption = rightDoorMirror.value,
            options = options
        ) { newValue -> rightDoorMirror.value = newValue }
        OptionDropdown(
            label = "Left Door Mirror",
            initialOption = leftDoorMirror.value,
            options = options
        ) { newValue -> leftDoorMirror.value = newValue }
        OptionDropdown(
            label = "Wipers",
            initialOption = wipers.value,
            options = options
        ) { newValue -> wipers.value = newValue }


        // Interior Section
        MediumTitleText("Interior")
        OptionDropdown(
            label = "Central Locking",
            initialOption = centralLocking.value,
            options = options
        ) { newValue -> centralLocking.value = newValue }
        OptionDropdown(
            label = "Dashboard",
            initialOption = dashBoard.value,
            options = options
        ) { newValue -> dashBoard.value = newValue }
        OptionDropdown(
            label = "Instrument Cluster",
            initialOption = instrumentCluster.value,
            options = options
        ) { newValue -> instrumentCluster.value = newValue }
        OptionDropdown(
            label = "Window Winders",
            initialOption = windowWinders.value,
            options = options
        ) { newValue -> windowWinders.value = newValue }
        OptionDropdown(
            label = "Cigarette Lighters",
            initialOption = cigaretteLighters.value,
            options = options
        ) { newValue -> cigaretteLighters.value = newValue }
        OptionDropdown(
            label = "Mats",
            initialOption = mats.value,
            options = options
        ) { newValue -> mats.value = newValue }
        OptionDropdown(
            label = "Owner's Manual",
            initialOption = ownersManual.value,
            options = options
        ) { newValue -> ownersManual.value = newValue }


        // Star Section
        MediumTitleText("Star")
        OptionDropdown(
            label = "Boot Star",
            initialOption = bootStar.value,
            options = options
        ) { newValue -> bootStar.value = newValue }
        OptionDropdown(
            label = "Bonnet Star",
            initialOption = bonnetStar.value,
            options = options
        ) { newValue -> bonnetStar.value = newValue }
        OptionDropdown(
            label = "Grill Star",
            initialOption = grillStar.value,
            options = options
        ) { newValue -> grillStar.value = newValue }

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ControlChecklist(
    jobCardName: String,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    val options = listOf("OK", "Not Okay", "Action Taken")
    val itemOptions = listOf("None", "Present")
    val technician = "Prosper"

    val oilLevel = remember { mutableStateOf("OK") }
    val coolant = remember { mutableStateOf("OK") }
    val steeringFluidLevel = remember { mutableStateOf("OK") }
    val batteryCondition = remember { mutableStateOf("OK") }
    val batteryTerminals = remember { mutableStateOf("OK") }
    val batteryWarningLight = remember { mutableStateOf("OK") }
    val wipers = remember { mutableStateOf("OK") }
    val lights = remember { mutableStateOf("OK") }
    val indicators = remember { mutableStateOf("OK") }
    val hooters = remember { mutableStateOf("OK") }
    val tyrePressure = remember { mutableStateOf("OK") }
    val radio = remember { mutableStateOf("OK") }
    val acOperation = remember { mutableStateOf("OK") }
    val malfunctionOnCluster = remember { mutableStateOf("OK") }
    val engineCheckLight = remember { mutableStateOf("OK") }
    val usedPartsStoredInBoot = remember { mutableStateOf("OK") }
    val wheelsTorqueing = remember { mutableStateOf("OK") }
    val engineCovers = remember { mutableStateOf("OK") }
    val sumpCovers = remember { mutableStateOf("OK") }
    val windowSwitch = remember { mutableStateOf("OK") }
    val belts = remember { mutableStateOf("OK") }
    val mirrors = remember { mutableStateOf("OK") }
    val brakes = remember { mutableStateOf("OK") }
    val seatBelts = remember { mutableStateOf("OK") }
    val airDucts = remember { mutableStateOf("OK") }
    val tyres = remember { mutableStateOf("OK") }
    val anyOther = remember { mutableStateOf("OK") }

    val spareWheel = remember { mutableStateOf("None") }
    val spanners = remember { mutableStateOf("None") }
    val jack = remember { mutableStateOf("None") }
    val fireExtinguisher = remember { mutableStateOf("None") }
    val triangle = remember { mutableStateOf("None") }
    val radios = remember { mutableStateOf("None") }
    val cds = remember { mutableStateOf("None") }
    val reflector = remember { mutableStateOf("None") }
    val firstAidKit = remember { mutableStateOf("None") }

    val date = remember { mutableStateOf(LocalDateTime.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$jobCardName Control Checklist",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray
                    )
                },
                actions = {
                    IconButton(
                        onClick = onClose,
                        icon = Icons.Default.Close,
                        color = Color.DarkGray
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Checklist created on ${date.value.truncatedTo(ChronoUnit.MINUTES)} by $technician",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Inspection Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp), color = Color.DarkGray
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
                    OptionDropdown(
                        label = "Oil Level:",
                        initialOption = oilLevel.value,
                        options = options
                    ).also { oilLevel.value = it }
                    OptionDropdown(
                        label = "Coolant:",
                        initialOption = coolant.value,
                        options = options
                    ).also { coolant.value = it }
                    OptionDropdown(
                        label = "Steering Fluid Level:",
                        initialOption = steeringFluidLevel.value,
                        options = options
                    ).also { steeringFluidLevel.value = it }
                    OptionDropdown(
                        label = "Battery Condition:",
                        initialOption = batteryCondition.value,
                        options = options
                    ).also { batteryCondition.value = it }
                    OptionDropdown(
                        label = "Battery Terminals:",
                        initialOption = batteryTerminals.value,
                        options = options
                    ).also { batteryTerminals.value = it }
                    OptionDropdown(
                        label = "Battery Warning Light:",
                        initialOption = batteryWarningLight.value,
                        options = options
                    ).also { batteryWarningLight.value = it }
                    OptionDropdown(
                        label = "Wipers:",
                        initialOption = wipers.value,
                        options = options
                    ).also { wipers.value = it }
                    OptionDropdown(
                        label = "Lights:",
                        initialOption = lights.value,
                        options = options
                    ).also { lights.value = it }
                    OptionDropdown(
                        label = "Indicators:",
                        initialOption = indicators.value,
                        options = options
                    ).also { indicators.value = it }
                    OptionDropdown(
                        label = "Hooters:",
                        initialOption = hooters.value,
                        options = options
                    ).also { hooters.value = it }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionDropdown(
                        label = "Tyre Pressure:",
                        initialOption = tyrePressure.value,
                        options = options
                    ).also { tyrePressure.value = it }
                    OptionDropdown(
                        label = "Radio:",
                        initialOption = radio.value,
                        options = options
                    ).also { radio.value = it }
                    OptionDropdown(
                        label = "A/C Operation:",
                        initialOption = acOperation.value,
                        options = options
                    ).also { acOperation.value = it }
                    OptionDropdown(
                        label = "Malfunction On Cluster:",
                        initialOption = malfunctionOnCluster.value,
                        options = options
                    ).also { malfunctionOnCluster.value = it }
                    OptionDropdown(
                        label = "Engine Check Light:",
                        initialOption = engineCheckLight.value,
                        options = options
                    ).also { engineCheckLight.value = it }
                    OptionDropdown(
                        label = "Used Parts Stored In Boot:",
                        initialOption = usedPartsStoredInBoot.value,
                        options = options
                    ).also { usedPartsStoredInBoot.value = it }
                    OptionDropdown(
                        label = "Wheels Torqueing:",
                        initialOption = wheelsTorqueing.value,
                        options = options
                    ).also { wheelsTorqueing.value = it }
                    OptionDropdown(
                        label = "Engine Covers:",
                        initialOption = engineCovers.value,
                        options = options
                    ).also { engineCovers.value = it }
                    OptionDropdown(
                        label = "Sump Covers:",
                        initialOption = sumpCovers.value,
                        options = options
                    ).also { sumpCovers.value = it }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp), color = Color.DarkGray
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
                    OptionDropdown(
                        label = "Spare Wheel:",
                        initialOption = spareWheel.value,
                        options = itemOptions
                    ).also { spareWheel.value = it }
                    OptionDropdown(
                        label = "Spanners:",
                        initialOption = spanners.value,
                        options = itemOptions
                    ).also { spanners.value = it }
                    OptionDropdown(
                        label = "Jack:",
                        initialOption = jack.value,
                        options = itemOptions
                    ).also { jack.value = it }
                    OptionDropdown(
                        label = "Fire Extinguisher:",
                        initialOption = fireExtinguisher.value,
                        options = itemOptions
                    ).also { fireExtinguisher.value = it }
                    OptionDropdown(
                        label = "Triangle:",
                        initialOption = triangle.value,
                        options = itemOptions
                    ).also { triangle.value = it }

                    OptionDropdown(
                        label = "Radios:",
                        initialOption = radios.value,
                        options = itemOptions
                    ).also { radios.value = it }
                    OptionDropdown(
                        label = "CDs:",
                        initialOption = cds.value,
                        options = itemOptions
                    ).also { cds.value = it }
                    OptionDropdown(
                        label = "Reflector:",
                        initialOption = reflector.value,
                        options = itemOptions
                    ).also { reflector.value = it }
                    OptionDropdown(
                        label = "First Aid Kit:",
                        initialOption = firstAidKit.value,
                        options = itemOptions
                    ).also { firstAidKit.value = it }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionDropdown(
                        label = "Window Switch:",
                        initialOption = windowSwitch.value,
                        options = itemOptions
                    ).also { windowSwitch.value = it }
                    OptionDropdown(
                        label = "Belts:",
                        initialOption = belts.value,
                        options = itemOptions
                    ).also { belts.value = it }
                    OptionDropdown(
                        label = "Mirrors:",
                        initialOption = mirrors.value,
                        options = itemOptions
                    ).also { mirrors.value = it }
                    OptionDropdown(
                        label = "Brakes:",
                        initialOption = brakes.value,
                        options = itemOptions
                    ).also { brakes.value = it }
                    OptionDropdown(
                        label = "Seat Belts:",
                        initialOption = seatBelts.value,
                        options = itemOptions
                    ).also { seatBelts.value = it }
                    OptionDropdown(
                        label = "Air Ducts:",
                        initialOption = airDucts.value,
                        options = itemOptions
                    ).also { airDucts.value = it }
                    OptionDropdown(
                        label = "Tyres:",
                        initialOption = tyres.value,
                        options = itemOptions
                    ).also { tyres.value = it }
                    OptionDropdown(
                        label = "Any Other:",
                        initialOption = anyOther.value,
                        options = itemOptions
                    ).also { anyOther.value }


                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClose) {
                    Text(text = "Close", color = BlueA700)
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Save")
                }
            }
        }
    }
}
