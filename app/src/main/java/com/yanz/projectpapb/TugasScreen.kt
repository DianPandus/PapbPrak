package com.yanz.projectpapb

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.collectAsState
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Database dan inisialisasi DAO
        val db = AppDatabase.getDatabase(this)
        val dao = db.tugasDao()

        // Inisialisasi ViewModel
        val viewModel: TugasViewModel = ViewModelProvider(
            this,
            TugasViewModelFactory(dao)
        )[TugasViewModel::class.java]

        setContent {
            TugasScreenContent(
                viewModel = viewModel,
                onProfileClick = {
                    val intent = Intent(this, GithubProfileActivity::class.java)
                    startActivity(intent)
                },
                onTasksClick = {
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
    viewModel: TugasViewModel,
    onProfileClick: () -> Unit,
    onTasksClick: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    // Mengakses data StateFlow dari ViewModel menggunakan collectAsState
    val tugasList by viewModel.tugasList.collectAsState(initial = emptyList())

    // Peluncur izin kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera = true
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Input untuk "nama matkul" dan "detail tugas"
            OutlinedTextField(
                value = viewModel.namaMatkul.value,
                onValueChange = { viewModel.namaMatkul.value = it },
                label = { Text("Nama Matkul") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.detailTugas.value,
                onValueChange = { viewModel.detailTugas.value = it },
                label = { Text("Detail Tugas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menampilkan CameraCapture jika izin telah diberikan
            if (showCamera) {
                CameraCapture(
                    onImageCaptured = { uri ->
                        imageUri = uri
                        showCamera = false
                    },
                    onError = { exception ->
                        Log.e("TugasScreen", "Gagal menangkap gambar", exception)
                    }
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        // Meminta izin kamera
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Camera")
                    }

                    Button(
                        onClick = {
                            viewModel.addTugas(viewModel.namaMatkul.value, viewModel.detailTugas.value, imageUri?.toString())
                            viewModel.namaMatkul.value = ""
                            viewModel.detailTugas.value = ""
                            imageUri = null
                        }
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Gambar Terambil",
                        modifier = Modifier.size(200.dp).padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar tugas
            LazyColumn {
                items(tugasList) { tugas ->
                    TugasCard(tugas = tugas, onToggleDone = { viewModel.toggleTugasStatus(it) })
                }
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            onProfileClick = onProfileClick,
            onTasksClick = onTasksClick
        )
    }
}


@Composable
fun CameraCapture(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraCapture", "Use case binding failed", exc)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val photoFile = File(
                    context.getExternalFilesDir(null),
                    "photo.jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onImageCaptured(Uri.fromFile(photoFile))
                        }

                        override fun onError(exception: ImageCaptureException) {
                            onError(exception)
                        }
                    }
                )
            }
        ) {
            Text("Capture")
        }
    }
}

@Composable
fun TugasCard(tugas: Tugas, onToggleDone: (Tugas) -> Unit) {
    var showImageDialog by remember { mutableStateOf(false) }

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

            // Icon to toggle task completion
            IconButton(onClick = { onToggleDone(tugas) }) {
                Icon(
                    imageVector = if (tugas.isDone) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (tugas.isDone) "Done" else "Mark as done"
                )
            }

            // Button to view image if available
            if (tugas.imageUri != null) {
                Button(onClick = { showImageDialog = true }) {
                    Text("View")
                }
            }

            // Dialog to display the image
            if (showImageDialog) {
                AlertDialog(
                    onDismissRequest = { showImageDialog = false },
                    title = { Text("Captured Image") },
                    text = {
                        Image(
                            painter = rememberAsyncImagePainter(tugas.imageUri),
                            contentDescription = "Captured Image",
                            modifier = Modifier.size(300.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showImageDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
