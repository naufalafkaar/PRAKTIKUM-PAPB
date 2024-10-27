// TugasKuliahScreen.kt
package com.example.praktikum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktikum.database.TugasKuliahEntity

@Composable
fun TugasKuliahScreen(viewModel: TugasKuliahViewModel = viewModel()) {
    var mataKuliah by remember { mutableStateOf(TextFieldValue("")) }
    var detailTugas by remember { mutableStateOf(TextFieldValue("")) }
    var submissionStatus by remember { mutableStateOf("") }

    // Mengambil daftar tugas dari ViewModel
    val tugasList by viewModel.tugasList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Input Tugas Kuliah",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = mataKuliah,
            onValueChange = { mataKuliah = it },
            label = { Text("Nama Mata Kuliah") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = detailTugas,
            onValueChange = { detailTugas = it },
            label = { Text("Detail Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (mataKuliah.text.isNotEmpty() && detailTugas.text.isNotEmpty()) {
                    val newTugas = TugasKuliahEntity(
                        mataKuliah = mataKuliah.text,
                        detailTugas = detailTugas.text,
                        isDone = false
                    )

                    viewModel.insertTugas(newTugas)
                    submissionStatus = "Tugas untuk ${mataKuliah.text} berhasil disimpan!"
                    mataKuliah = TextFieldValue("")
                    detailTugas = TextFieldValue("")
                } else {
                    submissionStatus = "Mohon isi semua kolom sebelum submit."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (submissionStatus.isNotEmpty()) {
            Text(
                text = submissionStatus,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menampilkan daftar tugas dari ViewModel
        LazyColumn {
            items(tugasList.size) { index ->
                TugasKuliahCard(tugas = tugasList[index], onStatusChange = {
                    viewModel.updateTugasStatus(tugasList[index].id, true)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TugasKuliahCard(tugas: TugasKuliahEntity, onStatusChange: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Mata Kuliah: ${tugas.mataKuliah}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Detail Tugas: ${tugas.detailTugas}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (tugas.isDone) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(onClick = onStatusChange) {
                    Text("Is Done")
                }
            }
        }
    }
}
