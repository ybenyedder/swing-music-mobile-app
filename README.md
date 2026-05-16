# Swing Music Mobile App

Custom Android client for Swing Music. Modified fork with a redesigned UI and custom features.

📦 [Download the APK](https://github.com/ybenyedder/swing-music-mobile-app/releases/latest)

## Overview

Android app built with Kotlin and Jetpack Compose for streaming your music library from your Swing Music server. It includes a simplified 3-tab interface, a dedicated playlist detail screen, and fast navigation.

## Features

### Navigation

- **3 bottom tabs**: Home · Favorites · Playlists
- Material3 NavigationBar with a white capsule on the selected item
- Labels always visible with white text on a dark background

### Home

- Web-style header with gradient avatar, search bar, and settings button
- Quick access tiles: Albums, Artists, Playlists, Favorites, Stats
- Inline navigation tabs: Home / Favorites / Playlists
- Recently played section
- Top artists this week
- Status bar padding to avoid overlap

### Favorites

- Favorite tracks list loaded from the server
- Tap a track to play it immediately
- Full queue prepared before playback
- Queue source: `FAVORITE`

### Playlists

- List of all playlists from the server
- Tap a playlist to open the dedicated **PlaylistDetailScreen**
- No partial loading: all tracks are fetched using `?limit=-1`

### PlaylistDetail

- Custom header with back button, playlist name, track count, and orange play button
- Tap the orange play button to play all tracks from index 0
- Tap an individual track to play from that position
- Queue source: `PLAYLIST(id, name)`

### Top Bar

- Hidden on Home, Favorites, Playlists, and PlaylistDetail
- Each main screen has its own custom header
- Visible on secondary screens such as Albums, Artists, Search, etc.
- Redesigned logo: white outline icon inside a blue circle, matching the web style

## Technical Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM + Repository pattern
- **Dependency Injection**: Hilt
- **Navigation**: Compose Destinations
- **Network**: Retrofit + OkHttp + Gson
- **Local Database**: Room
- **Player**: Media3 ExoPlayer
- **Images**: Coil

## Modules

```text
app                    Main activity + root navigation
auth                   Authentication + tokens
core                   Shared models + DTOs + mappers
database               Room DB with BaseUrl and User DAOs
network                Retrofit API service
uicomponent            Shared composables + theme
feature:home           Home, Favorites, Playlists, PlaylistDetail, Settings screens
feature:folder         Folder screens
feature:album          Albums + AlbumWithInfo
feature:artist         Artists + ArtistInfo
feature:player         MediaController + MiniPlayer + NowPlaying + Queue
feature:search         Search
feature:settings       Settings
feature:common         Shared browser interfaces
```

## API Endpoints

| Endpoint | Usage |
|---|---|
| `GET /favorites/tracks?start=0&limit=200` | Favorites list |
| `GET /playlists` | Playlists list |
| `GET /playlists/{id}?limit=-1` | All tracks from a playlist |

## Build

### Requirements

- Android SDK 34+
- JDK 17
- Gradle 8.x

### Debug Build

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Signed Release Build

```bash
export KEYSTORE_FILE=/absolute/path/release-key.keystore
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=your_alias
export KEY_PASSWORD=your_key_password
./gradlew :app:assembleRelease
```

Generated APK:

```text
app/build/outputs/apk/release/app-release.apk
```

## Installation

1. Download `swing-music-v1.0.0.apk` from [Releases](https://github.com/ybenyedder/swing-music-mobile-app/releases/latest)
2. Enable **Unknown sources** in Android settings
3. Open the APK to install it
4. Launch the app
5. Log in to your Swing Music server using your server URL and credentials

**Minimum SDK**: 26  
**Minimum Android version**: Android 8.0 Oreo

## Server Configuration

This app connects to a Swing Music server instance.

To host your own server:

- Backend repository: https://github.com/swingmx/swingmusic
- Documentation: https://swingmx.com/guide/introduction.html

## Roadmap

- [ ] Add R8/ProGuard rules to re-enable minify and reduce APK size
- [ ] Add pull-to-refresh on Favorites and Playlists
- [ ] Add search inside playlists
- [ ] Add playlist editing from the app
- [ ] Add offline mode with track caching

## Credits

Fork based on [swingmx/android](https://github.com/swingmx/android).

UI changes and custom features by [@ybenyedder](https://github.com/ybenyedder).

Server backend: [swingmx/swingmusic](https://github.com/swingmx/swingmusic).

## License

This project inherits the license from the original project. See [LICENSE](LICENSE).
