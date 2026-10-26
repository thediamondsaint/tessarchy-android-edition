# Tessarchy Android Edition: project guide

Tessarchy Android Edition (TAE) explores what it would take to make Tessarchy
the primary experience on an Android phone. The project starts with a
customizable launcher and can grow toward a deeper Android system experience
if the design and device work justify it.

## Product direction

TAE brings Tessarchy's spatial, customizable approach to a phone. Its first
visual language takes cues from Metro: a deliberate grid, confident color,
large readable type, quick glances, and tiles that behave like useful places
rather than a wall of tiny icons. Rounded corners remain restrained. Tessarchy's
navy and mint frame the interface while cobalt, teal, orange, violet, and magenta
give individual tiles clear identities.

The home experience should answer three questions quickly: what is happening
now, what can I do next, and how do I get to the rest of my phone? The launcher
must remain accessible, responsive to screen size and font scale, and compatible
with Android apps and services.

## What is built today

The Android app is a Jetpack Compose launcher that can be selected as the
Android Home app and currently includes:

- a live clock and date;
- Metro-style colored calendar, phone, messages, Tessa, photos, music, and Apps
  tiles;
- Android intents for common phone actions;
- a searchable list of installed launchable apps with their application icons;
- loading, empty, and no-match drawer states;
- three Metro palettes and editable tile visibility, size, and order;
- local settings persistence with AndroidX DataStore and Android backup rules;
- a wider four-column grid and accessible tile movement actions;
- localized UI strings and accessible control semantics.

The emulator captures in `../artifacts/` show the running prototype. The images
in `../assets/concepts/` are design explorations; they are not screenshots of
implemented features. The concept gallery on the project page labels these
separately.

## What this project is not yet

The current build is an app, not a replacement Android operating system. It
does not replace the lock screen, System UI, settings, notification shade,
kernel, vendor services, or firmware. It does not yet provide calendar or
message live content, third-party widget hosting, an import/export settings
flow, or a Tessarchy system image. Those are future investigations, not current
capabilities.

## Technical shape

The current application is a Kotlin and Jetpack Compose Android app. Android
continues to own device services, package management, permissions, telephony,
and application lifecycle. TAE provides the home surface and launcher behavior.

The longer-term system exploration is represented as layers:

1. Tessarchy home, app drawer, layouts, and interaction model.
2. Tessarchy system surfaces such as notifications, quick settings, and
   navigation.
3. Tessarchy apps and integration services.
4. Android framework and AOSP platform code.
5. Device-specific integrations and the Linux kernel / hardware base.

Each lower layer carries more maintenance and device risk. The project should
earn deeper platform work by proving useful behavior at the launcher layer
first. See [architecture notes](architecture.html) and the [OnePlus 7 bring-up
concept](media/oneplus-7-port-concept.webp).

## Development setup

Requirements: JDK 17, Android SDK Platform 36, Android Build Tools 36, and the
Android Emulator. `mise.toml` selects the project Java runtime. Set
`local.properties` to the local SDK path; this machine-specific file is ignored
by Git.

Build the debug app:

```sh
mise exec -- ./gradlew :app:assembleDebug
```

Run Android Lint:

```sh
mise exec -- ./gradlew :app:lintDebug
```

Run the preference model tests:

```sh
mise exec -- ./gradlew :app:testDebugUnitTest
```

Run the Compose smoke test on an available emulator:

```sh
mise exec -- ./gradlew :app:connectedDebugAndroidTest
```

Install onto a running emulator or attached Android device:

```sh
mise exec -- ./gradlew :app:installDebug
```

The local Android 17 preview virtual device is named `TAE_API_37` and is used
for visual comparison; CI runs on stable API 36. After installation, select
Tessarchy in Android's Default apps → Home app. The OnePlus 7 (standard
model, codename `guacamoleb`) is a later device target, not a tested release
target. Preserve a recovery path and validate boot, radio, camera, audio,
encryption, and update behavior before treating any system image as usable.

## Current development sequence

The 20-item audit in [audit-roadmap.html](audit-roadmap.html) tracks the current
release-candidate checks and remaining work. GitHub Actions runs lint, unit
tests, a debug build, and an emulator smoke test on pushes to `main` and pull
requests. Device-specific system work stays later in the sequence.

## Project principles

- Keep the phone usable while Tessarchy evolves.
- Make Metro's color and grid serve information, not decoration alone.
- Distinguish concepts from implemented behavior in every demo and document.
- Prefer Android APIs for hardware and app integration until a deeper layer is
  necessary.
- Track compatibility, accessibility, and recovery alongside visual polish.

## Contributing and discussion

Open a GitHub issue for a focused feature idea, bug, or design question. Include
the Android version and device or emulator when reporting behavior. For visual
changes, attach a screenshot and note whether it is a concept or a running
build. Pull requests should explain the user-visible change and include the
relevant lint/build result.

## License status

The repository is public, but no software license has been chosen. Public
visibility makes the work available for inspection and discussion; it does not
grant reuse rights under an open-source license.
