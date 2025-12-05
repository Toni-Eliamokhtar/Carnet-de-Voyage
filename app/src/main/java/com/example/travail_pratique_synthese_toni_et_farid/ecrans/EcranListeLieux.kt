package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

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
    var adresse by remember { mutableStateOf("") }

    // Message d'erreur pour le champ nom (il est obligatoire)
    var errorNom by remember { mutableStateOf<String?>(null) }
    var errorAdresse by remember { mutableStateOf<String?>(null) }

    // --- Gestion de la géolocalisation (latitude / longitude de l'utilisateur) ---
    val context = LocalContext.current
    val fournisseurLocalisation = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var messageErreurLocalisation by remember { mutableStateOf<String?>(null) }

    // Gestionnaire de permission pour la localisation
    val demandePermissionLocalisation = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { autorisee ->
        if (autorisee) {
            fournisseurLocalisation.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { localisation ->
                localisation?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    adresse = "Position actuelle"   // on remplace l'adresse par la position actuelle
                    messageErreurLocalisation = null
                } ?: run {
                    messageErreurLocalisation = "Impossible de récupérer la localisation"
                }
            }
        } else {
            messageErreurLocalisation = "Permission de localisation refusée"
        }
    }

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

        // Adresse obligatoire si on n'utilise pas la position
        OutlinedTextField(
            value = adresse,
            onValueChange = {
                adresse = it
                errorAdresse = null
                // si l'utilisateur change l'adresse manuellement, on enlève la localisation
                latitude = null
                longitude = null
            },
            label = { Text("Adresse (obligatoire si pas de position)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorAdresse != null
        )

        if (errorAdresse != null) {
            Text(
                text = errorAdresse!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))

        // Bouton pour obtenir la localisation actuelle de l'utilisateur
        Button(
            onClick = {
                val permissionState = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (permissionState == PackageManager.PERMISSION_GRANTED) {
                    // Permission déjà accordée
                    fournisseurLocalisation.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { localisation ->
                        localisation?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                            adresse = "Position actuelle"
                            messageErreurLocalisation = null
                        } ?: run {
                            messageErreurLocalisation =
                                "Impossible de récupérer la localisation"
                        }
                    }
                } else {
                    // Demander la permission
                    demandePermissionLocalisation.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Utiliser ma position actuelle")
        }

        // Affichage de la latitude / longitude si disponibles
        latitude?.let { lat ->
            longitude?.let { lon ->
                Spacer(Modifier.height(8.dp))
                Text(text = "Latitude : $lat")
                Text(text = "Longitude : $lon")
            }
        }

        // Message d'erreur de localisation (si nécessaire)
        if (messageErreurLocalisation != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = messageErreurLocalisation!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))

        // Bouton qui valide le formulaire et ajoute un lieu dans Firebase
        Button(
            onClick = {
                if (nom.isBlank()) {
                    // on empêche l'ajout si le nom est vide
                    errorNom = "Le nom du lieu est obligatoire."
                    return@Button
                }

                // on empêche aussi si ni position ni adresse n'ont été fournis
                if (latitude == null && adresse.isBlank()) {
                    errorAdresse = "Vous devez entrer une adresse ou utiliser votre position."
                    return@Button
                }

                // Pour l'instant les coordonnées sont fixes
                // Elles seront remplacées par la vraie géolocalisation dans la partie Maps
                // Si la localisation n'est pas disponible, on garde les valeurs par défaut
                val lieu = Lieu(
                    nom = nom,
                    description = description,
                    latitude = latitude ?: 0.0,   // 0.0 = pas de position précise
                    longitude = longitude ?: 0.0,
                    adresse = adresse,
                    imageUrl = "",
                    userId = "demo"
                )
                lieuxViewModel.ajouterLieu(lieu)

                // Reset du formulaire d'ajout
                nom = ""
                description = ""
                adresse = ""
                errorNom = null
                errorAdresse = null

                // Reset complet de la localisation aussi
                latitude = null
                longitude = null
                messageErreurLocalisation = null

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
            .clickable { onVoirDetail() }   // clic sur la carte
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
