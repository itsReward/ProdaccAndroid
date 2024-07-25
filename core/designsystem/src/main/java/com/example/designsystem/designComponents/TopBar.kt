package com.example.designsystem.designComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.Grey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        TopAppBar(
            title = {
                if (title != "Job Cards") Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                ) else {
                    Image(
                        painter = painterResource(com.example.designsystem.R.drawable.prodacc_word),
                        contentDescription = "logo",
                        modifier = Modifier.width(130.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(BlueA700)
                    )
                }
            },
            modifier = Modifier.padding(10.dp),
            actions = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    modifier = Modifier.size(30.dp)
                )
            },
            //navigationIcon = {Icon(imageVector = Icons.Default.Menu, contentDescription = "Drawer", modifier = Modifier.size(30.dp))}

        )
        SearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {},
            active = true,
            onActiveChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search",
                    tint = Grey
                )
            },
            placeholder = { Text("Search") },
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .wrapContentSize()
                .height(60.dp)

        ) {}

    }

}