package com.leson.pos.ui.widgets
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun EditMenuItemDialog(initialName: String = "", initialPricePence: String = "", initialCategory: String = "", onDismiss: ()->Unit, onSave: (String, Int, String)->Unit) {
  var name by remember { mutableStateOf(initialName) }
  var price by remember { mutableStateOf(initialPricePence) }
  var category by remember { mutableStateOf(initialCategory) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Menu Item") },
    text = {
      Column {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = price, onValueChange = { v -> price = v.filter { it.isDigit() } }, label = { Text("Price (pence)") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
      }
    },
    confirmButton = { Button(onClick = { val p = price.toIntOrNull() ?: 0; if (name.isNotBlank() && p>0) onSave(name, p, category) }) { Text("Save") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
  )
}