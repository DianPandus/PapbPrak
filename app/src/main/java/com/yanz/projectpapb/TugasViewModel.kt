package com.yanz.projectpapb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.activity.compose.rememberLauncherForActivityResult

class TugasViewModel(private val dao: TugasDao) : ViewModel() {
    var namaMatkul = mutableStateOf("")
    var detailTugas = mutableStateOf("")

    // StateFlow to observe data from the database
    val tugasList: StateFlow<List<Tugas>> = flow {
        dao.getAllTugas().collect { emit(it) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Function to add a new task with an image URI
    fun addTugas(namaMatkul: String, detailTugas: String, imageUri: String?) {
        viewModelScope.launch {
            val tugas = Tugas(namaMatkul = namaMatkul, detailTugas = detailTugas, imageUri = imageUri)
            dao.insertTugas(tugas)
        }
    }

    // Function to toggle task status
    fun toggleTugasStatus(tugas: Tugas) {
        viewModelScope.launch {
            val updatedTugas = tugas.copy(isDone = !tugas.isDone)
            dao.insertTugas(updatedTugas)
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
