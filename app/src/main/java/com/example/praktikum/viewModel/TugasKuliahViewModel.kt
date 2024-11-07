// TugasKuliahViewModel.kt
package com.example.praktikum.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum.database.Tugas
import com.example.praktikum.database.TugasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TugasKuliahViewModel.kt
class TugasKuliahViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TugasRepository = TugasRepository(application)

    private val _tugasList = MutableStateFlow<List<Tugas>>(emptyList())
    val tugasList: StateFlow<List<Tugas>> = _tugasList.asStateFlow()

    init {
        loadTugas()
    }

    private fun loadTugas() {
        viewModelScope.launch(Dispatchers.IO) {
            _tugasList.value = repository.getAllTugas()
        }
    }

    fun insertTugas(tugas: Tugas) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTugas(tugas)
            loadTugas() // Refresh data setelah insert
        }
    }
    // TugasKuliahViewModel.kt
    fun deleteTugas(tugas: Tugas) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTugas(tugas)
            loadTugas() // Refresh data setelah delete
        }
    }


    fun updateTugasStatus(id: Int, isDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val tugas = _tugasList.value.find { it.id == id }
            if (tugas != null) {
                tugas.selesai = isDone
                repository.updateTugas(tugas)
                loadTugas() // Refresh data setelah update
            }
        }
    }
}
