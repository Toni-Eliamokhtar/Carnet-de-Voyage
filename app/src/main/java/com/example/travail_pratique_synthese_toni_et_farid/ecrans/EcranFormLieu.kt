package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/* Écran qui gère :
- L'ajout d'un lieu avec formulaire
- La géolocalisation et les permissions associées
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranFormLieu(
    navController: NavController,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    val context = LocalContext.current

    // États des champs du formulaire
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }

    // Message d'erreur pour le champ nom (il est obligatoire)
    var errorNom by remember { mutableStateOf<String?>(null) }
    var errorAdresse by remember { mutableStateOf<String?>(null) }

    // --- Gestion de la géolocalisation (latitude / longitude de l'utilisateur) ---
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
                    adresse = context.getString(R.string.current_position)
                    messageErreurLocalisation = null
                } ?: run {
                    messageErreurLocalisation =
                        context.getString(R.string.error_location_unavailable)
                }
            }
        } else {
            messageErreurLocalisation =
                context.getString(R.string.error_location_permission_denied)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_this_place)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // 🔹 Formulaire d'ajout d'un lieu
            OutlinedTextField(
                value = nom,
                onValueChange = {
                    nom = it
                    if (it.isNotBlank()) errorNom = null
                },
                label = { Text(stringResource(R.string.place_name_required_star)) },
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
                label = { Text(stringResource(R.string.description_optional)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = adresse,
                onValueChange = {
                    adresse = it
                    errorAdresse = null
                    latitude = null
                    longitude = null
                },
                label = { Text(stringResource(R.string.address_required_if_no_position)) },
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

            Button(
                onClick = {
                    val permissionState = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    if (permissionState == PackageManager.PERMISSION_GRANTED) {
                        fournisseurLocalisation.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        ).addOnSuccessListener { localisation ->
                            localisation?.let {
                                latitude = it.latitude
                                longitude = it.longitude
                                adresse = context.getString(R.string.current_position)
                                messageErreurLocalisation = null
                            } ?: run {
                                messageErreurLocalisation =
                                    context.getString(R.string.error_location_unavailable)
                            }
                        }
                    } else {
                        demandePermissionLocalisation.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.use_my_current_position))
            }

            latitude?.let { lat ->
                longitude?.let { lon ->
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.latitude_label, lat.toString()))
                    Text(stringResource(R.string.longitude_label, lon.toString()))
                }
            }

            if (messageErreurLocalisation != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = messageErreurLocalisation!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.back))
                }

                Button(
                    onClick = {
                        if (nom.isBlank()) {
                            errorNom =
                                context.getString(R.string.error_place_name_required)
                            return@Button
                        }

                        if (latitude == null && adresse.isBlank()) {
                            errorAdresse =
                                context.getString(R.string.error_address_or_position_required)
                            return@Button
                        }

                        val lieu = Lieu(
                            nom = nom,
                            description = description,
                            latitude = latitude ?: 0.0,
                            longitude = longitude ?: 0.0,
                            adresse = adresse,
                            imageUrl = "",
                            userId = "demo"
                        )

                        lieuxViewModel.ajouterLieu(lieu)

                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.add_this_place))
                }
            }
        }
    }
}
