package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.RelationshipCategory
import com.example.ui.theme.CategoryFamily
import com.example.ui.theme.CategoryFriend
import com.example.ui.theme.CategoryOther
import com.example.ui.theme.CategoryRelative
import com.example.ui.theme.CategoryWork

@Composable
fun CategoryChipRow(
    selectedCategory: RelationshipCategory?,
    onlyFavorites: Boolean,
    onCategorySelected: (RelationshipCategory?) -> Unit,
    onToggleFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedCategory == null && !onlyFavorites,
            onClick = {
                if (onlyFavorites) onToggleFavorites()
                onCategorySelected(null)
            },
            label = { Text("All") },
            modifier = Modifier.testTag("filter_all")
        )

        FilterChip(
            selected = onlyFavorites,
            onClick = onToggleFavorites,
            leadingIcon = {
                Icon(
                    imageVector = if (onlyFavorites) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Favorites",
                    tint = if (onlyFavorites) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { Text("Favorites") },
            modifier = Modifier.testTag("filter_favorites")
        )

        RelationshipCategory.entries.forEach { category ->
            val isSelected = selectedCategory == category && !onlyFavorites
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (onlyFavorites) onToggleFavorites()
                    if (selectedCategory == category) {
                        onCategorySelected(null)
                    } else {
                        onCategorySelected(category)
                    }
                },
                label = {
                    Text("${category.emoji} ${category.label}")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getCategoryColor(category).copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("filter_category_${category.name.lowercase()}")
            )
        }
    }
}

fun getCategoryColor(category: RelationshipCategory) = when (category) {
    RelationshipCategory.FAMILY -> CategoryFamily
    RelationshipCategory.FRIEND -> CategoryFriend
    RelationshipCategory.RELATIVE -> CategoryRelative
    RelationshipCategory.WORK -> CategoryWork
    RelationshipCategory.OTHER -> CategoryOther
}
