package uk.edbetter.kemdaily.core.chemistry

data class Composition(val atoms: Map<String, Int>) {
    init {
        require(atoms.isNotEmpty()) { "A composition needs at least one atom" }
        require(atoms.all { (symbol, count) -> symbol.matches(Regex("[A-Z][a-z]?")) && count > 0 })
    }

    val atomCount: Int get() = atoms.values.sum()
    val elementCount: Int get() = atoms.size

    /** Stable lookup key. This deliberately represents composition, not bonding. */
    val key: String
        get() = atoms.toSortedMap().entries.joinToString("|") { (symbol, count) -> "$symbol:$count" }

    val formula: String
        get() {
            val order = if ("C" in atoms) {
                listOf("C", "H") + atoms.keys.filterNot { it == "C" || it == "H" }.sorted()
            } else atoms.keys.sorted()
            return order.filter { it in atoms }.joinToString("") { symbol ->
                symbol + atoms.getValue(symbol).takeIf { it > 1 }.orEmpty()
            }
        }

    companion object {
        fun fromSymbols(symbols: Iterable<String>): Composition =
            Composition(symbols.groupingBy { it }.eachCount())
    }
}

private fun Int?.orEmpty(): String = this?.toString().orEmpty()

enum class SubstanceCategory(val label: String) {
    ELEMENTAL("Elemental substance"),
    MOLECULE("Molecule"),
    ORGANIC("Organic compound"),
    IONIC("Ionic compound"),
    OXIDE("Oxide"),
    SALT("Salt"),
    BASE("Base"),
    ACID("Acid"),
    AMBIGUOUS("Multiple possible substances"),
    NETWORK_SOLID("Extended structure")
}

data class Substance(
    val id: String,
    val displayName: String,
    val formula: String,
    val composition: Composition,
    val category: SubstanceCategory,
    val fact: String,
    val difficultyTier: Int = 1,
    val aliases: List<String> = emptyList(),
    val isAmbiguous: Boolean = false,
) {
    /** Whether the current Molecule Hunt board can physically construct this composition. */
    val isPlayable: Boolean
        get() = composition.atomCount in 2..7 && composition.atoms.keys.all { it in PLAYABLE_ELEMENT_SYMBOLS }
}

val PLAYABLE_ELEMENT_SYMBOLS = setOf("H", "O", "C", "N", "Na", "Cl", "K", "S", "Ca", "Mg", "Li", "F")

sealed interface Recognition {
    data class Recognized(val substance: Substance) : Recognition
    data class Ambiguous(val composition: Composition, val substances: List<Substance>) : Recognition
    data class NotYetRecognized(val composition: Composition) : Recognition
}
