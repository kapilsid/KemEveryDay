package uk.edbetter.kemdaily.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScienceMysteriesTest {
    @Test fun catalogueCoversEveryCategoryWithCompleteClueSets() {
        assertEquals(MysteryCategory.entries.toSet(), ScienceMysteries.all.map { it.category }.toSet())
        assertEquals(180, ScienceMysteries.all.size)
        MysteryCategory.entries.forEach { category ->
            assertEquals(36, ScienceMysteries.all.count { it.category == category })
        }
        assertTrue(ScienceMysteries.all.all { it.clues.size == 5 })
        assertTrue(ScienceMysteries.all.all { it.normalizedAnswer.isNotBlank() })
        assertEquals(ScienceMysteries.all.size, ScienceMysteries.all.map { it.id }.toSet().size)
        assertEquals(ScienceMysteries.all.size, ScienceMysteries.all.map { it.normalizedAnswer }.toSet().size)
    }

    @Test fun sixtyDaysOfThreeWithoutRepeats() {
        val start = java.time.LocalDate.of(2026, 10, 16)
        val seen = (0L until 60L).flatMap { ScienceMysteries.forDay(start.plusDays(it)) }.map { it.id }
        assertEquals(180, seen.size)
        assertEquals(180, seen.toSet().size)
    }

    @Test fun eachDayMixesCategories() {
        val start = java.time.LocalDate.of(2026, 10, 16)
        (0L until 60L).forEach { day ->
            assertEquals(3, ScienceMysteries.forDay(start.plusDays(day)).map { it.category }.toSet().size)
        }
    }
}
