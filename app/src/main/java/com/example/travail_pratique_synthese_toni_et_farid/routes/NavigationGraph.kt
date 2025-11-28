package com.example.travail_pratique_synthese_toni_et_farid.routes

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranAccueil
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranConnexion
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = ViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Connexion.route
    ) {
        composable(Routes.Connexion.route) {
            EcranConnexion(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(Routes.Accueil.route) {
            EcranAccueil(
                navController = navController,
                viewModel = authViewModel
            )
        }
    }
}
