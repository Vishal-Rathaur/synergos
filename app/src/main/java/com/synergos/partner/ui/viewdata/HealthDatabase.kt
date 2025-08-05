package com.synergos.partner.ui.viewdata

import androidx.room.Database
import androidx.room.RoomDatabase
import com.synergos.partner.model.HealthData

@Database(entities = [HealthData::class], version = 1)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun healthDataDao(): HealthDataDao
}



