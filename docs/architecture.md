# TAE architecture

The launcher is a regular Android application registered for the Home role. It
draws Tessa's home surface in Jetpack Compose, asks Android to open phone and
content intents, and queries visible launcher activities for its app drawer.

The first version stays inside the normal Android app sandbox. Later system-image
work can replace or extend Android System UI and preinstall Tessarchy apps while
preserving the same visual language.

## Platform baseline

- `minSdk`: 31 (Android 12)
- `compileSdk` and `targetSdk`: 37 (Android 17)
- Compose compiler: 2.4.21
- Android Gradle Plugin: 9.4.1
- Gradle: 9.8.2

The OnePlus 7's Android 12 stock-firmware requirement is part of its ROM install
preparation. It does not define the launcher minimum SDK or the later ROM's Android
platform version.
