package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.prodacc.ui.jobcards.viewModels.CommentsViewModel
import com.example.prodacc.ui.jobcards.viewModels.CommentsViewModelFactory

@Composable
fun CommentsScreen(
    navController: NavController,
    jobId : String,
    commentsViewModel: CommentsViewModel = viewModel(
        factory = CommentsViewModelFactory(jobId)
    )
) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ){

    }
}