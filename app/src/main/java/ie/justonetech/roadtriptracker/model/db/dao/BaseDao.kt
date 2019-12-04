package ie.justonetech.roadtriptracker.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

///////////////////////////////////////////////////////////////////////////////////////////////////
// BaseDao
// Generic base class with common utilities for Data Access Objects
///////////////////////////////////////////////////////////////////////////////////////////////////

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: T): List<Long>

    @Delete
    fun delete(item: T)
}