package com.leson.pos.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.leson.pos.data.db.entity.*

@Dao interface MenuDao {
  @Query("SELECT * FROM menu_items ORDER BY category, name") fun observeAll(): Flow<List<MenuItemEntity>>
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: MenuItemEntity)
  @Query("DELETE FROM menu_items WHERE id=:id") suspend fun delete(id: String)
  @Query("SELECT COUNT(*) FROM menu_items") suspend fun count(): Int
}

@Dao interface OrderDao {
  @Insert suspend fun insert(o: OrderEntity)
  @Query("SELECT * FROM orders WHERE state NOT IN ('PAID','VOID') ORDER BY createdAt DESC") fun observeOpen(): Flow<List<OrderEntity>>
  @Query("SELECT * FROM orders WHERE id=:id") suspend fun get(id: String): OrderEntity?
  @Query("UPDATE orders SET state=:state WHERE id=:id") suspend fun setState(id: String, state: String)
}

@Dao interface OrderItemDao {
  @Insert suspend fun insertAll(items: List<OrderItemEntity>)
  @Query("SELECT * FROM order_items WHERE orderId=:orderId") fun observeByOrder(orderId: String): Flow<List<OrderItemEntity>>
}