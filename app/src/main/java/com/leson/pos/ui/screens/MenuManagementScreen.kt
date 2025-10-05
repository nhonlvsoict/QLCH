package com.leson.pos.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.db.entity.MenuItemEntity
import com.leson.pos.data.repo.Repo
import com.leson.pos.ui.widgets.EditMenuItemDialog
import java.util.UUID

@Composable
fun MenuManagementScreen() {
  val menu by Repo.observeMenu().collectAsState(initial = emptyList())
  var show by remember { mutableStateOf(false) }
  var editing: MenuItemEntity? by remember { mutableStateOf(null) }

  Scaffold(topBar = { TopAppBar(title = { Text("Manage Menu") }) },
    floatingActionButton = { ExtendedFloatingActionButton(onClick = { editing = null; show = true }, text={ Text("Add") }) }
  ) { p ->
    Column(Modifier.padding(p).padding(16.dp)) {
      LazyColumn {
        items(menu) { mi ->
          ListItem(
            headlineContent = { Text(mi.name) },
            supportingContent = { Text("${mi.category} — £" + "%.2f".format(mi.priceCents/100.0)) },
            trailingContent = {
              Row {
                TextButton(onClick = { editing = mi; show = true }) { Text("Edit") }
                TextButton(onClick = { /* delete */ kotlinx.coroutines.GlobalScope.launch { Repo.deleteMenu(mi.id) } }) { Text("Delete") }
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
        kotlinx.coroutines.GlobalScope.launch { Repo.upsertMenu(MenuItemEntity(id, name, price, category)) }
        show = false
      }
    )
  }
}