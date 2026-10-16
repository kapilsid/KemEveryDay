# Kemistri Everyday

Standalone Android app for EdBetter with application ID `uk.edbetter.kemdaily`.

The application intentionally runs in portrait orientation. Its board, discovery tray, daily cards, and one-handed controls are designed around a consistent portrait play area.

## Run in Android Studio

1. Open the **`KemistriEveryday` folder itself** in Android Studio (not the parent `StudioProjects` folder).
2. Allow the Gradle sync to finish. The project uses Android Gradle Plugin 8.9.3, Gradle 8.11.1, JDK 17, and compile SDK 36.
3. In **Settings › Build, Execution, Deployment › Build Tools › Gradle**, select Android Studio's embedded JDK 17 if no Gradle JDK is selected.
4. Open **Tools › Device Manager** and start an API 36 phone. On this development machine, `Medium_Phone_API_36` is ready to use.
5. Select the shared **Kemistri Everyday** run configuration and press Run. Android Studio will build, install, and open the default activity.

The `.run` directory provides three shared configurations:

- **Kemistri Everyday** — install and launch the app on the selected device.
- **Unit Tests** — run the pure Kotlin chemistry and game tests.
- **Emulator Smoke Test** — launch the app instrumentation test on the selected emulator.

If Android Studio asks for the SDK location, create `local.properties` containing your own SDK path:

```properties
sdk.dir=/path/to/Android/sdk
```

`local.properties` is intentionally excluded from source control because it is machine-specific.

## Command line

Open this directory in Android Studio, or run:

```bash
./gradlew testDebugUnitTest assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

To start the configured API 36 emulator, run all JVM and instrumented smoke tests, and leave the app open:

```bash
./scripts/emulator-smoke-test.sh
```

Pass a different installed AVD name as the first argument when required:

```bash
./scripts/emulator-smoke-test.sh Pixel_9
```

## Implemented MVP

- Pure Kotlin chemistry engine with nested formula parsing and order-independent composition matching.
- Versioned offline library of 127 known substances, two-to-three-sentence notes, all 118 element symbols, explicit isomer ambiguity, and no composition-to-structure overclaiming.
- Deterministic 5×5 Molecule Hunt engine: eight-direction paths, swipe or tap selection, backtracking, scoring, repeats, streaks, gravity, caps, hints, and shuffle recovery.
- Standard 10-in-20 mission and reproducible UTC daily seed.
- Compose home, hunt, discovery reveal, completion/result, daily, interactive Everyday Chemistry, pending-review, and settings screens.
- Discoveries belong to the current hunt and reset with a new game; there is no persistent collection.
- Device-local active-game restore, pending-review counts, and settings.
- Daily Board history and streak calendar: best result per UTC board date, current and best streaks, a Monday-first month calendar, and stale saved Daily games discarded at the UTC rollover.
- Accessibility descriptions, numbered selections, 48dp controls, live chemical symbols, and reduced-motion preference.
- Google AdMob interstitials with the UMP consent prompt, shown only when a finished board is left (at most one every three minutes), never during a hunt. Pending formulas never leave the device.

The catalogue model, gameplay/reference split, scientific caveats and source conventions are documented in `docs/CHEMISTRY_LIBRARY.md`.

## Google Play release

Release builds are shrunk with R8 and signed from an untracked `keystore.properties` (copy `keystore.properties.example`). Store listing text, the 512px icon, the feature graphic and phone screenshots are in `app/src/main/playstore/`, the privacy policy is `docs/privacy-policy.html`, and `docs/PLAY_RELEASE.md` has the step-by-step Play Console answers.

## Production gates

The brief is retained at `docs/DEVELOPMENT_SPEC.md`. Before store release:

- Replace the lightweight preferences repository with the specified Room schema and add migrations.
- Expand expert-reviewed chemistry/content provenance and localisation resources.
- Prove 10-discovery mission reachability over 10,000+ complete simulated sessions. Current generation guarantees a playable initial board and recovers dead boards, but does not claim the stronger full-mission mathematical guarantee.
- Add consent-compliant monetisation, analytics adapters, audio, and the optional special-lattice mode.
- Review and export the generated icon source into final adaptive production artwork and complete the illustration/audio inventory.

## Visual asset

The generated 1024px mascot source is `app/src/main/res/drawable-nodpi/kemistri_everyday_icon_source.png`. The launcher currently uses a small deterministic vector foreground for adaptive-icon safety.

The approved 1024px branded home/splash artwork is `app/src/main/res/drawable-nodpi/kemistri_everyday_brand.png`. Its embedded wordmark is used at display size, while the text-free mascot remains the adaptive launcher treatment so the icon stays legible when small or masked by Android.
