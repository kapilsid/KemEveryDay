package uk.edbetter.kemdaily.data

import java.time.LocalDate
import java.time.ZoneOffset

enum class MysteryCategory(val label: String, val prompt: String) {
    SCIENTIST("Scientist", "Who am I?"),
    ELEMENT("Element", "What am I?"),
    PLACE("Scientific place", "Where am I?"),
    DISCOVERY("Discovery", "What was discovered?"),
    INSTRUMENT("Instrument", "What is this?"),
}

data class ScienceMystery(
    val id: String,
    val category: MysteryCategory,
    val answer: String,
    val clues: List<String>,
    val story: String,
) {
    val normalizedAnswer: String get() = answer.uppercase().filter(Char::isLetter)
    val wordCount: Int get() = answer.trim().split(Regex("\\s+")).size
}

object ScienceMysteries {
    /** Categories are interleaved so consecutive days feel different. */
    val all = listOf(
        ScienceMystery(
            "marie_curie", MysteryCategory.SCIENTIST, "Marie Curie",
            listOf(
                "I was born in Warsaw in 1867.",
                "I studied invisible rays released by uranium compounds.",
                "I named one element after my homeland, Poland.",
                "My work led to the discovery of polonium and radium.",
                "I was the first person to receive Nobel Prizes in two different sciences.",
            ),
            "Marie Curie helped establish the science of radioactivity and shared the 1903 Physics Nobel Prize before receiving the 1911 Chemistry prize. Her painstaking work with radioactive ores changed physics, chemistry and medicine.",
        ),
        ScienceMystery(
            "gallium", MysteryCategory.ELEMENT, "Gallium",
            listOf(
                "I am a metal that can melt in a warm hand.",
                "My atomic number is 31.",
                "Mendeleev predicted me before I was discovered.",
                "I am used in semiconductors such as gallium arsenide.",
                "My name refers to Gaul, an ancient name connected with France.",
            ),
            "Gallium was discovered in 1875, several years after Mendeleev predicted an element with closely matching properties. Its low melting point is striking, but it should not be treated as a hand-melting toy because it can damage other metals.",
        ),
        ScienceMystery(
            "cern", MysteryCategory.PLACE, "CERN",
            listOf(
                "I sit near the border between Switzerland and France.",
                "Scientists from many countries work here together.",
                "A famous circular machine lies underground beneath me.",
                "The World Wide Web began here as a way to share information.",
                "My Large Hadron Collider helped reveal the Higgs boson.",
            ),
            "CERN is the European laboratory for particle physics and the home of the Large Hadron Collider. Its experiments investigate the fundamental particles and forces that make up the universe.",
        ),
        ScienceMystery(
            "penicillin", MysteryCategory.DISCOVERY, "Penicillin",
            listOf(
                "My story began with mould growing in a laboratory dish.",
                "I stop susceptible bacteria from building strong cell walls.",
                "Alexander Fleming noticed my antibacterial effect in 1928.",
                "Other researchers later learned how to purify and mass-produce me.",
                "I became one of the earliest widely used antibiotics.",
            ),
            "Fleming observed that a Penicillium mould inhibited bacteria, but turning that observation into a medicine required major work by teams including Howard Florey and Ernst Chain. Penicillin transformed the treatment of bacterial infections, although resistance means antibiotics must be used carefully.",
        ),
        ScienceMystery(
            "spectrometer", MysteryCategory.INSTRUMENT, "Spectrometer",
            listOf(
                "I separate a signal into a spectrum.",
                "My results often appear as peaks or coloured lines.",
                "I can help identify a substance without seeing its individual atoms.",
                "Different versions study light, mass or nuclear environments.",
                "Chemists use me to infer composition and molecular structure.",
            ),
            "A spectrometer measures how a signal is distributed across quantities such as wavelength, frequency or mass-to-charge ratio. Spectra act like evidence-rich fingerprints that help scientists identify and study matter.",
        ),
        ScienceMystery(
            "dorothy_hodgkin", MysteryCategory.SCIENTIST, "Dorothy Hodgkin",
            listOf(
                "I used patterns made when X-rays passed through crystals.",
                "I determined the structures of several important biological molecules.",
                "One of my most famous targets was vitamin B₁₂.",
                "I also worked out the structure of penicillin and later insulin.",
                "I received the 1964 Nobel Prize in Chemistry.",
            ),
            "Dorothy Crowfoot Hodgkin pioneered the use of X-ray crystallography to determine complex biochemical structures. Her results made the invisible three-dimensional architecture of medicines and biomolecules scientifically testable.",
        ),
        ScienceMystery(
            "neon", MysteryCategory.ELEMENT, "Neon",
            listOf(
                "I am a gas present only in tiny amounts in Earth’s atmosphere.",
                "My atoms are unusually reluctant to form chemical bonds.",
                "My atomic number is 10.",
                "An electrical discharge makes me glow orange-red.",
                "My name became shorthand for a whole style of illuminated sign.",
            ),
            "Neon is a noble gas discovered in 1898 during the study of liquefied air. Pure neon produces an orange-red discharge, while many so-called neon signs use other gases or coatings to create different colours.",
        ),
        ScienceMystery(
            "royal_institution", MysteryCategory.PLACE, "Royal Institution",
            listOf(
                "I opened in London near the end of the eighteenth century.",
                "Public demonstrations have always been central to my identity.",
                "Humphry Davy isolated several elements while working here.",
                "Michael Faraday carried out major electrical experiments here.",
                "My Christmas Lectures bring science to young audiences.",
            ),
            "The Royal Institution has combined scientific research with public communication since 1799. Its laboratories are associated with discoveries by Humphry Davy and Michael Faraday, while its Christmas Lectures remain a celebrated science tradition.",
        ),
        ScienceMystery(
            "x_rays", MysteryCategory.DISCOVERY, "X Rays",
            listOf(
                "I can pass through soft tissue more readily than through bone.",
                "I was noticed while experimenting with electrical discharge tubes.",
                "My discoverer used an unknown quantity as my name.",
                "An early image showed the bones and ring of a human hand.",
                "Wilhelm Röntgen reported me in 1895.",
            ),
            "X-rays are high-energy electromagnetic radiation first reported by Wilhelm Röntgen in 1895. They quickly became important for medical imaging and later helped reveal structures from crystals to DNA.",
        ),
        ScienceMystery(
            "geiger_counter", MysteryCategory.INSTRUMENT, "Geiger Counter",
            listOf(
                "I respond to ionising radiation.",
                "Each detected event can produce a familiar click.",
                "A gas-filled tube is at the heart of my classic design.",
                "I count events but do not precisely identify every radiation energy.",
                "My name includes that of physicist Hans Geiger.",
            ),
            "A Geiger counter uses a Geiger–Müller tube to detect ionising particles or photons through electrical pulses. It is excellent for surveying radiation, although more specialised detectors are needed for detailed energy measurements.",
        ),
        ScienceMystery(
            "dmitri_mendeleev", MysteryCategory.SCIENTIST, "Dmitri Mendeleev",
            listOf(
                "I was born in Siberia in 1834.",
                "I arranged known elements so repeating chemical patterns became visible.",
                "I deliberately left gaps where evidence suggested elements were missing.",
                "My predictions included properties later matched by gallium and germanium.",
                "My name is inseparable from the periodic table.",
            ),
            "Dmitri Mendeleev published a highly influential periodic classification in 1869 and used it to predict undiscovered elements. The modern table is ordered by atomic number, but its repeating chemical patterns preserve the central insight of his work.",
        ),
        ScienceMystery(
            "mercury", MysteryCategory.ELEMENT, "Mercury",
            listOf(
                "I am a metal that is liquid at ordinary room temperature.",
                "My atomic number is 80.",
                "My chemical symbol, Hg, comes from the name hydrargyrum.",
                "I was once common in thermometers and barometers.",
                "My vapour and many of my compounds are toxic.",
            ),
            "Mercury is a dense, silvery metal whose unusually low melting point keeps it liquid at room temperature. Because mercury exposure can damage the nervous system, safer alternatives have replaced it in many instruments and products.",
        ),
        ScienceMystery(
            "los_alamos", MysteryCategory.PLACE, "Los Alamos",
            listOf(
                "I am located high in the mountains of New Mexico.",
                "A secret wartime laboratory was established here in 1943.",
                "J. Robert Oppenheimer served as my first scientific director.",
                "My early work was a central part of the Manhattan Project.",
                "I remain home to a major United States national laboratory.",
            ),
            "Los Alamos National Laboratory grew from the wartime laboratory that designed the first nuclear weapons. Today its research spans national security, materials, computing, energy and fundamental science, while its origins demand careful historical and ethical study.",
        ),
        ScienceMystery(
            "radioactivity", MysteryCategory.DISCOVERY, "Radioactivity",
            listOf(
                "My first clear evidence came from uranium salts affecting a photographic plate.",
                "Henri Becquerel reported that observation in 1896.",
                "Marie Curie introduced the word used to describe me.",
                "I occur when unstable atomic nuclei release particles or energy.",
                "I can be useful in medicine, dating and power generation, but exposure must be controlled.",
            ),
            "Radioactivity is the spontaneous transformation of unstable nuclei accompanied by ionising radiation. Research by Becquerel, Marie Curie, Pierre Curie and many others revealed that atoms are not indivisible and created powerful scientific and medical tools.",
        ),
        ScienceMystery(
            "microscope", MysteryCategory.INSTRUMENT, "Microscope",
            listOf(
                "I reveal details too small for unaided human eyes.",
                "Many versions of me use lenses and visible light.",
                "My useful performance depends on resolution, not magnification alone.",
                "Robert Hooke used an early version of me when describing cells in cork.",
                "Electron versions can resolve much smaller structures than optical ones.",
            ),
            "A microscope forms enlarged, resolved images of structures that are otherwise difficult or impossible to see. Different designs use light, electrons or scanning probes, allowing scientists to study subjects from living cells to individual atoms.",
        ),
        ScienceMystery(
            "antoine_lavoisier", MysteryCategory.SCIENTIST, "Antoine Lavoisier",
            listOf(
                "I was a French scientist born in 1743.",
                "Careful weighing was central to my experiments.",
                "I helped replace the phlogiston explanation of combustion.",
                "I named oxygen and hydrogen and helped reform chemical language.",
                "I am strongly associated with conservation of mass in chemical reactions.",
            ),
            "Antoine Lavoisier made quantitative measurement central to chemistry and helped explain combustion using oxygen. His systematic language and experiments contributed greatly to modern chemistry, although scientific understanding of mass and reactions has continued to develop.",
        ),
        ScienceMystery(
            "tungsten", MysteryCategory.ELEMENT, "Tungsten",
            listOf(
                "I have the highest melting point of any pure metal.",
                "My atomic number is 74.",
                "My symbol W comes from the name wolfram.",
                "I was widely used for incandescent lamp filaments.",
                "My very hard carbide is used in cutting and drilling tools.",
            ),
            "Tungsten is a dense, exceptionally heat-resistant transition metal. Its high melting point and strong carbide compounds make it valuable in high-temperature components, electrical contacts and wear-resistant tools.",
        ),
        ScienceMystery(
            "cavendish_laboratory", MysteryCategory.PLACE, "Cavendish Laboratory",
            listOf(
                "I am a physics laboratory belonging to the University of Cambridge.",
                "I opened in the nineteenth century under James Clerk Maxwell.",
                "J. J. Thomson discovered the electron while leading me.",
                "Ernest Rutherford carried out important work on atomic structure here.",
                "Research connected with the structure of DNA also took place within my walls.",
            ),
            "The Cavendish Laboratory was founded at Cambridge in 1874 and has been associated with landmark work in atomic, nuclear and molecular science. Its history includes the electron, the neutron and crucial research leading to the DNA double-helix model.",
        ),
        ScienceMystery(
            "electron", MysteryCategory.DISCOVERY, "Electron",
            listOf(
                "I carry negative electric charge.",
                "I am found around atomic nuclei but am not made of known smaller components.",
                "Cathode-ray experiments provided decisive evidence for me.",
                "J. J. Thomson measured my charge-to-mass ratio in 1897.",
                "My movement through materials is central to electricity and electronics.",
            ),
            "The electron is an elementary particle with negative electric charge and a key constituent of atoms. J. J. Thomson's cathode-ray measurements showed that these particles were far lighter than atoms, overturning the idea that atoms were indivisible.",
        ),
        ScienceMystery(
            "calorimeter", MysteryCategory.INSTRUMENT, "Calorimeter",
            listOf(
                "I help measure energy transferred as heat.",
                "Temperature change is one of the observations used with me.",
                "Insulation helps reduce unwanted exchange with the surroundings.",
                "A bomb version of me studies combustion at constant volume.",
                "Chemists use me to investigate reactions and physical changes.",
            ),
            "A calorimeter measures heat transfer by relating observed temperature changes to the heat capacities of a controlled system. Different calorimeter designs are used for reactions, phase changes, foods and materials.",
        ),
        ScienceMystery(
            "linus_pauling", MysteryCategory.SCIENTIST, "Linus Pauling",
            listOf(
                "I used quantum ideas to explain chemical bonding.",
                "A scale bearing my name compares electronegativity.",
                "I proposed important protein structures including the alpha helix.",
                "I campaigned publicly against nuclear weapons testing.",
                "I received unshared Nobel Prizes in both Chemistry and Peace.",
            ),
            "Linus Pauling transformed chemists' understanding of bonds, molecular shapes and protein structure. He also became a prominent peace campaigner and remains the only person awarded two unshared Nobel Prizes.",
        ),
        ScienceMystery(
            "bromine", MysteryCategory.ELEMENT, "Bromine",
            listOf(
                "I am one of only two elements that are liquid near room temperature.",
                "My liquid and vapour are reddish-brown.",
                "My atomic number is 35 and I belong to the halogens.",
                "My name comes from a Greek word connected with a strong smell.",
                "My compounds have been used in photography and flame retardants.",
            ),
            "Bromine is a reactive halogen that normally exists as diatomic Br₂ and produces corrosive, toxic vapour. Bromide compounds occur naturally in seawater and have uses ranging from specialised medicines to industrial chemistry.",
        ),
        ScienceMystery(
            "royal_observatory", MysteryCategory.PLACE, "Royal Observatory",
            listOf(
                "I was founded in London in 1675 to improve astronomical observations for navigation.",
                "John Flamsteed became my first Astronomer Royal.",
                "A line through my grounds became longitude zero.",
                "The initials GMT connect me with the history of world timekeeping.",
                "Visitors can stand across my famous Prime Meridian at Greenwich.",
            ),
            "The Royal Observatory at Greenwich was founded to improve astronomy and navigation. Its meridian became the international reference for zero longitude, while its work helped connect precise observation, maps and timekeeping.",
        ),
        ScienceMystery(
            "dna_structure", MysteryCategory.DISCOVERY, "DNA Structure",
            listOf(
                "I explain how a biological information molecule can be copied.",
                "My repeating backbone lies outside while paired bases point inward.",
                "X-ray diffraction data were crucial evidence for my dimensions and shape.",
                "Rosalind Franklin and Maurice Wilkins produced important experimental evidence.",
                "Watson and Crick proposed my double-helix model in 1953.",
            ),
            "The double-helical structure of DNA emerged from chemical knowledge, model building and X-ray diffraction evidence, including crucial work by Rosalind Franklin and Maurice Wilkins. The 1953 model by James Watson and Francis Crick explained complementary base pairing and suggested a mechanism for copying genetic information.",
        ),
        ScienceMystery(
            "bunsen_burner", MysteryCategory.INSTRUMENT, "Bunsen Burner",
            listOf(
                "I produce a controllable flame in the laboratory.",
                "I mix a fuel gas with air before combustion.",
                "Opening my air hole usually creates a hotter blue flame.",
                "My collar helps regulate the air entering my barrel.",
                "My name honours the chemist Robert Bunsen.",
            ),
            "A Bunsen burner mixes gas with controlled amounts of air to produce flames suited to heating and flame tests. Robert Bunsen popularised the design with instrument maker Peter Desaga, building on earlier gas-burner principles.",
        ),
    )

    const val PER_DAY = 3

    /** Today's three mysteries. Consecutive days take the next three, so the catalogue cycles without repeats. */
    fun forDay(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): List<ScienceMystery> {
        val start = Math.floorMod(date.toEpochDay() * PER_DAY, all.size.toLong()).toInt()
        return List(PER_DAY) { all[(start + it) % all.size] }
    }

    fun today(date: LocalDate = LocalDate.now(ZoneOffset.UTC)): ScienceMystery = forDay(date).first()
}
