package uk.edbetter.kemdaily.data

import java.time.LocalDate
import java.time.ZoneOffset
import uk.edbetter.kemdaily.core.chemistry.ElementCatalog
import uk.edbetter.kemdaily.core.chemistry.Substance
import uk.edbetter.kemdaily.core.chemistry.SubstanceCatalog
import uk.edbetter.kemdaily.core.chemistry.SubstanceCategory
import uk.edbetter.kemdaily.core.game.DeterministicRandom
import uk.edbetter.kemdaily.core.game.shuffled

data class DailyElement(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val family: String,
    val period: Int,
    /** IUPAC group 1–18; null for the lanthanides and actinides shown in the separate f-block rows. */
    val group: Int?,
    /** Standard atomic weight; values in brackets are the mass number of the longest-lived isotope. */
    val atomicWeight: String,
    /** Ground-state configuration in noble-gas shorthand, e.g. [Rn]5f4 6d1 7s2 (digits are superscripts). */
    val configuration: List<Pair<String, Int>>,
    val core: String?,
    val state: String,
    val story: ElementStory,
)

/** Zero-based cell in an 18-column periodic table: rows 0–6 are periods, rows 8–9 the f-block. */
data class TablePosition(val row: Int, val column: Int)

object ElementOfDay {
    val all: List<DailyElement> by lazy { buildElements() }

    private fun buildElements(): List<DailyElement> = ElementCatalog.names.entries.mapIndexed { index, (symbol, name) ->
        val atomicNumber = index + 1
        val family = family(atomicNumber, symbol)
        DailyElement(
            atomicNumber = atomicNumber,
            symbol = symbol,
            name = name,
            family = family,
            period = period(atomicNumber),
            group = group(atomicNumber),
            atomicWeight = atomicWeights[index],
            configuration = valenceConfiguration(atomicNumber),
            core = nobleCore(atomicNumber)?.let { "[${ElementCatalog.names.keys.elementAt(it - 1)}]" },
            state = state(atomicNumber, symbol),
            story = ElementStories.forSymbol(symbol),
        )
    }

    fun today(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): DailyElement =
        all[Math.floorMod(date.toEpochDay(), all.size.toLong()).toInt()]

    fun bySymbol(symbol: String): DailyElement? = all.firstOrNull { it.symbol == symbol }

    /** Four choices for today's guess: the answer plus three from the same family where possible, in a daily-shuffled order. */
    fun guessOptions(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): List<DailyElement> {
        val answer = today(date)
        val random = DeterministicRandom(date.toEpochDay() * 31 + answer.atomicNumber)
        val sameFamily = all.filter { it.family == answer.family && it != answer }
        val nearby = all.filter { it != answer && it !in sameFamily }
            .sortedBy { kotlin.math.abs(it.atomicNumber - answer.atomicNumber) }
            .take(12)
        val pool = (sameFamily.shuffled(random) + nearby.shuffled(random)).distinct()
        return (pool.take(3) + answer).shuffled(random)
    }

    /** The discovery story with the element's own name and symbol blanked out, used as the clue before the guess. The memorable hook is kept for the reveal. */
    fun maskedClue(element: DailyElement): String {
        // The name anywhere in a word (ferrocerium), the symbol only as an exact standalone word (Fe).
        return element.story.discovery
            .replace(Regex("\\w*${Regex.escape(element.name)}\\w*", RegexOption.IGNORE_CASE), "_____")
            .replace(Regex("\\b${Regex.escape(element.symbol)}\\b"), "_____")
    }

    /** Up to three familiar compounds containing the element, simplest first. Pure elements are left out. */
    fun compounds(element: DailyElement, limit: Int = 3): List<Substance> = SubstanceCatalog.all
        .filter { element.symbol in it.composition.atoms && it.category != SubstanceCategory.ELEMENTAL && !it.isAmbiguous }
        .distinctBy { it.formula }
        .sortedWith(compareBy<Substance>({ it.difficultyTier }, { it.composition.atomCount }))
        .take(limit)

