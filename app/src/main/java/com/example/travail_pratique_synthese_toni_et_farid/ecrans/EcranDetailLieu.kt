package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDetailLieu(
    navController: NavController,
    lieuId: String,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    val lieux by lieuxViewModel.lieux.collectAsState()

    // On récupère le lieu correspondant à l'id
    val lieu = lieux.find { it.id == lieuId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail du lieu") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (lieu == null) {
                Text("Lieu introuvable.")
                return@Column
            }

            Text(text = "Nom : ${lieu.nom}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Description : ${lieu.description}")
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Latitude : ${lieu.latitude}")
            Text(text = "Longitude : ${lieu.longitude}")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Retour")
            }
        }
    }
}
