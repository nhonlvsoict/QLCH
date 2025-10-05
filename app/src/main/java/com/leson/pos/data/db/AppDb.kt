package com.leson.pos.data.db
import androidx.room.Database
import androidx.room.RoomDatabase
import com.leson.pos.data.db.dao.*
import com.leson.pos.data.db.entity.*

@Database(entities=[MenuItemEntity::class, OrderEntity::class, OrderItemEntity::class], version=1)
abstract class AppDb: RoomDatabase() {
  abstract fun menu(): MenuDao
  abstract fun orders(): OrderDao
  abstract fun items(): OrderItemDao
}