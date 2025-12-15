package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import android.content.res.Configuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration

/*
 * Écran d'accueil affiché après la connexion
 */
@Composable
fun EcranAccueil(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    // Utilisateur Firebase actuellement connecté
    val user = viewModel.utilisateurCourant

    // ViewModel des lieux (sert à afficher des statistiques)
    val lieuxViewModel: LieuxViewModel = viewModel()
    val lieux by lieuxViewModel.lieux.collectAsStateWithLifecycle()

    // Données calculées pour le tableau de bord
    val totalLieux = lieux.size
    val dernierLieuNom = lieux.lastOrNull()?.nom?.takeIf { it.isNotBlank() } ?: "-"

    // Détection de l'orientation de l'écran
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Permet de rendre l'écran scrollable
    val scrollState = rememberScrollState()

    // Profil utilisateur venant du ViewModel Auth
    val profil by viewModel.profilUtilisateur.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // Titre principal de l'écran
        Text(
            text = stringResource(R.string.home_title_places),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        // Message de bienvenue (nom affiché ou courriel)
        Text(
            stringResource(
                R.string.welcome_user,
                profil?.nomAffiche?.takeIf { it.isNotBlank() }
                    ?: user?.email
                    ?: stringResource(R.string.unknown_user)
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        // Mise en page différente selon l'orientation
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Carte du tableau de bord
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = stringResource(R.string.dashboard_total_places, totalLieux))
                        Text(text = stringResource(R.string.dashboard_last_added, dernierLieuNom))
                    }
                }

                // Boutons de navigation
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Accès à la liste des lieux
                    Button(
                        onClick = {
                            navController.navigate(Routes.ListeLieux.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.see_places))
                    }

                    // Accès aux paramètres du profil
                    Button(
                        onClick = {
                            navController.navigate(Routes.Profil.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.profile_settings))
                    }

                    // Déconnexion et retour à l'écran de connexion
                    Button(
                        onClick = {
                            viewModel.deconnexion()
                            navController.navigate(Routes.Connexion.route) {
                                popUpTo(Routes.Accueil.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.logout))
                    }
                }
            }
        } else {
            // Mode portrait : affichage vertical

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = stringResource(R.string.dashboard_total_places, totalLieux))
                    Text(text = stringResource(R.string.dashboard_last_added, dernierLieuNom))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Bouton vers la liste des lieux
            Button(
                onClick = {
                    navController.navigate(Routes.ListeLieux.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.see_places))
            }

            // Bouton vers l'écran profil
            Button(
                onClick = {
                    navController.navigate(Routes.Profil.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_settings))
            }

            Spacer(Modifier.height(16.dp))

            // Bouton de déconnexion
            Button(
                onClick = {
                    viewModel.deconnexion()
                    navController.navigate(Routes.Connexion.route) {
                        popUpTo(Routes.Accueil.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}
