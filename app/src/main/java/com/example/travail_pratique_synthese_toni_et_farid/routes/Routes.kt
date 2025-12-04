package com.example.travail_pratique_synthese_toni_et_farid.routes

sealed class Routes(val route: String) {
    data object Connexion : Routes("connexion")
    data object Accueil : Routes("accueil")
    data object ListeLieux : Routes("liste_lieux")

    // Route pour afficher le détail d'un lieu
    // On attend un paramètre d'URL ici : {lieuId}
    data object DetailLieu : Routes("detail_lieu/{lieuId}") {
        // On utilise un Helper pour construire la vraie route avec un id
        fun createRoute(lieuId: String) = "detail_lieu/$lieuId"
    }
}
