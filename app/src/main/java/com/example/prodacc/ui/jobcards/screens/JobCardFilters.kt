package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.prodacc.ui.jobcards.viewModels.JobCardViewModel
import com.prodacc.data.SignedInUserManager

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
    onClickWaitingForPayment: () -> Unit,
    jobCardFilterState: JobCardViewModel.JobCardsFilter,
    role: SignedInUserManager.Role
) {


    val jobCardsStatus = when (role) {
        is SignedInUserManager.Role.Technician -> {
            listOf(
                "all",
                "open",
                "approval",
                "diagnostics",
                "workInProgress",
                "testing",
                "waitingForClientApproval",
                "done",
                "onhold"
            )
        }

        else -> {
            listOf(
                "all",
                "open",
                "approval",
                "diagnostics",
                "workInProgress",
                "testing",
                "waitingForPayment",
                "done",
                "onhold"
            )
        }
    }

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
                    when (it) {
                        "all" -> {
                            onClickAll()
                        }
                        "open" -> {
                            onClickOpen()
                        }
                        "approval" -> {
                            onClickApproval()
                        }
                        "diagnostics" -> {
                            onClickDiagnostics()
                        }
                        "workInProgress" -> {
                            onClickWorkInProgress()
                        }
                        "testing" -> {
                            onClickTesting()
                        }
                        "waitingForPayment", "waitingForClientApproval" -> {
                            onClickWaitingForPayment()
                        }
                        "done" -> {
                            onClickDone()
                        }
                        "onhold" -> {
                            onClickFrozen()
                        }
                    }

                },
                label = {
                    Text(
                        when (it) {
                            "all" -> {
                                "All"
                            }
                            "open" -> {
                                "Open"
                            }
                            "approval" -> {
                                "Approval"
                            }
                            "diagnostics" -> {
                                "Diagnostics"
                            }
                            "workInProgress" -> {
                                "Work In Progress"
                            }
                            "testing" -> {
                                "Testing"
                            }
                            "waitingForPayment" -> {
                                "Waiting For Payment"
                            }
                            "waitingForClientApproval" -> {
                                "Client approval"
                            }
                            "done" -> {
                                "Done"
                            }
                            "onhold" -> {
                                "Frozen"
                            }
                            else -> {
                                ""
                            }
                        }
                    )
                },
                selected = when (it) {
                    "all" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.All
                    "open" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Open
                    "approval" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Approval
                    "diagnostics" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Diagnostics
                    "workInProgress" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.WorkInProgress
                    "testing" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.Testing
                    "waitingForPayment" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.WaitingForPayment
                    "waitingForClientApproval" -> jobCardFilterState is JobCardViewModel.JobCardsFilter.WaitingForPayment
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

        item {
            Spacer(modifier = Modifier.width(20.dp))
        }

    }


}
