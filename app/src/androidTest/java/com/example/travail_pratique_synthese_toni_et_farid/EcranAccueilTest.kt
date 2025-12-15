package com.example.travail_pratique_synthese_toni_et_farid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.travail_pratique_synthese_toni_et_farid.ecrans.EcranAccueil
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import org.junit.Rule
import org.junit.Test

class EcranAccueilTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Vérifie que le texte de bienvenue est affiché
    @Test
    fun ecranAccueil_afficheMessageBienvenue() {
        composeTestRule.setContent {
            EcranAccueil(
                navController = rememberNavController(),
                viewModel = AuthViewModel()
            )
        }

        composeTestRule
            .onNodeWithText("Bienvenue", substring = true)
            .assertIsDisplayed()
    }

    // Vérifie que le bouton pour voir les lieux est affiché
    @Test
    fun ecranAccueil_boutonVoirLieuxVisible() {
        composeTestRule.setContent {
            EcranAccueil(
                navController = rememberNavController(),
                viewModel = AuthViewModel()
            )
        }

        composeTestRule
            .onNodeWithText("Voir", substring = true)
            .assertIsDisplayed()
    }
}
