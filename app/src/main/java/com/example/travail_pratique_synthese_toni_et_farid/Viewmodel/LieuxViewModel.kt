package com.example.travail_pratique_synthese_toni_et_farid.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LieuxViewModel : ViewModel() {

    // Référence vers le noeud "lieux" dans Firebase Realtime Database
    private val referenceLieux =
        FirebaseDatabase.getInstance().getReference("lieux")

    // StateFlow utilisé par Compose pour afficher la liste
    private val _lieux = MutableStateFlow<List<Lieu>>(emptyList())
    val lieux: StateFlow<List<Lieu>> = _lieux

    init {
        // Écoute en temps réel des changements dans Firebase
        referenceLieux.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nouvelleListe = snapshot.children.mapNotNull { enfant ->
                    val lieu = enfant.getValue(Lieu::class.java)
                    val id = enfant.key
                    if (lieu != null && id != null) {
                        lieu.copy(id = id)
                    } else {
                        null
                    }
                }
                _lieux.value = nouvelleListe
            }

            override fun onCancelled(error: DatabaseError) {
                // Pour l'instant je vais ignorer l'erreur, je vais pouvoir logger plus tard
            }
        })
    }

    // Ajouter un lieu dans Firebase.
    fun ajouterLieu(lieu: Lieu) {
        val nouvelleRef = referenceLieux.push()
        val lieuAvecId = lieu.copy(id = nouvelleRef.key ?: "")
        nouvelleRef.setValue(lieuAvecId)
    }


    // Modifier un lieu existant (même id).
    fun modifierLieu(lieu: Lieu) {
        if (lieu.id.isBlank()) return
        referenceLieux.child(lieu.id).setValue(lieu)
    }

    // Supprimer un lieu par son id.
    fun supprimerLieu(id: String) {
        if (id.isBlank()) return
        referenceLieux.child(id).removeValue()
    }
}
