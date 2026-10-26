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
2. Compose renders the clock, calendar date, tiles, and app drawer.
3. Android package APIs provide launchable app labels and icons.
4. Tile actions use Android intents so the system can route calls, messages,
   calendar, media, and content requests to installed apps.
5. Android services and the device vendor stack continue to run underneath.

The installed-app query runs away from the main thread. UI strings use Android
resources, locale-aware date formatting observes configuration changes, and
interactive surfaces expose button semantics to accessibility services.

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
prototype screenshots come from the Android 17 API 37 emulator and do not
represent a tested OnePlus build. The stock-firmware Android 12 requirement is
part of preparation for future ROM research; it does not set this launcher's
minimum SDK or the Android version of a hypothetical later system image.

Before any device-specific system image is treated as usable, validate the
bootloader and recovery path, encryption, radio, camera, audio, sensors,
charging, thermal behavior, and update process. The [device port concept](../assets/concepts/oneplus-7-port-plan.svg)
is a planning illustration, not a flashable build guide.
