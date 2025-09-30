package data.model

// data/model/Entities.kt
import androidx.room.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.util.*

enum class OrderStatus { OPEN, SENT, PARTIAL_READY, READY, SERVED, CLOSED, PAID }
enum class ItemStatus { QUEUED, IN_PREP, READY, SERVED, VOID }
enum class Station { NOODLES, CRISPY_ROLL, STARTER_BAR }

@Serializable
data class Option(val key: String, val value: String)

class Converters {
    private val json = Json { encodeDefaults = true }
    @TypeConverter fun fromOptions(list: List<Option>?): String? = list?.let { json.encodeToString(it) }
    @TypeConverter fun toOptions(str: String?): List<Option>? = str?.let { json.decodeFromString(it) }
}

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val isActive: Boolean = true
)

@Entity(tableName = "menu_categories")
data class MenuItemCategoryEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val sort : Int
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: Long,
    val categoryId: Long,
    val name: String,
    val price: Double,
    val station: Station,
    val isActive: Boolean = true,
)

@Entity(tableName = "option_presets")
data class OptionPresetEntity(
    @PrimaryKey val id: Long,
    val menuItemId: Long,
    val key: String,
    val value: Double,
    val type: String,
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tableId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = "waiter",
    val status: OrderStatus = OrderStatus.OPEN
)

@Entity(
    tableName = "order_items",
    indices = [Index("orderId"), Index("station")]
)
data class OrderItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val menuItemId: Long,
    val qty: Int = 1,
    val note: String? = null,
    val optionsJson: String? = null,
    val status: ItemStatus = ItemStatus.QUEUED,
    val station: Station,
    val tableNumber : String
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val method: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val ref: String? = null
)