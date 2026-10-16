package uk.edbetter.kemdaily.core.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt
import uk.edbetter.kemdaily.core.chemistry.SubstanceCatalog

class GameEngineTest {
    @Test fun adjacencyAndSelectedTileToggleRules() {
        val engine = engineWithTestBoard()
        engine.select(Cell(0, 0))
        engine.select(Cell(0, 1))
        engine.select(Cell(0, 1))
        assertEquals(1, engine.state.selection.size)

        engine.select(Cell(0, 1))
        engine.select(Cell(0, 0))
        assertTrue(engine.state.selection.isEmpty())

        engine.select(Cell(0, 0))
        engine.select(Cell(4, 4))
        assertEquals(1, engine.state.selection.size)
        assertEquals("Choose a neighbouring tile", engine.state.message)
    }

    @Test fun recognizedDiscoveryConsumesMoveAndUnknownDoesNot() {
        val recognized = engineWithTestBoard()
        recognized.select(Cell(0, 0)) // H
        recognized.select(Cell(0, 1)) // O
        recognized.select(Cell(0, 2)) // Na
        assertTrue(recognized.submit() is SubmitResult.Accepted)
        assertEquals(19, recognized.state.movesRemaining)
        assertTrue("sodium_hydroxide" in recognized.state.uniqueDiscoveries)

        val unknown = engineWithTestBoard()
        unknown.select(Cell(0, 1)) // O
        unknown.select(Cell(0, 2)) // Na
        assertTrue(unknown.submit() is SubmitResult.PendingReview)
        assertEquals(20, unknown.state.movesRemaining)
        assertEquals(0, unknown.state.score)
    }

    @Test fun repeatedDiscoveryGetsQuarterScoreAndNoUniqueIncrement() {
        val substance = SubstanceCatalog.all.first { it.id == "sodium_hydroxide" }
        val first = GameEngine.score(substance, first = true, previousStreak = 0)
        val repeat = GameEngine.score(substance, first = false, previousStreak = 5)
        assertEquals(0, repeat.streak)
        assertEquals((first.awarded * .25f).roundToInt(), repeat.awarded)

        val engine = engineWithTestBoard(setOf(substance.id))
        engine.select(Cell(0, 0)); engine.select(Cell(0, 1)); engine.select(Cell(0, 2))
        val accepted = engine.submit() as SubmitResult.Accepted
        assertFalse(accepted.discovery.isFirst)
        assertEquals(1, engine.state.uniqueDiscoveries.size)
        assertEquals(19, engine.state.movesRemaining)
    }

    @Test fun gravityConservesSurvivorsFillsBoardAndRespectsCaps() {
        val board = testBoard()
        val removed = listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1))
        val result = GameEngine.applyGravity(board, removed, BoardGenerator(DeterministicRandom(7)))
        val removedIds = removed.map { board[it.index].id }.toSet()
        assertEquals(25, result.size)
        assertTrue(board.filterNot { it.id in removedIds }.all { survivor -> result.any { it.id == survivor.id } })
        val counts = result.groupingBy { it.symbol }.eachCount()
        assertTrue(counts.all { (symbol, count) -> count <= AtomConfig.caps.getValue(symbol) })
    }

    @Test fun deterministicBoardsAreStableAndPlayableAcrossSeeds() {
        repeat(1_000) { seed ->
            val first = BoardGenerator(DeterministicRandom(seed.toLong())).create()
            val second = BoardGenerator(DeterministicRandom(seed.toLong())).create()
            assertEquals(first.map { it.symbol }, second.map { it.symbol })
            assertNotNull("dead board at seed $seed", PathFinder.firstKnownPath(first))
            val counts = first.groupingBy { it.symbol }.eachCount()
            assertTrue(counts.all { (symbol, count) -> count <= AtomConfig.caps.getValue(symbol) })
        }
    }

    @Test fun dailySeedIsStable() {
        assertEquals(DailySeed.forDate("2026-10-16"), DailySeed.forDate("2026-10-16"))
        assertTrue(DailySeed.forDate("2026-10-16") != DailySeed.forDate("2026-10-17"))
    }

    private fun engineWithTestBoard(unique: Set<String> = emptySet()): GameEngine {
        val engine = GameEngine(99)
        engine.restore(GameState(GameMode.STANDARD, 99, testBoard(), uniqueDiscoveries = unique))
        return engine
    }

    private fun testBoard(): List<AtomTile> {
        val symbols = listOf("H", "O", "Na") + List(7) { "H" } + List(6) { "O" } +
            List(5) { "C" } + List(3) { "N" } + listOf("Cl")
        assertEquals(25, symbols.size)
        return symbols.mapIndexed { index, symbol -> AtomTile(index.toLong() + 1, symbol) }
    }
}
