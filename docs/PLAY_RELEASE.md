# Google Play release guide

Everything Play Console asks for, with the answers that match what the app actually does today (version 0.1.0, version code 1).

## 1. Create the upload key (once)

Play App Signing holds the real app-signing key; you only keep an upload key. Create it and keep a backup somewhere safe — if it is lost you must ask Google to reset it.

```bash
mkdir -p keystore
keytool -genkeypair -v -keystore keystore/kemistri-everyday-upload.jks -alias kemistri-everyday-upload -keyalg RSA -keysize 2048 -validity 10000
```

Then copy `keystore.properties.example` to `keystore.properties` and fill in the passwords. Both the keystore and `keystore.properties` are git-ignored.

## 2. Build the bundle

```bash
./gradlew testDebugUnitTest bundleRelease
```

Upload `app/build/outputs/bundle/release/app-release.aab`. `bundleRelease` refuses to run until `keystore.properties` is valid. For every later upload, raise `versionCode` (and usually `versionName`) in `app/build.gradle.kts`.

The release build uses R8 code shrinking and resource shrinking. Play Console also asks for the deobfuscation file: upload `app/build/outputs/mapping/release/mapping.txt` with the same release.

## 3. Privacy policy URL

Use the EdBetter mobile privacy page: https://www.edbetter.uk/mobile-privacy/

That page must name Kemistri Everyday (it currently lists Kemistri, Kromosome, 8K8tris and KraftyK8 Saga). It describes Firebase and AdMob, which this app does not include, so it helps to add a line that Kemistri Everyday contains no ads or analytics and stores progress only on the device. `docs/privacy-policy.html` has app-specific wording you can copy.

## 4. Store listing

| Field | Source |
| --- | --- |
| App name (30 chars) | `app/src/main/playstore/listing/en-GB/title.txt` |
| Short description (80 chars) | `app/src/main/playstore/listing/en-GB/short-description.txt` |
| Full description (4000 chars) | `app/src/main/playstore/listing/en-GB/full-description.txt` |
| App icon 512×512 | `app/src/main/playstore/listing-assets/icon_512.png` |
| Feature graphic 1024×500 | `app/src/main/playstore/listing-assets/feature_graphic_1024x500.png` |
| Phone screenshots (2–8) | `app/src/main/playstore/screenshots/1_home.png` … `5_streak_calendar.png` |
| Release notes | `app/src/main/playstore/release-notes/0.1.0.txt` |

Suggested category: **Education** (or Games › Educational). Default language: English (United Kingdom).

Tablet screenshots are only needed if you want the app featured on tablets; the app is portrait-only and phone-first.

## 5. App content answers

**Data safety**

- Does your app collect or share any of the required user data types? **No.**
- Is all user data encrypted in transit? Not applicable — the app makes no network requests (no INTERNET permission).
- Account creation: **No accounts.**
- Data deletion: users clear app storage or uninstall.

Game progress, streak history, pending reviews and settings stay in on-device app storage. Android Auto Backup is enabled; Google's guidance is that data backed up by the Android system is not counted as developer collection.

**Ads:** No, the app does not contain ads.

**App access:** All functionality is available without special access or login.

**Content rating (IARC questionnaire):** Category "Game" or "Reference/Education". No violence, sexual content, profanity, controlled substances, gambling, user interaction, location sharing or purchases. The Everyday Chemistry cards mention ordinary household chemistry with safety notes; answer "No" to drugs/alcohol/tobacco questions. Expected rating: PEGI 3 / Everyone.

**Target audience:** see the decision below.

**News app:** No. **COVID-19 contact tracing:** No. **Government app:** No. **Financial features:** None. **Health:** No.

## 6. Decision: target age groups

- **13 and over (recommended for the first release):** keeps the app outside Google Play's Families policy requirements while the content review is finished.
- **Including under-13s:** the app has no ads or data collection, so it is a good fit, but it must then meet the Families policy and Teacher Approved criteria and will get extra review.

## 7. Testing track

New personal developer accounts must run a **closed test with at least 12 testers for 14 consecutive days** before production access is granted. Organisation accounts skip this. Start with Internal testing to check the upload, then a Closed test.

## Pre-launch notes

- Target SDK is 36, above Play's current minimum.
- The README production gates (Room schema, expert content review, mission reachability simulation) are product-quality items, not Play requirements.
