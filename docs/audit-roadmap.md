# TAE code audit and improvement roadmap

Audit date: 2026-10-26
Scope: Android launcher source, manifest, Gradle setup, and first emulator prototype.

## Audit summary

The app is a small, understandable Compose launcher prototype. Its main flow works
on the Android 17 emulator: the home screen renders, the Tessa tile responds, and
the app drawer opens and searches installed launchable apps.

The first Android Lint run found three locale errors and six warnings. The errors
were caused by reading the process default locale directly from a composable. The
warnings covered outdated Gradle and Kotlin patch versions, a Compose modifier
parameter order, redundant orientation settings, explicit legacy backup policy,
and a URI helper.

## 20 improvements

### Fixed in this pass

1. **Observe locale changes in Compose.** Read the locale from
   `LocalConfiguration` so date formatting updates when the device language
   changes.
2. **Show installed app icons.** Render each launchable package’s actual icon in
   the drawer instead of repeating a letter tile.
3. **Handle app-list loading and empty results.** The drawer now distinguishes
   loading, no available apps, and no search matches.
4. **Add accessible tile roles.** Clickable home tiles and app rows expose button
   semantics and merged labels.
5. **Move UI copy into Android resources.** User-visible labels and feedback now
   have a string-resource home for translation.
6. **Correct the Phone tile symbol.** It now uses a phone glyph rather than a
   search-like mark; the SMS intent uses a constructed `smsto:` URI.
7. **Use AndroidX edge-to-edge handling.** This replaces deprecated direct system
   bar color setters while retaining safe-drawing insets.
8. **Let Android recreate the activity for display changes.** Removed the
   unnecessary `configChanges` and explicit `unspecified` orientation settings.

### Next development items

9. **Centralize the visual system.** Move colors, type scale, tile radii, and
   spacing into a Compose theme and shared design tokens.
10. **Represent tiles as data.** Separate tile identity, label, action, color, and
    size from the home-screen layout code.
11. **Make the home grid editable.** Add a layout mode for moving, resizing,
    adding, and removing tiles.
12. **Persist launcher preferences.** Store tile arrangement and theme settings,
    then define Android cloud-backup and device-transfer rules for that data.
13. **Give live tiles real data sources.** Connect calendar, messages, media, and
    Tessa tiles to useful system or app state.
14. **Replace prototype glyphs and app branding.** Build a consistent Tessarchy
    icon set and an adaptive Android launcher icon.
15. **Support the IME in app search.** Keep the search field and results usable
    when the keyboard is visible, and add clear/submit actions.
16. **Adapt to large text and varied screens.** Check font scaling, landscape,
    tablets, cutouts, and narrow phone widths; remove fixed-height clipping.
17. **Improve system navigation behavior.** Refine Home and Back behavior, focus
    order, keyboard navigation, and the Android launcher-role selection flow.
18. **Add automated app coverage.** Add unit tests for tile filtering and intent
    selection plus Compose tests for home, drawer, and empty states.
19. **Run checks on every change.** Add GitHub Actions for lint, debug builds, and
    tests on pull requests.
20. **Prepare device and release work.** Establish versioning/signing policy, then
    document recovery-safe OnePlus 7 (`guacamoleb`) bring-up and ROM validation.

## Validation

The initial `:app:lintDebug` run found three locale errors and six warnings. The
first correction pass removed those issues; a follow-up pass also addressed the
remaining Compose modifier-order and Android KTX warnings. The current audit
validation runs `:app:lintDebug` and `:app:assembleDebug`; do not treat emulator
performance on this host as a release device benchmark. No automated test suite
exists yet.
