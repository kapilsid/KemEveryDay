package uk.edbetter.kemdaily.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScienceMysteriesTest {
    @Test fun catalogueCoversEveryCategoryWithCompleteClueSets() {
        assertEquals(MysteryCategory.entries.toSet(), ScienceMysteries.all.map { it.category }.toSet())
        assertEquals(25, ScienceMysteries.all.size)
        MysteryCategory.entries.forEach { category ->
            assertEquals(5, ScienceMysteries.all.count { it.category == category })
        }
        assertTrue(ScienceMysteries.all.all { it.clues.size == 5 })
        assertTrue(ScienceMysteries.all.all { it.normalizedAnswer.isNotBlank() })
        assertEquals(ScienceMysteries.all.size, ScienceMysteries.all.map { it.id }.toSet().size)
    }
}
