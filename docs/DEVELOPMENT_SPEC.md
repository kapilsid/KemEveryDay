# Kemistri Everyday — Android Development Brief v1.0
Date: 16 October 2026
Owner: EdBetter / Kemistri
Status: implementation blueprint; chemistry and visual assets require production validation.

## 1. Product definition
A separate Android application, **Kemistri Everyday**, designed for short, enjoyable daily chemistry engagement. Primary mode: Molecule Hunt. Secondary features: Everyday Chemistry fact cards and a reproducible Daily Challenge. Cross-promote the existing Kemistri app without forcing sign-in.

**Brand:** use existing Kemistri typography, logo, colour palette, illustrations and visual conventions once original assets are supplied. The concept artwork supplied in chat is a visual reference, **not** a production sprite atlas or verified match to the current Kemistri design.

## 2. Technology and architecture
Recommended: Kotlin + Jetpack Compose, Material 3, MVVM with immutable `GameState`, StateFlow, Room persistence, DataStore settings, Hilt dependency injection, Navigation Compose. Implement a pure Kotlin game engine independent of UI, storage and ads. Target SDK and dependency versions should be chosen at implementation time from current stable releases.

Modules/packages:
- `core/chemistry`: element catalog, formula parser, composition normalization, substance records, structural rules, chemistry validation.
- `core/game`: board, path selection, recognition, gravity, constrained generation, move accounting, scoring, win state.
- `data`: Room DAOs for pending reviews, game saves and daily results; DataStore for preferences.
- `feature/hunt`, `feature/daily`, `feature/facts`, `feature/settings`.
- `monetization`: consent and ad orchestration separate from game logic.
- `analytics`: event interface with no personally identifying information.

## 3. Molecule Hunt: rules (agreed)
1. 5×5 board of **element-symbol tiles**, not individual letters. Each tile represents one atom, including multi-letter symbols such as Na, Cl, Li, Ca and Mg.
2. Player selects an ordered path of orthogonally or diagonally adjacent cells (8-neighbour connectivity). A tile cannot be reused within one path. Support swipe-drag on Android; tap-to-select is an accessibility fallback. Backtrack one tile and clear selection.
3. Formula lookup is independent of selection order: C,H,H,H,H = CH4 in any traversal order. However, the selected path must be contiguous.
4. Recognized valid composition: identify canonical chemical substance(s), award score, consume **one move**, remove all selected cells, apply gravity within columns, spawn replacement atoms at top, animate, persist.
5. Unknown composition: do **not** assert chemically impossible. Record it in local Pending Discovery Review, do not consume a move, allow retry. Deduplicate by canonical composition and track frequency. Rate-limit logs.
6. Initial mission: discover **10 unique substances in 20 successful moves**. Repeats consume a move and earn reduced points but do not increase the unique count. Unrecognized attempts do not consume a move.
7. At 10 unique discoveries, display success overlay with score, remaining moves, stars, and **Finish** / **Continue discovering**. Continue uses only remaining moves; mission completion is irrevocable.
8. At 0 moves, show result. Hint highlights one feasible path; shuffle costs no move in the prototype. In production, prevent infinite exploit loops (e.g. limited free shuffles or solvability-preserving automatic shuffle).
9. Optional separate 2-minute challenge; a daily seeded challenge uses the same deterministic board-generation rules for all players in the same challenge version.
10. No automatic game-over merely because the board temporarily has no paths; trigger guaranteed recovery.

## 4. Chemistry data model and correctness
Use a curated, versioned, reviewed dataset. Suggested record:
```
Substance(id, displayName, formula, canonicalComposition,
          category, aliases, learningFact, difficultyTier,
          validationStatus, structuralRuleId?, isAmbiguous)
```
Represent formulas as a map `Map<ElementSymbol, Int>`, e.g. NaOH -> {Na:1,O:1,H:1}; Li2O -> {Li:2,O:1}. Implement a real parser supporting parentheses and nested groups, e.g. Ca(OH)2, rather than a regex that only recognizes flat formulas. Normalize to composition for matching; do not infer chemical bonds from composition.

When multiple isomers share a formula (e.g. C2H6O), show `C2H6O — multiple possible substances` until bonding mechanics exist; never automatically claim a particular isomer was constructed. Distinguish discrete molecules, ionic formula units, elemental substances, radicals (optional advanced mode), and network solids. Only include radicals when clearly labeled.

**Diamond/graphite:** CCCC alone is not diamond. Implement as an optional *special lattice-pattern* mini-game with an explicit educational disclaimer that 2D tiles symbolize a fragment of a 3D network; do not add `C4 = diamond` to the ordinary formula catalog.

