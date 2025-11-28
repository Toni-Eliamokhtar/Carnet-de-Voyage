package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.EtatConnexion
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel

@Composable
fun EcranConnexion(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var courriel by remember { mutableStateOf("") }
    var motPasse by remember { mutableStateOf("") }

    val etat = viewModel.etatConnexion.collectAsStateWithLifecycle().value

    // Si déjà connecté, on va direct à l'accueil
    LaunchedEffect(etat) {
        when (etat) {
            is EtatConnexion.Authentifie,
            is EtatConnexion.NonVerifie -> {
                navController.navigate(Routes.Accueil.route) {
                    popUpTo(Routes.Connexion.route) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Connexion", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = courriel,
            onValueChange = { courriel = it },
            label = { Text("Courriel") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = motPasse,
            onValueChange = { motPasse = it },
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.connexion(courriel, motPasse) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se connecter")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.creerCompte(courriel, motPasse) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer un compte")
        }

        Spacer(Modifier.height(16.dp))

        when (etat) {
            is EtatConnexion.Chargement -> {
                Text("Connexion en cours...")
            }
            is EtatConnexion.Erreur -> {
                Text(
                    text = etat.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is EtatConnexion.NonVerifie -> {
                Text(
                    text = "Courriel non vérifié (plus tard on gérera ça)",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> Unit
        }
    }
}
