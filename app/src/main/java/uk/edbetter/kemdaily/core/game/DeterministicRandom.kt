package uk.edbetter.kemdaily.core.game

import java.nio.ByteBuffer
import java.security.MessageDigest

/** Platform-stable SplitMix64: the same seed produces the same board on every device. */
class DeterministicRandom(seed: Long) {
    private var state = seed
    fun nextLong(): Long {
        state += -7046029254386353131L
        var value = state
        value = (value xor (value ushr 30)) * -4658895280553007687L
        value = (value xor (value ushr 27)) * -7723592293110705685L
        return value xor (value ushr 31)
    }
    fun nextInt(bound: Int): Int {
        require(bound > 0)
        return ((nextLong() ushr 1) % bound).toInt()
    }
}

object DailySeed {
    const val VERSION = 1
    fun forDate(isoDateUtc: String): Long {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("kemistri-everyday|$isoDateUtc|$VERSION".toByteArray(Charsets.UTF_8))
        return ByteBuffer.wrap(digest.copyOfRange(0, 8)).long
    }
}
