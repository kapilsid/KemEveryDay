package uk.edbetter.kemdaily.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChampionLimitsTest {
    private fun entry(molecule: Int, unique: Int, science: Int, solved: Int) =
        ScienceScoreSubmission(LocalDate.of(2026, 10, 16), molecule, science, unique, solved, contentVersion = 1)

    @Test fun totalIsBothGamesAdded() {
        assertEquals(1150, entry(molecule = 750, unique = 10, science = 400, solved = 5).score)
    }

    @Test fun realisticScoresAreAccepted() {
        assertTrue(ChampionLimits.isPlausible(entry(molecule = 750, unique = 10, science = 400, solved = 5)))
        assertTrue(ChampionLimits.isPlausible(entry(molecule = 0, unique = 0, science = 0, solved = 0)))
        assertTrue(ChampionLimits.isPlausible(entry(molecule = 20 * 130, unique = 20, science = 100, solved = 1)))
    }

    @Test fun impossibleScoresAreRejected() {
        assertFalse(ChampionLimits.isPlausible(entry(molecule = 20 * 130 + 1, unique = 20, science = 0, solved = 0)))
        assertFalse(ChampionLimits.isPlausible(entry(molecule = 500, unique = 0, science = 0, solved = 0)))
        assertFalse(ChampionLimits.isPlausible(entry(molecule = 100, unique = 21, science = 0, solved = 0)))
        assertFalse(ChampionLimits.isPlausible(entry(molecule = 100, unique = 2, science = 301, solved = 3)))
        assertFalse(ChampionLimits.isPlausible(entry(molecule = -1, unique = 2, science = 0, solved = 0)))
    }
}
