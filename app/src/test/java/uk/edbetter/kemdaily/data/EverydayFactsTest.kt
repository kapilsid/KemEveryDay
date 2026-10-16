package uk.edbetter.kemdaily.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EverydayFactsTest {
    @Test fun cardsAreCompleteAndHaveUniqueIds() {
        val cards = EverydayFacts.all
        assertTrue(cards.size >= 12)
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
}
