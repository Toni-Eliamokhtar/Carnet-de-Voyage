package com.example.travail_pratique_synthese_toni_et_farid.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
ViewModel responsable de la gestion des lieux
Lecture et écriture des données dans Firebase Realtime Database
 */
class LieuxViewModel : ViewModel() {

    // Référence vers le noeud "lieux" dans Firebase
    private val db = FirebaseDatabase.getInstance().getReference("lieux")

    // Liste observable de lieux utilisée par l'interface
    private val _lieux = MutableStateFlow<List<Lieu>>(emptyList())
    val lieux: StateFlow<List<Lieu>> = _lieux

    // Listener Firebase conservé pour pouvoir être retiré proprement
    private var lieuxListener: ValueEventListener? = null

    init {
        // Démarrage de l'écoute en temps réel des données
        observerLieux()
    }

    // Écoute en temps réel des lieux dans Firebase
    private fun observerLieux() {
        lieuxListener?.let { db.removeEventListener(it) }

        lieuxListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val liste = snapshot.children.mapNotNull { enfant ->
                    val lieu = enfant.getValue(Lieu::class.java)
                    val id = enfant.key
                    if (lieu != null && id != null) {
                        // L'id Firebase est injecté dans l'objet Lieu
                        lieu.copy(id = id)
                    } else {
                        null
                    }
                }
                _lieux.value = liste
            }

            override fun onCancelled(error: DatabaseError) {
                // Erreur de lecture Firebase
            }
        }

        db.addValueEventListener(lieuxListener as ValueEventListener)
    }

    // Ajout d'un nouveau lieu dans Firebase
    fun ajouterLieu(lieu: Lieu) {
        val key = db.push().key ?: return
        val nouveauLieu = lieu.copy(id = key)
        db.child(key).setValue(nouveauLieu)
    }

    // Mise à jour d'un lieu existant
    fun modifierLieu(lieu: Lieu) {
        if (lieu.id.isNotBlank()) {
            db.child(lieu.id).setValue(lieu)
        }
    }

    // Suppression d'un lieu à partir de son identifiant
    fun supprimerLieu(id: String) {
        if (id.isNotEmpty()) {
            db.child(id).removeValue()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Nettoyage du listener lors de la destruction du ViewModel
        lieuxListener?.let { db.removeEventListener(it) }
        lieuxListener = null
    }
}
