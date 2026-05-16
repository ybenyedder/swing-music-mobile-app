# Swing Music Mobile App

Client Android personnalisé pour Swing Music. Fork modifié avec UI repensée et features custom.

📦 [Télécharger l'APK](https://github.com/ybenyedder/swing-music-mobile-app/releases/latest)

## Aperçu

Application Android (Kotlin + Jetpack Compose) pour streamer ta bibliothèque musicale depuis ton serveur Swing Music. Interface simplifiée à 3 onglets, écran détail playlist intégré, navigation rapide.

## Features

### Navigation
- **3 onglets bottom** : Home · Favorites · Playlists
- Material3 NavigationBar avec capsule blanche sur item sélectionné
- Labels toujours visibles (blanc sur fond sombre)

### Home
- Header style web : avatar gradient + barre de recherche + bouton settings
- Tiles parcours rapide (Albums, Artists, Playlists, Favorites, Stats)
- Onglets de navigation inline (Home / Favorites / Playlists)
- Recently played + Top artists this week
- Padding statut bar pour éviter chevauchement

### Favorites
- Liste de tracks favorites depuis le serveur
- Tap sur une track → joue immédiatement (queue complète préparée)
- Source de queue : `FAVORITE`

### Playlists
- Liste de toutes les playlists du serveur
- Tap sur une playlist → ouvre **PlaylistDetailScreen** dédié
- Pas de chargement partiel : toutes les tracks remontent (`?limit=-1`)

### PlaylistDetail (nouveau)
- Header custom : bouton back + nom playlist + nb tracks + bouton play orange
- Tap bouton play orange → joue toutes les tracks depuis index 0
- Tap track individuel → joue à partir de cette position
- Source de queue : `PLAYLIST(id, name)`

### Top bar
- Caché sur Home, Favorites, Playlists, PlaylistDetail (chaque écran a son propre header)
- Visible sur écrans secondaires (Albums, Artists, Search, etc.)
- Logo refait : icône outline blanche sur cercle bleu (style web cohérent)

## Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material3
- **Architecture** : MVVM + Repository pattern
- **DI** : Hilt
- **Navigation** : Compose Destinations (typesafe)
- **Réseau** : Retrofit + OkHttp + Gson
- **DB locale** : Room
- **Player** : Media3 ExoPlayer
- **Images** : Coil

## Modules

```
app                    Activité principale + navigation root
auth                   Authentification + tokens
core                   Modèles + DTOs + mappers partagés
database               Room DB (DAOs : BaseUrl, User)
network                Retrofit API service
uicomponent            Composables partagés + thème
feature:home           Écrans Home, Favorites, Playlists, PlaylistDetail, Settings
feature:folder         Écrans dossiers
feature:album          Albums + AlbumWithInfo
feature:artist         Artists + ArtistInfo
feature:player         MediaController + MiniPlayer + NowPlaying + Queue
feature:search         Recherche
feature:settings       Réglages
feature:common         Interfaces navigateur partagées
```

## Endpoints utilisés

| Endpoint | Usage |
|---|---|
| `GET /favorites/tracks?start=0&limit=200` | Liste favorites |
| `GET /playlists` | Liste playlists |
| `GET /playlists/{id}?limit=-1` | Tracks d'une playlist (toutes) |

## Build

### Prérequis
- Android SDK 34+
- JDK 17
- Gradle 8.x

### Debug
```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release signé
```bash
export KEYSTORE_FILE=/chemin/absolu/release-key.keystore
export KEYSTORE_PASSWORD=ton_password
export KEY_ALIAS=ton_alias
export KEY_PASSWORD=ton_key_password
./gradlew :app:assembleRelease
```

APK généré : `app/build/outputs/apk/release/app-release.apk`

## Installation

1. Télécharge `swing-music-v1.0.0.apk` depuis [Releases](https://github.com/ybenyedder/swing-music-mobile-app/releases/latest)
2. Active "Sources inconnues" dans paramètres Android (Sécurité)
3. Ouvre l'APK pour installer
4. Lance l'app → connecte-toi à ton serveur Swing Music (URL + identifiants)

**Min SDK** : 26 (Android 8.0 Oreo)

## Configuration serveur

L'app se connecte à une instance Swing Music. Pour héberger ton propre serveur :
- Repo backend : https://github.com/swingmx/swingmusic
- Docs : https://swingmx.com/guide/introduction.html

## Roadmap

- [ ] R8/proguard rules pour réactiver minify (taille APK réduite)
- [ ] Pull-to-refresh sur Favorites + Playlists
- [ ] Recherche dans playlist
- [ ] Édition playlist depuis l'app
- [ ] Mode hors ligne (cache tracks)

## Crédits

Fork basé sur [swingmx/android](https://github.com/swingmx/android). Modifications UI + features custom par [@ybenyedder](https://github.com/ybenyedder).

Backend serveur : [swingmx/swingmusic](https://github.com/swingmx/swingmusic).

## Licence

Hérite de la licence du projet original. Voir [LICENSE](LICENSE).