    /**
     * Ground-state electrons per shell (K, L, M, ...), as drawn in a Bohr model. Filled in Madelung
     * order, with the well-known exceptions where an electron moves between shells.
     */
    fun electronShells(atomicNumber: Int): List<Int> {
        val shells = IntArray(7)
        subshells(atomicNumber).forEach { (n, _, count) -> shells[n - 1] += count }
        return shells.toList().dropLastWhile { it == 0 }
    }

    /** Every occupied subshell as (n, l, electrons), using Madelung filling plus the known exceptions. */
    fun subshells(atomicNumber: Int): List<Triple<Int, Int, Int>> {
        val filled = mutableListOf<Triple<Int, Int, Int>>()
        var remaining = atomicNumber
        for ((n, l) in madelungOrder) {
            if (remaining == 0) break
            val added = minOf(remaining, 2 * (2 * l + 1))
            filled += Triple(n, l, added)
            remaining -= added
        }
        configurationExceptions[atomicNumber]?.let { changes ->
            changes.forEach { (n, l, count) ->
                filled.removeAll { it.first == n && it.second == l }
                if (count > 0) filled += Triple(n, l, count)
            }
        }
        return filled.sortedWith(compareBy({ it.first }, { it.second }))
    }

    private fun nobleCore(atomicNumber: Int): Int? = listOf(86, 54, 36, 18, 10, 2).firstOrNull { it < atomicNumber }

    private fun valenceConfiguration(atomicNumber: Int): List<Pair<String, Int>> {
        val core = nobleCore(atomicNumber)?.let(::subshells).orEmpty().map { it.first to it.second }.toSet()
        return subshells(atomicNumber)
            .filter { (n, l, count) -> (n to l) !in core || count != 2 * (2 * l + 1) }
            .map { (n, l, count) -> "$n${"spdf"[l]}" to count }
    }

    private fun state(atomicNumber: Int, symbol: String): String = when {
        symbol in setOf("H", "He", "N", "O", "F", "Ne", "Cl", "Ar", "Kr", "Xe", "Rn") -> "Gas"
        symbol == "Br" || symbol == "Hg" -> "Liquid"
        atomicNumber >= 100 -> "State unknown"
        else -> "Solid"
    }

    private val atomicWeights = listOf(
        "1.008", "4.0026", "6.94", "9.0122", "10.81", "12.011", "14.007", "15.999", "18.998", "20.180",
        "22.990", "24.305", "26.982", "28.085", "30.974", "32.06", "35.45", "39.95", "39.098", "40.078",
        "44.956", "47.867", "50.942", "51.996", "54.938", "55.845", "58.933", "58.693", "63.546", "65.38",
        "69.723", "72.630", "74.922", "78.971", "79.904", "83.798", "85.468", "87.62", "88.906", "91.224",
        "92.906", "95.95", "(98)", "101.07", "102.91", "106.42", "107.87", "112.41", "114.82", "118.71",
        "121.76", "127.60", "126.90", "131.29", "132.91", "137.33", "138.91", "140.12", "140.91", "144.24",
        "(145)", "150.36", "151.96", "157.25", "158.93", "162.50", "164.93", "167.26", "168.93", "173.05",
        "174.97", "178.49", "180.95", "183.84", "186.21", "190.23", "192.22", "195.08", "196.97", "200.59",
        "204.38", "207.2", "208.98", "(209)", "(210)", "(222)", "(223)", "(226)", "(227)", "232.04",
        "231.04", "238.03", "(237)", "(244)", "(243)", "(247)", "(247)", "(251)", "(252)", "(257)",
        "(258)", "(259)", "(266)", "(267)", "(268)", "(269)", "(270)", "(269)", "(278)", "(281)",
        "(282)", "(285)", "(286)", "(289)", "(290)", "(293)", "(294)", "(294)",
    )

