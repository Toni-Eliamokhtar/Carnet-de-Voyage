package com.example.travail_pratique_synthese_toni_et_farid

import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitaires pour le modèle Lieu
 * Vérifie l'intégrité des données métier
 */
class LieuTest {

    // Vérifie que les valeurs par défaut sont correctes
    @Test
    fun lieu_valeursParDefaut_sontCorrectes() {
        val lieu = Lieu()

        assertEquals("", lieu.id)
        assertEquals("", lieu.nom)
        assertEquals("", lieu.description)
        assertEquals(0.0, lieu.latitude, 0.0)
        assertEquals(0.0, lieu.longitude, 0.0)
        assertEquals("", lieu.adresse)
        assertEquals("", lieu.imageUrl)
        assertEquals("", lieu.userId)
    }

    // Vérifie qu'un lieu conserve les valeurs fournies
    @Test
    fun lieu_creation_conserveValeurs() {
        val lieu = Lieu(
            id = "1",
            nom = "Parc",
            description = "Un beau parc",
            latitude = 45.5,
            longitude = -73.6,
            adresse = "Montréal",
            imageUrl = "image.jpg",
            userId = "user123"
        )

        assertEquals("1", lieu.id)
        assertEquals("Parc", lieu.nom)
        assertEquals("Un beau parc", lieu.description)
        assertEquals(45.5, lieu.latitude, 0.0)
        assertEquals(-73.6, lieu.longitude, 0.0)
        assertEquals("Montréal", lieu.adresse)
        assertEquals("image.jpg", lieu.imageUrl)
        assertEquals("user123", lieu.userId)
    }
}
