package com.example.travail_pratique_synthese_toni_et_farid.routes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranAccueil
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranConnexion
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranListeLieux
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranDetailLieu

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
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
        composable(Routes.ListeLieux.route) {
            EcranListeLieux(navController = navController)
        }

        composable(
            route = Routes.DetailLieu.route,
            arguments = listOf(
                navArgument("lieuId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lieuId = backStackEntry.arguments?.getString("lieuId") ?: ""

            EcranDetailLieu(
                navController = navController,
                lieuId = lieuId
            )
        }


    }
}