Starter vetted examples for content review: H2, O2, N2, O3, H2O, H2O2, CO, CO2, CH4, NH3, NaOH, Li2O, NaCl, KOH, HCl, MgO, CaO, CaCO3, HNO3, H2SO4, SO2, SO3, C2H6, C2H4, C2H2, NaHCO3, Ca(OH)2. Check oxidation states, chemical stability, names and educational appropriateness before shipping. Formula lookup is **not** proof of feasible synthesis.

## 5. Scoring (agreed direction; tune through playtesting)
For first-time discovery:
```
A = number of selected atom tiles
E = number of distinct elements
R = min(40, sum(rarityBonus[element] for each distinct element))
base = 5 + 5*A + 5*E + R
streakBonus = min(15, 5*previousConsecutiveUniqueDiscoveries)
award = base + streakBonus
```
Suggested rarity: H/O=0, C/N=5, Cl/S=10, Na/K/Ca/Mg/F=15, Li=20. Rarity should be tied to **actual level spawn probabilities**, not geochemical abundance. For repeat discovery: `max(2, round(base*0.25))`, no streak bonus; reset streak. Unknown attempts: 0 points, no move cost; streak policy should be explicit (prototype resets it). Display scoring breakdown and score popup. Score can be balanced by configuration without changing engine.

## 6. Board generation and solvability
Spawn weights (starting point): H=6, O=5, C=3, N=2, Na/Cl/K/S/Ca/Mg/Li/F=1 each.
Caps on a 25-cell board: H<=8, O<=7, C<=5, N<=4, all others<=2. These are **limits**, not guaranteed target counts. Respect caps across the *entire board* when refilling, not independently for each column.

Critical: random weighted refill **alone does not guarantee solvability**. After every transition, enumerate all legal connected paths for known substances up to an appropriate length, and check for at least one *new* discoverable substance where feasible. If none, perform bounded rerolls/rebalancing or insert a validated path without breaking caps. To guarantee that 10 unique discoveries can be reached within 20 moves, use a constructive mission generator / precomputed solvable sequences, or dynamically guarantee progress; a mere one-move check is insufficient. Do not claim mathematical guarantee until automated property tests confirm it.

Algorithm for gravity: for each column, keep nonremoved tiles in bottom-to-top order, place them at the bottom, generate replacements for empty top slots, then animate falling. Use seeded PRNG so daily challenges replay deterministically. Validate both the initial board and each new board.

## 7. Screen-by-screen requirements
1. **Splash**: Kemistri Everyday identity, app icon, lightweight loading.
2. **Home**: Molecule Hunt (primary CTA), Today's Chemistry, Daily Challenge, review and settings; link to original Kemistri.
3. **Molecule Hunt**: 5x5 board, moves, unique progress, score, streak, selection tray, Discover/Clear, Hint/Shuffle, pause/help, last-discovery banner.
4. **Discovery reveal**: correct formula and name, classification, 1-line chemistry fact, points breakdown, continue. Respect reduced motion.
5. **Mission complete**: score, stars, remaining moves, Finish, Continue discovering.
6. **Game over**: summary, unique substances from this game and retry.
7. **In-game discoveries**: the Found counter opens a list scoped to the current hunt. Starting a new game clears it; there is no persistent collection.
8. **Pending Discovery Review**: locally recorded unmatched compositions, count, last tried; mark 'Not yet recognised', not 'invalid'; allow deletion.
9. **Everyday Chemistry**: one factual card daily, explanation, optional simple experiment with safety notes, share image/text.
10. **Daily Challenge**: date, seeded grid, rules, result, streak and historical calendar.
11. **Settings**: sound, vibration, reduced motion, privacy, consent options, language, accessibility.

## 8. Local persistence
Tables: `game_session(id, mode, seed, board_json, selected_path, moves_remaining, score, streak, state, updated_at)`, `discovery(id, substance_id, first_seen_at, times_found)`, `pending_formula(canonical_key, example_path_json, first_seen_at, last_seen_at, count, review_status)`, `daily_result(date_utc, version, seed, score, unique_count, completed)`. Save after each recognized move. Ensure app background/rotation/process death restores correctly. Keep unrecognized submissions on-device unless the user opts into telemetry and privacy terms allow it.

## 9. Daily challenge fairness
Use `seed = stableHash("kemistri-everyday|YYYY-MM-DD|challengeVersion")`; avoid language/runtime-dependent hash implementations. Store generated challenge version. All players should get equivalent content; if random replacement is involved, use the same deterministic PRNG and advance state deterministically. Define day rollover (recommend UTC) visibly and consistently. Prevent trivial date-change abuse where practical; offline play remains available.

## 10. Monetization
Core game should remain playable for free. Ads must not interrupt an atom-selection gesture, discovery animation or chemistry explanation. Consider a restrained interstitial only between completed sessions, subject to consent, age-appropriate treatment and platform policies. Rewarded ads can optionally grant cosmetic themes or extra hints, never be required for basic chemistry education. Children's-directed distribution requires a dedicated policy and SDK review; do not reuse existing Kemistri ad settings blindly. Avoid personalized ads where not permitted. No paid subscription required for the MVP.

