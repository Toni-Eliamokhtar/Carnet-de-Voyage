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

/*
* Écran d'accueil après la connexion
*/
@Composable
fun EcranAccueil(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val user = viewModel.utilisateurCourant

    val lieuxViewModel: LieuxViewModel = viewModel() // ViewModel des lieux pour afficher des stats
    val lieux by lieuxViewModel.lieux.collectAsStateWithLifecycle()

    val totalLieux = lieux.size
    val dernierLieuNom = lieux.lastOrNull()?.nom?.takeIf { it.isNotBlank() } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_title_places),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(
                R.string.welcome_user,
                user?.email ?: stringResource(R.string.unknown_user)
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        // Carte "tableau de bord" avec vraies données provenant de Firebase
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

        // Bouton qui mène vers l'écran avec la liste des lieux
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
