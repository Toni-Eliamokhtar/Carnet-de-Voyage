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
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranProfilParametres
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranFormLieu

/*
 Toutes les routes sont déclarées ici
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Connexion.route
    ) {
        // Écran de connexion
        composable(Routes.Connexion.route) {
            EcranConnexion(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // Écran d'accueil après la connexion
        composable(Routes.Accueil.route) {
            EcranAccueil(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // Écran profil / paramètres (Farid)
        composable(Routes.Profil.route) {
            EcranProfilParametres(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // Écran qui affiche et gère les lieux
        composable(Routes.ListeLieux.route) {
            EcranListeLieux(navController = navController)
        }

        // Écran de détail d'un lieu avec paramètre "lieuId"
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

        // Écran de gestion de lieux (ajout)
        composable(Routes.FormLieu.route) {
            EcranFormLieu(navController = navController, lieuId = null)
        }

        // Écran de gestion de lieux (modification)
        composable(
            route = Routes.FormLieuModifier.route,
            arguments = listOf(navArgument("lieuId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lieuId = backStackEntry.arguments?.getString("lieuId")
            EcranFormLieu(navController = navController, lieuId = lieuId)
        }
    }
}
