# Tessarchy Android Edition

**A Metro-inspired Android launcher experiment for a more personal phone.**

Tessarchy Android Edition (TAE) explores how Tessarchy could become the primary
experience on an Android device. The visual direction combines Metro's strong
grid, generous typography, quick-glance information, and saturated color with
Tessarchy's navy and mint identity. Android continues to provide the device,
application, and system foundations.

> **Project stage:** early prototype. The current deliverable is an Android
> launcher app, not a custom Android operating system or a OnePlus 7 ROM.

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
- Localized string resources and accessible button semantics.

The **real emulator screenshots and interaction recording** are in [`artifacts/`](artifacts/).
The **design concepts** are in [`assets/concepts/`](assets/concepts/) and are
clearly labeled as explorations; they do not represent implemented features.

## What we are making

The first layer is a useful Tessarchy home and app-navigation experience. From
there, the project can explore editable tile layouts, saved preferences, live
content, and additional system surfaces. Deeper Android and AOSP changes remain
later investigations because each layer adds device and maintenance work.

The project direction, implementation boundary, platform strategy, setup, and
contribution notes are in the [project guide](docs/project-guide.md). The
[architecture notes](docs/architecture.md) describe the application and system
layers. The [20-item code audit and improvement roadmap](docs/audit-roadmap.md)
records what has been fixed and what comes next.

## Metro direction

- Use flat, high-contrast color blocks to make actions easy to find.
- Keep a consistent modular grid and restrained rounded corners.
- Make type and live information part of the layout, not decoration.
- Let users shape the order, size, and emphasis of their tiles.
- Keep Tessarchy's navy and mint frame calm around the brighter tile field.

## Build and run

Requirements: JDK 17, Android SDK Platform 37, Android Build Tools 36, and the
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

The development AVD is named `TAE_API_37`. After installation, select
**Tessarchy** under Android Settings → Default apps → Home app. See the
[project guide](docs/project-guide.md) for emulator setup and device notes.

## Development order

The next work is to centralize design tokens, represent tiles as data, and add
editable layouts and preference persistence. Live content, keyboard behavior,
accessibility across screen sizes, automated coverage, and CI follow as the
interaction model settles. The full sequence is maintained in the roadmap.

## License

This repository is public, but no software license has been selected yet.
Public visibility allows inspection and discussion; it does not grant reuse
rights under an open-source license.