    // (n, l, electrons) overrides; 0 removes a subshell that Madelung filling would have used.
    private val configurationExceptions: Map<Int, List<Triple<Int, Int, Int>>> = mapOf(
        24 to listOf(Triple(3, 2, 5), Triple(4, 0, 1)),
        29 to listOf(Triple(3, 2, 10), Triple(4, 0, 1)),
        41 to listOf(Triple(4, 2, 4), Triple(5, 0, 1)),
        42 to listOf(Triple(4, 2, 5), Triple(5, 0, 1)),
        44 to listOf(Triple(4, 2, 7), Triple(5, 0, 1)),
        45 to listOf(Triple(4, 2, 8), Triple(5, 0, 1)),
        46 to listOf(Triple(4, 2, 10), Triple(5, 0, 0)),
        47 to listOf(Triple(4, 2, 10), Triple(5, 0, 1)),
        57 to listOf(Triple(4, 3, 0), Triple(5, 2, 1)),
        58 to listOf(Triple(4, 3, 1), Triple(5, 2, 1)),
        64 to listOf(Triple(4, 3, 7), Triple(5, 2, 1)),
        78 to listOf(Triple(5, 2, 9), Triple(6, 0, 1)),
        79 to listOf(Triple(5, 2, 10), Triple(6, 0, 1)),
        89 to listOf(Triple(5, 3, 0), Triple(6, 2, 1)),
        90 to listOf(Triple(5, 3, 0), Triple(6, 2, 2)),
        91 to listOf(Triple(5, 3, 2), Triple(6, 2, 1)),
        92 to listOf(Triple(5, 3, 3), Triple(6, 2, 1)),
        93 to listOf(Triple(5, 3, 4), Triple(6, 2, 1)),
        96 to listOf(Triple(5, 3, 7), Triple(6, 2, 1)),
        103 to listOf(Triple(6, 2, 0), Triple(7, 1, 1)),
    )

    /** The last subshell filled in Madelung order, e.g. "2p" for oxygen; this shapes the outer electron cloud. */
    fun outerSubshell(atomicNumber: Int): Pair<Int, Int> {
        var remaining = atomicNumber
        for ((n, l) in madelungOrder) {
            remaining -= 2 * (2 * l + 1)
            if (remaining <= 0) return n to l
        }
        return madelungOrder.last()
    }

    private val madelungOrder = (1..7).flatMap { n -> (0 until minOf(n, 4)).map { l -> n to l } }
        .sortedWith(compareBy<Pair<Int, Int>>({ it.first + it.second }, { it.first }))


    fun position(element: DailyElement): TablePosition = when (val group = element.group) {
        null -> TablePosition(
            row = if (element.atomicNumber in 57..71) 8 else 9,
            column = 2 + element.atomicNumber - if (element.atomicNumber in 57..71) 57 else 89,
        )
        else -> TablePosition(element.period - 1, group - 1)
    }

    private fun group(number: Int): Int? = when (number) {
        1 -> 1
        2 -> 18
        in 3..4 -> number - 2
        in 5..10 -> number + 8
        in 11..12 -> number - 10
        in 13..18 -> number
        in 19..36 -> number - 18
        in 37..54 -> number - 36
        in 55..56 -> number - 54
        in 57..71 -> null
        in 72..86 -> number - 68
        in 87..88 -> number - 86
        in 89..103 -> null
        else -> number - 100
    }

    private fun period(number: Int): Int = when (number) {
        in 1..2 -> 1
        in 3..10 -> 2
        in 11..18 -> 3
        in 19..36 -> 4
        in 37..54 -> 5
        in 55..86 -> 6
        else -> 7
    }

    private fun family(number: Int, symbol: String): String = when {
        symbol in setOf("He", "Ne", "Ar", "Kr", "Xe", "Rn", "Og") -> "Noble gases"
        symbol in setOf("F", "Cl", "Br", "I", "At", "Ts") -> "Halogens"
        symbol in setOf("Li", "Na", "K", "Rb", "Cs", "Fr") -> "Alkali metals"
        symbol in setOf("Be", "Mg", "Ca", "Sr", "Ba", "Ra") -> "Alkaline-earth metals"
        number in 57..71 -> "Lanthanides"
        number in 89..103 -> "Actinides"
        number in 21..30 || number in 39..48 || number in 72..80 || number in 104..112 -> "Transition metals"
        symbol in setOf("B", "Si", "Ge", "As", "Sb", "Te") -> "Metalloids"
        symbol in setOf("H", "C", "N", "O", "P", "S", "Se") -> "Reactive nonmetals"
        else -> "Post-transition metals"
    }
}
