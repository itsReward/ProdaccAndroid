package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.DarkOrange
import com.example.designsystem.theme.Green
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGreen
import com.example.designsystem.theme.LightGrey
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
    val jobCardsStatus = listOf(
        "all",
        "open",
        "approval",
        "diagnostics",
        "workInProgress",
        "testing",
        "done",
        "onhold"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    ) {
        item {
            Spacer(modifier = Modifier.width(20.dp))
        }

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
                    } else if (it == "onhold") {
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
                        } else if (it == "onhold") {
                            "Frozen"
                        } else {
                            ""
                        }
                    )
                },
                selected =  when (it) {
                    "all" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.All
                    "open" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Open
                    "approval" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Approval
                    "diagnostics" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Diagnostics
                    "workInProgress" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.WorkInProgress
                    "testing" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Testing
                    "done" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Done
                    "onhold" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Frozen
                    else -> false
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    disabledContainerColor = DarkGrey

                ),
                border = FilterChipDefaults.filterChipBorder(enabled = false, selected = false)
            )

        }

    }


}
