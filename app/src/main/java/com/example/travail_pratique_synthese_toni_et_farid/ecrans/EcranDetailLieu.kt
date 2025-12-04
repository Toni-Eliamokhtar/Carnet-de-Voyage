package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api

// Cet écran affiche les détails d'un lieu sélectionné dans la liste
// Pour l'instant, il montre seulement les infos en texte, pas de google maps pas de rien.
// Pour y accéder : Il faut cliquer sur un lieu de la liste, tout simplement
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDetailLieu(
    navController: NavController,
    lieuId: String,                                 // id du lieu reçu par la navigation
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    // On observe la liste des lieux actuelle (c'est le même StateFlow que dans la liste)
    val lieux by lieuxViewModel.lieux.collectAsState()

    // On récupère le lieu correspondant à l'id passé dans la route
    val lieu = lieux.find { it.id == lieuId }

    val context = LocalContext.current

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
                // Si l'id ne correspond à aucun lieu, on affiche ça
                // (au cas où il y a un problème avec des données supprimées par exemple)
                Text("Lieu introuvable.")
                return@Column
            }

            // Ici je montre les informations principales du lieu
            // Je vais ajouter la mini-carte Google Maps et la photo plus tard
            Text(text = "Nom : ${lieu.nom}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Description : ${lieu.description}")
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Latitude : ${lieu.latitude}")
            Text(text = "Longitude : ${lieu.longitude}")
            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour ouvrir la localisation dans Google Maps
            Button(
                onClick = {
                    val uri = Uri.parse(
                        "geo:${lieu.latitude},${lieu.longitude}?q=${lieu.latitude},${lieu.longitude}(${Uri.encode(lieu.nom)})"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ouvrir dans Google Maps")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Retour")
            }
        }
    }
}
