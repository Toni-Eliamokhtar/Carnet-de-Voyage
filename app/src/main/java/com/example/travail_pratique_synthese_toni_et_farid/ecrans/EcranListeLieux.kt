package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.notifications.NotificationsLieux
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranListeLieux(
    navController: NavController,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    val lieux by lieuxViewModel.lieux.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.places_list_title)) },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { navController.navigate(Routes.FormLieu.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ajouter")
                }

                Spacer(Modifier.height(8.dp))
            }

            if (lieux.isEmpty()) {
                item {
                    Text(stringResource(R.string.no_places_yet))
                }
            } else {
                items(lieux) { lieu ->
                    LieuItem(
                        lieu = lieu,
                        onSupprimer = {
                            if (lieu.id.isNotBlank()) {
                                val nomLieu = lieu.nom.ifBlank { context.getString(R.string.place_fallback) }
                                lieuxViewModel.supprimerLieu(lieu.id)
                                NotificationsLieux.notifierSuppression(context, nomLieu)
                            }
                        },
                        onVoirDetail = {
                            if (lieu.id.isNotBlank()) {
                                navController.navigate(Routes.DetailLieu.createRoute(lieu.id))
                            }
                        },
                        onModifier = {
                            if (lieu.id.isNotBlank()) {
                                navController.navigate(Routes.FormLieuModifier.createRoute(lieu.id))
                            }
                        }
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
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
            .clickable { onVoirDetail() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val miniPhoto = remember(lieu.imageUrl) {
                if (lieu.imageUrl.isNotBlank()) BitmapFactory.decodeFile(lieu.imageUrl) else null
            }

            if (miniPhoto != null) {
                Image(
                    bitmap = miniPhoto.asImageBitmap(),
                    contentDescription = "Mini photo du lieu",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("—")
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lieu.nom.ifBlank { stringResource(R.string.unnamed_place) },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    Button(onClick = onModifier) { Text("Modifier") }
                    Button(onClick = onSupprimer) { Text(stringResource(R.string.delete)) }
                }
            }
        }
    }
}
