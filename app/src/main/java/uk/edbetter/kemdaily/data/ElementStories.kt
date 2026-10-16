package uk.edbetter.kemdaily.data

/** The three things worth remembering about an element: how it was found, where you meet it, and a hook. */
data class ElementStory(val discovery: String, val uses: String, val hook: String)

private fun s(discovery: String, uses: String, hook: String) = ElementStory(discovery, uses, hook)

/**
 * Hand-written stories for every element, keyed by symbol. Kept short and checked against standard
 * references; dates are the commonly cited discovery or isolation years.
 */
object ElementStories {
    val bySymbol: Map<String, ElementStory> = mapOf(
        "H" to s(
            "Henry Cavendish made it in 1766 by dropping metals into acid and called it \"inflammable air\". Lavoisier later named it hydrogen, \"water-former\", because it burns to make water.",
            "Most hydrogen goes into making ammonia for fertiliser and into refining fuels. It also powers fuel-cell vehicles and rockets.",
            "Hydrogen is the fuel of the Sun: every second the Sun fuses about 600 million tonnes of it into helium.",
        ),
        "He" to s(
            "Astronomers spotted an unknown yellow line in sunlight during the 1868 eclipse, so helium was found on the Sun before it was found on Earth. William Ramsay isolated it from a uranium mineral in 1895.",
            "Liquid helium cools the superconducting magnets in MRI scanners. It also fills party balloons and weather balloons.",
            "It is named after Helios, the Greek Sun god, because the Sun is where it was first seen.",
        ),
        "Li" to s(
            "Johan August Arfwedson found lithium in 1817 while analysing the mineral petalite in Sweden.",
            "Lithium-ion batteries in phones, laptops and electric cars. Lithium carbonate is also an important medicine for bipolar disorder.",
            "It is the lightest metal of all: it is soft enough to cut with a knife and would float on water if it did not react with it.",
        ),
        "Be" to s(
            "Louis-Nicolas Vauquelin found it in 1798 in beryl and emeralds. The metal was isolated in 1828.",
            "Light, stiff beryllium makes the mirrors of the James Webb Space Telescope and windows for X-ray tubes.",
            "Emeralds are green beryl, so every emerald contains beryllium. Its compounds taste sweet, but they are highly toxic.",
        ),
        "B" to s(
            "Humphry Davy, and separately Gay-Lussac and Thénard, isolated boron in 1808 from boric acid.",
            "Borosilicate glass such as Pyrex survives sudden heat. Boron is also used in detergents, fibreglass and the control rods of nuclear reactors.",
            "Borax, the classic slime ingredient, is a boron compound.",
        ),
        "C" to s(
            "Carbon has been known since prehistory as charcoal and soot. In 1796 Smithson Tennant proved that diamond is pure carbon by burning one.",
            "Fuels, steel, plastics, pencil \"lead\" (graphite) and every living thing.",
            "Graphite and diamond are both pure carbon. The only difference is how the atoms are arranged.",
        ),
        "N" to s(
            "Daniel Rutherford isolated it in 1772 as the part of air that would not keep a mouse alive or a candle burning.",
            "Fertilisers, explosives, and liquid nitrogen for freezing food and medical samples.",
            "Almost four out of every five breaths you take are nitrogen, and your body simply breathes it back out.",
        ),
        "O" to s(
            "Carl Scheele made it around 1771 and Joseph Priestley published first in 1774. Lavoisier then showed what it does in burning.",
            "Breathing, hospital oxygen, steelmaking and rocket engines.",
            "Oxygen is the most abundant element in Earth's crust, nearly half of it by mass, mostly locked up in rocks.",
        ),
        "F" to s(
            "It was so reactive that several chemists were injured trying to isolate it. Henri Moissan finally succeeded in 1886 and won a Nobel Prize.",
            "Fluoride toothpaste, non-stick pan coatings (PTFE) and refrigerants.",
            "Fluorine is the most reactive element: it attacks glass, and even water can burn in it.",
        ),
        "Ne" to s(
            "William Ramsay and Morris Travers found neon in 1898 by slowly boiling liquefied air.",
            "Neon signs, and lasers in barcode scanners.",
            "The name means \"new\". A true neon sign glows red-orange; the other colours come from other gases or coatings.",
        ),
        "Na" to s(
            "Humphry Davy isolated sodium in 1807 by passing electricity through molten caustic soda.",
            "Table salt, baking soda, and the yellow-orange street lamps of the past.",
            "Its symbol Na comes from natrium. The pure metal is so reactive it is stored under oil, yet with chlorine it makes the salt on your chips.",
        ),
        "Mg" to s(
            "Joseph Black recognised it as an element in 1755. Humphry Davy isolated the metal in 1808.",
            "Lightweight alloys for bikes and laptops, fireworks, and antacid tablets.",
            "Every chlorophyll molecule has a magnesium atom at its centre, so plants need it to be green.",
        ),
        "Al" to s(
            "Hans Christian Ørsted made it in 1825, but it stayed rare until 1886, when Hall and Héroult found a cheap way to extract it with electricity.",
            "Cans, foil, aircraft, window frames and power lines.",
            "In the 1800s it was more precious than gold: Napoleon III served his most honoured guests with aluminium cutlery.",
        ),
        "Si" to s(
            "Jöns Jacob Berzelius isolated silicon in 1824.",
            "Computer chips, solar panels, glass, and silicone sealants.",
            "Sand is mostly silicon dioxide, so the chip in your phone began life as sand.",
        ),
        "P" to s(
            "Hennig Brand discovered it in 1669 by boiling down huge amounts of urine while searching for the philosopher's stone.",
            "Fertilisers, matches, and the DNA and bones in your body.",
            "It was the first element discovered by a named person, and its name means \"light-bearer\" because white phosphorus glows in the dark.",
        ),
        "S" to s(
            "Known since ancient times as \"brimstone\". Lavoisier showed in 1777 that it is an element.",
            "Sulfuric acid, the world's most-made industrial chemical, plus rubber vulcanisation and fertilisers.",
            "The smell of rotten eggs and burnt matches comes from sulfur compounds.",
        ),
        "Cl" to s(
            "Carl Scheele made the green gas in 1774. Humphry Davy proved it was an element in 1810 and named it after the Greek for pale green.",
            "Disinfecting drinking water and swimming pools, making PVC, and table salt.",
            "Chlorine gas is poisonous, yet as chloride it makes salt, which you need to live.",
        ),
        "Ar" to s(
            "Lord Rayleigh and William Ramsay found it in 1894 when nitrogen taken from air weighed slightly more than nitrogen made chemically.",
            "A protective gas for welding, the filling of light bulbs, and insulation in double glazing.",
            "Argon makes up almost 1% of the air you breathe. Its name means \"lazy\" because it hardly reacts at all.",
        ),
        "K" to s(
            "Humphry Davy isolated it in 1807 from potash, a few days before sodium. It was the first metal ever isolated by electrolysis.",
            "Fertilisers, soap, and the electrical signals in your nerves.",
            "Bananas are famous for potassium. The pure metal bursts into lilac flames when dropped in water.",
        ),
        "Ca" to s(
            "Humphry Davy isolated it in 1808. Its compounds, such as lime, had been used for thousands of years.",
            "Cement, plaster, chalk, and the bones and teeth in your body.",
            "Your skeleton holds about a kilogram of calcium. Seashells and limestone cliffs are made of calcium carbonate.",
        ),
        "Sc" to s(
            "Lars Fredrik Nilson found it in 1879 in Scandinavian minerals, close to where Mendeleev had predicted a missing element.",
            "Light, strong aluminium-scandium alloys for aircraft parts and sports gear.",
            "It filled a gap Mendeleev had predicted: he had called the missing element eka-boron.",
        ),
        "Ti" to s(
            "William Gregor found it in Cornish black sand in 1791. Martin Klaproth named it after the Titans of Greek myth.",
            "Hip and dental implants, aircraft, and the white pigment titanium dioxide in paint and sunscreen.",
            "Titanium is as strong as steel but almost half the weight, and the body does not reject it.",
        ),
        "V" to s(
            "Andrés Manuel del Río found it in Mexico in 1801, but others convinced him he was wrong. Nils Sefström rediscovered it in 1830.",
            "Tough steel for tools and springs, and large \"flow\" batteries for storing solar power.",
            "It is named after Vanadís, the Norse goddess of beauty, because its compounds come in so many colours.",
        ),
        "Cr" to s(
            "Louis-Nicolas Vauquelin discovered it in 1797 in a bright red Siberian mineral.",
            "Stainless steel and shiny chrome plating.",
            "The red of rubies and the green of emeralds both come from traces of chromium.",
        ),
        "Mn" to s(
            "Johan Gottlieb Gahn isolated it in 1774.",
            "Steelmaking, aluminium drink cans and batteries.",
            "Most steel would crack without it: around 90% of all manganese goes into iron and steel.",
        ),
        "Fe" to s(
            "Known since ancient times. The Iron Age began around 1200 BC, and some early iron came from meteorites.",
            "Steel for buildings, cars, bridges and tools.",
            "Iron carries oxygen in your blood, and Earth's core is mostly iron.",
        ),
        "Co" to s(
            "Georg Brandt identified it in 1735. It was the first metal discovered whose ore had not been known since ancient times.",
            "Rechargeable batteries, jet-engine alloys and blue pigments.",
            "Miners named it after the kobold, a mischievous goblin they blamed for ores that gave off poisonous fumes.",
        ),
        "Ni" to s(
            "Axel Fredrik Cronstedt isolated it in 1751 from a mineral miners called \"Kupfernickel\" (copper-demon).",
            "Stainless steel, coins and rechargeable batteries.",
            "A US five-cent coin is called a nickel, although it is mostly copper.",
        ),
        "Cu" to s(
            "Used for over 10,000 years. It was one of the first metals people worked, and Cyprus was a famous ancient source.",
            "Electrical wiring, plumbing, and bronze and brass.",
            "The Statue of Liberty is copper: it was brown when new and turned green as it weathered.",
        ),
        "Zn" to s(
            "Zinc was smelted in India by the 1300s. Andreas Marggraf isolated it in Europe in 1746.",
            "Galvanising steel against rust, brass, batteries and sunscreen.",
            "A thin coat of zinc stops steel rusting, even when it is scratched.",
        ),
        "Ga" to s(
            "Paul-Émile Lecoq de Boisbaudran discovered it in 1875, matching Mendeleev's predicted eka-aluminium.",
            "LEDs, laser diodes and fast phone chips (gallium arsenide and gallium nitride).",
            "It melts at about 30 °C, so it melts in your hand.",
        ),
        "Ge" to s(
            "Clemens Winkler found it in 1886, confirming Mendeleev's predicted eka-silicon.",
            "Fibre-optic cables, infrared night-vision lenses, and the very first transistors.",
            "Mendeleev predicted its density and properties 15 years before anyone saw it, and he was almost exactly right.",
        ),
        "As" to s(
            "Known since ancient times. Albertus Magnus is said to have isolated it around 1250.",
            "Wood preservatives in the past, and semiconductors such as gallium arsenide today.",
            "Arsenic was so popular with poisoners that it was nicknamed \"inheritance powder\".",
        ),
        "Se" to s(
            "Jöns Jacob Berzelius discovered it in 1817 in residue from a sulfuric acid works.",
            "Glassmaking, solar cells, and an essential trace nutrient.",
            "It is named after Selene, the Moon, because it sits next to tellurium, which is named after the Earth.",
        ),
        "Br" to s(
            "Antoine Balard found it in 1826 in sea-salt brine. Carl Löwig found it independently at almost the same time.",
            "Flame retardants, and some medicines and pool disinfectants.",
            "It is one of only two elements that are liquid at room temperature. Its name means \"stench\".",
        ),
        "Kr" to s(
            "William Ramsay and Morris Travers found it in 1898 in liquefied air.",
            "Energy-saving windows, high-powered lamps and photographic flash.",
            "From 1960 to 1983 the official metre was defined by the wavelength of krypton's orange light. And yes, Superman's planet borrowed the name.",
        ),
        "Rb" to s(
            "Robert Bunsen and Gustav Kirchhoff discovered it in 1861 using their new spectroscope.",
            "Atomic clocks, including the compact ones used in GPS satellites.",
            "It is named after the deep red lines in its light (rubidus means deep red).",
        ),
        "Sr" to s(
            "Adair Crawford recognised a new earth in 1790 in ore from Strontian, Scotland. Humphry Davy isolated the metal in 1808.",
            "Red fireworks and flares, and toothpaste for sensitive teeth.",
            "It is the only element named after a village in the UK: Strontian in the Scottish Highlands.",
        ),
        "Y" to s(
            "Johan Gadolin identified it in 1794 in a mineral from the Swedish quarry at Ytterby.",
            "Red phosphors in old TV screens, lasers and superconductors.",
            "Ytterby is a single village quarry that gave its name to four elements: yttrium, ytterbium, terbium and erbium.",
        ),
        "Zr" to s(
            "Martin Klaproth found it in zircon in 1789.",
            "Nuclear fuel rod casings, ceramic knives and dental crowns.",
            "Cubic zirconia, the cheap sparkling diamond look-alike, is zirconium oxide.",
        ),
        "Nb" to s(
            "Charles Hatchett found it in 1801 and named it columbium. The name niobium was officially adopted in 1949.",
            "Superconducting magnets in MRI scanners and particle accelerators, and strong pipeline steel.",
            "It is named after Niobe, daughter of Tantalus, because it is always found with tantalum.",
        ),
        "Mo" to s(
            "Carl Scheele identified it in 1778 and Peter Hjelm isolated it in 1781.",
            "Very strong steel for tools, engines and armour.",
            "Its name comes from the Greek for lead, because its ore was confused with lead ore.",
        ),
        "Tc" to s(
            "Carlo Perrier and Emilio Segrè made it in 1937 from a piece of a particle accelerator. It was the first element made artificially.",
            "Medical imaging: tens of millions of hospital scans each year use technetium-99m.",
            "Its name means \"artificial\", because it was made before it was found in nature.",
        ),
        "Ru" to s(
            "Karl Ernst Claus isolated it in 1844 in Kazan.",
            "Hard-wearing electrical contacts, hard drives, and catalysts.",
            "It is named after Ruthenia, the Latin name for Russia.",
        ),
        "Rh" to s(
            "William Hyde Wollaston discovered it in 1803 in platinum ore.",
            "Catalytic converters that clean car exhaust, and shiny jewellery plating.",
            "It is often the most expensive precious metal, at times worth many times more than gold.",
        ),
        "Pd" to s(
            "William Hyde Wollaston discovered it in 1802 and named it after the newly found asteroid Pallas.",
            "Catalytic converters, electronics and dental fillings.",
            "Palladium can soak up hundreds of times its own volume of hydrogen, like a metal sponge.",
        ),
        "Ag" to s(
            "Known and mined since ancient times. The Romans called it argentum.",
            "Jewellery, cutlery, mirrors, solar panels and electronics.",
            "Silver conducts electricity and heat better than any other metal. Argentina is named after it.",
        ),
        "Cd" to s(
            "Friedrich Stromeyer discovered it in 1817 in impure zinc carbonate.",
            "Older rechargeable batteries, bright yellow pigments and some solar panels.",
            "It is toxic and now heavily restricted, but artists still prize cadmium yellow.",
        ),
        "In" to s(
            "Ferdinand Reich and Hieronymous Richter found it in 1863 from a bright indigo line in its light.",
            "Indium tin oxide makes the touchscreen of your phone conduct while staying transparent.",
            "It is so soft that a bent bar \"cries\", making a creaking sound.",
        ),
        "Sn" to s(
            "Used for over 5,000 years. Adding it to copper made bronze and started the Bronze Age.",
            "Solder that joins electronics, tin-plated food cans, and pewter.",
            "\"Tin cans\" are really steel with a thin tin coat.",
        ),
        "Sb" to s(
            "Known since ancient times. Egyptian women used its sulfide as black eyeliner (kohl).",
            "Flame retardants, lead-acid batteries and bullets.",
            "Its symbol Sb comes from stibium, the Latin word for the eye make-up.",
        ),
        "Te" to s(
            "Franz-Joseph Müller von Reichenstein discovered it in 1782 in a gold ore from Transylvania.",
            "Solar panels (cadmium telluride) and rewritable DVDs.",
            "People exposed to tellurium can get breath that smells strongly of garlic.",
        ),
        "I" to s(
            "Bernard Courtois discovered it in 1811 when purple vapour rose from seaweed ash treated with acid.",
            "Antiseptics, iodised salt, and medical X-ray contrast.",
            "Your thyroid gland needs it, which is why iodine is added to salt. Its name means violet.",
        ),
        "Xe" to s(
            "William Ramsay and Morris Travers found it in 1898. Its name means \"stranger\".",
            "Bright car headlights, camera flashes and ion engines on spacecraft.",
            "Noble gases were thought never to react until 1962, when Neil Bartlett made the first xenon compound.",
        ),
        "Cs" to s(
            "Robert Bunsen and Gustav Kirchhoff found it in 1860 in mineral water, the first element discovered by spectroscopy.",
            "Atomic clocks: the official second is defined by caesium.",
            "Its name means sky blue, after its bright blue spectral lines.",
        ),
        "Ba" to s(
            "Carl Scheele identified it in 1774 and Humphry Davy isolated the metal in 1808.",
            "Barium meals for X-ray scans of the gut, and green fireworks.",
            "Patients drink a chalky barium drink so their insides show up on X-rays.",
        ),
        "La" to s(
            "Carl Gustaf Mosander discovered it in 1839 hidden inside cerium oxide.",
            "Camera lenses and the batteries of hybrid cars.",
            "Its name means \"to lie hidden\", because it hid inside another mineral for decades.",
        ),
        "Ce" to s(
            "Berzelius and Hisinger, and separately Klaproth, discovered it in 1803. It is named after the dwarf planet Ceres.",
            "Glass polishing, self-cleaning ovens and lighter flints.",
            "The sparks from a lighter come from a cerium-iron alloy called ferrocerium.",
        ),
        "Pr" to s(
            "Carl Auer von Welsbach separated it in 1885 from a mixture once thought to be one element.",
            "Strong magnets, aircraft engines, and welders' goggles.",
            "Its name means \"green twin\", because it was split from its twin, neodymium.",
        ),
        "Nd" to s(
            "Carl Auer von Welsbach separated it in 1885.",
            "The strongest permanent magnets, used in headphones, electric cars and wind turbines.",
            "A neodymium magnet the size of a coin can hold over 1,000 times its own weight.",
        ),
        "Pm" to s(
            "Jacob Marinsky, Lawrence Glendenin and Charles Coryell confirmed it in 1945 among uranium fission products.",
            "Long-life nuclear batteries and luminous paint.",
            "It is named after Prometheus, who stole fire from the gods. Every form of it is radioactive.",
        ),
        "Sm" to s(
            "Paul-Émile Lecoq de Boisbaudran discovered it in 1879.",
            "Samarium-cobalt magnets that keep working when hot, and cancer treatment.",
            "Through the mineral samarskite, it was the first element to carry a real person's name: the mine official Vasili Samarsky-Bykhovets.",
        ),
        "Eu" to s(
            "Eugène-Anatole Demarçay discovered it in 1901.",
            "Red and blue phosphors in screens and lamps.",
            "Euro banknotes contain europium phosphors that glow under ultraviolet light to fight forgery.",
        ),
        "Gd" to s(
            "Jean Charles Galissard de Marignac discovered it in 1880. It is named after Johan Gadolin.",
            "Contrast agents for MRI scans, and nuclear reactor control rods.",
            "It is magnetic only when it is cooler than about room temperature, 20 °C.",
        ),
        "Tb" to s(
            "Carl Gustaf Mosander discovered it in 1843.",
            "Green phosphors in screens, and alloys that change shape in a magnetic field.",
            "One of four elements named after the tiny Swedish village of Ytterby.",
        ),
        "Dy" to s(
            "Paul-Émile Lecoq de Boisbaudran discovered it in 1886 after dozens of attempts to separate it.",
            "Magnets for electric car motors and wind turbines.",
            "Its name means \"hard to get\", which describes the work of separating it.",
        ),
        "Ho" to s(
            "Per Teodor Cleve, and independently Delafontaine and Soret, found it in 1878.",
            "Medical lasers for kidney stones, and the strongest magnetic fields in magnets.",
            "It is named after Holmia, the Latin name for Stockholm.",
        ),
        "Er" to s(
            "Carl Gustaf Mosander discovered it in 1843.",
            "Fibre-optic amplifiers for internet cables, and pink-tinted glass.",
            "Erbium boosts the light signals that carry the internet under the oceans.",
        ),
        "Tm" to s(
            "Per Teodor Cleve discovered it in 1879.",
            "Portable X-ray machines and some surgical lasers.",
            "It is named after Thule, an ancient name for the far north of Scandinavia.",
        ),
        "Yb" to s(
            "Jean Charles Galissard de Marignac discovered it in 1878.",
            "Some of the most accurate atomic clocks ever built, and lasers.",
            "The fourth element named after Ytterby, the quarry village in Sweden.",
        ),
        "Lu" to s(
            "Georges Urbain, Carl Auer von Welsbach and Charles James found it independently in 1907.",
            "Cancer treatment and oil-refining catalysts.",
            "It is named after Lutetia, the Roman name for Paris.",
        ),
        "Hf" to s(
            "Dirk Coster and George de Hevesy found it in 1923 in Copenhagen, following a prediction from Niels Bohr's atomic theory.",
            "Nuclear reactor control rods and modern computer chips.",
            "It is named after Hafnia, the Latin name for Copenhagen.",
        ),
        "Ta" to s(
            "Anders Gustaf Ekeberg discovered it in 1802.",
            "Tiny capacitors in phones and laptops, and surgical implants.",
            "It is named after Tantalus from Greek myth, because it will not absorb acid, just as Tantalus could never drink.",
        ),
        "W" to s(
            "The brothers Juan José and Fausto Elhuyar isolated it in 1783.",
            "Old light-bulb filaments, drill bits and very hard cutting tools.",
            "It has the highest melting point of any metal, 3,422 °C. Its symbol W comes from wolfram.",
        ),
        "Re" to s(
            "Walter Noddack, Ida Tacke and Otto Berg found it in 1925. It was the last stable element to be discovered.",
            "Jet-engine turbine blades and petrol-refining catalysts.",
            "It is named after the Rhine, Rhenus in Latin.",
        ),
        "Os" to s(
            "Smithson Tennant discovered it in 1803 in platinum residue.",
            "Very hard tips for fountain pens and electrical contacts.",
            "It is the densest natural element: a cube the size of a sugar lump weighs about 22 grams. Its name means smell.",
        ),
        "Ir" to s(
            "Smithson Tennant discovered it in 1803, alongside osmium.",
            "Spark plugs and crucibles that must survive extreme heat.",
            "A thin layer of iridium-rich clay around the world marks the asteroid impact that ended the dinosaurs.",
        ),
        "Pt" to s(
            "Used in South America before Europeans arrived. Antonio de Ulloa described it in 1748.",
            "Catalytic converters, jewellery, and cancer drugs such as cisplatin.",
            "Spanish explorers threw it away as \"little silver\" because it got in the way of gold.",
        ),
        "Au" to s(
            "Known since prehistory, and found as shiny nuggets in rivers.",
            "Jewellery, electronics, money and dentistry.",
            "All the gold ever mined would fit in a cube roughly 22 metres on each side. Its symbol Au comes from aurum.",
        ),
        "Hg" to s(
            "Known since ancient times. It has been found in Egyptian tombs from around 1500 BC.",
            "Old thermometers and barometers, and some lamps. Its use is now restricted because it is toxic.",
            "It is the only metal that is liquid at room temperature. Its symbol Hg comes from hydrargyrum, \"liquid silver\".",
        ),
        "Tl" to s(
            "William Crookes discovered it in 1861 from a bright green spectral line.",
            "Specialist electronics and heart-scan imaging.",
            "Its name means \"green shoot\". It is so toxic it was once used as rat poison.",
        ),
        "Pb" to s(
            "Known since ancient times. The Romans used it widely for water pipes.",
            "Car batteries and radiation shielding.",
            "\"Plumber\" comes from plumbum, the Latin for lead, which is also where its symbol Pb comes from.",
        ),
        "Bi" to s(
            "Known since the Middle Ages, but often confused with tin and lead. Claude François Geoffroy showed in 1753 that it is a separate element.",
            "Stomach remedies such as Pepto-Bismol, and lead-free solder.",
            "It forms spectacular rainbow-coloured staircase crystals.",
        ),
        "Po" to s(
            "Marie and Pierre Curie discovered it in 1898 in uranium ore.",
            "Static eliminators in industry, and early nuclear triggers.",
            "Marie Curie named it after her homeland, Poland.",
        ),
        "At" to s(
            "Dale Corson, Kenneth MacKenzie and Emilio Segrè made it in 1940.",
            "Researched for targeted cancer treatment.",
            "It is the rarest natural element: probably less than a gram exists in Earth's crust at any moment.",
        ),
        "Rn" to s(
            "Friedrich Ernst Dorn discovered it in 1900 as a gas given off by radium.",
            "Once used in cancer treatment. Today homes are tested for it.",
            "It seeps out of granite rocks and can build up in basements, so some homes need radon fans.",
        ),
        "Fr" to s(
            "Marguerite Perey discovered it in 1939 at the Curie Institute.",
            "Used only in research.",
            "It was the last element found in nature, and it is named after France.",
        ),
        "Ra" to s(
            "Marie and Pierre Curie discovered it in 1898.",
            "Once used in glowing watch dials and cancer treatment.",
            "Factory workers who painted glowing radium dials became ill, which helped bring in workplace safety laws.",
        ),
        "Ac" to s(
            "André-Louis Debierne discovered it in 1899.",
            "Used in research and targeted cancer therapy.",
            "Its name comes from the Greek for \"ray\", and it glows a faint blue in the dark.",
        ),
        "Th" to s(
            "Jöns Jacob Berzelius discovered it in 1829 and named it after Thor, the Norse god of thunder.",
            "Gas lantern mantles in the past, and possible future nuclear fuel.",
            "There is about three times as much thorium in Earth's crust as uranium.",
        ),
        "Pa" to s(
            "Kasimir Fajans and Oswald Göhring identified it in 1913. Lise Meitner and Otto Hahn found a longer-lived form in 1918.",
            "Used only in research.",
            "It is one of the rarest and most expensive natural elements.",
        ),
        "U" to s(
            "Martin Klaproth discovered it in 1789 and named it after the newly discovered planet Uranus.",
            "Nuclear power stations, and dating rocks.",
            "One uranium fuel pellet the size of a fingertip holds as much energy as about a tonne of coal.",
        ),
        "Np" to s(
            "Edwin McMillan and Philip Abelson made it in 1940 at Berkeley. It was the first element heavier than uranium.",
            "Neutron detectors and research.",
            "It is named after Neptune, the next planet after Uranus.",
        ),
        "Pu" to s(
            "Glenn Seaborg and his team made it in 1940 to 1941.",
            "Nuclear power, and space probes such as Voyager and the Mars rovers.",
            "It is named after Pluto, next after Neptune. Plutonium batteries have powered Voyager for over 45 years.",
        ),
        "Am" to s(
            "Glenn Seaborg's team made it in 1944.",
            "The smoke detector in your home probably contains a tiny amount.",
            "It is named after the Americas, just as its neighbour above it, europium, is named after Europe.",
        ),
        "Cm" to s(
            "Glenn Seaborg's team made it in 1944.",
            "Powering instruments on space missions and Mars rovers.",
            "It is named after Marie and Pierre Curie.",
        ),
        "Bk" to s(
            "Glenn Seaborg's team made it in 1949 at Berkeley.",
            "Used to make even heavier elements.",
            "It is named after the city of Berkeley, California, where it was made.",
        ),
        "Cf" to s(
            "Glenn Seaborg's team made it in 1950.",
            "Starting nuclear reactors and finding gold and oil in rock.",
            "A single gram can cost tens of millions of pounds.",
        ),
        "Es" to s(
            "It was found in 1952 in the debris of the first hydrogen bomb test.",
            "Used only in research.",
            "It is named after Albert Einstein, and its discovery was kept secret for years.",
        ),
        "Fm" to s(
            "It was found in 1952 in the same hydrogen bomb debris as einsteinium.",
            "Used only in research.",
            "It is named after Enrico Fermi, who built the first nuclear reactor.",
        ),
        "Md" to s(
            "Albert Ghiorso, Glenn Seaborg and colleagues made it in 1955, at first only 17 atoms.",
            "Used only in research.",
            "It is named after Dmitri Mendeleev, the creator of the periodic table.",
        ),
        "No" to s(
            "Its discovery was disputed for years. Soviet scientists at Dubna confirmed it in the 1960s.",
            "Used only in research.",
            "It is named after Alfred Nobel, founder of the Nobel Prizes.",
        ),
        "Lr" to s(
            "Albert Ghiorso's team at Berkeley made it in 1961.",
            "Used only in research.",
            "It is named after Ernest Lawrence, inventor of the cyclotron particle accelerator.",
        ),
        "Rf" to s(
            "Teams in Dubna (1964) and Berkeley (1969) both claimed it.",
            "Used only in research.",
            "It is named after Ernest Rutherford, who discovered the atomic nucleus.",
        ),
        "Db" to s(
            "Made in the late 1960s by teams in Dubna and Berkeley.",
            "Used only in research.",
            "It is named after Dubna, the Russian science town where it was made.",
        ),
        "Sg" to s(
            "Albert Ghiorso's team made it in 1974.",
            "Used only in research.",
            "Glenn Seaborg was still alive when it was named after him, a first for any element.",
        ),
        "Bh" to s(
            "A team at GSI in Darmstadt, Germany, made it in 1981.",
            "Used only in research.",
            "It is named after Niels Bohr, who explained how electrons sit in atoms.",
        ),
        "Hs" to s(
            "The GSI team in Darmstadt made it in 1984.",
            "Used only in research.",
            "It is named after Hesse, the German state where it was made.",
        ),
        "Mt" to s(
            "The GSI team in Darmstadt made it in 1982, starting from a single atom.",
            "Used only in research.",
            "It is named after Lise Meitner, who helped discover nuclear fission.",
        ),
        "Ds" to s(
            "The GSI team made it in 1994.",
            "Used only in research.",
            "It is named after Darmstadt, the German city where it was made.",
        ),
        "Rg" to s(
            "The GSI team made it in 1994.",
            "Used only in research.",
            "It is named after Wilhelm Röntgen, who discovered X-rays.",
        ),
        "Cn" to s(
            "The GSI team made it in 1996.",
            "Used only in research.",
            "It is named after Nicolaus Copernicus, who placed the Sun at the centre of the solar system.",
        ),
        "Nh" to s(
            "A team at RIKEN in Japan made it in 2004 after years of experiments.",
            "Used only in research.",
            "It was the first element discovered in Asia. Nihon means Japan.",
        ),
        "Fl" to s(
            "A Dubna–Livermore team made it in 1998 to 1999.",
            "Used only in research.",
            "It is named after the Flerov Laboratory in Dubna, Russia.",
        ),
        "Mc" to s(
            "A Dubna–US team made it in 2003.",
            "Used only in research.",
            "It is named after Moscow Oblast, the region around Moscow.",
        ),
        "Lv" to s(
            "A Dubna–Livermore team made it in 2000.",
            "Used only in research.",
            "It is named after Lawrence Livermore National Laboratory in California.",
        ),
        "Ts" to s(
            "A Dubna–US team made it in 2010.",
            "Used only in research.",
            "It is named after Tennessee, home of laboratories that helped make it.",
        ),
        "Og" to s(
            "A Dubna–Livermore team made it in 2002, only a handful of atoms.",
            "Used only in research.",
            "It is the heaviest element so far, and like seaborgium it was named after a living scientist: Yuri Oganessian.",
        ),
    )

    fun forSymbol(symbol: String): ElementStory = bySymbol.getValue(symbol)
}
