# Tessarchy Android Edition

**A Metro-inspired Android launcher experiment for a more personal phone.**

Tessarchy Android Edition (TAE) explores how Tessarchy could become the primary
experience on an Android device. The visual direction combines Metro's strong
grid, generous typography, quick-glance information, and saturated color with
Tessarchy's navy and mint identity. Android continues to provide the device,
application, and system foundations.

> **Project stage:** launcher release-candidate work in progress. The current
> deliverable is an Android launcher app, not a custom Android operating system
> or a OnePlus 7 ROM.

## Project page

Visit the [Tessarchy Android Edition project page](https://thediamondsaint.github.io/tessarchy-android-edition/)
for the full visual direction, emulator screenshots, concept gallery, platform
layers, and development sequence.

## What runs today

- A Jetpack Compose app that can be selected as Android's Home app.
- A live clock and date above a Metro-style grid of colored rectangular tiles.
- Phone, messages, calendar, photos, music, and Tessa launcher actions.
- A searchable drawer of installed launcher apps with their actual app icons.
- Loading, no-app, and no-search-match states in the drawer.
- Metro tiles that can be hidden, resized, reordered, and recolored with three
  saved themes.
- Drag reorder and accessible move actions, with a four-column layout on wider
  windows.
- Settings saved locally with AndroidX DataStore and included in Android backup.
- App discovery refreshed when returning to the launcher, with per-activity
  identity for apps that expose multiple launcher entries.
- Localized string resources, accessible control names, and adaptive app icons.

The **real emulator screenshots and interaction recording** are in [`artifacts/`](artifacts/).
The **design concepts** are in [`assets/concepts/`](assets/concepts/) and are
clearly labeled as explorations; they do not represent implemented features.

## What we are making

The first layer is a useful Tessarchy home and app-navigation experience. The
current launcher can be personalized and keeps those choices across launches.
Live content, widgets, additional system surfaces, and deeper Android or AOSP
changes remain later investigations because each layer adds device and
maintenance work.

The project direction, implementation boundary, platform strategy, setup, and
contribution notes are in the [project guide](docs/project-guide.md). The
[architecture notes](docs/architecture.md) describe the application and system
layers. The [30-area GitHub research survey](docs/github-research.md) records
the reference projects, star snapshots, and license review. The [20-item code
audit and improvement roadmap](docs/audit-roadmap.md) records what has been fixed
and what comes next.

## Metro direction

- Use flat, high-contrast color blocks to make actions easy to find.
- Keep a consistent modular grid and restrained rounded corners.
- Make type and live information part of the layout, not decoration.
- Let users shape the order, size, and emphasis of their tiles.
- Keep Tessarchy's navy and mint frame calm around the brighter tile field.

## Build and run

Requirements: JDK 17, Android SDK Platform 36, Android Build Tools 36, and the
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

The local Android 17 preview AVD is named `TAE_API_37`; it is used for visual
comparison. CI tests on stable API 36. After installation, select **Tessarchy**
under Android Settings → Default apps → Home app. See the
[project guide](docs/project-guide.md) for emulator setup and device notes.

## Development order

The release candidate now passes local unit, lint, build, and Compose smoke checks,
plus GitHub Actions on stable Android API 36. Next, review wide-screen behavior,
accessibility, and keyboard navigation on more devices. Play Store submission needs
separate signing, listing, policy, and device-compatibility work; it is not part of
this candidate.

## License

This repository is public, but no software license has been selected yet.
Public visibility allows inspection and discussion; it does not grant reuse
rights under an open-source license.
