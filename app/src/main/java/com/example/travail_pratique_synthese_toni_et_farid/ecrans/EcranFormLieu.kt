package com.example.travail_pratique_synthese_toni_et_farid.ecrans

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travail_pratique_synthese_toni_et_farid.Modele.Lieu
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.Viewmodel.LieuxViewModel
import com.example.travail_pratique_synthese_toni_et_farid.notifications.NotificationsLieux
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranFormLieu(
    navController: NavController,
    lieuId: String?,
    lieuxViewModel: LieuxViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val lieux by lieuxViewModel.lieux.collectAsState(initial = emptyList())
    val lieuAModifier = remember(lieuId, lieux) {
        if (lieuId.isNullOrBlank()) null else lieux.firstOrNull { it.id == lieuId }
    }

    val modeAjout = lieuId.isNullOrBlank()
    val titre = if (modeAjout) "Ajouter un lieu" else "Modifier le lieu"
    val texteBouton = if (modeAjout) "Ajouter" else "Enregistrer"

    // Champs
    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }

    // Image
    var imagePath by remember { mutableStateOf("") }
    var imagePreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var messageErreurImage by remember { mutableStateOf<String?>(null) }

    // Erreurs formulaire
    var errorNom by remember { mutableStateOf<String?>(null) }
    var errorAdresse by remember { mutableStateOf<String?>(null) }

    // Localisation
    val fournisseurLocalisation = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var messageErreurLocalisation by remember { mutableStateOf<String?>(null) }

    // Pré-remplissage en modification
    LaunchedEffect(lieuAModifier?.id) {
        lieuAModifier?.let { lieu ->
            nom = lieu.nom
            description = lieu.description
            adresse = lieu.adresse
            latitude = lieu.latitude
            longitude = lieu.longitude
            imagePath = lieu.imageUrl
            imagePreviewBitmap =
                if (lieu.imageUrl.isNotBlank()) BitmapFactory.decodeFile(lieu.imageUrl) else null
        }
    }

    fun lirePositionActuelle() {
        val permissionOk = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionOk) {
            messageErreurLocalisation = context.getString(R.string.error_location_permission_denied)
            return
        }

        fournisseurLocalisation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    latitude = loc.latitude
                    longitude = loc.longitude
                    adresse = context.getString(R.string.current_position)
                    errorAdresse = null
                    messageErreurLocalisation = null
                } else {
                    messageErreurLocalisation = context.getString(R.string.error_location_unavailable)
                }
            }
    }

    // Permission localisation
    val demandePermissionLocalisation =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { autorisee ->
            if (autorisee) lirePositionActuelle()
            else messageErreurLocalisation =
                context.getString(R.string.error_location_permission_denied)
        }

    // Caméra
    val lancerCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imagePath = sauvegarderBitmapEnLocal(context.filesDir, bitmap)
                imagePreviewBitmap = bitmap
                messageErreurImage = null
            } else {
                messageErreurImage = "Aucune image capturée."
            }
        }

    // Galerie
    val choisirGalerie =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val bitmap = chargerBitmapDepuisUri(context, uri)
                    imagePath = sauvegarderUriEnLocal(context, uri)
                    imagePreviewBitmap = bitmap
                    messageErreurImage = null
                } catch (e: Exception) {
                    messageErreurImage = "Erreur lors du chargement de l'image."
                }
            } else {
                messageErreurImage = "Aucune image sélectionnée."
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            val previewHeight = when {
                maxHeight < 420.dp -> 140.dp
                maxHeight < 600.dp -> 180.dp
                else -> 220.dp
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = {
                        nom = it
                        if (it.isNotBlank()) errorNom = null
                    },
                    label = { Text(stringResource(R.string.place_name_required_star)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorNom != null
                )
                if (errorNom != null) {
                    Text(
                        text = errorNom!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = adresse,
                    onValueChange = {
                        adresse = it
                        errorAdresse = null
                        latitude = null
                        longitude = null
                    },
                    label = { Text(stringResource(R.string.address_required_if_no_position)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorAdresse != null
                )
                if (errorAdresse != null) {
                    Text(
                        text = errorAdresse!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Photo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { lancerCamera.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Caméra") }

                    OutlinedButton(
                        onClick = { choisirGalerie.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Galerie") }
                }

                if (messageErreurImage != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = messageErreurImage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                imagePreviewBitmap?.let {
                    Spacer(Modifier.height(10.dp))
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Aperçu de la photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(previewHeight)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Localisation
                Button(
                    onClick = {
                        val permissionOk = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (permissionOk) lirePositionActuelle()
                        else demandePermissionLocalisation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.use_my_current_position))
                }

                if (messageErreurLocalisation != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = messageErreurLocalisation!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Submit
                Button(
                    onClick = {
                        if (nom.isBlank()) {
                            errorNom = context.getString(R.string.error_place_name_required)
                            return@Button
                        }

                        if (latitude == null && adresse.isBlank()) {
                            errorAdresse = context.getString(R.string.error_address_or_position_required)
                            return@Button
                        }

                        if (modeAjout && imagePath.isBlank()) {
                            messageErreurImage = "Une photo est obligatoire."
                            return@Button
                        }

                        val lieu = Lieu(
                            id = if (modeAjout) "" else (lieuId ?: ""),
                            nom = nom,
                            description = description,
                            latitude = latitude ?: 0.0,
                            longitude = longitude ?: 0.0,
                            adresse = adresse,
                            imageUrl = imagePath,
                            userId = "demo"
                        )

                        if (modeAjout) {
                            lieuxViewModel.ajouterLieu(lieu)
                            NotificationsLieux.notifierAjout(context, lieu.nom.ifBlank { context.getString(R.string.place_fallback) })
                        } else {
                            lieuxViewModel.modifierLieu(lieu)
                            NotificationsLieux.notifierModification(context, lieu.nom.ifBlank { context.getString(R.string.place_fallback) })
                        }

                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(texteBouton)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* ---------- Helpers ---------- */

private fun sauvegarderBitmapEnLocal(baseDir: File, bitmap: Bitmap): String {
    val dossier = File(baseDir, "lieux_images")
    if (!dossier.exists()) dossier.mkdirs()

    val fichier = File(dossier, "lieu_${System.currentTimeMillis()}.jpg")
    FileOutputStream(fichier).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return fichier.absolutePath
}

private fun sauvegarderUriEnLocal(context: android.content.Context, uri: android.net.Uri): String {
    val dossier = File(context.filesDir, "lieux_images")
    if (!dossier.exists()) dossier.mkdirs()

    val fichier = File(dossier, "lieu_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri).use { input ->
        if (input != null) {
            FileOutputStream(fichier).use { output -> input.copyTo(output) }
        }
    }
    return fichier.absolutePath
}

private fun chargerBitmapDepuisUri(
    context: android.content.Context,
    uri: android.net.Uri
): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, uri)
        )
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
