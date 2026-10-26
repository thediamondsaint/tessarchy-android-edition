# Play submission checklist

## Prepared in this repository

- [x] Current Android 17/API 37 target and Android App Bundle build task
- [x] Launcher privacy disclosure before app inventory access
- [x] In-app privacy controls and public policy page
- [x] Store listing draft and final phone screenshot assets
- [x] Feature graphic and app icon
- [x] Tablet listing screenshot captured from the adaptive four-column layout
- [ ] Verify all external tile intents and Home-role setup on physical devices
- [ ] Final accessibility and contrast review on light/dark themes
- [ ] Run Play pre-launch report and resolve every crash/ANR
- [ ] Final human review of content rating, Data safety, target audience, app access, and store text in Play Console

## Release build

1. Confirm `versionCode` and `versionName` in `app/build.gradle.kts`.
2. Use the local upload key stored outside the repository and enroll in Play App Signing. Do not put keystores or passwords in GitHub; keep a secure backup of the key.
3. The local untracked `keystore.properties` file contains `tae.release.storeFile`, `tae.release.storePassword`, `tae.release.keyAlias`, and `tae.release.keyPassword` (see `.gitignore`).
4. Build `mise exec -- ./gradlew :app:bundleRelease` and confirm the AAB signature with `jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab`.
5. Upload to an internal Play testing track first. Install the Play-generated build and test Home selection, app-library disclosure, tile actions, backup/restore behavior, and return to the previous launcher.

## Play Console declarations to complete

- App category and contact email / website.
- Content rating questionnaire.
- Target audience and child-safety declarations.
- Ads declaration: no ads in this build.
- Data safety: accurately declare that Tessarchy does not collect the installed-app inventory or transmit it to Tessarchy or an AI provider. The optional app settings backup is handled by Android Backup when enabled by the user; include that detail in reviewer notes and verify the Play Console answers against the current backup behavior. Reassess if SDKs or assistant providers are added.
- App access: no login or reviewer credentials required.
- Main store listing, privacy policy URL, icon, feature graphic, and phone/tablet screenshots.
- Developer account verification and any testing-track requirements shown by Play Console.

New personal developer accounts created after November 13, 2023 need a closed test with at least 12 continuously opted-in testers for 14 days before requesting production access. This is an account-level gate; confirm the account type and creation date in Play Console. See [Google Play's current testing requirements](https://support.google.com/googleplay/android-developer/answer/14151465).

Google Play currently requires new apps and updates to target Android 16 (API 36) or higher from August 31, 2026. TAE targets Android 17/API 37. Google has announced API 37 as the next Play target level in August 2027. Recheck the [target API policy](https://support.google.com/googleplay/android-developer/answer/11926878) immediately before upload.

## Accurate reviewer notes

The app declares only launcher-intent visibility in `<queries>` and does not request `QUERY_ALL_PACKAGES`. The app library is a core launcher feature, but inventory access is opt-in and disclosed before the query. Names/icons stay in memory and are used only to display and search local apps. The release has no internet permission, analytics SDK, ads SDK, account, AI provider, or remote service.
