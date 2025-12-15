package com.example.travail_pratique_synthese_toni_et_farid.Modele

/*
Modèle de données d'un lieu tel qu'il est stocké dans Firebase
L'id correspondra à la clé du noeud dans la base de données
 */

data class Lieu(
    var id: String = "",
    var nom: String = "",
    var description: String = "",
    var latitude: Double = 0.0,     // Latitude et longitude seront mis à jour avec la vraie géolocalisation
    var longitude: Double = 0.0,
    var adresse: String = "",       // Adresse textuelle du lieu (ou "Position actuelle")
    var imageUrl: String = "",      // URL de la photo dans le stockage
    var userId: String = ""         // Permet de savoir quel utilisateur a créé le lieu
)
