package com.leson.pos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width   // <-- add this
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
    // Observe menu items
    val menuItems by Repo.observeMenu().collectAsState(initial = emptyList())

    // UI state
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }     // accepts "6.50", "650", or "6"
    var category by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu Management") }) }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
        ) {
            // List card
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    Text(
                        "Current Items",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(8.dp)
                    )
                    Divider()

                    if (menuItems.isEmpty()) {
                        Text("No items yet. Add your first menu item below.", Modifier.padding(12.dp))
                    } else {
                        LazyColumn {
                            items(menuItems, key = { it.id }) { mi ->
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
                                            // preload fields for quick edit
                                            showAdd = true
                                            name = mi.name
                                            price = (mi.priceCents / 100.0).toString()
                                            category = mi.category
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

            // Add / Edit form
            if (showAdd) {
                Spacer(Modifier.width(0.dp))
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            if (isEditingExisting(menuItems, name, category, price)) "Edit Item" else "New Item",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        TextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price (£)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        TextField(
                            value = category,
                            onValueChange = { category = it },
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
                                name = ""; price = ""; category = ""
                            }) { Text("Cancel") }
                            Spacer(Modifier.width(12.dp))
                            Button(onClick = {
                                val cents = parsePriceToCents(price)
                                if (cents != null && name.isNotBlank() && category.isNotBlank()) {
                                    val existingId = menuItems.find {
                                        it.name.equals(name.trim(), ignoreCase = true) &&
                                        it.category.equals(category.trim(), ignoreCase = true)
                                    }?.id
                                    val id = existingId ?: UUID.randomUUID().toString()

                                    scope.launch(Dispatchers.IO) {
                                        Repo.upsertMenu(
                                            MenuItemEntity(
                                                id = id,
                                                name = name.trim(),
                                                priceCents = cents,
                                                category = category.trim()
                                            )
                                        )
                                    }
                                    showAdd = false
                                    name = ""; price = ""; category = ""
                                }
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Save")
                            }
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
        if (t.contains('.')) (t.toDouble() * 100).toInt()
        else {
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
    val pc = parsePriceToCents(price) ?: return false
    return items.any {
        it.name.equals(name.trim(), true) &&
        it.category.equals(category.trim(), true) &&
        it.priceCents == pc
    }
}
