package com.leson.pos.data.db.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
  @PrimaryKey val id: String,
  val name: String,
  val priceCents: Int,
  val category: String
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey val id: String,
  val tableNo: String,
  val note: String?,
  val state: String,
  val createdAt: Long
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
  @PrimaryKey val id: String,
  val orderId: String,
  val menuItemId: String,
  val name: String,
  val unitPriceCents: Int,
  val qty: Int,
  val note: String?
)