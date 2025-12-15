package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import androidx.compose.ui.Alignment

/* Écran qui gère :
- La liste des lieux (lecture depuis Firebase)
- La suppression de lieux (écriture directe sur Firebase)
*/
@Composable
fun EcranListeLieux(
    navController: NavController,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    // On observe la liste des lieux exposée par le ViewModel
    // L'UI se met à jour automatiquement quand Firebase change
    val lieux by lieuxViewModel.lieux.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Ligne du haut : titre + boutons
        Text(
            text = stringResource(R.string.places_list_title),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Routes.FormLieu.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ajouter")
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.back))
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Affichage de la liste des lieux
        if (lieux.isEmpty()) {
            Text(stringResource(R.string.no_places_yet))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lieux) { lieu ->
                    LieuItem(
                        lieu = lieu,
                        onSupprimer = {
                            if (lieu.id.isNotBlank()) {
                                lieuxViewModel.supprimerLieu(lieu.id)
                            }
                        },
                        onVoirDetail = {
                            if (lieu.id.isNotBlank()) {
                                // Navigation vers l'écran de détails en passant l'id dans la route
                                navController.navigate(
                                    Routes.DetailLieu.createRoute(lieu.id)
                                )
                            }
                        },
                        onModifier = {
                            if (lieu.id.isNotBlank()) {
                                navController.navigate(
                                    Routes.FormLieuModifier.createRoute(lieu.id)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LieuItem(
    lieu: Lieu,
    onSupprimer: () -> Unit,
    onVoirDetail: () -> Unit,
    onModifier: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Clic sur la carte = ouvrir les détails du lieu
            .clickable { onVoirDetail() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = lieu.nom.ifBlank { stringResource(R.string.unnamed_place) },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lieu.description.ifBlank { stringResource(R.string.no_description) },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onModifier) {
                    Text("Modifier")
                }
                Button(onClick = onSupprimer) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}
