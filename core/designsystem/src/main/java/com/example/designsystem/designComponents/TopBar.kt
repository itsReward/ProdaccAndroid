package com.example.designsystem.designComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.R
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.logOutIcon
import com.example.designsystem.theme.pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onSearchClick: () -> Unit, logOut: () -> Unit) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            //.padding(bottom = 10.dp)
    ) {


        TopAppBar(
            title = {
                if (title != "Job Cards") Text(
                    text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.DarkGray
                ) else {
                    Text(
                        text = "jobkeep",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        color = BlueA700,
                        fontSize = 30.sp
                    )

                    /*Image(
                        painter = painterResource(com.example.designsystem.R.drawable.jobkeep_word),
                        contentDescription = "logo",
                        modifier = Modifier.width(130.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(BlueA700)
                    )*/
                }
            },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .drawBehind {
                    when (title) {
                        "Job Cards" -> {}
                        else -> drawLine(strokeWidth = 1f, color = LightGrey, start = Offset(0f, size.height), end = Offset(size.width, size.height))
                    }
                }
            ,
            actions = {
                IconButton(onClick = onSearchClick, icon = Icons.Default.Search, color = Color.DarkGray)
                IconButton(onClick = logOut, icon = logOutIcon, color = Color.DarkGray)

            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )


    }

}
