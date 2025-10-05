package com.leson.pos.ui.screens
import android.content.Context
import android.print.PrintManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import com.leson.pos.ui.widgets.EditMenuItemDialog

@Composable
fun MenuScreen(tableName: String) {
  val ctx = LocalContext.current
  LaunchedEffect(Unit) { Repo.init(ctx) }
  val menu by Repo.observeMenu().collectAsState(initial = emptyList())
  var copies by remember { mutableStateOf(3) }

  Scaffold(topBar = { TopAppBar(title = { Text("Menu – $tableName") },
    actions = {
      Row {
        Text("Copies:")
        Spacer(Modifier.width(8.dp))
        var s by remember { mutableStateOf("3") }
        OutlinedTextField(value = s, onValueChange = { v -> s = v; copies = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(1,10) ?: 3 },
          singleLine = true, modifier = Modifier.width(80.dp))
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = { /* navigate to cart if you wire it */ }) { Text("Cart") }
      }
    })
  }) { p ->
    LazyColumn(Modifier.padding(p)) {
      items(menu) { mi ->
        var note by remember { mutableStateOf("") }
        ListItem(
          headlineContent = { Text(mi.name) },
          supportingContent = { Column { Text("£" + "%.2f".format(mi.priceCents/100.0)); OutlinedTextField(value=note,onValueChange={note=it}, label={Text("Note")}) } },
          trailingContent = { FilledTonalButton(onClick = {
            // ensure order & add item
            // For simplicity, we (re)create an order each time—adapt as needed.
            // Real app: persist current orderId in a ViewModel.
          }) { Text("Add") } }
        )
        Divider()
      }
    }
  }
}