## 11. Analytics events
`hunt_started(mode,level,seed_version)`, `formula_submitted(result,atom_count,element_count)`, `substance_discovered(substance_id,is_first,points)`, `hunt_completed(score,moves_used,unique_count)`, `hunt_abandoned(moves_remaining)`, `hint_used`, `shuffle_used`, `daily_started`, `daily_completed`, `fact_viewed`, `review_recorded`. Do not include personal data or raw device identifiers. Measure win rate, median session length, distinct discoveries/session, hint rate, stuck-board rate, Day-1/Day-7 retention, crash-free sessions.

## 12. Visual asset inventory
**Brand assets (source from existing Kemistri project):** official Kemistri logo/wordmark, palette, font specification, existing mascot/illustration, button style, background treatment. Do not claim concept images precisely match without originals.

**Required new assets:**
- App icon: 1024×1024 source illustration and Android adaptive icon foreground/background layers; no text in icon.
- Splash/home background, landscape-independent and portrait-safe (vector or layered art preferred).
- Molecule Hunt banner; Everyday Chemistry banner; Daily Challenge banner.
- Atom tile base shapes for normal/selected/hinted/disabled/rare states (vector XML/Compose Canvas); **render chemical symbols as localized UI text**, not baked into PNGs.
- Discovery glow/sparkles, falling-tile particles, score burst, success stars, trophy, streak flame.
- Chemistry illustration sets: molecules (scientifically accurate), lab glassware, onion/apple/rust everyday facts.
- Navigation icons, hint, shuffle, sound, settings, current-game discoveries, share, back.
- Empty state illustrations and pending-review icon.
- Audio: selection tick, valid discovery, rare discovery, mission win; provide mute.

**Deliverable formats:** vector SVG for illustrations where possible, Android VectorDrawable for simple icons, transparent WebP/PNG for raster characters, layered source file where available, strings.xml for all visible text. Use density-independent dp and scalable typography; never rely on a fixed 1080px device. Use drawable-nodpi for resolution-independent illustrations and appropriate Android mipmap/adaptive icon resources.

Concept artwork is **not** a transparent export of individual assets. Individual sprites should be generated/redrawn and reviewed before shipping. Avoid using AI-generated text embedded in production UI.

## 13. Accessibility and localization
All controls >=48dp hit target; color must not be the only selection indicator; selected path numbered; TalkBack reads element name, position and selection state; adjustable text size; no timer in standard mission; optional reduced motion. Localize text via Android resources, including chemical names and pluralization. Chemical formulas should use consistent subscripts in accessible formatted text; symbols remain standard IUPAC notation.

## 14. Acceptance tests
- CHHHH (any path order) resolves CH4; Na+O+H resolves NaOH; Li+Li+O resolves Li2O; N+N resolves N2; C+O resolves CO.
- Nonadjacent path is rejected; selected cell cannot be reused; selection backtracks.
- Invalid/unknown submissions do not use a move and appear in review exactly once with incremented attempt count.
- Repeated discoveries award 25% of base, consume one move, and do not increment unique count.
- 10th unique discovery opens completion overlay; continuing preserves score, moves and success status.
- Gravity conserves all unselected tiles, fills exactly 25 cells, never exceeds caps.
- Solvability/property tests across at least 10,000 deterministic seeds: no dead boards; mission completion reachable within allotted moves under the chosen guarantee algorithm.
- No loss of progress after process death; daily puzzle deterministic across devices and locales.
- Formula parser correctly handles Ca(OH)2; isomer ambiguity shown; CCCC not mislabeled as diamond.
- No ads during active play; consent/privacy flows pass review.

## 15. Suggested implementation order
**Sprint 1:** pure Kotlin chemistry parser, catalog, path recognizer and unit tests.
**Sprint 2:** immutable game engine, scoring, gravity, controlled generation, solvability tests.
**Sprint 3:** Compose board, touch/swipe input, animation, score and result UI.
**Sprint 4:** Active-game saves, pending discovery review, daily challenge and interactive Everyday Chemistry.
**Sprint 5:** Everyday Chemistry cards, branding, assets, accessibility, localization.
**Sprint 6:** analytics, privacy/consent, appropriate ads, QA, Play Store beta.

## 16. Open decisions
- Exact visual parity requires original Kemistri screenshots/logo assets.
- Curated chemistry database needs expert verification before production.
- Decide whether first release includes radicals or only common stable substances.
- Decide whether daily challenge uses 10 discoveries/20 moves or a fixed target list.
- Decide whether a hint or shuffle should be unlimited after user testing.
- Decide how special lattice structures are represented scientifically.
