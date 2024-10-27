// TugasKuliahViewModel.kt
package com.example.praktikum

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum.database.AppDatabase
import com.example.praktikum.database.TugasKuliahEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TugasKuliahViewModel(application: Application) : AndroidViewModel(application) {
    private val tugasDao = AppDatabase.getDatabase(application).tugasKuliahDao()

    private val _tugasList = MutableStateFlow<List<TugasKuliahEntity>>(emptyList())
    val tugasList: StateFlow<List<TugasKuliahEntity>> = _tugasList.asStateFlow()

    init {
        loadTugas()
    }

    private fun loadTugas() {
        viewModelScope.launch(Dispatchers.IO) {
            _tugasList.value = tugasDao.getAllTugas()
        }
    }

    fun insertTugas(tugas: TugasKuliahEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tugasDao.insertTugas(tugas)
            loadTugas() // Refresh data
        }
    }

    fun updateTugasStatus(id: Int, isDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tugasDao.updateTugasStatus(id, isDone)
            loadTugas() // Refresh data setelah update
        }
    }
}
