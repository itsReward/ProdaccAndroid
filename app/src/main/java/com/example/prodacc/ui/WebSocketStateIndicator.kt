package com.example.prodacc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.Orange
import com.prodacc.data.remote.WebSocketInstance
import kotlinx.coroutines.delay

@Composable
fun WebSocketStateIndicator(
    modifier: Modifier = Modifier,
    viewModel: WebSocketViewModel = hiltViewModel()
) {
    val webSocketState by viewModel.webSocketState.collectAsState()
    val webSocketIndicator by viewModel.webSocketIndicator.collectAsState()

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        when(webSocketState) {
            is WebSocketInstance.WebSocketState.Connected -> {
                LaunchedEffect(Unit) {
                    delay(1500)
                    viewModel.websocketIndicatorToggle(false)
                }
                AnimatedVisibility(
                    visible = webSocketIndicator,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(BlueA700)
                            .padding(horizontal = 50.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "online",
                            color = Blue50,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding()
                        )
                    }
                }
            }
            is WebSocketInstance.WebSocketState.Disconnected -> {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(Orange)
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Disconnected, ",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = "click to reconnect",
                        color = Blue50,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .clickable(onClick = { viewModel.reconnectWebSocket() }),
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        fontSize = 16.sp
                    )
                }
            }
            is WebSocketInstance.WebSocketState.Error -> {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(Orange)
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connection Error, ",
                        color = Blue50,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = "refresh",
                        color = Blue50,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .clickable(onClick = { viewModel.reconnectWebSocket() }),
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        fontSize = 16.sp
                    )
                }
            }
            is WebSocketInstance.WebSocketState.Reconnecting -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        strokeCap = StrokeCap.Square,
                        color = BlueA700,
                        modifier = Modifier.fillMaxWidth()
                    )
                    var visibility by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(1)
                        visibility = true
                    }
                    AnimatedVisibility(
                        visible = visibility,
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        Text(
                            text = "Reconnecting, ",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                    }
                }
            }
        }
    }
}