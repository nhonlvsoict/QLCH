package com.leson.pos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
    val menu by Repo.observeMenu().collectAsState(initial = emptyList())
    var showAdd by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var newPrice by rememberSaveable { mutableStateOf("") }
    var newCategory by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopAppBar(title = { Text("Manage Menu") })

        // List existing menu items
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false)
        ) {
            items(menu) { mi ->
                Column(Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(mi.name)
                            Text("${mi.category} — £${"%.2f".format(mi.priceCents / 100.0)}")
                        }
                        Row {
                            TextButton(onClick = {
                                scope.launch { Repo.deleteMenu(mi.id) }
                            }) { Text("Delete") }
                        }
                    }
                    Divider()
                }
            }
        }

        // Add new item form
        if (showAdd) {
            TextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
            TextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price pence") })
            TextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("Category") })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val id = UUID.randomUUID().toString()
                    val priceInt = newPrice.toIntOrNull() ?: 0
                    scope.launch {
                        Repo.upsertMenu(MenuItemEntity(id, newName, priceInt, newCategory))
                    }
                    newName = ""; newPrice = ""; newCategory = ""; showAdd = false
                }) { Text("Save") }
                Button(onClick = { showAdd = false }) { Text("Cancel") }
            }
        } else {
            Button(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Menu Item")
            }
        }
    }
}
