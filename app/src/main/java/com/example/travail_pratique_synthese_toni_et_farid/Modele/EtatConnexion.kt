package com.example.travail_pratique_synthese_toni_et_farid.Modele

data class EtatConnexion(
    val courriel: String = "",
    val motDePasse: String = "",
    val enChargement: Boolean = false,
    val messageErreur: String? = null,
    val utilisateurConnecte: Utilisateur? = null
)