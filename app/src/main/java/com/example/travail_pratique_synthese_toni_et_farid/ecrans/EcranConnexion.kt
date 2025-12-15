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
import android.content.res.Configuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment

/*
 * Écran de connexion / création de compte
 * Gère aussi la vérification du courriel et la réinitialisation du mot de passe
 */
@Composable
fun EcranConnexion(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    // Champs saisis par l'utilisateur
    var courriel by remember { mutableStateOf("") }
    var motPasse by remember { mutableStateOf("") }

    // État de connexion provenant du ViewModel
    val etat = viewModel.etatConnexion.collectAsStateWithLifecycle().value

    // Message local pour la réinitialisation du mot de passe
    var messageReset by remember { mutableStateOf<String?>(null) }
    var resetSucces by remember { mutableStateOf(false) }

    // Navigation automatique seulement si l'utilisateur est authentifié et vérifié
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

    // Détection de l'orientation de l'écran
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Permet de rendre l'écran scrollable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // Titre de l'écran
        Text(
            stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        // Mise en page différente selon l'orientation
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Colonne des champs et actions
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Champ courriel
                    OutlinedTextField(
                        value = courriel,
                        onValueChange = {
                            courriel = it
                            messageReset = null
                        },
                        label = { Text(stringResource(R.string.email)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Champ mot de passe (masqué)
                    OutlinedTextField(
                        value = motPasse,
                        onValueChange = { motPasse = it },
                        label = { Text(stringResource(R.string.password)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    // Bouton mot de passe oublié
                    TextButton(
                        onClick = {
                            viewModel.reinitialiserMotPasse(courriel) { succes, msg ->
                                resetSucces = succes
                                messageReset = msg
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.forgot_password))
                    }

                    // Bouton connexion
                    Button(
                        onClick = { viewModel.connexion(courriel, motPasse) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.sign_in))
                    }

                    // Bouton création de compte
                    OutlinedButton(
                        onClick = { viewModel.creerCompte(courriel, motPasse) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.create_account))
                    }
                }

                // Colonne des messages d'état
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    // Message pour le reset du mot de passe
                    messageReset?.let { msg ->
                        Text(
                            text = msg,
                            color = if (resetSucces)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    // Messages selon l'état de connexion
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
                            Text(
                                text = stringResource(R.string.email_not_verified),
                                color = MaterialTheme.colorScheme.error
                            )

                            // Bouton pour renvoyer le courriel de vérification
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
        } else {
            // Mode portrait : affichage vertical

            OutlinedTextField(
                value = courriel,
                onValueChange = {
                    courriel = it
                    messageReset = null
                },
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

            Spacer(Modifier.height(8.dp))

            // Mot de passe oublié
            TextButton(
                onClick = {
                    viewModel.reinitialiserMotPasse(courriel) { succes, msg ->
                        resetSucces = succes
                        messageReset = msg
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.forgot_password))
            }

            Spacer(Modifier.height(8.dp))

            // Connexion
            Button(
                onClick = { viewModel.connexion(courriel, motPasse) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.sign_in))
            }

            Spacer(Modifier.height(8.dp))

            // Création de compte
            OutlinedButton(
                onClick = { viewModel.creerCompte(courriel, motPasse) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create_account))
            }

            Spacer(Modifier.height(16.dp))

            // Message de reset du mot de passe
            messageReset?.let { msg ->
                Text(
                    text = msg,
                    color = if (resetSucces)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            // Messages selon l'état de connexion
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
                    Text(
                        text = stringResource(R.string.email_not_verified),
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(8.dp))

                    // Renvoi du courriel de vérification
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
}
