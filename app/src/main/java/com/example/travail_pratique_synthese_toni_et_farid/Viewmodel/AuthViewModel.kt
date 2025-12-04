package com.example.travail_pratique_synthese_toni_et_farid.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travail_pratique_synthese_toni_et_farid.Modele.EtatConnexion
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Utilisateur
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // ----- Firebase -----
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("utilisateurs")

    // ----- ÉTAT DE CONNEXION -----
    private val _etatConnexion = MutableStateFlow<EtatConnexion>(EtatConnexion.Inactif)
    val etatConnexion: StateFlow<EtatConnexion> = _etatConnexion.asStateFlow()

    // ----- PROFIL UTILISATEUR (Realtime Database) -----
    private val _profilUtilisateur = MutableStateFlow<Utilisateur?>(null)
    val profilUtilisateur: StateFlow<Utilisateur?> = _profilUtilisateur.asStateFlow()

    // ----- UTILISATEUR COURANT (FirebaseAuth) -----
    val utilisateurCourant
        get() = auth.currentUser


    // Vérifie l’utilisateur stocké par FirebaseAuth
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

    // --------------------------------------------------
    //   GESTION DU PROFIL DANS REALTIME DATABASE
    // --------------------------------------------------

    fun chargerProfilUtilisateur() {
        val user = auth.currentUser ?: return

        database.child(user.uid).get()
            .addOnSuccessListener { snapshot ->
                val profil = snapshot.getValue(Utilisateur::class.java)
                if (profil != null) {
                    _profilUtilisateur.value = profil
                } else {
                    // Aucun profil : on en crée un par défaut
                    val nouveauProfil = Utilisateur(
                        id = user.uid,
                        nomAffiche = user.displayName ?: "",
                        courriel = user.email ?: "",
                        langue = "fr",
                        notificationsActives = true
                    )
                    database.child(user.uid).setValue(nouveauProfil)
                    _profilUtilisateur.value = nouveauProfil
                }
            }
    }

    /**
     * Met à jour le profil de l'utilisateur :
     *  - displayName dans FirebaseAuth
     *  - données dans Realtime Database (noeud "utilisateurs/<uid>")
     */
    fun mettreAJourProfil(
        nomAffiche: String,
        langue: String,
        notificationsActives: Boolean,
        onResult: (Boolean) -> Unit = {}
    ) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false)
            return
        }

        // 1) Mettre à jour le displayName dans FirebaseAuth
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(nomAffiche)
            .build()

        user.updateProfile(request).addOnCompleteListener { authTask ->
            if (!authTask.isSuccessful) {
                onResult(false)
                return@addOnCompleteListener
            }

            // 2) Sauvegarder aussi dans Realtime Database
            val profilMaj = Utilisateur(
                id = user.uid,
                nomAffiche = nomAffiche,
                courriel = user.email ?: "",
                langue = langue,
                notificationsActives = notificationsActives
            )

            database.child(user.uid).setValue(profilMaj)
                .addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        _profilUtilisateur.value = profilMaj
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
        }
    }

    // --------------------------------------------------
    //   CONNEXION
    // --------------------------------------------------
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

                            // Charger le profil après la connexion
                            chargerProfilUtilisateur()
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

    // --------------------------------------------------
    //   CRÉATION D’UN COMPTE
    // --------------------------------------------------
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
                        verifierUtilisateur()
                        // Créer aussi un profil par défaut
                        chargerProfilUtilisateur()
                    } else {
                        val message = task.exception?.message ?: "Erreur de création de compte"
                        _etatConnexion.value = EtatConnexion.Erreur(message)
                    }
                }
        }
    }

    // --------------------------------------------------
    //   DÉCONNEXION
    // --------------------------------------------------
    fun deconnexion() {
        auth.signOut()
        _etatConnexion.value = EtatConnexion.Inactif
        _profilUtilisateur.value = null
    }
}
