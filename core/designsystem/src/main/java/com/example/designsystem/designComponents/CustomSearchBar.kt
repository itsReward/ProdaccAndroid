package com.example.designsystem.designComponents

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier,
    placeHolder: String
) {

    CustomTextField(
        value = query,
        onValueChange = onQueryChange,
        onSearch = onSearch,
        modifier = modifier,
        placeHolder = placeHolder
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(10.dp),
    placeHolder: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = CardGrey
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onKeyEvent { event ->
            if (event.key == Key.Enter && event.type == KeyEventType.KeyUp){
                onSearch()
                true
            } else {
                false
            }
        },
        interactionSource = interactionSource,
        enabled = false,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),  // Add this
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = {
                Box(
                    modifier = Modifier.padding(contentPadding)
                ) {
                    innerTextField()
                }
            },
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(50.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = Grey)
            },
            placeholder = {Text(placeHolder)}
        )
    }
}

