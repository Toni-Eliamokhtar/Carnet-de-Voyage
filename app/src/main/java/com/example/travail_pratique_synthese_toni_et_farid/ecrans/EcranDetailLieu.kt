package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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
                title = { Text(stringResource(R.string.place_detail_title)) }
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
                Text(stringResource(R.string.place_not_found))
                return@Column
            }

            // Ici je montre les informations principales du lieu
            // Je vais ajouter la mini-carte Google Maps et la photo plus tard
            Text(
                text = stringResource(R.string.name_label, lieu.nom),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = stringResource(R.string.description_label, lieu.description))
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = stringResource(R.string.address_label, lieu.adresse))
            Spacer(modifier = Modifier.height(16.dp))

            // Mini-carte Google Maps centrée sur les coordonnées du lieu (si on a une vraie position)
            if (lieu.latitude != 0.0 && lieu.longitude != 0.0) {
                val positionLieu = LatLng(lieu.latitude, lieu.longitude)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(positionLieu, 10f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = positionLieu),
                        title = lieu.nom.ifBlank { stringResource(R.string.place_fallback) },
                        snippet = lieu.description
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton pour ouvrir la localisation précise dans Google Maps
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
                    Text(stringResource(R.string.open_in_google_maps))
                }
            } else {
                // Si on n'a pas de position exacte, on ouvre juste Google Maps sans chercher l'adresse
                Button(
                    onClick = {
                        val uri = Uri.parse("geo:0,0")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.open_google_maps))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.back))
            }
        }
    }
}
