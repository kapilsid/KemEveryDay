package uk.edbetter.kemdaily.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElementOfDayTest {
    @Test fun coversTheNamedPeriodicTableInAtomicNumberOrder() {
        assertEquals(118, ElementOfDay.all.size)
        assertEquals("H", ElementOfDay.all.first().symbol)
        assertEquals("Og", ElementOfDay.all.last().symbol)
        assertEquals((1..118).toList(), ElementOfDay.all.map { it.atomicNumber })
    }

    @Test fun dailySelectionIsDeterministic() {
        val date = LocalDate.of(2026, 10, 16)
        assertEquals(ElementOfDay.today(date), ElementOfDay.today(date))
    }

    @Test fun groupsMatchThePeriodicTable() {
        val groups = mapOf("H" to 1, "He" to 18, "Be" to 2, "B" to 13, "Ne" to 18, "Na" to 1, "Cl" to 17,
            "K" to 1, "Fe" to 8, "Zn" to 12, "Kr" to 18, "Ag" to 11, "Hf" to 4, "Au" to 11, "Rn" to 18, "Ra" to 2, "Og" to 18)
        groups.forEach { (symbol, group) -> assertEquals(symbol, group, ElementOfDay.bySymbol(symbol)?.group) }
        assertEquals(null, ElementOfDay.bySymbol("La")?.group)
        assertEquals(null, ElementOfDay.bySymbol("Lr")?.group)
    }

    @Test fun everyElementHasItsOwnTableCell() {
        val positions = ElementOfDay.all.map(ElementOfDay::position)
        assertEquals(118, positions.toSet().size)
        assertTrue(positions.all { it.column in 0..17 && it.row in 0..9 && it.row != 7 })
        assertEquals(TablePosition(2, 16), ElementOfDay.position(ElementOfDay.bySymbol("Cl")!!))
        assertEquals(TablePosition(8, 2), ElementOfDay.position(ElementOfDay.bySymbol("La")!!))
        assertEquals(TablePosition(9, 16), ElementOfDay.position(ElementOfDay.bySymbol("Lr")!!))
    }

    @Test fun everyElementHasAStoryWithoutRepeatingTheBasics() {
        assertEquals(118, ElementStories.bySymbol.size)
        ElementOfDay.all.forEach { element ->
            val story = element.story
            listOf(story.discovery, story.uses, story.hook).forEach { text ->
                assertTrue(element.symbol, text.isNotBlank() && text.trim().endsWith("."))
                assertTrue(element.symbol, !text.contains("atomic number", ignoreCase = true))
            }
        }
    }

    @Test fun electronShellsMatchStandardConfigurations() {
        assertEquals(listOf(1), ElementOfDay.electronShells(1))
        assertEquals(listOf(2, 8, 1), ElementOfDay.electronShells(11))
        assertEquals(listOf(2, 8, 8, 1), ElementOfDay.electronShells(19))
        assertEquals(listOf(2, 8, 14, 2), ElementOfDay.electronShells(26))
        assertEquals(listOf(2, 8, 18, 1), ElementOfDay.electronShells(29))
        assertEquals(listOf(2, 8, 18, 32, 18, 1), ElementOfDay.electronShells(79))
        assertEquals(listOf(2, 8, 18, 32, 32, 18, 8), ElementOfDay.electronShells(118))
        (1..118).forEach { z -> assertEquals(z, ElementOfDay.electronShells(z).sum()) }
    }
}
