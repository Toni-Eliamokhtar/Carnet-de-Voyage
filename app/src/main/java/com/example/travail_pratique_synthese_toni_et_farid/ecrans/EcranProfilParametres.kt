package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.AuthViewModel
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import androidx.core.content.edit

private const val PROFIL_NOTIFICATION_CHANNEL_ID = "profil_notifications"
private const val PROFIL_NOTIFICATION_GROUP_KEY = "profil_notification_group"

// Stockage local de la photo de profil (dans le stockage interne de l'app)
private const val PROFIL_PREFS_NAME = "profil_prefs"
private const val PROFIL_PHOTO_PATH_KEY_PREFIX = "profil_photo_path_" // Clé différente par utilisateur
private const val PROFIL_PHOTO_FILE_PREFIX = "photo_profil_"          // Fichier différent par utilisateur

@Composable
fun EcranProfilParametres(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uid = viewModel.utilisateurCourant?.uid.orEmpty() // UID de l'utilisateur courant (sert à isoler la photo)

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
    var langue by rememberSaveable { mutableStateOf("fr") }      // sélection (temp)
    var notificationsActives by rememberSaveable { mutableStateOf(true) }

    var langueInitiale by rememberSaveable { mutableStateOf("") } // langue sauvegardée

    // Photo locale (caméra, persistée localement)
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Sauvegarde la photo dans le stockage interne et retourne le chemin
    fun sauvegarderPhotoLocalement(bitmap: Bitmap): String {
        val fichier = File(context.filesDir, "$PROFIL_PHOTO_FILE_PREFIX$uid.jpg") // Un fichier par utilisateur
        FileOutputStream(fichier).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return fichier.absolutePath
    }

    // Charge une photo depuis un chemin local
    fun chargerPhotoLocale(path: String): Bitmap? {
        return try {
            FileInputStream(File(path)).use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Charger le profil au 1er affichage
    LaunchedEffect(Unit) {
        viewModel.chargerProfilUtilisateur()

        // Recharge la photo sauvegardée localement de cet utilisateur si elle existe
        if (uid.isNotBlank()) {
            val prefs = context.getSharedPreferences(PROFIL_PREFS_NAME, Context.MODE_PRIVATE)
            val key = "$PROFIL_PHOTO_PATH_KEY_PREFIX$uid"
            val path = prefs.getString(key, null)
            if (!path.isNullOrBlank()) {
                photoBitmap = chargerPhotoLocale(path)
            }
        }
    }

    // Quand le profil change (DB), on remplit les champs
    //  écouter aussi profilState?.langue (pas seulement l'id)
    LaunchedEffect(profilState?.id, profilState?.langue, profilState?.notificationsActives, profilState?.nomAffiche) {
        profilState?.let {
            nomAffiche = it.nomAffiche
            courriel = it.courriel

            // langue sauvegardée (sert pour "Langue actuelle")
            langueInitiale = it.langue

            // langue sélectionnée par défaut = langue sauvegardée
            langue = it.langue

            notificationsActives = it.notificationsActives
        }
    }

    // Lanceur pour la caméra
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                photoBitmap = bitmap

                // Sauvegarde la photo localement pour cet utilisateur
                if (uid.isNotBlank()) {
                    val path = sauvegarderPhotoLocalement(bitmap)
                    val prefs = context.getSharedPreferences(PROFIL_PREFS_NAME, Context.MODE_PRIVATE)
                    val key = "$PROFIL_PHOTO_PATH_KEY_PREFIX$uid"
                    prefs.edit { putString(key, path) }
                }
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

        // Appliquer la langue AU MOMENT de sauvegarder (et une seule fois)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(
                if (langue == "fr") "fr-FR" else "en-US"
            )
        )

        viewModel.mettreAJourProfil(
            nomAffiche = nomAffiche,
            langue = langue,
            notificationsActives = notificationsActives
        ) { succes ->
            if (succes) {
                // "Langue actuelle" change seulement après succès
                langueInitiale = langue
            } else {
                // rollback si la sauvegarde échoue
                if (langueInitiale.isNotBlank()) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(
                            if (langueInitiale == "fr") "fr-FR" else "en-US"
                        )
                    )
                }
            }

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

        compteurNotifications += 1
        val notificationId = 1000 + compteurNotifications

        val titre = context.getString(R.string.profile_notification_title, compteurNotifications)
        val texte = context.getString(R.string.profile_notification_text)

        val notificationBuilder =
            NotificationCompat.Builder(context, PROFIL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titre)
                .setContentText(texte)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(PROFIL_NOTIFICATION_GROUP_KEY)

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
    val scrollState = rememberScrollState() // Permet de faire défiler l'écran si le contenu dépasse
    val isLandscape =
        androidx.compose.ui.platform.LocalConfiguration.current.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Rend l'écran scrollable
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(R.string.profile_settings_title),
            style = MaterialTheme.typography.titleLarge
        )

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                                text = if (langueInitiale.startsWith("fr"))
                                    stringResource(R.string.current_language_french)
                                else
                                    stringResource(R.string.current_language_english)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                }
            }
        } else {
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
                        text = if (langueInitiale.startsWith("fr"))
                            stringResource(R.string.current_language_french)
                        else
                            stringResource(R.string.current_language_english)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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

}
