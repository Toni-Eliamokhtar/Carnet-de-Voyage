package com.example.travail_pratique_synthese_toni_et_farid.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travail_pratique_synthese_toni_et_farid.Modele.EtatConnexion
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ----- ÉTAT -----
    private val _etatConnexion = MutableStateFlow<EtatConnexion>(EtatConnexion.Inactif)
    val etatConnexion: StateFlow<EtatConnexion> = _etatConnexion.asStateFlow()

    // ----- UTILISATEUR -----
    val utilisateurCourant
        get() = auth.currentUser


    // Vérifie l’utilisateur stocké par Firebase
    fun verifierUtilisateur() {
        val user = auth.currentUser

        _etatConnexion.value = if (user != null) {
            if (user.isEmailVerified)
                EtatConnexion.Authentifie(user)
            else
                EtatConnexion.NonVerifie(user)
        } else {
            EtatConnexion.Inactif
        }
    }


    fun resetEtat() {
        _etatConnexion.value = EtatConnexion.Inactif
    }


    // ----- CONNEXION -----
    fun connexion(courriel: String, motPasse: String) {
        viewModelScope.launch {

            if (courriel.isBlank() || motPasse.isBlank()) {
                _etatConnexion.value = EtatConnexion.Erreur("Courriel ou mot de passe vide")
                return@launch
            }

            _etatConnexion.value = EtatConnexion.Chargement

            auth.signInWithEmailAndPassword(courriel, motPasse)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            _etatConnexion.value =
                                if (user.isEmailVerified)
                                    EtatConnexion.Authentifie(user)
                                else
                                    EtatConnexion.NonVerifie(user)

                        } else {
                            _etatConnexion.value = EtatConnexion.Inactif
                        }
                    } else {
                        val message = task.exception?.message ?: "Erreur de connexion"
                        _etatConnexion.value = EtatConnexion.Erreur(message)
                    }
                }
        }
    }


    // ----- CRÉATION D’UN COMPTE -----
    fun creerCompte(courriel: String, motPasse: String) {
        viewModelScope.launch {

            if (courriel.isBlank() || motPasse.isBlank()) {
                _etatConnexion.value = EtatConnexion.Erreur("Courriel ou mot de passe vide")
                return@launch
            }

            _etatConnexion.value = EtatConnexion.Chargement

            auth.createUserWithEmailAndPassword(courriel, motPasse)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        verifierUtilisateur()   // Laisse Firebase donner l’info
                    } else {
                        val message = task.exception?.message ?: "Erreur de création de compte"
                        _etatConnexion.value = EtatConnexion.Erreur(message)
                    }
                }
        }
    }


    // ----- DÉCONNEXION -----
    fun deconnexion() {
        auth.signOut()
        _etatConnexion.value = EtatConnexion.Inactif
    }
}