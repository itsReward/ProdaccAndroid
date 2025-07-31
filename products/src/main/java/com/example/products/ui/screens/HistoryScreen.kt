package com.example.products.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.FormattedTime
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Green
import com.example.designsystem.theme.Orange
import com.example.designsystem.theme.Red
import com.example.products.navigation.NavigationBar
import com.example.products.viewModels.InventoryTransactionViewModel
import com.prodacc.data.remote.dao.product.InventoryTransaction


@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: InventoryTransactionViewModel = hiltViewModel()
){
    val transactions = viewModel.inventoryTransactions.collectAsState().value

    Scaffold(
        containerColor = Color.Transparent, topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(end = 20.dp, start = 20.dp),
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = filter,
                        contentDescription = "Filter By Product"
                    )
                }
            }
        }, modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            NavigationBar(navController = navController)

        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {

            when (viewModel.loadingState.collectAsState().value){
                is InventoryTransactionViewModel.LoadingState.Error -> {
                    ErrorStateColumn(
                        title = (viewModel.loadingState.collectAsState().value as InventoryTransactionViewModel.LoadingState.Error).message,
                        buttonText = "Reload",
                        buttonOnClick = { viewModel.reload() }
                    )
                }
                is InventoryTransactionViewModel.LoadingState.Loading -> {
                    LoadingStateColumn(
                        "Fetching Inventory Transactions"
                    )
                }
                is InventoryTransactionViewModel.LoadingState.Success -> {

                    Row (
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Inventory Transactions",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clip(RoundedCornerShape(100))
                                .background(CardGrey).padding( horizontal = 5.dp)
                        )
                    }

                    if (transactions.size != 0){
                        LazyColumn(
                            //modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            items(transactions) { index ->
                                InventoryTransactionItem(item = index)
                            }
                        }

                    } else {
                        ErrorStateColumn(
                            title = "No transactions found",
                            buttonText = "Reload",
                            buttonOnClick = { viewModel.reload() }
                        )
                    }

                }
            }

        }
    }
}


@Composable
fun InventoryTransactionItem(
    item: InventoryTransaction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.productName ?: "Unknown Product",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                FormattedTime(
                    time = item.transactionDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

        }
        Column {
            Text(
                text = item.transactionType,
                style = MaterialTheme.typography.bodyMedium,
                color = when (item.transactionType) {
                    "PURCHASE" -> Orange
                    "SALE" -> Green
                    "ADJUSTMENT" -> DarkGrey
                    "RETURN" -> Red
                    "TRANSFER" -> BlueA700
                    else -> Color.Black
                }
            )
            Text(
                text = if (item.transactionType == "IN") "+${item.quantity}" else "-${item.quantity}",
                color = if (item.transactionType == "IN") Color(0xFF388E3C) else Color(0xFFD32F2F),
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }
}