# Daily Champions leaderboard (Firebase)

The Android app is registered with Firebase project `kemistri-everyday`. There is one worldwide
ranking per UTC day. A player's entry is their **Molecule Board score + Science Uncovered score**,
submitted only once both of today's games are finished. The first completed total for a day is final.

The app signs players in anonymously, gives them a generated public nickname (editable, 3–16
letters, numbers or spaces), shows today's top 100 and keeps working offline with local scores.

## Firebase console setup (free Spark plan)

1. **Authentication → Sign-in method → Anonymous**: enable.
2. **Firestore Database**: create in Production mode, European location.
3. **Firestore Database → Rules**: paste the whole of `firestore.rules` and click **Publish**.
4. **App Check → Apps → Kemistri Everyday → Play Integrity**: register, add the Play App Signing
   SHA-256 (Play Console → Setup → App signing) in Project settings, then turn on **Enforce** for
   Cloud Firestore once the release build is live. This stops scripts that are not your real app.

`app/google-services.json` is ignored by Git. Keep it out of public repositories.

## Data model

```text
players/{uid}
  displayName, normalizedName, updatedAt

daily_champions/{yyyy-MM-dd}/players/{uid}
  displayName, score, moleculeScore, scienceScore, uniqueDiscoveries,
  mysteriesSolved, contentVersion, submittedAt, verified
```

## What the rules enforce

- Only the signed-in player can create their own entry, once per date; no updates or deletes.
- The date must be today or yesterday (UTC).
- `score` must equal `moleculeScore + scienceScore`.
- Molecule Board: at most 20 finds, at most 130 points per first find and 30 per repeat, and
  nothing without a find. Science Uncovered: at most 3 mysteries a day, 100 points each.
- The name must match the player's profile; `verified` must be false; no extra fields.
- Signed-in players can read the board; profiles are private.

The same limits live in `ChampionLimits` (app) and `functions/index.js`. Change all three together.

## Limits of a client-written leaderboard

The rules stop impossible scores but cannot prove a score was really played. App Check closes most
of that gap. For a fully verified board, upgrade to Blaze and deploy the optional
`submitDailyChampions` function, which writes `verified: true`; a later step would replay the run
from a submitted move list.

```bash
npx firebase-tools login
cd functions && npm install && cd ..
npx firebase-tools deploy --only firestore:rules,functions
```

## Testing the rules

`firestore-tests/` runs 21 checks against the Firestore emulator (valid score accepted; wrong totals,
caps, dates, names, extra fields, second writes and updates rejected):

```bash
cd firestore-tests && npm install && npm test
```
