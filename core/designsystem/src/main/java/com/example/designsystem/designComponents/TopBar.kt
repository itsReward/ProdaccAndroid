package com.example.designsystem.designComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.logOutIcon
import com.example.designsystem.theme.pretendard
import com.prodacc.data.SignedInUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onSearchClick: () -> Unit, logOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {

        TopAppBar(
            title = {
                if (title != "Job Cards") Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                ) else {
                    Text(
                        text = "jobkeep",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        color = BlueA700,
                        fontSize = 30.sp
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .drawBehind {
                    when (title) {
                        "Job Cards" -> {}
                        else -> drawLine(
                            strokeWidth = 1f,
                            color = LightGrey,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height)
                        )
                    }
                },
            actions = {
                IconButton(
                    onClick = onSearchClick,
                    icon = Icons.Default.Search,
                    color = Color.DarkGray
                )
                IconButton(onClick = logOut, icon = logOutIcon, color = Color.DarkGray)


            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )


    }

}

@Composable
fun JobCardsScreenTopBar(onSearchClick: () -> Unit, logOut: () -> Unit, products: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 15.dp)
            ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var dropDown by remember { mutableStateOf(false) }

        Text(
            text = "jobkeep",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = pretendard,
            color = BlueA700,
            fontSize = 30.sp
        )
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = onSearchClick,
                icon = Icons.Default.Search,
                color = Color.DarkGray
            )
            when(SignedInUser.role){
                SignedInUser.Role.Technician -> {
                    IconButton(onClick = logOut, icon = logOutIcon, color = Color.DarkGray)
                }
                else -> {
                    if (dropDown){
                        IconButton(onClick = {dropDown =! dropDown}, icon = Icons.Default.Close, Color.DarkGray)
                    } else {
                        IconButton(onClick = {dropDown =! dropDown}, icon = Icons.Default.MoreVert, Color.DarkGray)
                    }

                }
            }

            DropdownMenu(
                expanded = dropDown,
                onDismissRequest = { dropDown != dropDown },
                modifier = Modifier.background(Color.White).widthIn(min = 200.dp),
                properties = PopupProperties(
                    dismissOnClickOutside = true
                )
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Products") },
                    onClick = products ,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Product",
                            tint = Color.LightGray
                        )
                    })
                DropdownMenuItem(
                    text = { Text(text = "LogOut") },
                    onClick = logOut,
                    leadingIcon = { Icon(logOutIcon, "logout", tint = Color.LightGray) }
                )

            }
        }




    }

}
