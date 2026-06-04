package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.MedTrackRepository
import com.example.ui.MainAppContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MedTrackViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Securely initialize Room sandboxed repository database
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = MedTrackRepository(database)

        // Generate VM using our robust factory class
        val viewModel: MedTrackViewModel by viewModels {
            MedTrackViewModel.Factory(application, repository)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Main App Layout centered around HIPAA client tabs deck
                    MainAppContainer(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
