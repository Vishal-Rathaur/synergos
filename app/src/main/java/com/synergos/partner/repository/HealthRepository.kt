package com.synergos.partner.repository

import com.synergos.partner.model.HealthData
import com.synergos.partner.ui.viewdata.HealthDataDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class HealthRepository @Inject constructor(private val dao: HealthDataDao) {

    fun getAllDataFlow(): Flow<List<HealthData>> = dao.getAllDataFlow()

    fun getDataByTimeRangeFlow(start: Long, end: Long): Flow<List<HealthData>> = dao.getDataByTimeRangeFlow(start, end)

    fun getDataSortedByValueAscFlow(): Flow<List<HealthData>> = dao.getDataSortedByValueAscFlow()

    fun getDataSortedByValueDescFlow(): Flow<List<HealthData>> = dao.getDataSortedByValueDescFlow()

    suspend fun insert(data: HealthData) = dao.insert(data)

    suspend fun update(data: HealthData) = dao.update(data)

    suspend fun delete(data: HealthData) = dao.delete(data)
}

