package com.leson.pos.ui.widgets
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun NewOrderDialog(onDismiss: ()->Unit, onCreate: (String, String?)->Unit) {
  var table by remember { mutableStateOf("") }
  var note by remember { mutableStateOf("") }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("New Order") },
    text = {
      Column {
        OutlinedTextField(value = table, onValueChange = { table = it }, label = { Text("Table No") })
        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note (optional)") })
      }
    },
    confirmButton = { Button(onClick = { if (table.isNotBlank()) onCreate(table, note.ifBlank { null }) }) { Text("Create") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
  )
}