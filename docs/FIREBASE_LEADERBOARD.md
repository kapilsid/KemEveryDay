# Daily Champions leaderboard (Firebase)

The Android app is registered with Firebase project `kemistri-everyday`. There is one worldwide
ranking per UTC day. A player's entry is their **Molecule Board score + Science Uncovered score**.
An entry is posted as soon as either daily game is finished; the unplayed game counts as zero. If the
player later finishes the other game, the same entry is increased to the new combined total.
The **This Week** board adds those daily totals from Monday through Sunday UTC and starts again each
Monday. There is no permanent cumulative worldwide leaderboard.

The app signs players in anonymously, gives them a generated public nickname (editable, 3–16
letters, numbers or spaces), shows the daily and weekly top 100, and keeps working offline with local scores.

At 00:00 UTC the app reads a new dated ranking. Older date documents remain in Firestore and in the
device history; they are not deleted. There is deliberately no all-time worldwide ranking in this
version, so a strong score from an easier or older content set cannot permanently dominate new days.

## Firebase console setup (free Spark plan)

1. **Authentication → Sign-in method → Anonymous**: enable.
2. **Firestore Database**: create in Production mode, European location.
3. **Firestore Database → Rules**: paste the whole of `firestore.rules` and click **Publish**.
4. **Do not enforce App Check while doing the first emulator tests.** Debug builds already use the
   App Check debug provider and release builds use Play Integrity. Before production, register the
   Android app under **App Check → Apps → Kemistri Everyday → Play Integrity**, add the Play App
   Signing SHA-256 (Play Console → Setup → App signing) in Project settings, verify both debug and
   release tokens, and only then turn on **Enforce** for Cloud Firestore.

`app/google-services.json` is ignored by Git. Keep it out of public repositories.

## Data model

```text
players/{uid}
  displayName, normalizedName, updatedAt

daily_champions/{yyyy-MM-dd}/players/{uid}
  displayName, score, moleculeScore, scienceScore, uniqueDiscoveries,
  mysteriesSolved, contentVersion, submittedAt, verified

weekly_champions/{week-start-yyyy-MM-dd}/players/{uid}
  displayName, score, moleculeScore, scienceScore, daysPlayed,
  uniqueDiscoveries, mysteriesSolved, contentVersion, updatedAt, verified
```

## What the rules enforce

- Only the signed-in player can create their own dated entry. Updates may only increase its component
  scores and total, so finishing the second game improves the same entry; reductions and deletes fail.
- Weekly entries use the same monotonic protection, allow at most seven played days, and cap their
  game totals, discoveries and solved mysteries accordingly.
- The date must be today or yesterday (UTC).
- `score` must equal `moleculeScore + scienceScore`.
- Molecule Board: at most 20 finds, at most 130 points per first find and 30 per repeat, and
  nothing without a find. Science Uncovered: at most 3 mysteries a day and 175 points per solved mystery (100 base, 25 perfect bonus, 50 per life left).
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

`firestore-tests/` checks the rules against the Firestore emulator (partial and improved scores accepted;
wrong totals, caps, dates, names, extra fields, reductions and score rewrites rejected):

```bash
cd firestore-tests && npm install && npm test
```
