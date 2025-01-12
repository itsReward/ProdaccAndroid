package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkOrange
import com.example.designsystem.theme.Green
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGreen
import com.example.designsystem.theme.LightOrange
import com.example.designsystem.theme.Red
import com.example.prodacc.ui.jobcards.viewModels.JobCardViewModel

@Composable
fun JobStatusFilters(
    onClickAll: () -> Unit,
    onClickOpen: () -> Unit,
    onClickApproval: () -> Unit,
    onClickDiagnostics: () -> Unit,
    onClickWorkInProgress: () -> Unit,
    onClickTesting: () -> Unit,
    onClickDone: () -> Unit,
    onClickFrozen: () -> Unit,
    jobCardFilterState: JobCardViewModel.JobCardsFilter
) {
    var all by remember { mutableStateOf(true) }
    var open by remember { mutableStateOf(false) }
    var approval by remember { mutableStateOf(false) }
    var diagnostics by remember { mutableStateOf(false) }
    var workInProgress by remember { mutableStateOf(false) }
    var testing by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    var frozen by remember { mutableStateOf(false) }

    val jobCardsStatus = listOf(
        "all",
        "open",
        "approval",
        "diagnostics",
        "workInProgress",
        "testing",
        "done",
        "frozen"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, bottom = 5.dp, end = 20.dp)
    ) {
        items(jobCardsStatus) {
            FilterChip(
                onClick =
                {
                    if (it == "all") {
                        onClickAll()
                    } else if (it == "open") {
                        onClickOpen()
                    } else if (it == "approval") {
                        onClickApproval()
                    } else if (it == "diagnostics") {
                        onClickDiagnostics()
                    } else if (it == "workInProgress") {
                        onClickWorkInProgress()
                    } else if (it == "testing") {
                        onClickTesting()
                    } else if (it == "done") {
                        onClickDone()
                    } else if (it == "frozen") {
                        onClickFrozen()
                    }

                },
                label = {
                    Text(
                        if (it == "all") {
                            "All"
                        } else if (it == "open") {
                            "Open"
                        } else if (it == "approval") {
                            "Approval"
                        } else if (it == "diagnostics") {
                            "Diagnostics"
                        } else if (it == "workInProgress") {
                            "Work In Progress"
                        } else if (it == "testing") {
                            "Testing"
                        } else if (it == "done") {
                            "Done"
                        } else if (it == "frozen") {
                            "Frozen"
                        } else {
                            ""
                        }
                    )
                },
                selected = all,
                leadingIcon = if (all) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = all,
                    borderColor = Grey
                )
            )

        }

        item {
            FilterChip(
                onClick = { all = !all },
                label = {
                    Text("All")
                },
                selected = all,
                leadingIcon = if (all) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = all,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { open = !open },
                label = {
                    Text("Open")
                },
                selected = open,
                leadingIcon = if (open) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = open,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { diagnostics = !diagnostics },
                label = {
                    Text("Diagnostics")
                },
                selected = diagnostics,
                leadingIcon = if (diagnostics) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightOrange,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = diagnostics,
                    borderColor = Grey
                )
            )

        }

        item {
            FilterChip(
                onClick = { approval = !approval },
                label = {
                    Text("Waiting for Approval")
                },
                selected = approval,
                leadingIcon = if (approval) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkOrange,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = approval,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { workInProgress = !workInProgress },
                label = {
                    Text("Work In Progress")
                },
                selected = workInProgress,
                leadingIcon = if (workInProgress) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkGreen,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = workInProgress,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { testing = !testing },
                label = {
                    Text("Testing")
                },
                selected = testing,
                leadingIcon = if (testing) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightGreen,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = testing,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { done = !done },
                label = {
                    Text("Done")
                },
                selected = done,
                leadingIcon = if (done) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Green,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = done,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = { frozen = !frozen },
                label = {
                    Text("Frozen")
                },
                selected = frozen,
                leadingIcon = if (frozen) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Red,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = frozen,
                    borderColor = Grey
                )
            )
        }

    }


}