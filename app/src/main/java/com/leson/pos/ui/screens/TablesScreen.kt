package com.leson.pos.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.repo.Repo
import com.leson.pos.ui.widgets.NewOrderDialog
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add


@Composable
fun TablesScreen(onCreateOrder: (String)->Unit, onManageMenu: ()->Unit) {
  val orders by Repo.observeOpenOrders().collectAsState(initial = emptyList())
  var showDialog by remember { mutableStateOf(false) }

  @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
  Scaffold(
    topBar = { TopAppBar(title = { Text("LeSon – Tables") }, actions = { TextButton(onClick = onManageMenu){ Text("Manage Menu") } }) },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showDialog = true },
        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
        text = { Text("New Order") }
      )
    }
  ) { p ->
    Column(Modifier.padding(p)) {
      LazyColumn {
        items(orders) { o ->
          ListItem(headlineContent={ Text("Table ${o.tableNo}") }, supportingContent={ Text(o.state) },
            modifier = Modifier.clickable { onCreateOrder(o.tableNo) })
          Divider()
        }
      }
    }
  }

  if (showDialog) {
    NewOrderDialog(
      onDismiss = { showDialog = false },
      onCreate = { table, note ->
        // this composable doesn't know orderId; next screen ensures/creates one again
        onCreateOrder(table); showDialog = false
      }
    )
  }
}