package com.example.travail_pratique_synthese_toni_et_farid.Modele

/*
Modèle de données d'un lieu tel qu'il est stocké dans Firebase
L'id correspondra à la clé du noeud dans la base de données
 */

// Ça se peut que je fais des changements dépendant de la partie de google maps
// je n'ai pas encore vérifié comment on gère ça... donc des changements peuvent être apportés
data class Lieu(
    val id: String = "",
    val nom: String = "",
    val description: String = "",
    val latitude: Double = 0.0,     // Latitude et longitude seront mis à jour avec la vraie géolocalisation
    val longitude: Double = 0.0,
    val imageUrl: String = "",      // URL de la photo dans le stockage (Je vais le faire plus tard)
    val userId: String = ""         // Permet de savoir quel utilisateur a créé le lieu
)