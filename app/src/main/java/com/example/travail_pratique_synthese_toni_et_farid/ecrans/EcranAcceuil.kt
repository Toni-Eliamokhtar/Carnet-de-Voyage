package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.home_title_places))

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(
                R.string.welcome_user,
                user?.email ?: stringResource(R.string.unknown_user)
            )
        )

        Spacer(Modifier.height(24.dp))

        // Bouton qui mène vers l'écran avec la liste des lieux
        Button(
            onClick = {
                navController.navigate(Routes.ListeLieux.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.see_places))
        }

        Button(
            onClick = { navController.navigate(Routes.Profil.route) },
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
