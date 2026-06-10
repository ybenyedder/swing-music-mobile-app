import os
import re

files_to_update = [
    "feature/folder/src/main/java/com/android/swingmusic/folder/presentation/screen/FoldersAndTracks.kt",
    "feature/search/src/main/java/com/android/swingmusic/search/presentation/screen/Search.kt",
    "feature/search/src/main/java/com/android/swingmusic/search/presentation/screen/ViewAllSearchResults.kt",
    "feature/player/src/main/java/com/android/swingmusic/player/presentation/screen/Queue.kt",
    "feature/artist/src/main/java/com/android/swingmusic/artist/presentation/screen/ArtistInfo.kt",
    "feature/artist/src/main/java/com/android/swingmusic/artist/presentation/screen/ViewAllOnArtist.kt",
    "feature/album/src/main/java/com/android/swingmusic/album/presentation/screen/AlbumWithInfo.kt"
]

replacements = {
    r'"Go to Artist"': '"View artist"',
    r'"Go to Album"': '"View album"',
    r'"Add to playing queue"': '"Add to queue"',
    r'"Play Next"': '"Play next"'
}

for filepath in files_to_update:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        modified = content
        for old_str, new_str in replacements.items():
            modified = re.sub(old_str, new_str, modified)
            
        if modified != content:
            with open(filepath, 'w') as f:
                f.write(modified)
            print(f"Updated {filepath}")

