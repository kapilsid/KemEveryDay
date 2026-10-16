# Chemistry Library

Kemistri Everyday ships with a versioned, offline chemistry library. Version 2 contains 127 curated substances, and the formula parser recognises all 118 currently named element symbols.

## Library and gameplay scopes

The library deliberately separates reference coverage from the current Molecule Hunt board:

- **Playable** entries contain 2–7 atoms drawn only from H, O, C, N, Na, Cl, K, S, Ca, Mg, Li and F.
- **Reference** entries remain browsable, but require an element or atom count not currently available on the board.
- Board generation and hint finding use playable entries only, so adding reference material cannot create an impossible target.

Every entry has a stable ID, display formula, category, aliases where useful, and a concise two- or three-sentence educational note. This is an internal recognition/reference catalogue rather than a persistent player collection. The UI shows substances discovered in the current hunt only, and a new game begins with an empty discovery list.

## Formula and structure

A molecular formula specifies composition, not a unique arrangement of atoms. The catalogue therefore keeps distinct substances with the same composition as separate records and returns an ambiguous recognition result instead of guessing. Examples include ethanol and dimethyl ether (C2H6O), glucose and fructose (C6H12O6), and propene and cyclopropane (C3H6).

Extended solids are also labelled separately. For example, silicon dioxide and ionic salts are not described as collections of small, isolated molecules merely because a compact formula is used.

## Validation and provenance

Automated tests require:

- at least 125 records and no duplicate stable IDs;
- a parseable formula whose parsed composition matches the stored composition;
- two or three complete sentences for every educational note;
- valid playable-element and selection-length constraints; and
- all 118 element symbols in the parser catalogue.

Content and terminology were prepared against authoritative reference conventions, including the [IUPAC Gold Book definition of molecular formula](https://goldbook.iupac.org/terms/view/M03987/pdf), [PubChem](https://pubchem.ncbi.nlm.nih.gov/) and the [NIST Chemistry WebBook](https://webbook.nist.gov/chemistry/). The notes are short educational summaries, not handling, medical or emergency advice. A qualified chemistry editor should review every record before a public store release, and future revisions should add per-entry source identifiers.

## Adding an entry

Add records to `ExpandedSubstanceCatalog.kt` with a unique snake-case ID. Keep the displayed formula compatible with the parser (element symbols, positive integer subscripts and parentheses), write two or three complete sentences, and add aliases rather than duplicate records unless the compounds genuinely have different structures. Increase `SubstanceCatalog.VERSION` whenever a release changes catalogue meaning or identity.
