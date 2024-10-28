package com.yanz.projectpapb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TugasViewModel(private val dao: TugasDao) : ViewModel() {
    // Menggunakan StateFlow untuk mengamati data dari database
    val tugasList: StateFlow<List<Tugas>> = flow {
        dao.getAllTugas().collect { emit(it) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Fungsi untuk menambahkan tugas baru ke database
    fun addTugas(namaMatkul: String, detailTugas: String) {
        viewModelScope.launch {
            dao.insertTugas(Tugas(namaMatkul = namaMatkul, detailTugas = detailTugas))
        }
    }

    // Fungsi untuk mengubah status isDone dari tugas
    fun toggleTugasStatus(tugas: Tugas) {
        viewModelScope.launch {
            val updatedTugas = tugas.copy(isDone = !tugas.isDone)
            dao.insertTugas(updatedTugas) // Perbarui tugas di database
        }
    }
}

// Factory untuk membuat instance TugasViewModel dengan parameter dao
class TugasViewModelFactory(private val dao: TugasDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TugasViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
