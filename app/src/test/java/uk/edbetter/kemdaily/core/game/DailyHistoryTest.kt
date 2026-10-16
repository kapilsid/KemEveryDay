package uk.edbetter.kemdaily.core.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class DailyHistoryTest {
    private val today = LocalDate.of(2026, 10, 16)

    private fun record(date: LocalDate, score: Int = 100, unique: Int = 5, mission: Boolean = false) =
        DailyRecord(date, score, unique, mission, completedAt = 1L)

    private fun days(vararg offsets: Long) = offsets.map { today.minusDays(it) }.toSet()

    @Test
    fun streakIncludesTodayWhenPlayed() {
        val summary = DailyHistory.streaks(days(0, 1, 2), today)
        assertEquals(3, summary.current)
        assertTrue(summary.playedToday)
    }

    @Test
    fun unplayedTodayKeepsYesterdaysStreakAlive() {
        val summary = DailyHistory.streaks(days(1, 2, 3, 4), today)
        assertEquals(4, summary.current)
        assertFalse(summary.playedToday)
    }

    @Test
    fun missingYesterdayBreaksStreak() {
        assertEquals(0, DailyHistory.streaks(days(2, 3), today).current)
        assertEquals(1, DailyHistory.streaks(days(0, 2, 3), today).current)
    }

    @Test
    fun longestStreakSpansMonthAndYearBoundaries() {
        val played = (0L..5L).map { LocalDate.of(2025, 12, 29).plusDays(it) }.toSet() + days(0)
        val summary = DailyHistory.streaks(played, today)
        assertEquals(6, summary.longest)
        assertEquals(1, summary.current)
        assertEquals(7, summary.daysPlayed)
    }

    @Test
    fun futureDatesFromClockChangesAreIgnored() {
        val summary = DailyHistory.streaks(days(0) + today.plusDays(1), today)
        assertEquals(1, summary.current)
        assertEquals(1, summary.longest)
        assertEquals(1, summary.daysPlayed)
    }

    @Test
    fun emptyHistoryHasNoStreak() {
        assertEquals(StreakSummary(0, 0, 0, false), DailyHistory.streaks(emptySet(), today))
    }

    @Test
    fun mergeKeepsBestResultPerDate() {
        val first = record(today, score = 120, unique = 6)
        var history = DailyHistory.merge(emptyMap(), first)
        history = DailyHistory.merge(history, record(today, score = 90))
        assertEquals(first, history[today])
        history = DailyHistory.merge(history, record(today, score = 120, unique = 7))
        assertEquals(7, history[today]?.uniqueDiscoveries)
        history = DailyHistory.merge(history, record(today, score = 120, unique = 7, mission = true))
        assertTrue(history.getValue(today).missionAchieved)
        assertEquals(1, history.size)
    }

    @Test
    fun encodeDecodeRoundTripsAndSkipsCorruptRows() {
        val history = listOf(record(today, 140, 10, true), record(today.minusDays(1), 80, 4))
            .fold(emptyMap<LocalDate, DailyRecord>(), DailyHistory::merge)
        val encoded = DailyHistory.encode(history)
        assertEquals(history, DailyHistory.decode(encoded))
        val corrupted = encoded + "\nnot-a-date,1,2,true,3\n2026-10-01,1,2\n\n"
        assertEquals(history, DailyHistory.decode(corrupted))
        assertTrue(DailyHistory.decode(null).isEmpty())
    }

    @Test
    fun monthGridStartsOnMondayAndPadsWholeWeeks() {
        // 1 October 2026 is a Thursday.
        val grid = DailyHistory.monthGrid(YearMonth.of(2026, 10), mapOf(today to record(today)), today)
        assertEquals(0, grid.size % 7)
        assertEquals(35, grid.size)
        assertTrue(grid.take(3).all { it.date == null })
        assertEquals(LocalDate.of(2026, 10, 1), grid[3].date)
        assertEquals(31, grid.count { it.date != null })
        val todayCell = grid.single { it.isToday }
        assertEquals(today, todayCell.date)
        assertEquals(100, todayCell.record?.score)
        assertNull(grid.first { it.date == LocalDate.of(2026, 10, 15) }.record)
    }

    @Test
    fun monthStartingOnMondayHasNoLeadingPadding() {
        // 1 June 2026 is a Monday.
        val grid = DailyHistory.monthGrid(YearMonth.of(2026, 6), emptyMap(), today)
        assertEquals(LocalDate.of(2026, 6, 1), grid.first().date)
        assertEquals(35, grid.size)
    }
}
