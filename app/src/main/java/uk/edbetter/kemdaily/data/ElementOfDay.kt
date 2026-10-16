package uk.edbetter.kemdaily.data

import java.time.LocalDate
import java.time.ZoneOffset
import uk.edbetter.kemdaily.core.chemistry.ElementCatalog

data class DailyElement(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val family: String,
    val period: Int,
    /** IUPAC group 1–18; null for the lanthanides and actinides shown in the separate f-block rows. */
    val group: Int?,
    val story: ElementStory,
)

/** Zero-based cell in an 18-column periodic table: rows 0–6 are periods, rows 8–9 the f-block. */
data class TablePosition(val row: Int, val column: Int)

object ElementOfDay {
    val all: List<DailyElement> = ElementCatalog.names.entries.mapIndexed { index, (symbol, name) ->
        val atomicNumber = index + 1
        val family = family(atomicNumber, symbol)
        DailyElement(
            atomicNumber = atomicNumber,
            symbol = symbol,
            name = name,
            family = family,
            period = period(atomicNumber),
            group = group(atomicNumber),
            story = ElementStories.forSymbol(symbol),
        )
    }

    fun today(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): DailyElement =
        all[Math.floorMod(date.toEpochDay(), all.size.toLong()).toInt()]

    fun bySymbol(symbol: String): DailyElement? = all.firstOrNull { it.symbol == symbol }

    /**
     * Ground-state electrons per shell (K, L, M, ...), as drawn in a Bohr model. Filled in Madelung
     * order, with the well-known exceptions where an electron moves between shells.
     */
    fun electronShells(atomicNumber: Int): List<Int> {
        shellExceptions[atomicNumber]?.let { return it }
        val subshells = (1..7).flatMap { n -> (0 until n).map { l -> n to l } }
            .sortedWith(compareBy<Pair<Int, Int>>({ it.first + it.second }, { it.first }))
        val shells = IntArray(7)
        var remaining = atomicNumber
        for ((n, l) in subshells) {
            if (remaining == 0) break
            val added = minOf(remaining, 2 * (2 * l + 1))
            shells[n - 1] += added
            remaining -= added
        }
        return shells.toList().dropLastWhile { it == 0 }
    }

    private val shellExceptions = mapOf(
        24 to listOf(2, 8, 13, 1), 29 to listOf(2, 8, 18, 1),
        41 to listOf(2, 8, 18, 12, 1), 42 to listOf(2, 8, 18, 13, 1), 44 to listOf(2, 8, 18, 15, 1),
        45 to listOf(2, 8, 18, 16, 1), 46 to listOf(2, 8, 18, 18), 47 to listOf(2, 8, 18, 18, 1),
        57 to listOf(2, 8, 18, 18, 9, 2), 58 to listOf(2, 8, 18, 19, 9, 2), 64 to listOf(2, 8, 18, 25, 9, 2),
        78 to listOf(2, 8, 18, 32, 17, 1), 79 to listOf(2, 8, 18, 32, 18, 1),
        89 to listOf(2, 8, 18, 32, 18, 9, 2), 90 to listOf(2, 8, 18, 32, 18, 10, 2),
        91 to listOf(2, 8, 18, 32, 20, 9, 2), 92 to listOf(2, 8, 18, 32, 21, 9, 2),
        93 to listOf(2, 8, 18, 32, 22, 9, 2), 96 to listOf(2, 8, 18, 32, 25, 9, 2),
        103 to listOf(2, 8, 18, 32, 32, 8, 3),
    )

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
