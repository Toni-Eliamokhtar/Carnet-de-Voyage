package com.example.travail_pratique_synthese_toni_et_farid.routes

sealed class Routes(val route: String) {
    data object Connexion : Routes("connexion")
    data object Accueil : Routes("accueil")
    data object ListeLieux : Routes("liste_lieux")

    // Route pour afficher le détail d'un lieu
    data object DetailLieu : Routes("detail_lieu/{lieuId}") {
        fun createRoute(lieuId: String) = "detail_lieu/$lieuId"
    }
}
