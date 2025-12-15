package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.EtatConnexion
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes

@Composable
fun EcranConnexion(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var courriel by remember { mutableStateOf("") }
    var motPasse by remember { mutableStateOf("") }

    val etat = viewModel.etatConnexion.collectAsStateWithLifecycle().value

    // Mode strict: navigation seulement si l'utilisateur est vérifié
    LaunchedEffect(etat) {
        when (etat) {
            is EtatConnexion.Authentifie -> {
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
        Text(stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = courriel,
            onValueChange = { courriel = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = motPasse,
            onValueChange = { motPasse = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.connexion(courriel, motPasse) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_in))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.creerCompte(courriel, motPasse) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_account))
        }

        Spacer(Modifier.height(16.dp))

        when (etat) {
            is EtatConnexion.Chargement -> {
                Text(stringResource(R.string.logging_in))
            }
            is EtatConnexion.Erreur -> {
                Text(
                    text = etat.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is EtatConnexion.NonVerifie -> {
                // Message clair indiquant que l'email doit être vérifié
                Text(
                    text = stringResource(R.string.email_not_verified),
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(8.dp))

                // Permet de renvoyer le courriel de vérification
                OutlinedButton(
                    onClick = { viewModel.renvoyerEmailVerification() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.resend_verification_email))
                }
            }
            else -> Unit
        }
    }
}
