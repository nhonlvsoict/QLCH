package com.leson.pos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import com.leson.pos.ui.widgets.EditMenuItemDialog
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen() {
  val menu by Repo.observeMenu().collectAsState(initial = emptyList())
  var show by remember { mutableStateOf(false) }
  var editing: MenuItemEntity? by remember { mutableStateOf(null) }
  val scope = rememberCoroutineScope()

  androidx.compose.material3.Scaffold(
    topBar = { TopAppBar(title = { Text("Manage Menu") }) },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { editing = null; show = true },
        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
        text = { Text("Add") }
      )
    }
  ) { p ->
    Column(Modifier.padding(p).padding(16.dp)) {
      LazyColumn {
        items(menu) { mi ->
          ListItem(
            headlineContent = { Text(mi.name) },
            supportingContent = { Text("${mi.category} — £" + "%.2f".format(mi.priceCents / 100.0)) },
            trailingContent = {
              Row {
                TextButton(onClick = { editing = mi; show = true }) { Text("Edit") }
                TextButton(onClick = { scope.launch { Repo.deleteMenu(mi.id) } }) { Text("Delete") }
              }
            }
          )
          Divider()
        }
      }
    }
  }

  if (show) {
    val e = editing
    EditMenuItemDialog(
      initialName = e?.name ?: "",
      initialPricePence = e?.priceCents?.toString() ?: "",
      initialCategory = e?.category ?: "",
      onDismiss = { show = false },
      onSave = { name, price, category ->
        val id = e?.id ?: UUID.randomUUID().toString()
        scope.launch { Repo.upsertMenu(MenuItemEntity(id, name, price, category)) }
        show = false
      }
    )
  }
}
