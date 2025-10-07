import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.leson.pos.ui.screens.MenuManagementScreen
import com.leson.pos.ui.screens.MenuScreen
import com.leson.pos.ui.screens.TablesScreen
import com.leson.pos.data.repo.Repo


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Repo.init(applicationContext)
    setContent { AppNav() }
  }
}

@Composable
fun AppNav() {
  val nav = rememberNavController()
  MaterialTheme {
    NavHost(navController = nav, startDestination = "tables") {
      composable("tables") {
        TablesScreen(
          onCreateOrder = { table -> nav.navigate("menu/$table") },
          onManageMenu = { nav.navigate("manageMenu") }
        )
      }

      composable("menu/{table}") {
        val table = it.arguments?.getString("table") ?: "T1"
        MenuScreen(table)
      }
      //composable("cart/{orderId}") { CartScreen() }
      composable("cart/{orderId}") {
        // Temporary stub to avoid CartScreen IR lowering crash.
        androidx.compose.material3.TopAppBar(title = { Text("Cart (temporarily disabled)") })
      }
      composable("manageMenu") { MenuManagementScreen() }
    }
  }
}