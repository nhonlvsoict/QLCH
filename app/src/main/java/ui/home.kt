package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ----- ENTRY POINT -----
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            TopSearchBar()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SectionTitle("Pinned")
                    NoteCard("Singapore 🇸🇬 and Philippines 🇵🇭", "8:51 AM  Supertree Grove", "Trips")
                    NoteCard("Packing", "Thursday  Sunscreen 🧴", "Trips")
                }

                item {
                    SectionTitle("Previous 7 Days")
                }

                items(sampleNotes) { note ->
                    NoteCard(note.title, note.subtitle, note.tag)
                }
            }
        }
    }
}

// ----- COMPONENTS -----
@Composable
fun TopSearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        placeholder = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}

@Composable
fun NoteCard(title: String, subtitle: String, tag: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(text = tag, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            label = { Text("Ordered") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("In-Prep") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("Ready") },
            icon = {}
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            label = { Text("Served") },
            icon = {}
        )
    }
}

// ---- Fake Data ----
data class Note(val title: String, val subtitle: String, val tag: String)

val sampleNotes = listOf(
    Note("Countries to Visit", "Wednesday  Asia", "Trips"),
    Note("TV Shows to Rewatch", "Tuesday  Santa Clarita Diet 🔪", "Ming House"),
    Note("Bucket List", "Sunday  Burn Almond Cake 🍰🥐", "Fooood")
)
