package com.synergos.partner.ui.viewdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.synergos.partner.model.HealthData
import kotlinx.coroutines.flow.Flow


@Dao
interface HealthDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: HealthData)

    @Update
    suspend fun update(data: HealthData)

    @Delete
    suspend fun delete(data: HealthData)

    @Query("SELECT * FROM health_data ORDER BY timestamp DESC")
    fun getAllDataFlow(): Flow<List<HealthData>>

    @Query("SELECT * FROM health_data WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getDataByTimeRangeFlow(start: Long, end: Long): Flow<List<HealthData>>

    @Query("SELECT * FROM health_data ORDER BY value ASC")
    fun getDataSortedByValueAscFlow(): Flow<List<HealthData>>

    @Query("SELECT * FROM health_data ORDER BY value DESC")
    fun getDataSortedByValueDescFlow(): Flow<List<HealthData>>
}


