package uk.edbetter.kemdaily.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EverydayFactsTest {
    @Test fun cardsAreCompleteAndHaveUniqueIds() {
        val cards = EverydayFacts.all
        assertEquals(62, cards.size)
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
        assertTrue(cards.all { it.guesses.size == 3 })
        assertTrue(cards.all { it.correctGuess in it.guesses.indices })
        assertTrue(cards.all { it.question.endsWith('?') })
        assertTrue(cards.all { it.explanation.isNotBlank() && it.equation.isNotBlank() })
    }

    @Test fun dailySelectionIsDeterministic() {
        val date = LocalDate.of(2026, 10, 16)
        assertEquals(EverydayFacts.today(date), EverydayFacts.today(date))
    }

    @Test fun twoMonthsWithoutRepeats() {
        val start = LocalDate.of(2026, 10, 16)
        val ids = (0L until 60L).map { EverydayFacts.today(start.plusDays(it)).id }
        assertEquals(60, ids.toSet().size)
    }
}
