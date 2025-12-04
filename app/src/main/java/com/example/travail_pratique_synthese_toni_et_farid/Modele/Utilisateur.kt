package com.example.travail_pratique_synthese_toni_et_farid.Modele

data class Utilisateur(
    val id: String = "",
    val courriel: String = "",
    val nomAffiche: String = "",
    val photoUrl: String? = null,
    val langue: String = "fr",   // "fr" ou "en"
    val notificationsActives: Boolean = true

)