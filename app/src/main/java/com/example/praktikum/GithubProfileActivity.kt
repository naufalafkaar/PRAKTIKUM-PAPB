// GithubProfileActivity.kt
package com.example.praktikum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.praktikum.network.GithubApiService
import com.example.praktikum.network.GithubProfile
import com.example.praktikum.ui.theme.PRAKTIKUMTheme
import kotlinx.coroutines.launch

class GithubProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRAKTIKUMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GithubProfileScreen()
                }
            }
        }
    }
}

@Composable
fun GithubProfileScreen() {
    var profile by remember { mutableStateOf<GithubProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Mulai fetch data dari GitHub API
    LaunchedEffect(Unit) {
        scope.launch {
            val apiService = GithubApiService.create()
            try {
                profile = apiService.getProfile("naufalafkaar")
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    // Tampilkan UI sesuai dengan status loading atau error
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    } else if (profile != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gambar Profil
            val image: Painter = rememberAsyncImagePainter(model = profile!!.avatar_url)
            Image(
                painter = image,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            Text(text = "Username: ${profile!!.login}", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            // Nama
            Text(text = "Name: ${profile!!.name ?: "Unknown"}", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            // Followers
            Text(text = "Followers: ${profile!!.followers}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            // Following
            Text(text = "Following: ${profile!!.following}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}
