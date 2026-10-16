package uk.edbetter.kemdaily.core.chemistry

internal fun substance(
    id: String,
    name: String,
    formula: String,
    category: SubstanceCategory,
    fact: String,
    tier: Int = 1,
    ambiguous: Boolean = false,
    aliases: List<String> = emptyList(),
) = Substance(id, name, formula, FormulaParser.parse(formula), category, fact, tier, aliases, ambiguous)

object SubstanceCatalog {
    const val VERSION = 2

    /** Curated starter data plus the expanded offline reference library. */
    private val starter: List<Substance> = listOf(
        substance("hydrogen", "Hydrogen", "H2", SubstanceCategory.ELEMENTAL, "Hydrogen is the lightest element and normally occurs as pairs of H atoms. It is used in fuel cells and in making ammonia."),
        substance("oxygen", "Oxygen", "O2", SubstanceCategory.ELEMENTAL, "Oxygen gas supports respiration and many combustion reactions. It makes up about one fifth of Earth's atmosphere."),
        substance("nitrogen", "Nitrogen", "N2", SubstanceCategory.ELEMENTAL, "Nitrogen gas makes up most of Earth's atmosphere. Its strong triple bond makes the molecule relatively unreactive under ordinary conditions."),
        substance("ozone", "Ozone", "O3", SubstanceCategory.MOLECULE, "Ozone is a three-atom form of oxygen. High in the atmosphere it absorbs much ultraviolet radiation, while at ground level it is an irritating pollutant."),
        substance("water", "Water", "H2O", SubstanceCategory.MOLECULE, "Water has a bent, polar molecular structure and forms hydrogen bonds. Those interactions help explain its unusually high boiling point for such a small molecule."),
        substance("hydrogen_peroxide", "Hydrogen peroxide", "H2O2", SubstanceCategory.MOLECULE, "Hydrogen peroxide is an oxidising compound that gradually decomposes into water and oxygen. Dilute solutions are used in cleaning and bleaching products."),
        substance("carbon_monoxide", "Carbon monoxide", "CO", SubstanceCategory.MOLECULE, "Carbon monoxide is a colourless, odourless and toxic gas. It binds strongly to haemoglobin, reducing the blood's ability to carry oxygen."),
        substance("carbon_dioxide", "Carbon dioxide", "CO2", SubstanceCategory.MOLECULE, "Plants use carbon dioxide during photosynthesis, while respiration and combustion release it. It also absorbs infrared radiation and contributes to Earth's greenhouse effect."),
        substance("methane", "Methane", "CH4", SubstanceCategory.ORGANIC, "Methane is the simplest hydrocarbon and the main component of natural gas. It is also a potent greenhouse gas when released into the atmosphere."),
        substance("ammonia", "Ammonia", "NH3", SubstanceCategory.MOLECULE, "Ammonia is a pungent, alkaline gas that dissolves readily in water. It is widely used to manufacture fertilisers and many other nitrogen compounds."),
        substance("sodium_hydroxide", "Sodium hydroxide", "NaOH", SubstanceCategory.BASE, "Sodium hydroxide is a strong base commonly called caustic soda. It is used in soap making, paper production and drain cleaners, and concentrated solutions are corrosive."),
        substance("lithium_oxide", "Lithium oxide", "Li2O", SubstanceCategory.OXIDE, "Lithium oxide is an ionic solid containing lithium and oxide ions. It is studied in ceramics and in materials designed to absorb carbon dioxide."),
        substance("sodium_chloride", "Sodium chloride", "NaCl", SubstanceCategory.SALT, "Sodium chloride forms a giant ionic lattice rather than separate NaCl molecules. It is familiar as table salt and is abundant in seawater."),
        substance("potassium_hydroxide", "Potassium hydroxide", "KOH", SubstanceCategory.BASE, "Potassium hydroxide is a strong alkali also known as caustic potash. It is used in some liquid soaps and alkaline batteries, and concentrated solutions are corrosive."),
        substance("hydrogen_chloride", "Hydrogen chloride", "HCl", SubstanceCategory.MOLECULE, "Hydrogen chloride is a colourless gas with a sharp odour. When dissolved in water it forms hydrochloric acid."),
        substance("magnesium_oxide", "Magnesium oxide", "MgO", SubstanceCategory.OXIDE, "Magnesium oxide is a white ionic solid. It forms when magnesium burns in oxygen with an intense white light."),
        substance("calcium_oxide", "Calcium oxide", "CaO", SubstanceCategory.OXIDE, "Calcium oxide is also called quicklime. Adding water converts it to calcium hydroxide and releases heat."),
        substance("calcium_carbonate", "Calcium carbonate", "CaCO3", SubstanceCategory.SALT, "Calcium carbonate occurs in limestone, chalk and many seashells. It reacts with acids to release carbon dioxide gas.", 2),
        substance("nitric_acid", "Nitric acid", "HNO3", SubstanceCategory.ACID, "Nitric acid is a strong acid and oxidising agent. Industry uses it to manufacture fertilisers, dyes and other chemicals.", 2),
        substance("sulfuric_acid", "Sulfuric acid", "H2SO4", SubstanceCategory.ACID, "Sulfuric acid is a strong, highly corrosive acid central to fertiliser and chemical production. Concentrated acid attracts water strongly and must be handled with great care.", 2),
        substance("sulfur_dioxide", "Sulfur dioxide", "SO2", SubstanceCategory.MOLECULE, "Sulfur dioxide is a sharp-smelling gas produced when sulfur-containing fuels burn. In the atmosphere it can contribute to acid rain and fine-particle pollution."),
        substance("sulfur_trioxide", "Sulfur trioxide", "SO3", SubstanceCategory.MOLECULE, "Sulfur trioxide is an important intermediate in sulfuric acid manufacture. It reacts vigorously with water and forms acidic aerosols."),
        substance("ethane", "Ethane", "C2H6", SubstanceCategory.ORGANIC, "Ethane is a two-carbon alkane found in natural gas. It is mainly used as a feedstock for producing ethene.", 2),
        substance("ethene", "Ethene", "C2H4", SubstanceCategory.ORGANIC, "Ethene contains a carbon–carbon double bond and is an important industrial feedstock. Plants also use it as a hormone that helps control fruit ripening.", 2),
        substance("ethyne", "Ethyne", "C2H2", SubstanceCategory.ORGANIC, "Ethyne, also called acetylene, contains a carbon–carbon triple bond. Its hot flame with oxygen is used in welding and cutting metals.", 2),
        substance("sodium_hydrogen_carbonate", "Sodium hydrogen carbonate", "NaHCO3", SubstanceCategory.SALT, "Sodium hydrogen carbonate is commonly called baking soda. It releases carbon dioxide when heated or mixed with an acid, helping baked foods rise.", 2),
        substance("calcium_hydroxide", "Calcium hydroxide", "Ca(OH)2", SubstanceCategory.BASE, "Calcium hydroxide is also called slaked lime. Its clear aqueous solution, limewater, turns cloudy when carbon dioxide forms calcium carbonate.", 2),
    )

    val all: List<Substance> = starter + ExpandedSubstanceCatalog.all

    private val byComposition = all.groupBy { it.composition.key }

    fun recognize(symbols: List<String>): Recognition = recognize(Composition.fromSymbols(symbols))

    fun recognize(composition: Composition): Recognition {
        val matches = byComposition[composition.key].orEmpty()
        return when {
            matches.isEmpty() -> Recognition.NotYetRecognized(composition)
            matches.size > 1 || matches.any { it.isAmbiguous } -> Recognition.Ambiguous(composition, matches)
            else -> Recognition.Recognized(matches.single())
        }
    }
}
