package com.synergos.partner.utils

import android.content.Context
import androidx.room.Room
import com.synergos.partner.repository.HealthRepository
import com.synergos.partner.ui.viewdata.HealthDataDao
import com.synergos.partner.ui.viewdata.HealthDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HealthDatabase {
        return Room.databaseBuilder(
            context,
            HealthDatabase::class.java,
            "health_database"
        ).build()
    }

    @Provides
    fun provideHealthDao(db: HealthDatabase): HealthDataDao = db.healthDataDao()

    @Provides
    @Singleton
    fun provideHealthRepository(dao: HealthDataDao): HealthRepository {
        return HealthRepository(dao)
    }
}
