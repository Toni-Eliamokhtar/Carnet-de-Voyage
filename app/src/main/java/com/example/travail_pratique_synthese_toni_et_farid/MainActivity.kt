package com.example.travail_pratique_synthese_toni_et_farid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.travail_pratique_synthese_toni_et_farid.routes.NavigationGraph
import com.example.travail_pratique_synthese_toni_et_farid.ui.theme.TPSyntheseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPSyntheseTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    NavigationGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}
