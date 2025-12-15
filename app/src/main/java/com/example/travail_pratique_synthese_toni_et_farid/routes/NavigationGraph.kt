package com.example.travail_pratique_synthese_toni_et_farid.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.travail_pratique_synthese_toni_et_farid.Modele.itemsNavBas
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranAccueil
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranConnexion
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranListeLieux
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranDetailLieu
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranProfilParametres

/*
 Toutes les routes sont déclarées ici
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    // Permet de connaître l'écran actuellement affiché
    val backStackEntry by navController.currentBackStackEntryAsState()
    val routeCourante = backStackEntry?.destination?.route

    // Permet d'afficher la barre de navigation seulement sur les écrans principaux
    val afficherBarreBas =
        routeCourante != Routes.Connexion.route &&
                routeCourante != Routes.DetailLieu.route

    Scaffold(
        // Barre de navigation du bas
        bottomBar = {
            if (afficherBarreBas) {
                NavigationBar {
                    itemsNavBas.forEach { item ->
                        NavigationBarItem(
                            selected = routeCourante == item.route,
                            onClick = {
                                // Navigation sans recréer plusieurs fois le même écran
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    popUpTo(Routes.Accueil.route) { inclusive = false }
                                }

                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(stringResource(item.labelRes))
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Connexion.route,
            modifier = Modifier.padding(paddingValues)
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

            // Écran profil / paramètres
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
        }
    }
}
