package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes

private const val PROFIL_NOTIFICATION_CHANNEL_ID = "profil_notifications"
private const val PROFIL_NOTIFICATION_GROUP_KEY = "profil_notification_group"

@Composable
fun EcranProfilParametres(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current

    // ---------- PERMISSION NOTIFICATIONS ----------
    var hasNotificationPermission by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val notificationsPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasNotificationPermission = granted
        }

    // IDs uniques pour les notifs
    var compteurNotifications by rememberSaveable { mutableStateOf(0) }

    // ---------- PROFIL : états venant du ViewModel ----------
    val profilState by viewModel.profilUtilisateur.collectAsStateWithLifecycle()

    var nomAffiche by rememberSaveable { mutableStateOf("") }
    var courriel by rememberSaveable { mutableStateOf("") }
    var langue by rememberSaveable { mutableStateOf("fr") }
    var notificationsActives by rememberSaveable { mutableStateOf(true) }

    // Photo locale (caméra, non persistée)
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Charger le profil au 1er affichage
    LaunchedEffect(Unit) {
        viewModel.chargerProfilUtilisateur()
    }

    // Quand le profil change (DB), on remplit les champs
    LaunchedEffect(profilState?.id) {
        profilState?.let {
            nomAffiche = it.nomAffiche
            courriel = it.courriel
            langue = it.langue
            notificationsActives = it.notificationsActives
        }
    }

    // Lanceur pour la caméra
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                photoBitmap = bitmap
            }
        }

    // Création du canal de notifications (Android 8+)
    LaunchedEffect(Unit) {
        val channel = NotificationChannel(
            PROFIL_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.profile_notifications_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun sauvegarderProfil() {
        if (nomAffiche.isBlank()) {
            Toast.makeText(context, context.getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.mettreAJourProfil(
            nomAffiche = nomAffiche,
            langue = langue,
            notificationsActives = notificationsActives
        ) { succes ->
            Toast.makeText(
                context,
                if (succes) context.getString(R.string.profile_updated)
                else context.getString(R.string.profile_update_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun envoyerNotificationTest() {
        // Android 13+ : permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasNotificationPermission
        ) {
            notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        if (!notificationsActives) {
            Toast.makeText(
                context,
                context.getString(R.string.enable_local_notifications_first),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Nouvelle notification -> ID unique
        compteurNotifications += 1
        val notificationId = 1000 + compteurNotifications

        val titre = context.getString(R.string.profile_notification_title, compteurNotifications)
        val texte = context.getString(R.string.profile_notification_text)

        // Notification "normale" du groupe
        val notificationBuilder =
            NotificationCompat.Builder(context, PROFIL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titre)
                .setContentText(texte)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(PROFIL_NOTIFICATION_GROUP_KEY)

        // Notification de résumé pour le groupage
        val summaryTitre = context.getString(R.string.profile_notifications_summary_title)
        val summaryTexte = context.getString(R.string.profile_notifications_summary_text, compteurNotifications)

        val summaryNotification =
            NotificationCompat.Builder(context, PROFIL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(summaryTitre)
                .setContentText(summaryTexte)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setSummaryText(summaryTexte)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(PROFIL_NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notificationBuilder.build())
            notify(9999, summaryNotification)
        }
    }

    // ------------------- UI -----------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(R.string.profile_settings_title),
            style = MaterialTheme.typography.titleLarge
        )

        // --- Carte avec les infos utilisateur ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Photo
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap!!.asImageBitmap(),
                        contentDescription = stringResource(R.string.profile_photo_cd),
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.photo_placeholder))
                    }
                }

                Button(onClick = { cameraLauncher.launch(null) }) {
                    Text(stringResource(R.string.take_picture))
                }

                OutlinedTextField(
                    value = nomAffiche,
                    onValueChange = { nomAffiche = it },
                    label = { Text(stringResource(R.string.display_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = courriel.ifBlank { stringResource(R.string.unknown_email) },
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = { sauvegarderProfil() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save_profile))
                }
            }
        }

        // --- Changement de langue FR / EN ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = { langue = "fr" }) {
                        Text(stringResource(R.string.lang_fr_short))
                    }
                    TextButton(onClick = { langue = "en" }) {
                        Text(stringResource(R.string.lang_en_short))
                    }
                }

                Text(
                    text = if (langue == "fr")
                        stringResource(R.string.current_language_french)
                    else
                        stringResource(R.string.current_language_english)
                )
            }
        }

        // --- Notifications locales ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.local_notifications),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.enable_notifications))
                    Switch(
                        checked = notificationsActives,
                        onCheckedChange = { notificationsActives = it }
                    )
                }

                Button(
                    onClick = { envoyerNotificationTest() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.test_notification))
                }
            }
        }

        // --- Déconnexion ---
        Button(
            onClick = {
                viewModel.deconnexion()
                navController.navigate(Routes.Connexion.route) {
                    popUpTo(Routes.Accueil.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}
