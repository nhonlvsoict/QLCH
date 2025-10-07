package com.leson.pos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
    val menuItems by Repo.observeMenu().collectAsState(initial = emptyList())

    var showAdd by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") } // e.g. "6.50" or "650"
    var newCategory by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu Management") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // List
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    Text(
                        "Current Items",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(8.dp)
                    )
                    Divider()
                    if (menuItems.isEmpty()) {
                        Text(
                            "No items yet. Add your first menu item below.",
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        LazyColumn {
                            items(menuItems) { mi ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(mi.name, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            "£${formatPounds(mi.priceCents)} • ${mi.category}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Row {
                                        TextButton(onClick = {
                                            // preload fields for edit
                                            showAdd = true
                                            newName = mi.name
                                            newPrice = (mi.priceCents / 100.0).toString()
                                            newCategory = mi.category
                                        }) { Text("Edit") }
                                        Spacer(Modifier.width(8.dp))
                                        TextButton(onClick = {
                                            scope.launch(Dispatchers.IO) { Repo.deleteMenu(mi.id) }
                                        }) { Text("Delete") }
                                    }
                                }
                                Divider()
                            }
                        }
                    }
                }
            }

            // Add / Edit panel
            if (showAdd) {
                Spacer(Modifier.width(0.dp))
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            if (isEditingExisting(menuItems, newName, newCategory, newPrice)) "Edit Item" else "New Item",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        TextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Price (£)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        TextField(
                            value = newCategory,
                            onValueChange = { newCategory = it },
                            label = { Text("Category") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                showAdd = false
                                newName = ""; newPrice = ""; newCategory = ""
                            }) { Text("Cancel") }
                            Spacer(Modifier.width(12.dp))
                            Button(onClick = {
                                val priceCents = parsePriceToCents(newPrice)
                                if (priceCents != null && newName.isNotBlank() && newCategory.isNotBlank()) {
                                    val existingId = menuItems.find {
                                        it.name.equals(newName.trim(), ignoreCase = true) &&
                                                it.category.equals(newCategory.trim(), ignoreCase = true)
                                    }?.id
                                    val id = existingId ?: UUID.randomUUID().toString()

                                    scope.launch(Dispatchers.IO) {
                                        Repo.upsertMenu(
                                            MenuItemEntity(
                                                id = id,
                                                name = newName.trim(),
                                                priceCents = priceCents,
                                                category = newCategory.trim()
                                            )
                                        )
                                    }
                                    showAdd = false
                                    newName = ""; newPrice = ""; newCategory = ""
                                }
                            }) { Text("Save") }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { showAdd = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Menu Item")
                    }
                }
            }
        }
    }
}

/** Helpers **/

private fun parsePriceToCents(text: String): Int? {
    val t = text.trim()
    if (t.isEmpty()) return null
    return try {
        if (t.contains('.')) {
            (t.toDouble() * 100).toInt()
        } else {
            val asInt = t.toInt()
            if (asInt < 100) asInt * 100 else asInt
        }
    } catch (_: NumberFormatException) {
        null
    }
}

private fun formatPounds(cents: Int): String = String.format("%.2f", cents / 100.0)

private fun isEditingExisting(
    items: List<MenuItemEntity>,
    name: String,
    category: String,
    price: String
): Boolean {
    if (name.isBlank() || category.isBlank()) return false
    val priceCents = parsePriceToCents(price) ?: return false
    return items.any {
        it.name.equals(name.trim(), ignoreCase = true) &&
        it.category.equals(category.trim(), ignoreCase = true) &&
        it.priceCents == priceCents
    }
}
