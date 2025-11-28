package com.example.travail_pratique_synthese_toni_et_farid.Modele

import com.google.firebase.auth.FirebaseUser

sealed class EtatConnexion {
    object Inactif : EtatConnexion()
    object Chargement : EtatConnexion()
    data class Authentifie(val utilisateur: FirebaseUser) : EtatConnexion()
    data class NonVerifie(val utilisateur: FirebaseUser) : EtatConnexion()
    data class Erreur(val message: String) : EtatConnexion()
}