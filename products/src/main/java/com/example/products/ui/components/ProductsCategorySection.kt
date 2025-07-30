package com.example.products.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.label
import com.example.products.viewModels.ViewProductViewModel
import com.example.products.viewModels.ViewProductViewModel.OperationState
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import java.util.UUID

@Composable
fun ProductCategoriesSection(
    product: Product?,
    allCategories: List<ProductCategory>,
    onAddCategory: (UUID) -> Unit,
    onRemoveCategory: (UUID) -> Unit,
    onShowCategorySelection: () -> Unit,
    addCategoryState: ViewProductViewModel.OperationState,
    removeCategoryState: ViewProductViewModel.OperationState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = label,
                contentDescription = "Categories",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // Categories Display
        /*if (product?.categories?.isNotEmpty() == true) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(product.categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = true,
                        onRemove = { onRemoveCategory(category.id) },
                        isLoading = removeCategoryState is ViewProductViewModel.OperationState.Loading
                    )
                }

                // Add Category Button
                item {
                    AddCategoryChip(
                        onClick = onShowCategorySelection,
                        isLoading = addCategoryState is ViewProductViewModel.OperationState.Loading
                    )
                }
            }
        } else
        {
            // No categories state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No categories assigned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )

                AddCategoryChip(
                    onClick = onShowCategorySelection,
                    isLoading = addCategoryState is ViewProductViewModel.OperationState.Loading
                )
            }
        }*/

        // Show loading/error states
        when {
            addCategoryState is ViewProductViewModel.OperationState.Error -> {
                Text(
                    text = addCategoryState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            removeCategoryState is OperationState.Error -> {
                Text(
                    text = removeCategoryState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: ProductCategory,
    isSelected: Boolean,
    onRemove: () -> Unit,
    isLoading: Boolean = false
) {
    FilterChip(
        selected = isSelected,
        onClick = { if (!isLoading) onRemove() },
        label = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = if (isLoading) {
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        } else null,
        trailingIcon = if (!isLoading) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove category",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onRemove() }
                )
            }
        } else null,
        shape = RoundedCornerShape(100),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = !isLoading,
            selected = isSelected,
            disabledBorderColor = Color.Transparent,
            selectedBorderColor = Color.Transparent
        ),
        modifier = Modifier.animateContentSize()
    )
}

@Composable
fun AddCategoryChip(
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    FilterChip(
        selected = false,
        onClick = { if (!isLoading) onClick() },
        label = {
            Text(
                text = "Add Category",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = if (isLoading) {
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        } else {
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add category",
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        shape = RoundedCornerShape(100),
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = !isLoading,
            selected = false,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier.animateContentSize()
    )
}

// Category Selection Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionBottomSheet(
    allCategories: List<ProductCategory>,
    currentProductCategories: List<ProductCategory>,
    onCategorySelected: (ProductCategory) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filter out categories that are already assigned to the product
            val availableCategories = allCategories.filter { category ->
                currentProductCategories.none { it.id == category.id }
            }

            if (availableCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All categories are already assigned to this product",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(availableCategories) { category ->
                        CategorySelectionItem(
                            category = category,
                            onSelected = { onCategorySelected(category) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategorySelectionItem(
    category: ProductCategory,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            if (!category.description.isNullOrBlank()) {
                Text(
                    text = category.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "${category.productCount} products",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}