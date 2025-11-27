package com.example.travail_pratique_synthese_toni_et_farid.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.travail_pratique_synthese_toni_et_farid.Modele.EtatConnexion
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Utilisateur
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EtatConnexion())
    val uiState: StateFlow<EtatConnexion> = _uiState.asStateFlow()

    fun changerCourriel(nouveauCourriel: String) {
        _uiState.value = _uiState.value.copy(courriel = nouveauCourriel)
    }

    fun changerMotDePasse(nouveauMotDePasse: String) {
        _uiState.value = _uiState.value.copy(motDePasse = nouveauMotDePasse)
    }

    // Validation 1 : login "factice" (plus tard : Firebase)
    fun seConnecter() {
        val etatActuel = _uiState.value

        if (etatActuel.courriel.isBlank() || etatActuel.motDePasse.isBlank()) {
            _uiState.value = etatActuel.copy(
                messageErreur = "Courriel ou mot de passe vide"
            )
            return
        }

        val utilisateur = Utilisateur(
            id = "1",
            courriel = etatActuel.courriel,
            nomAffiche = "Utilisateur"
        )

        _uiState.value = etatActuel.copy(
            utilisateurConnecte = utilisateur,
            messageErreur = null
        )
    }

    fun seDeconnecter() {
        _uiState.value = _uiState.value.copy(utilisateurConnecte = null)
    }

    fun changerNomAffiche(nouveauNom: String) {
        val etatActuel = _uiState.value
        val utilisateur = etatActuel.utilisateurConnecte

        if (utilisateur != null) {
            val utilisateurModifie = utilisateur.copy(nomAffiche = nouveauNom)
            _uiState.value = etatActuel.copy(utilisateurConnecte = utilisateurModifie)
        }
    }

    fun changerLangue(nouvelleLangue: String) {
        val etatActuel = _uiState.value
        val utilisateur = etatActuel.utilisateurConnecte

        if (utilisateur != null) {
            val utilisateurModifie = utilisateur.copy(langue = nouvelleLangue)
            _uiState.value = etatActuel.copy(utilisateurConnecte = utilisateurModifie)

        }
    }
}