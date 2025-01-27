package com.example.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val BlueA700 = Color(0xFF2962FF)
val BlueA60 = Color(0xFF5D88FF)
val Blue50 = Color(0xFFE3F2FD)
val ElectricBlue = Color(0xFFB3B3B3)
val DarkBlue = Color(0xFF6E8BD8)

val BabyPowder = Color(0xFFFDFFFC)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val DarkGrey = Color(0xFF333333)
val Grey = Color(0xFF818181)
val LightGrey = Color(0xFFE6E6E6)
val CardGrey = Color(0xFFF3F3F3)
val LightCardGrey = Color(0xFFFAFAFA)
val Orange = Color(0xFFFFA500)
val LightOrange = Color(0xFFFFD700)
val DarkOrange = Color(0xFFF57C00)

val Red = Color(0xFFFF0000)
val LightRed = Color(0xFFFFC3C3)
val DarkRed = Color(0xFF8B0000)


val Green = Color(0xFF00FF00)
val LightGreen = Color(0xFF90EE90)
val DarkGreen = Color(0xFF008000)


fun generateRandomColor(): Color {
    val random = Random.Default
    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    return Color(red, green, blue, 255)
}