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


@Composable
fun EcranConnexion(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var courriel by remember { mutableStateOf("") }
    var motPasse by remember { mutableStateOf("") }

    val etat = viewModel.etatConnexion.collectAsStateWithLifecycle().value

    // Message local pour le reset du mot de passe (succès/erreur)
    var messageReset by remember { mutableStateOf<String?>(null) }
    var resetSucces by remember { mutableStateOf(false) }

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

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                    Button(
                        onClick = { viewModel.connexion(courriel, motPasse) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.sign_in))
                    }

                    OutlinedButton(
                        onClick = { viewModel.creerCompte(courriel, motPasse) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.create_account))
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    messageReset?.let { msg ->
                        Text(
                            text = msg,
                            color = if (resetSucces) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }

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

            messageReset?.let { msg ->
                Text(
                    text = msg,
                    color = if (resetSucces) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

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
