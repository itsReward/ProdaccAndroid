package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.IconButton
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.bookmarkFull
import com.example.designsystem.theme.bookmarkOutline
import com.example.designsystem.theme.chat
import com.example.designsystem.theme.people
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel
import com.prodacc.data.SignedInUserManager


@Composable
fun TopBar(
    jobCardName: String,
    priority: Boolean,
    navController: NavController,
    onClickPeople: () -> Unit,
    onClickDelete: () -> Unit,
    onClickPriority: () -> Unit,
    saveState: JobCardDetailsViewModel.SaveState,
    showButtons: Boolean,
    jobId: String,
    comments: Boolean,
    role: SignedInUserManager.Role
) {
    var optionDropDown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        WebSocketStateIndicator(modifier = Modifier)
        Row(
            modifier = Modifier

                .background(Color.White)
                .wrapContentSize()
                .fillMaxWidth()
                .padding(top = 20.dp)
                .drawBehind {
                    drawLine(
                        color = Color.LightGray,
                        strokeWidth = 2f,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height)
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = { navController.navigateUp() },
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    color = DarkGrey
                )
                MediumTitleText(
                    name = jobCardName, color = DarkGrey
                )
            }

            if (showButtons) {
                Row(
                    modifier = Modifier
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    IconButton(
                        onClick = onClickPriority,
                        icon = if (priority) bookmarkFull else bookmarkOutline,
                        color = DarkGrey,
                        enabled = when (role) {
                            SignedInUserManager.Role.Supervisor -> false
                            SignedInUserManager.Role.Technician -> false
                            else -> {
                                true
                            }
                        }
                    )

                    IconButton(
                        onClick = onClickPeople, icon = people, color = DarkGrey
                    )

                    androidx.compose.material3.IconButton(onClick = {
                        optionDropDown = !optionDropDown
                    }) {
                        Row {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                            if (comments){
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Orange)
                                        .size(8.dp)
                                )
                            }
                        }

                    }

                    DropdownMenu(
                        expanded = optionDropDown,
                        onDismissRequest = { optionDropDown = !optionDropDown },
                        modifier = Modifier
                            .background(Color.White)
                            .widthIn(min = 200.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ){
                                    Text(text = "Comments")
                                    if(comments){
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Orange)
                                                .padding(horizontal = 5.dp)
                                        ){
                                            Text(text = "view", color = Color.White)
                                        }
                                    }
                                }

                            },
                            onClick = {
                                navController.navigate(
                                    Route.Comments.path.replace(
                                        "{jobId}",
                                        jobId
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = chat,
                                    contentDescription = "Comments",
                                    tint = Color.LightGray
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = "Products") },
                            onClick = { /*TODO*/ },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Products Used", tint = Color.LightGray
                                )
                            }
                        )

                        when (role) {
                            SignedInUserManager.Role.Supervisor -> {}
                            SignedInUserManager.Role.Technician -> {}
                            SignedInUserManager.Role.ServiceAdvisor -> {}
                            else -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "Delete JobCard")
                                    },
                                    onClick = onClickDelete,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete", tint = Color.LightGray
                                        )
                                    }
                                )

                            }
                        }
                    }

                }
            }


        }

        when (saveState) {
            is JobCardDetailsViewModel.SaveState.Saving -> {
                LinearProgressIndicator(
                    color = BlueA700,
                    trackColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {}
        }
    }

}