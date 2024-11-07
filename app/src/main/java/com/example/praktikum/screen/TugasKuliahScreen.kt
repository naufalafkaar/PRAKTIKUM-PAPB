// TugasKuliahScreen.kt
package com.example.praktikum.screen

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.praktikum.database.Tugas
import com.example.praktikum.navigation.BottomNavItem
import com.example.praktikum.viewModel.TugasKuliahViewModel
import java.io.File
import java.io.OutputStream

@Composable
fun TugasKuliahScreen(viewModel: TugasKuliahViewModel = viewModel()) {
    var mataKuliah by remember { mutableStateOf(TextFieldValue("")) }
    var detailTugas by remember { mutableStateOf(TextFieldValue("")) }
    var gambarNama by remember { mutableStateOf(TextFieldValue("")) }
    var submissionStatus by remember { mutableStateOf("") }
    var isCameraOpen by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Mengambil daftar tugas dari ViewModel
    val tugasList by viewModel.tugasList.collectAsState()

    // Launcher untuk meminta izin kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true // Buka kamera jika izin diberikan
        } else {
            submissionStatus = "Izin kamera tidak diberikan."
        }
    }

    // Menggunakan Scaffold agar navbar tetap di bawah dan konten lain bisa di-scroll
    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        if (isCameraOpen) {
            // Menampilkan tampilan kamera jika isCameraOpen bernilai true
            CameraPreview(
                onImageCaptured = { uri ->
                    capturedImageUri = uri
                    isCameraOpen = false
                },
                onError = {
                    submissionStatus = "Gagal membuka kamera."
                    isCameraOpen = false
                }
            )
        } else {
            // Menampilkan form dan daftar tugas jika isCameraOpen bernilai false
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp), // Padding untuk isi
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Tugas Kuliah",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = mataKuliah,
                        onValueChange = { mataKuliah = it },
                        label = { Text("Nama Mata Kuliah") },
                        leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = detailTugas,
                        onValueChange = { detailTugas = it },
                        label = { Text("Detail Tugas") },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = gambarNama,
                        onValueChange = { gambarNama = it },
                        label = { Text("Nama Gambar") },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (mataKuliah.text.isNotEmpty() && detailTugas.text.isNotEmpty() && gambarNama.text.isNotEmpty()) {
                                    val newTugas = Tugas(
                                        matkul = mataKuliah.text,
                                        detailTugas = detailTugas.text,
                                        gambarNama = gambarNama.text,
                                        selesai = false,
                                        gambarUri = capturedImageUri?.toString() // Set URI gambar ke Tugas
                                    )
                                    viewModel.insertTugas(newTugas)
                                    submissionStatus = "Tugas untuk ${mataKuliah.text} berhasil disimpan!"
                                    mataKuliah = TextFieldValue("")
                                    detailTugas = TextFieldValue("")
                                    gambarNama = TextFieldValue("")
                                    capturedImageUri = null
                                } else {
                                    submissionStatus = "Mohon isi semua kolom sebelum submit."
                                }
                            },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text("Submit", color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                                ) {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                } else {
                                    isCameraOpen = true
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Text("Mulai Kamera", color = Color.White)
                        }
                    }

                    if (submissionStatus.isNotEmpty()) {
                        Text(
                            text = submissionStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Menampilkan daftar tugas
                items(tugasList.size) { index ->
                    val tugas = tugasList[index]
                    val imageUri = tugas.gambarUri?.let { Uri.parse(it) }

                    TugasKuliahCard(
                        tugas = tugas,
                        onStatusChange = {
                            viewModel.updateTugasStatus(tugas.id, true)
                        },
                        onDelete = {
                            viewModel.deleteTugas(tugas)
                        },
                        imageUri = imageUri
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TugasKuliahCard(
    tugas: Tugas,
    onStatusChange: () -> Unit,
    onDelete: () -> Unit,
    imageUri: Uri?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nama Mata Kuliah dan Detail Tugas
            Text(
                text = "Mata Kuliah: ${tugas.matkul}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Detail Tugas: ${tugas.detailTugas}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Menampilkan nama gambar jika ada
            if (tugas.gambarNama.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nama Gambar: ${tugas.gambarNama}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Menampilkan gambar tugas jika ada
            imageUri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Delete dan Status Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Hapus
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Tombol Selesai / Centang
                if (tugas.selesai) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Task Completed",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = onStatusChange,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Selesai", color = Color.White)
                    }
                }
            }
        }
    }
}



@Composable
fun BottomNavigationBar() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val items = listOf(
            BottomNavItem.JadwalKuliah,
            BottomNavItem.TugasKuliah,
            BottomNavItem.Profil,
            BottomNavItem.Logout
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        item.title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                selected = false,
                onClick = {
                    // Handle navigation logic
                },
                alwaysShowLabel = true,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? = remember { null }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Tombol untuk mengambil gambar
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)  // Posisikan tombol di atas navigation bar
        ) {
            IconButton(
                onClick = {
                    val photoFile = File(
                        context.externalCacheDir,
                        "photo_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                // Simpan gambar ke galeri
                                saveImageToGallery(context, savedUri)
                                // Callback untuk hasil yang disimpan
                                onImageCaptured(savedUri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                onError(exception)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(8.dp)
            ) {
                // Lingkaran putih sebagai tombol ambil gambar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Gray, shape = CircleShape)
                )
            }
        }
    }
}


fun saveImageToGallery(context: Context, imageUri: Uri) {
    val contentResolver = context.contentResolver
    val fileName = "photo_${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
    }

    val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri?.let {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val outputStream: OutputStream? = contentResolver.openOutputStream(it)
        inputStream?.copyTo(outputStream!!)
        inputStream?.close()
        outputStream?.close()
    }
}

