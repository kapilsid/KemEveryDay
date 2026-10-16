package uk.edbetter.kemdaily.core.chemistry

class FormulaParseException(message: String) : IllegalArgumentException(message)

/** Parses element symbols, integer multipliers and arbitrarily nested parenthesised groups. */
object FormulaParser {
    fun parse(formula: String): Composition {
        val source = formula.trim()
        if (source.isEmpty()) throw FormulaParseException("Formula is empty")
        val cursor = Cursor(source)
        val atoms = parseGroup(cursor, expectsClose = false)
        if (!cursor.atEnd()) throw FormulaParseException("Unexpected '${cursor.peek()}' at ${cursor.index}")
        return Composition(atoms)
    }

    private fun parseGroup(cursor: Cursor, expectsClose: Boolean): MutableMap<String, Int> {
        val result = linkedMapOf<String, Int>()
        while (!cursor.atEnd()) {
            when (val char = cursor.peek()) {
                '(' -> {
                    cursor.advance()
                    val nested = parseGroup(cursor, expectsClose = true)
                    val multiplier = cursor.readNumber()
                    nested.forEach { (symbol, count) -> result.add(symbol, count * multiplier) }
                }
                ')' -> {
                    if (!expectsClose) throw FormulaParseException("Unexpected ')' at ${cursor.index}")
                    cursor.advance()
                    if (result.isEmpty()) throw FormulaParseException("Empty group")
                    return result
                }
                in 'A'..'Z' -> {
                    val symbol = buildString {
                        append(char)
                        cursor.advance()
                        if (!cursor.atEnd() && cursor.peek().isLowerCase()) append(cursor.take())
                    }
                    if (symbol !in ElementCatalog.symbols) throw FormulaParseException("Unknown element $symbol")
                    result.add(symbol, cursor.readNumber())
                }
                else -> throw FormulaParseException("Unexpected '$char' at ${cursor.index}")
            }
        }
        if (expectsClose) throw FormulaParseException("Missing ')'")
        if (result.isEmpty()) throw FormulaParseException("Formula is empty")
        return result
    }

    private fun MutableMap<String, Int>.add(symbol: String, count: Int) {
        this[symbol] = (this[symbol] ?: 0) + count
    }

    private class Cursor(private val source: String) {
        var index = 0
            private set
        fun atEnd() = index >= source.length
        fun peek() = source[index]
        fun advance() { index++ }
        fun take(): Char = source[index++]
        fun readNumber(): Int {
            val start = index
            while (!atEnd() && peek().isDigit()) advance()
            if (start == index) return 1
            return source.substring(start, index).toIntOrNull()?.takeIf { it > 0 }
                ?: throw FormulaParseException("Invalid multiplier")
        }
    }
}

object ElementCatalog {
    /** All 118 currently named elements, allowing the reference library to grow beyond board tiles. */
    val names = linkedMapOf(
        "H" to "Hydrogen", "He" to "Helium", "Li" to "Lithium", "Be" to "Beryllium",
        "B" to "Boron", "C" to "Carbon", "N" to "Nitrogen", "O" to "Oxygen",
        "F" to "Fluorine", "Ne" to "Neon", "Na" to "Sodium", "Mg" to "Magnesium",
        "Al" to "Aluminium", "Si" to "Silicon", "P" to "Phosphorus", "S" to "Sulfur",
        "Cl" to "Chlorine", "Ar" to "Argon", "K" to "Potassium", "Ca" to "Calcium",
        "Sc" to "Scandium", "Ti" to "Titanium", "V" to "Vanadium", "Cr" to "Chromium",
        "Mn" to "Manganese", "Fe" to "Iron", "Co" to "Cobalt", "Ni" to "Nickel",
        "Cu" to "Copper", "Zn" to "Zinc", "Ga" to "Gallium", "Ge" to "Germanium",
        "As" to "Arsenic", "Se" to "Selenium", "Br" to "Bromine", "Kr" to "Krypton",
        "Rb" to "Rubidium", "Sr" to "Strontium", "Y" to "Yttrium", "Zr" to "Zirconium",
        "Nb" to "Niobium", "Mo" to "Molybdenum", "Tc" to "Technetium", "Ru" to "Ruthenium",
        "Rh" to "Rhodium", "Pd" to "Palladium", "Ag" to "Silver", "Cd" to "Cadmium",
        "In" to "Indium", "Sn" to "Tin", "Sb" to "Antimony", "Te" to "Tellurium",
        "I" to "Iodine", "Xe" to "Xenon", "Cs" to "Caesium", "Ba" to "Barium",
        "La" to "Lanthanum", "Ce" to "Cerium", "Pr" to "Praseodymium", "Nd" to "Neodymium",
        "Pm" to "Promethium", "Sm" to "Samarium", "Eu" to "Europium", "Gd" to "Gadolinium",
        "Tb" to "Terbium", "Dy" to "Dysprosium", "Ho" to "Holmium", "Er" to "Erbium",
        "Tm" to "Thulium", "Yb" to "Ytterbium", "Lu" to "Lutetium", "Hf" to "Hafnium",
        "Ta" to "Tantalum", "W" to "Tungsten", "Re" to "Rhenium", "Os" to "Osmium",
        "Ir" to "Iridium", "Pt" to "Platinum", "Au" to "Gold", "Hg" to "Mercury",
        "Tl" to "Thallium", "Pb" to "Lead", "Bi" to "Bismuth", "Po" to "Polonium",
        "At" to "Astatine", "Rn" to "Radon", "Fr" to "Francium", "Ra" to "Radium",
        "Ac" to "Actinium", "Th" to "Thorium", "Pa" to "Protactinium", "U" to "Uranium",
        "Np" to "Neptunium", "Pu" to "Plutonium", "Am" to "Americium", "Cm" to "Curium",
        "Bk" to "Berkelium", "Cf" to "Californium", "Es" to "Einsteinium", "Fm" to "Fermium",
        "Md" to "Mendelevium", "No" to "Nobelium", "Lr" to "Lawrencium", "Rf" to "Rutherfordium",
        "Db" to "Dubnium", "Sg" to "Seaborgium", "Bh" to "Bohrium", "Hs" to "Hassium",
        "Mt" to "Meitnerium", "Ds" to "Darmstadtium", "Rg" to "Roentgenium", "Cn" to "Copernicium",
        "Nh" to "Nihonium", "Fl" to "Flerovium", "Mc" to "Moscovium", "Lv" to "Livermorium",
        "Ts" to "Tennessine", "Og" to "Oganesson",
    )
    val symbols: Set<String> = names.keys
}
