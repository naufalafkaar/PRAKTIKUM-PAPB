// ListActivity.kt
package com.example.praktikum.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.praktikum.entity.MataKuliah
import com.example.praktikum.R
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
                                    GithubIconButton {
                                        val intent = Intent(this@ListActivity, GithubProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        JadwalKuliahScreen(padding = paddingValues)
                    }
                }
            }
        }
    }
}

@Composable
fun GithubIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.github_mark),
            contentDescription = "GitHub Icon",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun JadwalKuliahScreen(padding: PaddingValues) {
    val firestore = FirebaseFirestore.getInstance()
    val jadwalList = remember { mutableStateListOf<MataKuliah>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

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
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jadwal Kuliah",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
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

@Composable
fun KuliahCard(mataKuliah: MataKuliah) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mata Kuliah: ${mataKuliah.mataKuliah}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hari: ${mataKuliah.hari}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "Jam: ${mataKuliah.jam}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "Ruang: ${mataKuliah.ruang}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (mataKuliah.isPraktikum) Icons.Filled.Face else Icons.Filled.ThumbUp,
                    contentDescription = null,
                    tint = if (mataKuliah.isPraktikum) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (mataKuliah.isPraktikum) "Praktikum" else "Teori",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
