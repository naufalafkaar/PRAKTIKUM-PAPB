// ListActivity.kt
package com.example.praktikum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.praktikum.ui.theme.PRAKTIKUMTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ListActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRAKTIKUMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Jadwal Kuliah") },
                                actions = {
                                    // Gunakan GithubIconButton dengan onClick untuk navigasi
                                    GithubIconButton {
                                        val intent = Intent(this@ListActivity, GithubProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        // Teruskan padding dari Scaffold ke JadwalKuliahScreen
                        JadwalKuliahScreen(padding = paddingValues)
                    }
                }
            }
        }
    }
}

// Fungsi composable untuk tombol dengan ikon GitHub
@Composable
fun GithubIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        // Memuat gambar ikon dari drawable menggunakan painterResource
        Image(
            painter = painterResource(id = R.drawable.github_mark), // Sesuaikan nama file dengan yang ada di drawable
            contentDescription = "GitHub Icon",
            modifier = Modifier.size(24.dp) // Menyesuaikan ukuran ikon
        )
    }
}

// Fungsi composable untuk menampilkan Jadwal Kuliah dengan parameter padding
@Composable
fun JadwalKuliahScreen(padding: PaddingValues) {
    val firestore = FirebaseFirestore.getInstance()
    val jadwalList = remember { mutableStateListOf<MataKuliah>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetching the data from Firestore
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = firestore.collection("JadwalKuliah").get().await()
            val data = result.documents.map { document ->
                MataKuliah(
                    hari = document.getString("hari") ?: "",
                    jam = document.getString("jam") ?: "",
                    mataKuliah = document.getString("mata_kuliah") ?: "",
                    ruang = document.getString("ruang") ?: "",
                    isPraktikum = document.getBoolean("is_praktikum") ?: false
                )
            }
            jadwalList.addAll(data)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching documents: ${e.localizedMessage}")
            errorMessage = e.localizedMessage ?: "Error fetching data"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding) // Gunakan padding dari Scaffold
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jadwal Kuliah",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (jadwalList.isEmpty()) {
            Text(
                text = "No classes available.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn {
                items(jadwalList.size) { index ->
                    KuliahCard(jadwalList[index])
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Fungsi composable untuk menampilkan kartu kuliah
@Composable
fun KuliahCard(mataKuliah: MataKuliah) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Mata Kuliah: ${mataKuliah.mataKuliah}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Hari: ${mataKuliah.hari}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Jam: ${mataKuliah.jam}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ruang: ${mataKuliah.ruang}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (mataKuliah.isPraktikum) "Praktikum" else "Teori",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
