
# Favorite Songs

**Favorite Songs** is an Android music player app built with **Kotlin** that allows users to listen to their favorite songs, toggle repeat modes, manage a playlist, and view their favorite tracks. It uses **MVVM architecture**, **Room Database**, and **MediaPlayer** for seamless music playback.

---

## 🎶 Features
- **Browse and Add Music**: Add music from your device's external storage.
- **Play, Pause, and Seek**: Control playback with play/pause buttons and seek through tracks.
- **Repeat Modes**: Toggle between **No Repeat**, **Repeat One**, and **Repeat All** modes.
- **Playlist Management**: Navigate through the playlist with next/previous buttons.
- **LiveData Integration**: Updates the UI reactively based on song data.

---

## 🚀 Installation

Follow the steps below to set up the project locally:

### 1. **Clone the repository**

Clone the repository using Git:

```bash
git clone https://github.com/JohnNck/mp3Player.git


2. Open the project
Open the project in Android Studio. Make sure you have the latest version installed.

3. Sync Gradle dependencies
Android Studio will automatically sync the Gradle dependencies. If not, click Sync Now in the top toolbar.

4. Run the app
To run the app, connect your Android device or use an emulator, and click the Run button in Android Studio.

📱 App Structure
This app follows the MVVM (Model-View-ViewModel) architecture, which separates UI logic from business logic. Here's a breakdown of the structure:

Model
SongEntity: Contains data for individual songs (title, artist, path).
SongViewModel: Manages the logic of playing songs, navigating through the playlist, and managing repeat modes.
View
HomeFragment: Displays a list of available songs.
SongPlaying Activity: Handles the playback UI and controls (play/pause, repeat mode).
ViewModel
LiveData: Observes changes to song data and repeat mode, automatically updating the UI.

🛠️ Technologies Used
Kotlin: The primary programming language for Android development.
Android SDK: For creating Android apps.
Room Database: Local database for storing song metadata.
LiveData: To observe data changes and update the UI reactively.
MediaPlayer: For audio playback.
RecyclerView: To display songs in a list.

⚙️ How to Use
1. Add Music
Tap on the "Add Song" button to browse your device and add songs.

2. Play Music
Tap on a song in the list to start playing it.
Use the play/pause button to control playback.
Seek through the track using the seek bar.

3. Repeat Modes
Toggle between repeat modes by tapping the Repeat button:
No Repeat: The song will stop after it finishes.
Repeat One: The current song will repeat indefinitely.
Repeat All: The playlist will repeat after reaching the last song.

Home Screen: Displays the list of available songs.
Player Screen: Displays the current song and playback controls.

📝 Acknowledgments
Android Documentation: For providing useful resources and examples.
Room Database: For providing a simple and efficient way to store data locally.
MediaPlayer: For enabling music playback in the app.
Material Design: For UI components like FloatingActionButton and RecyclerView.
🐞 Troubleshooting
If you encounter any issues while using the app, here are some solutions:

Permissions: Ensure that your app has the correct permissions to access external storage (especially for Android 13+).
App Crashes: Check Logcat for detailed error logs and create an issue in the repository with the error message.
Song Loading Issues: Make sure you've granted storage permissions and that your songs are correctly added to the app.
