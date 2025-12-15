package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDetailLieu(
    navController: NavController,
    lieuId: String,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    val lieux by lieuxViewModel.lieux.collectAsState()
    val lieu = lieux.find { it.id == lieuId }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.place_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (lieu == null) {
                Text(stringResource(R.string.place_not_found))
                return@Column
            }

            // Photo du lieu
            if (lieu.imageUrl.isNotBlank()) {
                val bitmap = remember(lieu.imageUrl) {
                    BitmapFactory.decodeFile(lieu.imageUrl)
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Photo du lieu",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Text(
                text = stringResource(R.string.name_label, lieu.nom),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(stringResource(R.string.description_label, lieu.description))
            Spacer(modifier = Modifier.height(8.dp))

            Text(stringResource(R.string.address_label, lieu.adresse))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(
                            Routes.FormLieuModifier.createRoute(lieu.id)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Modifier")
                }

                Button(
                    onClick = {
                        lieuxViewModel.supprimerLieu(lieu.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.delete))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val lienMaps =
                        if (lieu.latitude != 0.0 && lieu.longitude != 0.0)
                            "https://www.google.com/maps/search/?api=1&query=${lieu.latitude},${lieu.longitude}"
                        else
                            "https://www.google.com/maps"

                    val message = buildString {
                        append("Lieu : ${lieu.nom}\n")
                        if (lieu.description.isNotBlank()) append("Description : ${lieu.description}\n")
                        if (lieu.adresse.isNotBlank()) append("Adresse : ${lieu.adresse}\n")
                        append("Carte : $lienMaps")
                    }

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Lieu à visiter : ${lieu.nom}")
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    context.startActivity(Intent.createChooser(intent, "Partager"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Partager")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Maps
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
                        title = lieu.nom,
                        snippet = lieu.description
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val uri = Uri.parse(
                            "geo:${lieu.latitude},${lieu.longitude}?q=${lieu.latitude},${lieu.longitude}(${Uri.encode(lieu.nom)})"
                        )
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.open_in_google_maps))
                }
            } else {
                Button(
                    onClick = {
                        val uri = Uri.parse("geo:0,0")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.open_google_maps))
                }
            }
        }
    }
}
