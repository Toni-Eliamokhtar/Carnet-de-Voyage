package com.example.travail_pratique_synthese_toni_et_farid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranListeLieux
import org.junit.Rule
import org.junit.Test

/**
 * Test instrumenté pour l'écran de liste des lieux
 * Vérifie l'affichage lorsqu'aucun lieu n'est présent
 */
class EcranListeLieuxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ecranListeLieux_afficheMessageAucunLieu() {
        composeTestRule.setContent {
            EcranListeLieux(
                navController = rememberNavController()
            )
        }

        composeTestRule
            .onNodeWithText("Aucun", substring = true)
            .assertIsDisplayed()
    }
}
