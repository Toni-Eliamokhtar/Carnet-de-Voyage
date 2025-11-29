package com.example.travail_pratique_synthese_toni_et_farid.routes

sealed class Routes(val route: String) {
    data object Connexion : Routes("connexion")
    data object Accueil : Routes("accueil")
    data object ListeLieux : Routes("liste_lieux")
}