package uk.edbetter.kemdaily.core.game

import uk.edbetter.kemdaily.core.chemistry.Composition
import uk.edbetter.kemdaily.core.chemistry.SubstanceCatalog

object AtomConfig {
    val weights = linkedMapOf(
        "H" to 6, "O" to 5, "C" to 3, "N" to 2,
        "Na" to 1, "Cl" to 1, "K" to 1, "S" to 1,
        "Ca" to 1, "Mg" to 1, "Li" to 1, "F" to 1,
    )
    val caps = mapOf(
        "H" to 8, "O" to 7, "C" to 5, "N" to 4,
        "Na" to 2, "Cl" to 2, "K" to 2, "S" to 2,
        "Ca" to 2, "Mg" to 2, "Li" to 2, "F" to 2,
    )
    val rarity = mapOf(
        "H" to 0, "O" to 0, "C" to 5, "N" to 5, "Cl" to 10, "S" to 10,
        "Na" to 15, "K" to 15, "Ca" to 15, "Mg" to 15, "F" to 15, "Li" to 20,
    )
}

class BoardGenerator(private val random: DeterministicRandom) {
    private var nextId = 1L

    fun create(): List<AtomTile> {
        repeat(100) {
            val board = fill(BOARD_CELLS, mutableMapOf())
            if (PathFinder.firstKnownPath(board) != null) return board
        }
        return constructWith(Composition.fromSymbols(listOf("H", "H")))
    }

    fun refill(board: List<AtomTile?>): List<AtomTile> {
        require(board.size == BOARD_CELLS)
        val counts = board.filterNotNull().groupingBy { it.symbol }.eachCount().toMutableMap()
        return board.map { tile -> tile ?: AtomTile(nextId++, choose(counts)) }
    }

    fun shuffled(board: List<AtomTile>): List<AtomTile> {
        repeat(80) {
            val copy = board.toMutableList()
            for (index in copy.lastIndex downTo 1) {
                val other = random.nextInt(index + 1)
                val swap = copy[index]
                copy[index] = copy[other]
                copy[other] = swap
            }
            if (PathFinder.firstKnownPath(copy) != null) return copy
        }
        return create()
    }

    private fun constructWith(composition: Composition): List<AtomTile> {
        val symbols = composition.atoms.flatMap { (symbol, count) -> List(count) { symbol } }
        val counts = symbols.groupingBy { it }.eachCount().toMutableMap()
        val rest = fill(BOARD_CELLS - symbols.size, counts)
        return (symbols.map { AtomTile(nextId++, it) } + rest)
    }

    private fun fill(amount: Int, counts: MutableMap<String, Int>): List<AtomTile> =
        List(amount) { AtomTile(nextId++, choose(counts)) }

    private fun choose(counts: MutableMap<String, Int>): String {
        val eligible = AtomConfig.weights.filterKeys { symbol ->
            (counts[symbol] ?: 0) < AtomConfig.caps.getValue(symbol)
        }
        val total = eligible.values.sum()
        var roll = random.nextInt(total)
        val symbol = eligible.entries.first { (_, weight) ->
            roll -= weight
            roll < 0
        }.key
        counts[symbol] = (counts[symbol] ?: 0) + 1
        return symbol
    }
}

object PathFinder {
    fun firstKnownPath(board: List<AtomTile>, excludedIds: Set<String> = emptySet()): List<Cell>? {
        val targets = SubstanceCatalog.all
            .filter { it.id !in excludedIds && it.isPlayable && it.composition.atomCount <= MAX_SELECTION_ATOMS }
            .sortedBy { it.composition.atomCount }
        for (target in targets) find(board, target.composition)?.let { return it }
        return null
    }

    fun find(board: List<AtomTile>, target: Composition): List<Cell>? {
        if (target.atomCount > board.size) return null
        for (start in board.indices) {
            val counts = target.atoms.toMutableMap()
            val symbol = board[start].symbol
            if ((counts[symbol] ?: 0) == 0) continue
            counts[symbol] = counts.getValue(symbol) - 1
            val path = mutableListOf(Cell.fromIndex(start))
            if (search(board, path, counts, target.atomCount)) return path
        }
        return null
    }

    private fun search(
        board: List<AtomTile>,
        path: MutableList<Cell>,
        remaining: MutableMap<String, Int>,
        length: Int,
    ): Boolean {
        if (path.size == length) return remaining.values.all { it == 0 }
        val last = path.last()
        for (row in (last.row - 1)..(last.row + 1)) for (column in (last.column - 1)..(last.column + 1)) {
            if (row !in 0 until BOARD_SIZE || column !in 0 until BOARD_SIZE) continue
            val cell = Cell(row, column)
            if (cell in path) continue
            val symbol = board[cell.index].symbol
            if ((remaining[symbol] ?: 0) <= 0) continue
            path += cell
            remaining[symbol] = remaining.getValue(symbol) - 1
            if (search(board, path, remaining, length)) return true
            remaining[symbol] = remaining.getValue(symbol) + 1
            path.removeAt(path.lastIndex)
        }
        return false
    }
}
