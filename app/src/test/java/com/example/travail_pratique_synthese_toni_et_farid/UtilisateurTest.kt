package com.example.travail_pratique_synthese_toni_et_farid

import com.example.travail_pratique_synthese_toni_et_farid.Modele.Utilisateur
import org.junit.Assert.*
import org.junit.Test

class UtilisateurTest {

    // Valide les valeurs par défaut (important car ton ViewModel crée un profil par défaut)
    @Test
    fun utilisateur_valeursParDefaut_sontCorrectes() {
        val u = Utilisateur()

        assertEquals("", u.id)
        assertEquals("", u.courriel)
        assertEquals("", u.nomAffiche)
        assertNull(u.photoUrl)
        assertEquals("fr", u.langue)
        assertTrue(u.notificationsActives)
    }

    // Valide qu'un utilisateur créé avec des valeurs conserve exactement ces valeurs (pas de modification silencieuse)
    @Test
    fun utilisateur_creation_conserveValeurs() {
        val u = Utilisateur(
            id = "uid1",
            courriel = "test@email.com",
            nomAffiche = "Farid",
            photoUrl = "path/photo.jpg",
            langue = "en",
            notificationsActives = false
        )

        assertEquals("uid1", u.id)
        assertEquals("test@email.com", u.courriel)
        assertEquals("Farid", u.nomAffiche)
        assertEquals("path/photo.jpg", u.photoUrl)
        assertEquals("en", u.langue)
        assertFalse(u.notificationsActives)
    }
}
