package com.example.cryptotracker

import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.data.CryptoDatabase
import com.example.cryptotracker.network.CoinDto
import com.example.cryptotracker.repository.CryptoRepository
import com.example.cryptotracker.repository.TransactionRepository
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import com.example.cryptotracker.windows.AddNotificationWindow
import com.example.cryptotracker.windows.AddWindow
import com.example.cryptotracker.windows.DetailWindow
import com.example.cryptotracker.windows.MainWindow
import com.example.cryptotracker.windows.TransactionWindow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = CryptoDatabase.getDatabase(applicationContext)
        val Crepository = CryptoRepository(db.cryptoDao())
        val Trepository = TransactionRepository(db.transDao())
        val viewModel = CryptoViewModel(Crepository, Trepository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainWindow(
                        viewModel = viewModel,
                        onNavigateToAddForm = { navController.navigate("addForm") },
                        onNavigateToDetail = { coinId ->
                            navController.navigate("detail/$coinId")
                        },
                        onNavigateToNotification =  {navController.navigate("addNotification")},
                        onNavigateToHistory =  {navController.navigate("transactions")}
                    )
                }
                composable("addForm") {
                    AddWindow(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("detail/{coinId}") { backStackEntry ->
                    val coinId = backStackEntry.arguments?.getString("coinId") ?: return@composable
                    DetailWindow(
                        coinId = coinId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("addNotification") {
                    AddNotificationWindow(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("transactions"){
                    TransactionWindow(
                        viewModel = viewModel,
                        onBack = {navController.popBackStack()}
                    )
                }
            }


        }
    }
}