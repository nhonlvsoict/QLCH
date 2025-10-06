package com.leson.pos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leson.pos.data.repo.Repo

import androidx.compose.runtime.setValue


@Composable
fun MenuScreen(tableName: String) {
  LaunchedEffect(Unit) { /* Repo.init happens in MainActivity */ }
  val menu by Repo.observeMenu().collectAsState(initial = emptyList())
  var copies by remember { mutableStateOf(3) }
  val copiesText = remember { mutableStateOf("3") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Menu – $tableName") },
        actions = {
          Row {
            Text("Copies:")
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
              value = copiesText.value,
              onValueChange = { v ->
                copiesText.value = v
                copies = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(1, 10) ?: 3
              },
              singleLine = true,
              modifier = Modifier.width(80.dp)
            )
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { /* TODO: navigate to cart when wired */ }) { Text("Cart") }
          }
        }
      )
    }
  ) { p ->
    LazyColumn(Modifier.padding(p)) {
      items(menu) { mi ->
        val note = remember { mutableStateOf("") }
        ListItem(
          headlineContent = { Text(mi.name) },
          supportingContent = {
            Column {
              Text("£" + "%.2f".format(mi.priceCents / 100.0))
              OutlinedTextField(
                value = note.value,
                onValueChange = { note.value = it },
                label = { Text("Note") }
              )
            }
          },
          trailingContent = {
            TextButton(onClick = {
              // TODO: ensure an order exists and Repo.addItem(orderId, mi.name, mi.priceCents, note.value)
            }) { Text("Add") }
          }
        )
        Divider()
      }
    }
  }
}
