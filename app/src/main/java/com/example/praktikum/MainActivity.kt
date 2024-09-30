package com.example.praktikum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.praktikum.ui.theme.PRAKTIKUMTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
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
                    LoginScreen(auth = auth) { onLoginSuccess() }
                }
            }
        }
    }

    private fun onLoginSuccess() {
        // Log success and attempt navigation
        Log.d("MainActivity", "Login successful, attempting to navigate to ListActivity")

        try {
            // Navigate to ListActivity
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity after login
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to navigate to ListActivity: ${e.localizedMessage}")
            Toast.makeText(this, "Navigation failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
    var unexpectedError by remember { mutableStateOf("") } // To handle unexpected errors
    val isFormValid = inputEmail.isNotEmpty() && emailError.isEmpty() && passwordError.isEmpty() && inputPassword.length >= 8

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
                Log.d("LoginScreen", "Submit button clicked, attempting login for user: $inputEmail")

                loginUser(auth, inputEmail, inputPassword) { success, message ->
                    if (success) {
                        Log.d("LoginScreen", "Login successful, navigating to ListActivity")
                        try {
                            onLoginSuccess() // Navigate to the ListActivity
                        } catch (e: Exception) {
                            unexpectedError = "Failed to navigate: ${e.localizedMessage}"
                            Log.e("LoginScreen", "Navigation error: ${e.localizedMessage}")
                        }
                    } else {
                        loginError = message
                        Log.e("LoginScreen", "Login failed: $message")
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        // Display login error message if login fails
        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Display unexpected error message (e.g., navigation issues)
        if (unexpectedError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = unexpectedError,
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
                Log.d("LoginUser", "Firebase login success for user: $email")
                onResult(true, "")
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Login gagal."
                Log.e("LoginUser", "Firebase login failed: $errorMessage")
                onResult(false, errorMessage)
            }
        }
}
