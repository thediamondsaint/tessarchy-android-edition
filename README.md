# Tessarchy Android Edition

**An agent-first Tessarchy experience, starting with a Metro-inspired Android launcher.**

Tessarchy Android Edition (TAE) explores how Tessarchy could become the primary
experience on an Android device. The visual direction combines Metro's strong grid and generous type with image-backed glass tiles, compact real-world details, and Tessarchy's navy and mint identity. The longer-term direction adds a user-chosen AI provider and deeper Android/AOSP integration.

> **Project stage:** launcher release-candidate work in progress. The current
> deliverable is an Android launcher app, not a custom Android operating system
> or a OnePlus 7 ROM.

## Project page

Visit the [Tessarchy Android Edition project page](https://thediamondsaint.github.io/tessarchy-android-edition/)
for the full visual direction, emulator screenshots, concept gallery, platform
layers, and development sequence.

## What runs today

- A Jetpack Compose app that can be selected as Android's Home app.
- A live clock and date beside the Tessarchy mark.
- Rounded, image-backed glass Metro tiles for Tessarchy, calendar, phone, messages, Tessa, photos, music, and the app library.
- A compact Tessa assistant-preview tile and a provider-neutral concept screen; no AI provider is connected yet.
- A bottom Window Tape prototype that switches among Home, Apps, Tessa, and Studio. Arbitrary Android app windows do not yet scroll side by side.
- Phone, messages, calendar, photos, music, and Tessa launcher actions.
- A searchable drawer of installed launcher apps with their actual app icons.
- An optional, explained app-library permission choice. App names and icons are read only after consent, stay on device, and can be disabled in Studio → Privacy & data.
- Loading, no-app, and no-search-match states in the drawer.
- Metro tiles that can be hidden, resized, reordered, and recolored with three
  saved themes.
- Drag reorder and accessible move actions, with a four-column layout on wider
  windows.
- Settings saved locally with AndroidX DataStore and included in Android backup.
- App discovery refreshed when returning to the launcher, with per-activity
  identity for apps that expose multiple launcher entries.
- Localized string resources, accessible control names, and adaptive app icons.
- A Home-app setup action that opens Android's system role chooser.

The **current emulator screenshots and interaction recording** are in [`docs/media/`](docs/media/). The **design concepts** are also kept in that folder and labeled as explorations; they do not represent implemented features.

## What we are making

The first layer is a useful Tessarchy home and app-navigation experience. The
current launcher can be personalized and keeps those choices across launches.
The next layers are tile and widget creation tools, live tile data, and a provider adapter that exposes named Tessarchy actions to a chosen assistant. Android app and system access needs supported APIs or explicit bridges. A rooted AOSP system with deeper AI control is the long-range goal, not a current capability.

The project direction, implementation boundary, platform strategy, setup, and
contribution notes are in the [project guide](docs/project-guide.md). The
[architecture notes](docs/architecture.md) describe the application and system
layers. The [30-area GitHub research survey](docs/github-research.md) records
the reference projects, star snapshots, and license review. The [20-item code
audit and improvement roadmap](docs/audit-roadmap.md) records what has been fixed
and what comes next.

## Metro direction

- Use image-backed glass tiles with rounded corners, restrained translucency, and clear type.
- Keep the modular Metro grid glanceable; let tile size and imagery give information room.
- Show real stats where available, and never invent app counts or activity.
- Keep Tessa and user-created widgets close to the home surface.
- Use Window Tape to move between adjacent phone contexts with a continuous gesture.
- Keep Tessarchy's navy and mint frame calm around the brighter tile field.

## Build and run

Requirements: JDK 17, Android SDK Platform 37, Android Build Tools 37, and the
Android Emulator. `mise.toml` selects the project-local Java runtime. Create a
local `local.properties` file that points to your Android SDK; Git ignores it.

```sh
mise exec -- ./gradlew :app:assembleDebug
```

To run checks and install on a running emulator:

```sh
mise exec -- ./gradlew :app:lintDebug
mise exec -- ./gradlew :app:installDebug
```

To create a signed Play App Bundle, keep `keystore.properties` and the upload
keystore outside Git, then run:

```sh
mise exec -- ./gradlew :app:bundleRelease
```

The current release bundle is written to
`app/build/outputs/bundle/release/app-release.aab`. Play App Signing should be
enabled for the app in Play Console. The local upload key is for uploads only;
protect it and keep a secure backup. Store listing copy, artwork, screenshots,
privacy details, and the remaining Console checklist are in [`play-store/`](play-store/).

The local Android 17 AVD is named `TAE_API_37`; it is used for visual
comparison and release-candidate testing. CI is configured to test API 37. After installation, select **Tessarchy**
under Android Settings → Default apps → Home app. See the
[project guide](docs/project-guide.md) for emulator setup and device notes.

## Development order

The current candidate targets Android 17/API 37 and includes opt-in disclosure for
launcher app visibility, an in-app privacy page, a public privacy policy, release
shrinking, and a Play listing draft. Local Android 17 emulator review and the
instrumented launcher smoke test cover the current consent, app drawer, and search
flow. Store submission still requires the Play Console account declarations and
review, physical-device feedback, and any testing-track gates tied to the developer
account.

## License

This repository is public, but no software license has been selected yet.
Public visibility allows inspection and discussion; it does not grant reuse
rights under an open-source license.
