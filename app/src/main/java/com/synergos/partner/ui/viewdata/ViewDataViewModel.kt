package com.synergos.partner.ui.viewdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synergos.partner.model.HealthData
import com.synergos.partner.repository.HealthRepository
import com.synergos.partner.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewDataViewModel @Inject constructor(
    private val repo: HealthRepository
) : ViewModel() {

    private val _dataState = MutableStateFlow<UIState<List<HealthData>>>(UIState.Idle)
    val dataState: StateFlow<UIState<List<HealthData>>> = _dataState.asStateFlow()

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        viewModelScope.launch {
            _dataState.value = UIState.Loading
            try {
                repo.getAllDataFlow().collect { data ->
                    _dataState.value = UIState.Success(data)
                }
            } catch (e: Exception) {
                _dataState.value = UIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getByTimeRange(start: Long, end: Long) {
        viewModelScope.launch {
            _dataState.value = UIState.Loading
            try {
                repo.getDataByTimeRangeFlow(start, end).collect { data ->
                    _dataState.value = UIState.Success(data)
                }
            } catch (e: Exception) {
                _dataState.value = UIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getSortedAsc() {
        viewModelScope.launch {
            _dataState.value = UIState.Loading
            try {
                repo.getDataSortedByValueAscFlow().collect { data ->
                    _dataState.value = UIState.Success(data)
                }
            } catch (e: Exception) {
                _dataState.value = UIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getSortedDesc() {
        viewModelScope.launch {
            _dataState.value = UIState.Loading
            try {
                repo.getDataSortedByValueDescFlow().collect { data ->
                    _dataState.value = UIState.Success(data)
                }
            } catch (e: Exception) {
                _dataState.value = UIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun insert(data: HealthData) = viewModelScope.launch {
        repo.insert(data)
        fetchAllData()
    }

    fun update(data: HealthData) = viewModelScope.launch {
        repo.update(data)
        fetchAllData()
    }

    fun delete(data: HealthData) = viewModelScope.launch {
        repo.delete(data)
        fetchAllData()
    }
}
