package com.example.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.data.CryptoDatabase
import com.example.cryptotracker.network.CoinDto
import com.example.cryptotracker.repository.CryptoRepository
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import com.example.cryptotracker.windows.AddWindow
import com.example.cryptotracker.windows.MainWindow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = CryptoDatabase.getDatabase(applicationContext)
        val repository = CryptoRepository(db.cryptoDao())
        val viewModel = CryptoViewModel(repository)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainWindow(
                        viewModel = viewModel,
                        onNavigateToAddForm = { navController.navigate("addForm") }
                    )
                }
                composable("addForm") {
                    AddWindow(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }


        }
    }
}