# Tessarchy Android Edition architecture

## Current app boundary

The first milestone is a regular Android application registered as a Home app.
It draws the Tessarchy home surface with Jetpack Compose, asks Android to open
phone and content intents, and queries installed launcher activities for the
app drawer. Android retains responsibility for application lifecycle,
permissions, package management, telephony, hardware, and system services.

This approach gives TAE a real phone surface while keeping it installable and
reversible like any other app. The app is not a system image, a fork of Android,
or a OnePlus 7 ROM.

## Runtime flow

1. Android launches `MainActivity` as the selected Home activity.
2. After the user accepts the installed-app disclosure, package APIs enumerate
   launcher activities off the main thread. Entries use component identity and
   bounded icon bitmaps; the consent choice can be revoked in Studio.
3. Compose renders the clock, calendar date, configurable Metro tiles, and app
   drawer.
4. Preferences DataStore stores tile order, hidden tiles, tile widths, and the
   selected palette. Android backup rules include those preferences.
5. Tile actions use Android intents so the system can route calls, messages,
   calendar, media, and content requests to installed apps.
6. Android services and the device vendor stack continue to run underneath.

The installed-app query runs away from the main thread. The app drawer refreshes
when the launcher resumes after opt-in. App names and icons stay in memory; TAE
does not request internet access or transmit that inventory. UI strings use Android resources, locale-aware date
formatting observes configuration changes, and tile reorder controls provide
both drag handles and TalkBack move actions.

## Current launcher features

- Three local palettes: Midnight, Daylight, and Cobalt.
- Optional tiles can be hidden, resized, and moved; the Apps tile remains
  available so the user can always reach installed apps.
- Two-column phone layout and four-column layout when the available width is at
  least 640 dp.
- Searchable app drawer with component-specific app entries and actual icons.
- First-run disclosure and opt-in for installed-app inventory; revocation clears
  the in-memory list. The public privacy policy is linked in Studio.
- An Android Home role request in Studio to guide the user through setting the
  launcher as default.
- The Tessa surface clearly marks assistant functionality as a future feature.

The first release does not host third-party widgets, read calendar events, or
change Android System UI. App tiles delegate to installed system apps through
intents and do not request phone or SMS permissions.

## Layering strategy

The long-term concept separates five areas of work:

1. **Tessarchy launcher:** Home, app drawer, tile layout, gestures, and phone /
   tablet adaptation. This is the current implementation layer.
2. **Tessarchy system surfaces:** Navigation, notifications, quick settings,
   lock screen, and settings integration. This may require privileged apps or
   System UI customization.
3. **Tessarchy apps and services:** Tessa and companion experiences that use
   Android's app, media, messaging, and accessibility APIs.
4. **Android framework and AOSP:** Platform behavior, application runtime,
   permissions, and system services. Changes here raise maintenance cost.
5. **Device integration and hardware:** Vendor interfaces, radio, camera,
   audio, sensors, power, boot chain, kernel, and device-specific configuration.

The layers describe an exploration path, not a promise that every layer will be
replaced. Deeper work should follow only when launcher-level behavior proves the
need and can be validated on a virtual device first.

## Platform baseline

- `minSdk`: 31 (Android 12)
- `compileSdk` and `targetSdk`: 37 (Android 17)
- Kotlin Compose compiler plugin: 2.4.21
- Android Gradle Plugin: 9.4.1
- Gradle: 9.8.2
- UI toolkit: Jetpack Compose with Material 3 components

## Device strategy

The standard OnePlus 7 (`guacamoleb`) is a later hardware target. Current
prototype screenshots come from the Android 17 API 37 emulator. They are used
for release-candidate validation only. They do not represent a tested OnePlus
build. The stock-firmware Android 12 requirement is
part of preparation for future ROM research; it does not set this launcher's
minimum SDK or the Android version of a hypothetical later system image.

Before any device-specific system image is treated as usable, validate the
bootloader and recovery path, encryption, radio, camera, audio, sensors,
charging, thermal behavior, and update process. The [device port concept](media/oneplus-7-port-concept.webp)
is a planning illustration, not a flashable build guide.
