package com.example.prodacc.ui.jobcards.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.BodyText
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.White
import com.example.designsystem.theme.send
import com.example.prodacc.ui.jobcards.viewModels.CommentsViewModel
import com.prodacc.data.remote.dao.JobCardComment
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    navController: NavController,
    commentsViewModel: CommentsViewModel = hiltViewModel()
) {
    val comments = commentsViewModel.comments.collectAsState().value
    val jobCardName = if (comments.isNotEmpty()) comments.first().jobCardName else ""
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Handle IME padding
            .navigationBarsPadding() // Handle navigation bar padding
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$jobCardName Comments",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.White,
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = commentsViewModel.comment.collectAsState().value,
                        onValueChange = commentsViewModel::updateComment,
                        placeholder = { Text(text = "   Comment") },
                        shape = RoundedCornerShape(60f),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGrey,
                            unfocusedContainerColor = LightGrey,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { commentsViewModel.createComment() },
                                colors = IconButtonColors(
                                    containerColor = BlueA700,
                                    contentColor = Color.White,
                                    disabledContainerColor = Grey,
                                    disabledContentColor = White
                                ),
                                enabled = commentsViewModel.comment.collectAsState().value != ""
                            ) {
                                Icon(imageVector = send, contentDescription = "send button")
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = lazyListState
            ) {
                itemsIndexed(comments){ index, item ->
                    CommentBubble(
                        comment = item,
                        onDeleteClick = commentsViewModel::deleteComment,
                        showAvatar = index!=0 && comments[index-1].employeeId == item.employeeId
                    )
                }

            }
        }
    }
}

@Composable
fun CommentBubble(
    comment: JobCardComment,
    onDeleteClick: (UUID) -> Unit,
    showAvatar: Boolean = true
) {
    var isLongPressed by remember { mutableStateOf(false) }
    val initials = comment.employeeName.split(" ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isLongPressed) CardGrey else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 3.dp)

    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!showAvatar){
                ProfileAvatar(
                    initials = "${initials[0].first()} ${initials[1].first()}",
                    size = 30.dp
                )
            } else {
                Box(modifier = Modifier.size(30.dp))
            }


            //Bubble
            Column(
                modifier = Modifier
                    .widthIn(min = 150.dp)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 10.dp,
                            topStart = 0.dp,
                            bottomEnd = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .background(CardGrey)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                isLongPressed = true
                            },
                            onTap = {
                                isLongPressed = false
                            }
                        )
                    }
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 6.dp),

                ) {
                BodyText(
                    text = comment.employeeName,
                    style = MaterialTheme.typography.labelLarge,
                    color = Grey
                )
                Text(
                    text = comment.comment,
                )
                FormattedTime(
                    time = comment.commentDate,
                    color = Grey,
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelLarge
                )


            }


        }

        // Delete button overlay
        AnimatedVisibility(
            visible = isLongPressed,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.End)
                .padding(20.dp)
        ) {
            IconButton(
                onClick = {
                    onDeleteClick(comment.commentId)
                    isLongPressed = false
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete comment",
                    tint = Color.White
                )
            }
        }
    }
}

