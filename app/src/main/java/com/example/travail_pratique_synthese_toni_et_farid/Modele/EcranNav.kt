package com.example.travail_pratique_synthese_toni_et_farid.Modele

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import com.example.travail_pratique_synthese_toni_et_farid.R
import com.example.travail_pratique_synthese_toni_et_farid.routes.Routes

sealed class EcranNav(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object Accueil : EcranNav(
        route = Routes.Accueil.route,
        labelRes = R.string.nav_home,
        icon = Icons.Filled.Home
    )

    data object Lieux : EcranNav(
        route = Routes.ListeLieux.route,
        labelRes = R.string.nav_places,
        icon = Icons.Filled.Place
    )

    data object Profil : EcranNav(
        route = Routes.Profil.route,
        labelRes = R.string.nav_profile,
        icon = Icons.Filled.Person
    )
}

val itemsNavBas = listOf(
    EcranNav.Accueil,
    EcranNav.Lieux,
    EcranNav.Profil
)
