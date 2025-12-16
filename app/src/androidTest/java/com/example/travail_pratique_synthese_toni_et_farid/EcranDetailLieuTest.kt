package com.example.travail_pratique_synthese_toni_et_farid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranDetailLieu
import org.junit.Rule
import org.junit.Test

/**
 * Test instrumenté pour l'écran de détail d'un lieu
 * Vérifie le message lorsque le lieu n'existe pas
 */
class EcranDetailLieuTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ecranDetailLieu_lieuInexistant_afficheMessage() {
        composeTestRule.setContent {
            EcranDetailLieu(
                navController = rememberNavController(),
                lieuId = "id_invalide"
            )
        }

        composeTestRule
            .onNodeWithText("introuvable", substring = true)
            .assertIsDisplayed()
    }
}
