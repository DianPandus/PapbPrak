package com.yanz.projectpapb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


class TugasScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mendapatkan instance dari database dan DAO
        val db = AppDatabase.getDatabase(this)
        val dao = db.tugasDao()

        // Inisialisasi ViewModel dengan Factory
        val viewModel: TugasViewModel = ViewModelProvider(
            this,
            TugasViewModelFactory(dao)
        )[TugasViewModel::class.java]

        setContent {
            TugasScreenContent(
                dao = dao,
                viewModel = viewModel,
                onProfileClick = {
                    // Arahkan ke halaman profil
                    val intent = Intent(this, GithubProfileActivity::class.java)
                    startActivity(intent)
                },
                onTasksClick = {
                    // Kembali ke halaman tugas (atau buat logika lain jika diperlukan)
                    val intent = Intent(this, TugasScreen::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreenContent(
    dao: TugasDao,
    viewModel: TugasViewModel = viewModel(factory = TugasViewModelFactory(dao)),
    onProfileClick: () -> Unit,
    onTasksClick: () -> Unit
) {
    val namaMatkul = remember { mutableStateOf("") }
    val detailTugas = remember { mutableStateOf("") }
    val tugasList by viewModel.tugasList.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // Agar konten dan BottomNavigationBar dipisahkan
    ) {
        // Konten bagian atas
        Column {
            OutlinedTextField(
                value = namaMatkul.value,
                onValueChange = { namaMatkul.value = it },
                label = { Text("Nama Matkul") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = detailTugas.value,
                onValueChange = { detailTugas.value = it },
                label = { Text("Detail Tugas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addTugas(namaMatkul.value, detailTugas.value)
                    namaMatkul.value = ""
                    detailTugas.value = ""
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(tugasList) { tugas ->
                    TugasCard(tugas = tugas, onToggleDone = { viewModel.toggleTugasStatus(it) })
                }
            }
        }

        // Bottom Navigation Bar di bagian bawah
        BottomNavigationBar(
            onProfileClick = onProfileClick,
            onTasksClick = onTasksClick
        )
    }
}



@Composable
fun TugasCard(tugas: Tugas, onToggleDone: (Tugas) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Mata Kuliah: ${tugas.namaMatkul}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Detail: ${tugas.detailTugas}", style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = { onToggleDone(tugas) }) {
                Icon(
                    painter = painterResource(id = if (tugas.isDone) R.drawable.done else R.drawable.ceklis),
                    contentDescription = if (tugas.isDone) "Done" else "Mark as done"
                )
            }

            // Menampilkan tulisan "done" jika tugas selesai
            if (tugas.isDone) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
