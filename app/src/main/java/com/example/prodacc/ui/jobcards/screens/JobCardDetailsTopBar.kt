package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.IconButton
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.people
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel
import com.prodacc.data.SignedInUser


@Composable
fun TopBar(
    jobCardName: String,
    navController: NavController,
    onClickPeople: () -> Unit,
    onClickDelete: () -> Unit,
    saveState: JobCardDetailsViewModel.SaveState
) {
    Column (
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
                }
            ,
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

            Row(
                modifier = Modifier
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {


                IconButton(
                    onClick = onClickPeople, icon = people, color = DarkGrey
                )

                when(SignedInUser.role){
                    SignedInUser.Role.Supervisor -> {}
                    SignedInUser.Role.Technician -> {}
                    SignedInUser.Role.ServiceAdvisor -> {}
                    else -> {
                        IconButton(
                            onClick = onClickDelete, icon = Icons.Filled.Delete, color = DarkGrey
                        )
                    }
                }

            }


        }

        when (saveState){
            is JobCardDetailsViewModel.SaveState.Saving -> {
                LinearProgressIndicator(color = BlueA700, trackColor = Color.Transparent, modifier = Modifier.fillMaxWidth())
            }
            else -> {}
        }
    }

}