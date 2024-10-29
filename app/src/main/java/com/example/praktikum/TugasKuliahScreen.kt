// TugasKuliahScreen.kt
package com.example.praktikum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktikum.database.Tugas

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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        OutlinedTextField(
            value = detailTugas,
            onValueChange = { detailTugas = it },
            label = { Text("Detail Tugas") },
            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Button(
            onClick = {
                if (mataKuliah.text.isNotEmpty() && detailTugas.text.isNotEmpty()) {
                    val newTugas = Tugas(
                        matkul = mataKuliah.text,
                        detailTugas = detailTugas.text,
                        selesai = false
                    )

                    viewModel.insertTugas(newTugas)
                    submissionStatus = "Tugas untuk ${mataKuliah.text} berhasil disimpan!"
                    mataKuliah = TextFieldValue("")
                    detailTugas = TextFieldValue("")
                } else {
                    submissionStatus = "Mohon isi semua kolom sebelum submit."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Submit", color = Color.White)
        }

        if (submissionStatus.isNotEmpty()) {
            Text(
                text = submissionStatus,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tugasList.size) { index ->
                TugasKuliahCard(
                    tugas = tugasList[index],
                    onStatusChange = {
                        viewModel.updateTugasStatus(tugasList[index].id, true)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TugasKuliahCard(tugas: Tugas, onStatusChange: () -> Unit) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (tugas.selesai) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = onStatusChange,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Is Done", color = Color.White)
                }
            }
        }
    }
}
