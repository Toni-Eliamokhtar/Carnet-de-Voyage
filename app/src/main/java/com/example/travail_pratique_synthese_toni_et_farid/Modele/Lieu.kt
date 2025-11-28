package com.example.travail_pratique_synthese_toni_et_farid.Modele

// Ça se peut que je fais des changements dépendant de la partie de google maps
// je n'ai pas encore vérifié comment on gère ça... donc des changements peuvent être apportés
data class Lieu(
    val id: String = "",
    val nom: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val userId: String = ""
)