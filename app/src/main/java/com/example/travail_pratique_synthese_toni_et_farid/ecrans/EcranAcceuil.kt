package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel

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
        Text("Accueil - Lieux à visiter")

        Spacer(Modifier.height(16.dp))

        Text("Bienvenue : ${user?.email ?: "Utilisateur inconnu"}")

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.deconnexion()
                navController.navigate(Routes.Connexion.route) {
                    popUpTo(Routes.Accueil.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se déconnecter")
        }
    }
}
