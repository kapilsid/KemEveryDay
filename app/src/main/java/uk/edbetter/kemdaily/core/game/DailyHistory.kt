package uk.edbetter.kemdaily.core.game

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/** The best Daily Board result for one UTC board date. */
data class DailyRecord(
    val date: LocalDate,
    val score: Int,
    val uniqueDiscoveries: Int,
    val missionAchieved: Boolean,
    val completedAt: Long,
) {
    /** A higher score wins; equal scores are broken by unique discoveries, then by mission status. */
    fun beats(other: DailyRecord): Boolean = when {
        score != other.score -> score > other.score
        uniqueDiscoveries != other.uniqueDiscoveries -> uniqueDiscoveries > other.uniqueDiscoveries
        else -> missionAchieved && !other.missionAchieved
    }
}

data class StreakSummary(val current: Int, val longest: Int, val daysPlayed: Int, val playedToday: Boolean)

/** One square of a Monday-first month calendar. [date] is null for leading and trailing padding. */
data class CalendarDay(val date: LocalDate?, val record: DailyRecord?, val isToday: Boolean)

/**
 * Pure daily-history rules, kept free of Android so they can be unit tested. Dates are always the
 * UTC board date, matching [DailySeed], so a streak never depends on the device time zone.
 */
object DailyHistory {
    /** Inserts [candidate], keeping only the better of two results for the same date. */
    fun merge(history: Map<LocalDate, DailyRecord>, candidate: DailyRecord): Map<LocalDate, DailyRecord> {
        val previous = history[candidate.date]
        return if (previous == null || candidate.beats(previous)) history + (candidate.date to candidate) else history
    }

    /**
     * The current streak counts back from today, or from yesterday when today's board has not been
     * played yet, so an unplayed morning does not break a streak until the UTC day has ended.
     */
    fun streaks(playedDates: Set<LocalDate>, today: LocalDate): StreakSummary {
        val playedToday = today in playedDates
        var cursor = if (playedToday) today else today.minusDays(1)
        var current = 0
        while (cursor in playedDates) {
            current++
            cursor = cursor.minusDays(1)
        }
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in playedDates.filter { !it.isAfter(today) }.sorted()) {
            run = if (previous != null && previous.plusDays(1) == date) run + 1 else 1
            longest = maxOf(longest, run)
            previous = date
        }
        return StreakSummary(current, longest, playedDates.count { !it.isAfter(today) }, playedToday)
    }

    /** Monday-first weeks for [month], padded to whole rows of seven. */
    fun monthGrid(month: YearMonth, history: Map<LocalDate, DailyRecord>, today: LocalDate): List<CalendarDay> {
        val leading = month.atDay(1).dayOfWeek.value - DayOfWeek.MONDAY.value
        val days = (1..month.lengthOfMonth()).map { day ->
            val date = month.atDay(day)
            CalendarDay(date, history[date], date == today)
        }
        val padded = List(leading) { CalendarDay(null, null, false) } + days
        val trailing = (7 - padded.size % 7) % 7
        return padded + List(trailing) { CalendarDay(null, null, false) }
    }

    fun encode(history: Map<LocalDate, DailyRecord>): String = history.values
        .sortedBy { it.date }
        .joinToString("\n") { record ->
            listOf(record.date, record.score, record.uniqueDiscoveries, record.missionAchieved, record.completedAt)
                .joinToString(",")
        }

    /** Malformed rows are skipped rather than discarding the whole history. */
    fun decode(encoded: String?): Map<LocalDate, DailyRecord> = encoded.orEmpty()
        .lineSequence()
        .filter(String::isNotBlank)
        .mapNotNull { line ->
            val fields = line.split(',')
            if (fields.size != 5) return@mapNotNull null
            runCatching {
                DailyRecord(
                    date = LocalDate.parse(fields[0]),
                    score = fields[1].toInt(),
                    uniqueDiscoveries = fields[2].toInt(),
                    missionAchieved = fields[3].toBooleanStrict(),
                    completedAt = fields[4].toLong(),
                )
            }.getOrNull()
        }
        .fold(emptyMap()) { history, record -> merge(history, record) }
}
