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
    val description: String,
)

/** Zero-based cell in an 18-column periodic table: rows 0–6 are periods, rows 8–9 the f-block. */
data class TablePosition(val row: Int, val column: Int)

object ElementOfDay {
    private val spotlights = mapOf(
        "H" to "Stars fuse hydrogen into heavier elements, while fuel cells can use it to carry energy.",
        "He" to "Its exceptionally low boiling point makes helium valuable for cooling powerful superconducting magnets.",
        "Li" to "Lithium ions move between electrodes in many rechargeable batteries.",
        "C" to "Different arrangements of carbon atoms produce materials as different as graphite and diamond.",
        "N" to "Nitrogen gas dominates Earth’s atmosphere, and fixed nitrogen is essential for proteins and DNA.",
        "O" to "Oxygen supports respiration and many oxidation reactions, including combustion and corrosion.",
        "F" to "Fluorine is extremely reactive, although fluoride compounds can help protect tooth enamel.",
        "Ne" to "An electrical discharge through low-pressure neon produces its familiar orange-red glow.",
        "Na" to "Sodium ions help nerves and muscles work, while sodium metal itself reacts vigorously with water.",
        "Mg" to "Magnesium sits at the centre of chlorophyll and burns with an intense white light.",
        "Al" to "A thin oxide layer protects aluminium, helping the lightweight metal resist further corrosion.",
        "Si" to "Silicon is central to most computer chips and is abundant in minerals and silicate rocks.",
        "P" to "Phosphorus compounds are essential in DNA, cell membranes and the molecules cells use to transfer energy.",
        "S" to "Sulfur occurs in proteins and is used on a vast scale to manufacture sulfuric acid.",
        "Cl" to "Chloride ions are essential in the body, while chlorine compounds are widely used to disinfect water.",
        "Ar" to "Argon’s low reactivity makes it useful as a protective atmosphere in welding and lighting.",
        "K" to "Potassium ions are vital for electrical signalling in nerves and are an important plant nutrient.",
        "Ca" to "Calcium compounds strengthen bones and shells and also help muscles and cells communicate.",
        "Fe" to "Iron carries oxygen in haemoglobin and is the main element in steels.",
        "Cu" to "Copper conducts electricity well and develops a protective green patina outdoors.",
        "Ag" to "Silver is an excellent electrical conductor and its compounds are light-sensitive and antimicrobial.",
        "Au" to "Gold’s low reactivity and easy shaping make it useful in jewellery and reliable electrical contacts.",
        "Hg" to "Mercury is liquid near room temperature, but it and many of its compounds are toxic.",
        "Pb" to "Lead is dense and useful for radiation shielding, but exposure can damage the nervous system.",
        "U" to "Uranium nuclei can release energy through fission, and all uranium isotopes are radioactive.",
    )

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
            description = "$name has atomic number $atomicNumber, so each neutral atom contains $atomicNumber protons and $atomicNumber electrons. " +
                (spotlights[symbol] ?: "It is conventionally placed among the ${family.lowercase()} in the periodic table."),
        )
    }

    fun today(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): DailyElement =
        all[Math.floorMod(date.toEpochDay(), all.size.toLong()).toInt()]

    fun bySymbol(symbol: String): DailyElement? = all.firstOrNull { it.symbol == symbol }

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
