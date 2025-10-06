package com.leson.pos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.repo.Repo
import com.leson.pos.ui.widgets.NewOrderDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
  onCreateOrder: (String) -> Unit,
  onManageMenu: () -> Unit
) {
  val orders by Repo.observeOpenOrders().collectAsState(initial = emptyList())
  val showDialog = remember { mutableStateOf(false) }

  androidx.compose.material3.Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("LeSon – Tables") },
        actions = { TextButton(onClick = onManageMenu) { Text("Manage Menu") } }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showDialog.value = true },
        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
        text = { Text("New Order") }
      )
    }
  ) { p ->
    Column(Modifier.padding(p)) {
      LazyColumn {
        items(orders) { o ->
          ListItem(
            headlineContent = { Text("Table ${o.tableNo}") },
            supportingContent = { Text(o.state) },
            modifier = Modifier.clickable { onCreateOrder(o.tableNo) }
          )
          Divider()
        }
      }
    }
  }

  if (showDialog.value) {
    NewOrderDialog(
      onDismiss = { showDialog.value = false },
      onCreate = { table, _ ->
        onCreateOrder(table)
        showDialog.value = false
      }
    )
  }
}
