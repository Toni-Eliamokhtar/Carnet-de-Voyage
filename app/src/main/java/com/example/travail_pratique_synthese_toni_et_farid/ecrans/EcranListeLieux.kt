package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import androidx.compose.foundation.clickable

/* Écran qui gère :
- La liste des lieux (lecture depuis Firebase)
- L'ajout (avec un formulaire) / Suppression de lieux (écriture directe sur Firebase)
*/
@Composable
fun EcranListeLieux(
    navController: NavController,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    // On observe la liste des lieux exposée par le ViewModel
    // L'UI se met à jour automatiquement quand Firebase change
    val lieux by lieuxViewModel.lieux.collectAsState(initial = emptyList())

    // États des champs du formulaire
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Message d'erreur pour le champ nom (il est obligatoire)
    var errorNom by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Ligne du haut : titre + retour vers l'écran d'accueil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Liste des lieux", style = MaterialTheme.typography.titleLarge)

            Button(onClick = { navController.popBackStack() }) {
                Text("Retour")
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Formulaire d'ajout d'un lieu
        OutlinedTextField(
            value = nom,
            onValueChange = {
                nom = it
                // On enlève le message d'erreur si l'utilisateur commence à taper un nom
                if (it.isNotBlank()) {
                    errorNom = null
                }
            },
            label = { Text("Nom du lieu*") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorNom != null
        )

        if (errorNom != null) {
            Text(
                text = errorNom!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (optionnel)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Bouton qui valide le formulaire et ajoute un lieu dans Firebase
        Button(
            onClick = {
                if (nom.isBlank()) {
                    // on empêche l'ajout si le nom est vide
                    errorNom = "Le nom du lieu est obligatoire."
                    return@Button
                }

                // Pour l'instant les coordonnées sont fixes
                // Elles seront remplacées par la vraie géolocalisation dans la partie Maps
                val lieu = Lieu(
                    nom = nom,
                    description = description,
                    latitude = 45.5,   // temporaire jusqu'à ajout de Maps
                    longitude = -73.6,
                    imageUrl = "",
                    userId = "demo"
                )
                lieuxViewModel.ajouterLieu(lieu)

                // Reset du formulaire d'ajout
                nom = ""
                description = ""
                errorNom = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter ce lieu")
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Affichage de la liste des lieux
        if (lieux.isEmpty()) {
            Text("Aucun lieu pour le moment.")
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
    onVoirDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Clic sur la carte = ouvrir les détails du lieu
            .clickable { onVoirDetail() }   // 👈 clic sur la carte
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = lieu.nom.ifBlank { "Sans nom" },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lieu.description.ifBlank { "Aucune description" },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            // Bouton de suppression du lieu
            Button(onClick = onSupprimer) {
                Text("Supprimer")
            }
        }
    }
}
