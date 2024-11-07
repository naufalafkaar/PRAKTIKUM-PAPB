package com.example.praktikum

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.praktikum.ui.theme.PRAKTIKUMTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.praktikum.navigation.MainScreen
import android.Manifest


class MainActivity : ComponentActivity() {
    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            PRAKTIKUMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(auth)
                }
            }
        }
    }
}

@Composable
fun MainApp(auth: FirebaseAuth) {
    val isLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

    if (isLoggedIn.value) {
        // Tampilkan MainScreen jika sudah login
        MainScreen(onLogout = {
            // Saat logout, ubah status login
            auth.signOut()
            isLoggedIn.value = false
        })
    } else {
        // Tampilkan layar login jika belum login
        LoginScreen(auth = auth) {
            // Jika login sukses, ubah status login
            isLoggedIn.value = true
        }
    }
}
@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Jika izin diberikan, panggil fungsi yang diberikan
            onPermissionGranted()
        } else {
            // Jika izin ditolak, panggil fungsi yang diberikan
            onPermissionDenied()
        }
    }

    // LaunchedEffect untuk meminta izin
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            onPermissionGranted()
        }
    }
}


@Composable
fun LoginScreen(auth: FirebaseAuth, onLoginSuccess: () -> Unit) {
    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    val isFormValid = inputEmail.isNotEmpty() && inputPassword.length >= 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input Field for Email
        TextField(
            value = inputEmail,
            onValueChange = {
                inputEmail = it
                emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                    "Email tidak valid."
                } else {
                    ""
                }
            },
            label = { Text("Masukkan Email Kamu") },
            isError = emailError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field for Password
        TextField(
            value = inputPassword,
            onValueChange = {
                inputPassword = it
                passwordError = if (it.length < 8) "Password harus terdiri dari minimal 8 karakter." else ""
            },
            label = { Text("Masukkan Password Kamu") },
            isError = passwordError.isNotEmpty(),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                loginUser(auth, inputEmail, inputPassword) { success, message ->
                    if (success) {
                        onLoginSuccess() // Jika login berhasil, panggil onLoginSuccess
                    } else {
                        loginError = message
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        // Display error message if login fails
        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun loginUser(auth: FirebaseAuth, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "")
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Login gagal."
                onResult(false, errorMessage)
            }
        }
}

