# TAE code audit and improvement roadmap

Audit date: 2026-10-26
Scope: Android launcher source, manifest, build configuration, and emulator-ready release candidate.

## Audit summary

The initial implementation was a compact Compose prototype with launcher discovery, a fixed tile grid, and a basic app list. The audit found that activity discovery ran only at startup, app activities could be collapsed by package name, tile layout and theme were fixed, there was no persistent settings model, and the primary UI was concentrated in one activity. It also had no automated tests or CI.

The current implementation separates app discovery, tile preferences, data persistence, design tokens, and the launcher screens. The Android 16/API 36 release-candidate check set passes. Wider screen-size, accessibility, keyboard, and physical-device review remain before a production release.

## 20 improvement areas

### Implemented in this pass

1. **Centralize visual tokens.** Three Metro palettes plus shared spacing, radius, and type tokens are in `ui/theme`.
2. **Represent tiles as stable data.** `TileId` contains title, identity, default width, and required-tile behavior.
3. **Edit tile visibility.** Personalization can hide optional tiles; the app drawer tile stays available.
4. **Resize and reorder tiles.** Width switches, drag reorder, and accessible earlier/later actions are implemented.
5. **Persist launcher choices.** Preferences DataStore saves order, hidden tiles, width, and theme.
6. **Recover malformed or old settings.** Preference sanitization restores missing tile IDs and required tile constraints.
7. **Refresh installed activities.** App discovery runs off the main thread on activity resume.
8. **Preserve multiple app entries.** Drawer items use `ComponentName` identity, not package-only identity.
9. **Bound icon memory use.** App icon bitmaps are capped at 96 px before conversion.
10. **Improve app search.** Drawer search supports the IME, clear action, empty state, and no-match feedback.
11. **Adapt to wide windows.** The grid uses four columns above 640 dp and two columns on narrower windows.
12. **Improve accessibility semantics.** Tiles, app rows, search, personalization, and reorder controls expose labels or actions.
13. **Make the Tessa boundary clear.** The Tessa screen explains that assistant functionality is not part of this launcher build.
14. **Use system intents for phone tasks.** Phone, messaging, calendar, music, and photo actions delegate to Android apps.
15. **Use adaptive launcher icon resources.** Android 8+ adaptive and round icon resource entries are defined.
16. **Configure user-data backup.** DataStore preferences are included in cloud backup and device-transfer rules.
17. **Add preference-model unit tests.** Tests cover order changes, invalid moves, required tiles, and preference sanitization.
18. **Document the external research.** A 30-area survey records examples, star snapshots, and license decisions in [`github-research.md`](github-research.md).

### Release-candidate checks completed

19. **Verify core runtime behavior.** API 37 preview-emulator review covered the home grid, app drawer, personalization, search, and theme persistence across restart. The Compose smoke test opens the required Apps tile, searches, and clears the query.
20. **Automate release checks.** GitHub Actions runs lint, unit tests, a debug build, and the Compose smoke test on a stable API 36 emulator. The same instrumentation test also passes locally on API 37 after pinning Espresso 3.7.0, which replaces a removed reflective `InputManager.getInstance` call.

Broader validation remains: Home-app selection, all external intents, Back behavior, wide layouts, keyboard navigation, accessibility review, and physical devices.

## Research and reuse notes

The most relevant Metro-specific repository found was GridLauncher (112 stars at the survey snapshot). General launcher references are much larger, including Lawnchair (13,786 stars), Kvaesitso (5,315), Olauncher (3,914), and KISS (3,523). Several use GPL licenses or lack clear license metadata, so they are reference-only. TAE uses the Apache-2.0 Reorderable library as a dependency and otherwise keeps its implementation original.

## Validation record

The latest local `:app:testDebugUnitTest`, `:app:lintDebug`, `:app:assembleDebug`, `:app:compileDebugAndroidTestKotlin`, and `:app:connectedDebugAndroidTest` checks pass. GitHub Actions is green for both the local checks and stable API 36 emulator smoke test. Lint reports one warning for the version qualifier on the adaptive icon resources. Manual API 37 preview-emulator review covered home, app drawer, personalization, and theme persistence across an app restart; the instrumented smoke test also passes there with Espresso 3.7.0. This is a launcher release candidate, not a Play Store release: signing, store policy review, privacy disclosures, release assets, and wider device validation remain.
