package com.example.recipebox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipebox.ui.state.IngredientInput

@Composable
fun IngredientInputItem(
    ingredient: IngredientInput,
    showRemove: Boolean,
    onUpdate: (IngredientInput) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = { onUpdate(ingredient.copy(name = it)) },
            placeholder = { Text("Ingredient", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
            modifier = Modifier.weight(2f),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = ingredient.qty,
            onValueChange = { onUpdate(ingredient.copy(qty = it)) },
            placeholder = { Text("Qty", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = { onUpdate(ingredient.copy(unit = it)) },
            placeholder = { Text("Unit", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )
        if (showRemove) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
