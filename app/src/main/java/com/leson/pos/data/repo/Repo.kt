package com.leson.pos.data.repo
import android.content.Context
import androidx.room.Room
import com.leson.pos.data.db.*
import com.leson.pos.data.db.dao.*
import com.leson.pos.data.db.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

object Repo {
  @Volatile private var db: AppDb? = null
  fun init(ctx: Context) {
    if (db == null) db = Room.databaseBuilder(ctx, AppDb::class.java, "leson.db").fallbackToDestructiveMigration().build()
  }
  private fun database(): AppDb = db ?: error("Repo not initialised")

  // Menu
  fun observeMenu(): Flow<List<MenuItemEntity>> = database().menu().observeAll()
  suspend fun upsertMenu(mi: MenuItemEntity) = database().menu().upsert(mi)
  suspend fun deleteMenu(id: String) = database().menu().delete(id)

  // Orders
  fun observeOpenOrders(): Flow<List<OrderEntity>> = database().orders().observeOpen()
  suspend fun createOrder(table: String, note: String?): OrderEntity {
    val e = OrderEntity(UUID.randomUUID().toString(), table, note, "NEW", System.currentTimeMillis())
    database().orders().insert(e); return e
  }
  suspend fun setOrderState(id: String, state: String) = database().orders().setState(id, state)
  suspend fun getOrder(id: String) = database().orders().get(id)

  // Items
  fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>> = database().items().observeByOrder(orderId)
  suspend fun addItem(orderId: String, name: String, priceCents: Int, note: String?) {
    val item = OrderItemEntity(UUID.randomUUID().toString(), orderId, "menu", name, priceCents, 1, note)
    database().items().insertAll(listOf(item))
  }
}