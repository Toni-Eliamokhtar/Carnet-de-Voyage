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
C'est le ViewModel responsable de la gestion des lieux (lecture et écriture dans Firebase)
 */
class LieuxViewModel : ViewModel() {

    // Référence vers le noeud "lieux" dans Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance().getReference("lieux")

    // Liste observable de lieux pour l'UI (StateFlow utilisé par Compose)
    private val _lieux = MutableStateFlow<List<Lieu>>(emptyList())
    val lieux: StateFlow<List<Lieu>> = _lieux

    init {
        // Dès la création du ViewModel, on commence directe à écouter Firebase
        observerLieux()
    }

    // Lecture en temps réel des lieux
    private fun observerLieux() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val liste = snapshot.children.mapNotNull { enfant ->
                    val lieu = enfant.getValue(Lieu::class.java)
                    val id = enfant.key
                    if (lieu != null && id != null) {
                        // on force l'id à être la clé Firebase
                        lieu.copy(id = id)
                    } else {
                        null
                    }
                }
                _lieux.value = liste
            }

            override fun onCancelled(error: DatabaseError) {
                // Ici je pourrais logger l'erreur si nécessaire
            }
        })
    }

    // Cette fonction ajoute un lieu dans Firebase
    fun ajouterLieu(lieu: Lieu) {
        val key = db.push().key ?: return
        val nouveauLieu = lieu.copy(id = key)

        db.child(key).setValue(nouveauLieu)

        // on met aussi à jour localement pour que l'UI réagisse directe
        _lieux.value = _lieux.value + nouveauLieu
    }

    // Supprimer un lieu par son id
    fun supprimerLieu(id: String) {
        if (id.isNotEmpty()) {
            db.child(id).removeValue()
            // Ici on met à jour la liste locale
            _lieux.value = _lieux.value.filterNot { it.id == id }
        }
    }
}
