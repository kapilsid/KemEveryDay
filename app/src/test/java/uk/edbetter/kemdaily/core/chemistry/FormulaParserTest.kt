package uk.edbetter.kemdaily.core.chemistry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormulaParserTest {
    @Test fun parsesFlatAndNestedFormulae() {
        assertEquals(mapOf("C" to 1, "H" to 4), FormulaParser.parse("CH4").atoms)
        assertEquals(mapOf("Ca" to 1, "O" to 2, "H" to 2), FormulaParser.parse("Ca(OH)2").atoms)
        assertEquals(mapOf("Ca" to 3, "S" to 2, "O" to 8), FormulaParser.parse("Ca3(S(O)4)2").atoms)
    }

    @Test fun normalizesSelectionOrder() {
        assertEquals(
            Composition.fromSymbols(listOf("C", "H", "H", "H", "H")).key,
            Composition.fromSymbols(listOf("H", "C", "H", "H", "H")).key,
        )
    }

    @Test fun recognizesRequiredAcceptanceExamples() {
        val examples = listOf(
            listOf("C", "H", "H", "H", "H") to "methane",
            listOf("Na", "O", "H") to "sodium_hydroxide",
            listOf("Li", "Li", "O") to "lithium_oxide",
            listOf("N", "N") to "nitrogen",
            listOf("C", "O") to "carbon_monoxide",
        )
        examples.forEach { (symbols, expectedId) ->
            val result = SubstanceCatalog.recognize(symbols) as Recognition.Recognized
            assertEquals(expectedId, result.substance.id)
        }
    }

    @Test fun marksIsomerCompositionAmbiguousAndDoesNotCallC4Diamond() {
        assertTrue(SubstanceCatalog.recognize(listOf("C", "C") + List(6) { "H" } + "O") is Recognition.Ambiguous)
        assertTrue(SubstanceCatalog.recognize(List(4) { "C" }) is Recognition.NotYetRecognized)
    }

    @Test fun expandedLibraryIsLargeUniqueAndInternallyValid() {
        val substances = SubstanceCatalog.all
        assertTrue("Expected a substantial offline library", substances.size >= 125)
        assertEquals(substances.size, substances.map { it.id }.toSet().size)
        assertTrue(substances.all { it.id.matches(Regex("[a-z0-9_]+")) })
        assertTrue(substances.all { FormulaParser.parse(it.formula) == it.composition })
        assertTrue(substances.all { it.fact.sentenceCount() in 2..3 })
    }

    @Test fun playableEntriesOnlyUseBoardElementsAndReferenceParserSupportsPeriodicTable() {
        assertEquals(118, ElementCatalog.symbols.size)
        assertTrue(SubstanceCatalog.all.count { it.isPlayable } >= 35)
        assertTrue(
            SubstanceCatalog.all.filter { it.isPlayable }.all {
                it.composition.atomCount in 2..7 &&
                    it.composition.atoms.keys.all(PLAYABLE_ELEMENT_SYMBOLS::contains)
            },
        )
    }

    private fun String.sentenceCount(): Int = Regex("[.!?](?:\\s|$)").findAll(this).count()
}
