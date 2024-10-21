// MainNavigation.kt
package com.example.praktikum

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.material.icons.filled.ExitToApp

// List of screens in the bottom navigation, adding Logout as new item
sealed class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object JadwalKuliah : BottomNavItem("jadwal_kuliah", "Jadwal Kuliah", Icons.Filled.DateRange)
    object TugasKuliah : BottomNavItem("tugas_kuliah", "Tugas Kuliah", Icons.Filled.Edit)
    object Profil : BottomNavItem("profil", "Profil", Icons.Filled.AccountCircle)
    object Logout : BottomNavItem("logout", "Logout", Icons.Filled.ExitToApp)  // Tambahkan item Logout
}

// Main screen with bottom navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, onLogout = onLogout) }
    ) { innerPadding ->
        NavigationHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

// Bottom Navigation Bar component, adding Logout logic
@Composable
fun BottomNavigationBar(navController: NavHostController, onLogout: () -> Unit) {
    NavigationBar {
        val items = listOf(
            BottomNavItem.JadwalKuliah,
            BottomNavItem.TugasKuliah,
            BottomNavItem.Profil,
            BottomNavItem.Logout  // Menambahkan item Logout di Bottom Navigation
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false,  // Add logic to highlight selected item if needed
                onClick = {
                    if (item.route == BottomNavItem.Logout.route) {
                        // Jika item Logout di-klik, jalankan logika logout
                        onLogout()
                    } else {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

// Navigation host to manage different screens
@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = BottomNavItem.JadwalKuliah.route) {
        composable(BottomNavItem.JadwalKuliah.route) {
            JadwalKuliahScreen(padding = PaddingValues()) // Use your existing screen
        }
        composable(BottomNavItem.TugasKuliah.route) {
            TugasKuliahScreen()
        }
        composable(BottomNavItem.Profil.route) {
            GithubProfileScreen() // Use your existing Github profile screen
        }
    }
}
