package com.defianttech.convertme;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class UnitCollection {

    private final String[] names;
    public String[] getNames() {
        return names;
    }

    private final SingleUnit[] items;
    public SingleUnit[] getItems() {
        return items;
    }

    public SingleUnit get(int index) {
        return items[index];
    }

    public int length() {
        return items.length;
    }

    public UnitCollection(String collectionName, SingleUnit[] collectionItems) {
        this(new String[] { collectionName }, collectionItems);
    }

    public UnitCollection(String[] collectionNames, SingleUnit[] collectionItems) {
        names = collectionNames;
        items = collectionItems;
    }

    public static int collectionIndexByName(String name) {
        for (int i = 0; i < COLLECTION.length; i++) {
            for (String cName : COLLECTION[i].getNames()) {
                if (cName.equals(name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public final static UnitCollection[] COLLECTION = new UnitCollection[]{

            new UnitCollection("Acceleration",
                    new SingleUnit[]{
                            new SingleUnit("centimeter/s²", 100.0, 0.0),
                            new SingleUnit("foot/s²", 3.280839895013123, 0.0),
                            new SingleUnit("meter/s²", 1.0, 0.0),
                            new SingleUnit("millimeter/s²", 1000.0, 0.0),
                            new SingleUnit("surface gravity (g)", 0.101971621297793, 0.0),

                    }),

            new UnitCollection("Angle",
                    new SingleUnit[]{
                            new SingleUnit("degree (°)", 1.0, 0.0),
                            new SingleUnit("grad", 1.11111111111111111, 0.0),
                            new SingleUnit("radian", 0.017453292519943296, 0.0),
                            new SingleUnit("revolution", 2.777777777777777e-3, 0.0),
                            new SingleUnit("minute (′)", 60.0, 0.0),
                            new SingleUnit("second (″)", 3600.0, 0.0),
                            new SingleUnit("mil (real)", 17.453292519943296, 0.0),
                            new SingleUnit("mil (NATO)", 17.777777777777777, 0.0),

                    }),

            new UnitCollection("Area",
                    new SingleUnit[]{
                            new SingleUnit("acre", 2.4710538146716534e-4, 0.0),
                            new SingleUnit("centimeter²", 10000.0, 0.0),
                            new SingleUnit("foot²", 10.7639104167097223, 0.0),
                            new SingleUnit("hectare", 1.0e-4, 0.0),
                            new SingleUnit("inch²", 1550.0031000062, 0.0),
                            new SingleUnit("kilometer²", 1.0e-6, 0.0),
                            new SingleUnit("meter²", 1.0, 0.0),
                            new SingleUnit("mile²", 3.86102158542445847e-7, 0.0),
                            new SingleUnit("millimeter²", 1.0e+6, 0.0),
                            new SingleUnit("yard²", 1.19599004630108026, 0.0),
                            new SingleUnit("rood", 9.88421525868661369e-4, 0.0),
                            new SingleUnit("perch", 0.039536861034746455, 0.0),

                    }),

            new UnitCollection("Data",
                    new SingleUnit[]{
                            new SingleUnit("bit", 8192.0, 0.0),
                            new SingleUnit("byte", 1024.0, 0.0),
                            new SingleUnit("nibble", 2048.0, 0.0),
                            new SingleUnit("kilobit", 8.0, 0.0),
                            new SingleUnit("megabit", 0.0078125, 0.0),
                            new SingleUnit("gigabit", 7.62939453125e-6, 0.0),
                            new SingleUnit("kilobyte (KB)", 1.024, 0.0),
                            new SingleUnit("kibibyte (KiB)", 1.0, 0.0),
                            new SingleUnit("megabyte (MB)", 1.024e-3, 0.0),
                            new SingleUnit("mebibyte (MiB)", 9.765625e-4, 0.0),
                            new SingleUnit("gigabyte (GB)", 1.024e-6, 0.0),
                            new SingleUnit("gibibyte (GiB)", 9.5367431640625e-7, 0.0),
                            new SingleUnit("terabyte (TB)", 1.024e-9, 0.0),
                            new SingleUnit("tebibyte (TiB)", 9.31322574615478515625e-10, 0.0),
                            new SingleUnit("petabyte (PB)", 1.024e-12, 0.0),
                            new SingleUnit("pebibyte (PiB)", 9.094947017729282379e-13, 0.0),
                            new SingleUnit("exabyte (EB)", 1.024e-15, 0.0),
                            new SingleUnit("exbibyte (EiB)", 8.881784197001252323e-16, 0.0),
                            new SingleUnit("zettabyte (ZB)", 1.024e-18, 0.0),
                            new SingleUnit("zebibyte (ZiB)", 8.673617379884035472e-19, 0.0),
                            new SingleUnit("yottabyte (YB)", 1.024e-21, 0.0),
                            new SingleUnit("yobibyte (YiB)", 8.47032947254300339e-22, 0.0),

                    }),

            new UnitCollection("Density",
                    new SingleUnit[]{

                            new SingleUnit("gram/centimeter³", 0.001, 0.0),
                            new SingleUnit("gram/meter³", 1000.0, 0.0),
                            new SingleUnit("kilogram/meter³", 1.0, 0.0),
                            new SingleUnit("milligram/meter³", 1000000.0, 0.0),
                            new SingleUnit("ounce/gallon", 0.1335265, 0.0),
                            new SingleUnit("pound/foot³", 0.06242796, 0.0),
                            new SingleUnit("pound/inch³", 3.612729e-5, 0.0),
                            new SingleUnit("ton/yard³", 0.00075248, 0.0),

                    }),

            new UnitCollection(new String[] { "Distance", "Length" },
                    new SingleUnit[]{
                            new SingleUnit("angstrom (Å)", 1.0e+10, 0.0),
                            new SingleUnit("astr. unit (AU)", 6.6844919786e-12, 0.0),
                            new SingleUnit("centimeter (cm)", 100.0, 0.0),
                            new SingleUnit("decimeter (dm)", 10.0, 0.0),
                            new SingleUnit("dekameter (dam)", 0.1, 0.0),
                            new SingleUnit("fathom", 0.54680664916885383, 0.0),
                            new SingleUnit("foot (ft)", 3.280839895013123, 0.0),
                            new SingleUnit("furlong", 4.9709695378986712e-3, 0.0),
                            new SingleUnit("hand", 9.84251968503937, 0.0),
                            new SingleUnit("hectometer (hm)", 0.01, 0.0),
                            new SingleUnit("inch (in)", 39.37007874015748, 0.0),
                            new SingleUnit("kilometer (km)", 0.001, 0.0),
                            new SingleUnit("light year (ly)", 1.0570008340246154637e-16, 0.0),
                            new SingleUnit("meter (m)", 1.0, 0.0),
                            new SingleUnit("micrometer (µm)", 1.0e+6, 0.0),
                            new SingleUnit("mile (mi)", 6.2137119223733397e-4, 0.0),
                            new SingleUnit("millimeter (mm)", 1000.0, 0.0),
                            new SingleUnit("nanometer (nm)", 1.0e+9, 0.0),
                            new SingleUnit("nautical mile", 5.399554642178303e-4, 0.0),
                            new SingleUnit("parsec (pc)", 3.2407786545502059e-17, 0.0),
                            new SingleUnit("span", 4.374453193350831, 0.0),
                            new SingleUnit("yard (yd)", 1.0936132983377078, 0.0),
                            new SingleUnit("chain", 0.0497096953789867, 0.0),
                            new SingleUnit("link", 4.97096953789867, 0.0),
                            new SingleUnit("rod", 0.198838781515947, 0.0),
                            new SingleUnit("league", 2.07123730745778e-4, 0.0),
                            new SingleUnit("cable", 0.0053961182483769, 0.0),

                    }),

            new UnitCollection("Energy",
                    new SingleUnit[]{
                            new SingleUnit("BTU", 0.00094781712, 0.0),
                            new SingleUnit("calorie", 0.238902957619, 0.0),
                            new SingleUnit("electron volt (eV)", 6.24150974e+18, 0.0),
                            new SingleUnit("erg", 10000000.0, 0.0),
                            new SingleUnit("foe", 1.0e-44, 0.0),
                            new SingleUnit("hoursepower-hour", 3.72506136e-7, 0.0),
                            new SingleUnit("joule (J)", 1.0, 0.0),
                            new SingleUnit("kilocalorie", 0.000238902957619, 0.0),
                            new SingleUnit("kiloelectron volt (keV)", 6.24150974e+15, 0.0),
                            new SingleUnit("kilojoule (kJ)", 0.001, 0.0),
                            new SingleUnit("kilowatt-hour (kWh)", 2.77777778e-7, 0.0),
                            new SingleUnit("megaelectron volt (MeV)", 6.24150974e+12, 0.0),
                            new SingleUnit("megajoule (MJ)", 0.000001, 0.0),
                            new SingleUnit("megawatt-hour (MWh)", 2.77777778e-10, 0.0),
                            new SingleUnit("microjoule (µJ)", 1000000.0, 0.0),
                            new SingleUnit("millijoule (mJ)", 1000.0, 0.0),
                            new SingleUnit("therm", 9.4781712e-9, 0.0),
                            new SingleUnit("TNT ton", 2.390057361376673e-10, 0.0),
                            new SingleUnit("TNT kiloton", 2.390057361376673e-13, 0.0),
                            new SingleUnit("TNT megaton", 2.390057361376673e-16, 0.0),
                            new SingleUnit("watt-hour (Wh)", 0.000277777778, 0.0),

                    }),

            new UnitCollection("Flow",
                    new SingleUnit[]{
                            new SingleUnit("meter³/sec", 1.0, 0.0),
                            new SingleUnit("meter³/min", 60.0, 0.0),
                            new SingleUnit("meter³/hr", 3600.0, 0.0),
                            new SingleUnit("foot³/sec", 35.31, 0.0),
                            new SingleUnit("foot³/min (cfm)", 2119.0, 0.0),
                            new SingleUnit("foot³/hr", 127100.0, 0.0),
                            new SingleUnit("liter/sec", 1000.0, 0.0),
                            new SingleUnit("liter/min", 60000.0, 0.0),
                            new SingleUnit("liter/hr", 3600000.0, 0.0),
                            new SingleUnit("gallon/day (US)", 22820000.0, 0.0),
                            new SingleUnit("gallon/hr (US) (gph)", 951000.0, 0.0),
                            new SingleUnit("gallon/min (US) (gpm)", 15850.0, 0.0),
                            new SingleUnit("gallon/day (Imp)", 19010000.0, 0.0),
                            new SingleUnit("gallon/hr (Imp)", 791900.0, 0.0),
                            new SingleUnit("gallon/min (Imp)", 13200.0, 0.0),
                    }),

            new UnitCollection("Force",
                    new SingleUnit[]{
                            new SingleUnit("newton (N)", 1.0, 0.0),
                            new SingleUnit("kilonewton", 0.001, 0.0),
                            new SingleUnit("ounce-force", 3.5969431019, 0.0),
                            new SingleUnit("pound-force", 0.22480894387, 0.0),
                            new SingleUnit("gram-force", 101.9716213, 0.0),
                            new SingleUnit("kilogram-force", 0.1019716, 0.0),
                            new SingleUnit("pond", 101.9716213, 0.0),
                            new SingleUnit("kip", 0.0002248, 0.0),
                            new SingleUnit("dyne", 100000, 0.0),
                            new SingleUnit("poundal", 7.2330140801, 0.0),
                            new SingleUnit("sthene", 0.001, 0.0),
                    }),

            new UnitCollection("Light",
                    new SingleUnit[]{
                            new SingleUnit("centimeter-candle", 1.0, 0.0),
                            new SingleUnit("flame", 232.2576, 0.0),
                            new SingleUnit("foot-candle", 929.0304, 0.0),
                            new SingleUnit("lumen/cm²", 1.0, 0.0),
                            new SingleUnit("lumen/ft²", 929.030, 0.0),
                            new SingleUnit("lux (lumen/m²)", 10000.0, 0.0),
                            new SingleUnit("meter-candle", 10000.0, 0.0),
                            new SingleUnit("nox", 10000000.0, 0.0),
                            new SingleUnit("phot", 1.0, 0.0),
                            new SingleUnit("watt/cm²", 0.00146412884334, 0.0),
                    }),

            new UnitCollection(new String[] { "Mass", "Weight" },
                    new SingleUnit[]{
                            new SingleUnit("carat", 5000.0, 0.0),
                            new SingleUnit("drachm", 564.3833911932866, 0.0),
                            new SingleUnit("earth mass (M<sub><small>⊕</small></sub>)", 1.67400398206061581e-25, 0.0),
                            new SingleUnit("grain (gr)", 15432.35835294143065, 0.0),
                            new SingleUnit("gram (g)", 1000.0, 0.0),
                            new SingleUnit("jupiter mass (M<sub><small><small>Ј</small></small></sub>)", 5.266979064014641e-28, 0.0),
                            new SingleUnit("hundredweight (cwt)", 0.0196841305522212, 0.0),
                            new SingleUnit("kilogram (kg)", 1.0, 0.0),
                            new SingleUnit("megagram (Mg)", 0.001, 0.0),
                            new SingleUnit("metric ton (t)", 0.001, 0.0),
                            new SingleUnit("microgram (µg)", 1.0e+9, 0.0),
                            new SingleUnit("milligram (mg)", 1.0e+6, 0.0),
                            new SingleUnit("ounce (oz)", 35.2739619806867227, 0.0),
                            new SingleUnit("pennyweight (dwt)", 643.01493137255961, 0.0),
                            new SingleUnit("pound (lb)", 2.20462262379292017, 0.0),
                            new SingleUnit("slug", 0.068521765843675, 0.0),
                            new SingleUnit("solar mass (M<sub><small>☉</small></sub>)", 5.0278543128934296e-31, 0.0),
                            new SingleUnit("stone", 0.157473044556637155, 0.0),
                            new SingleUnit("ton (tn)", 0.00110231131189646, 0.0),
                            new SingleUnit("troy ounce (oz)", 32.15074656862798, 0.0),
                    }),

            new UnitCollection("Power",
                    new SingleUnit[]{
                            new SingleUnit("BTU/hour", 3415.179027, 0.0),
                            new SingleUnit("BTU/minute", 56.91965045, 0.0),
                            new SingleUnit("BTU/second", 0.9486608408, 0.0),
                            new SingleUnit("calorie/sec", 239.0585106, 0.0),
                            new SingleUnit("horsepower", 1.34102209, 0.0),
                            new SingleUnit("kilowatt", 1.0, 0.0),
                            new SingleUnit("lb-ft/min", 44253.72896, 0.0),
                            new SingleUnit("lb-ft/sec", 737.5621493, 0.0),
                            new SingleUnit("megawatt", 0.001, 0.0),
                            new SingleUnit("watt", 1000.0, 0.0),
                    }),

            new UnitCollection("Pressure",
                    new SingleUnit[]{
                            new SingleUnit("atmosphere (atm)", 0.0680459639, 0.0),
                            new SingleUnit("centimeter mercury", 5.171493, 0.0),
                            new SingleUnit("dyne/cm²", 68947.57, 0.0),
                            new SingleUnit("inch mercury", 2.036021, 0.0),
                            new SingleUnit("inch water", 27.7075924014815, 0.0),
                            new SingleUnit("kilogram/cm²", 0.07030696, 0.0),
                            new SingleUnit("kilogram/m²", 703.0696, 0.0),
                            new SingleUnit("kilopascal (kPa)", 6.894757, 0.0),
                            new SingleUnit("megapascal (MPa)", 0.006894757, 0.0),
                            new SingleUnit("millibar", 68.94757, 0.0),
                            new SingleUnit("millimeter mercury", 51.71493, 0.0),
                            new SingleUnit("pascal (Pa)", 6894.757, 0.0),
                            new SingleUnit("pound/inch² (PSI)", 1.0, 0.0),
                            new SingleUnit("pound/foot²", 144.0, 0.0),
                            new SingleUnit("torr", 51.71493, 0.0),
                    }),

            new UnitCollection("Radiation dose",
                    new SingleUnit[]{
                            new SingleUnit("gray (Gy)", 1.0, 0.0),
                            new SingleUnit("rad", 100.0, 0.0),
                            new SingleUnit("röntgen (R)", 115.0, 0.0),
                    }),

            new UnitCollection("Radioactivity",
                    new SingleUnit[]{
                            new SingleUnit("becquerel (Bq)", 1.0, 0.0),
                            new SingleUnit("curie (Ci)", 2.7027027027027027e-11, 0.0),
                            new SingleUnit("rutherford (rd)", 0.000001, 0.0),
                    }),

            new UnitCollection("SI prefixes",
                    new SingleUnit[]{
                            new SingleUnit("yotta (Y)", 1.0e-24, 0.0),
                            new SingleUnit("zetta (Z)", 1.0e-21, 0.0),
                            new SingleUnit("exa (E)", 1.0e-18, 0.0),
                            new SingleUnit("peta (P)", 1.0e-15, 0.0),
                            new SingleUnit("tera (T)", 1.0e-12, 0.0),
                            new SingleUnit("giga (G)", 1.0e-9, 0.0),
                            new SingleUnit("mega (M)", 1.0e-6, 0.0),
                            new SingleUnit("kilo (k)", 0.001, 0.0),
                            new SingleUnit("hecto (h)", 0.01, 0.0),
                            new SingleUnit("deca (da)", 0.1, 0.0),
                            new SingleUnit("[one]", 1.0, 0.0),
                            new SingleUnit("deci (d)", 10.0, 0.0),
                            new SingleUnit("centi (c)", 100.0, 0.0),
                            new SingleUnit("milli (m)", 1000.0, 0.0),
                            new SingleUnit("micro (µ)", 1.0e+6, 0.0),
                            new SingleUnit("nano (n)", 1.0e+9, 0.0),
                            new SingleUnit("pico (p)", 1.0e+12, 0.0),
                            new SingleUnit("femto (f)", 1.0e+15, 0.0),
                            new SingleUnit("atto (a)", 1.0e+18, 0.0),
                            new SingleUnit("zepto (z)", 1.0e+21, 0.0),
                            new SingleUnit("yocto (y)", 1.0e+24, 0.0),
                    }),

            new UnitCollection("Speed",
                    new SingleUnit[]{
                            new SingleUnit("foot/sec", 3280.839895013123, 0.0),
                            new SingleUnit("foot/min", 196850.39370078738, 0.0),
                            new SingleUnit("foot/hour", 11811023.622047243, 0.0),
                            new SingleUnit("kilometer/sec", 1.0, 0.0),
                            new SingleUnit("kilometer/min", 60.0, 0.0),
                            new SingleUnit("kilometer/hour", 3600.0, 0.0),
                            new SingleUnit("knot", 1943.84449244060475, 0.0),
                            new SingleUnit("light speed (c)", 3.3356409519815205e-6, 0.0),
                            new SingleUnit("mach", 2.9385836, 0.0),
                            new SingleUnit("meter/sec", 1000.0, 0.0),
                            new SingleUnit("meter/min", 60000.0, 0.0),
                            new SingleUnit("meter/hour", 3.6e+6, 0.0),
                            new SingleUnit("mile/sec", 0.6213711922373339, 0.0),
                            new SingleUnit("mile/min", 37.282271534240034, 0.0),
                            new SingleUnit("mile/hour", 2236.936292054402, 0.0),

                    }),

            new UnitCollection("Temperature",
                    new SingleUnit[]{
                            new SingleUnit("celsius (°C)", 1.0, 0.0),
                            new SingleUnit("delisle (°De)", -1.5, 150.0),
                            new SingleUnit("fahrenheit (°F)", 1.8, 32.0),
                            new SingleUnit("kelvin (K)", 1.0, 273.15),
                            new SingleUnit("newton (°N)", 0.33, 0.0),
                            new SingleUnit("rankine (°Ra)", 1.8, 491.67),
                            new SingleUnit("réaumur (°R)", 0.8, 0.0),
                            new SingleUnit("rømer (°Rø)", 0.525, 7.5),
                    }),

            new UnitCollection("Time",
                    new SingleUnit[]{
                            new SingleUnit("century", 2.73790933079226e-5, 0.0),
                            new SingleUnit("day", 1.0, 0.0),
                            new SingleUnit("decade", 0.000273790933079226, 0.0),
                            new SingleUnit("eon", 2.73790933079226e-9, 0.0),
                            new SingleUnit("fortnight", 0.071428571428571429, 0.0),
                            new SingleUnit("hour", 24.0, 0.0),
                            new SingleUnit("microsecond (µs)", 8.64e+10, 0.0),
                            new SingleUnit("millennium", 2.73790933079226e-6, 0.0),
                            new SingleUnit("millisecond (ms)", 8.64e+7, 0.0),
                            new SingleUnit("minute", 1440.0, 0.0),
                            new SingleUnit("month (30 days)", 0.0333333333333333, 0.0),
                            new SingleUnit("nanosecond", 8.64e+13, 0.0),
                            new SingleUnit("second (s)", 86400.0, 0.0),
                            new SingleUnit("week", 0.14285714285714286, 0.0),
                            new SingleUnit("year", 0.00273790933079226, 0.0),

                    }),

            new UnitCollection("Torque",
                    new SingleUnit[]{
                            new SingleUnit("newton meter", 1.0, 0.0),
                            new SingleUnit("newton cm", 100.0, 0.0),
                            new SingleUnit("newton mm", 1000.0, 0.0),
                            new SingleUnit("dyne meter", 100000.0, 0.0),
                            new SingleUnit("dyne cm", 10000000.0, 0.0),
                            new SingleUnit("dyne mm", 100000000.0, 0.0),
                            new SingleUnit("kg-force meter", 0.10197162129779, 0.0),
                            new SingleUnit("kg-force cm", 10.197162129779, 0.0),
                            new SingleUnit("kg-force mm", 101.97162129779, 0.0),
                            new SingleUnit("gram-force meter", 101.97162129779, 0.0),
                            new SingleUnit("gram-force cm", 10197.162129779, 0.0),
                            new SingleUnit("gram-force mm", 101971.62129779, 0.0),
                            new SingleUnit("oz-force foot", 11.800994078, 0.0),
                            new SingleUnit("oz-force inch", 141.611928936, 0.0),
                            new SingleUnit("lb-force foot", 0.737562121, 0.0),
                            new SingleUnit("lb-force inch", 8.850745454, 0.0),

                    }),

            new UnitCollection("Volume",
                    new SingleUnit[]{
                            new SingleUnit("centimeter³", 1000.0, 0.0),
                            new SingleUnit("meter³", 0.001, 0.0),
                            new SingleUnit("dram", 270.51218161474397756, 0.0),
                            new SingleUnit("yard³", 0.0013079506193144, 0.0),
                            new SingleUnit("foot³", 0.0353146667214886, 0.0),
                            new SingleUnit("inch³", 61.023744094732284, 0.0),
                            new SingleUnit("liter", 1.0, 0.0),
                            new SingleUnit("milliliter", 1000.0, 0.0),
                            new SingleUnit("gallon (US)", 0.264172052358148416, 0.0),
                            new SingleUnit("quart (US)", 1.05668820943259366, 0.0),
                            new SingleUnit("pint (US)", 2.11337641886518732, 0.0),
                            new SingleUnit("ounce (US)", 33.81402270184299719, 0.0),
                            new SingleUnit("gallon (Imp)", 0.21996924829908779, 0.0),
                            new SingleUnit("quart (Imp)", 0.87987699319635115, 0.0),
                            new SingleUnit("pint (Imp)", 1.7597539863927023, 0.0),
                            new SingleUnit("ounce (Imp)", 35.195079727854046, 0.0),
                            new SingleUnit("gill (Imp)", 7.0390159455708092, 0.0),
                            new SingleUnit("cup", 4.22675283773037465, 0.0),
                            new SingleUnit("tablespoon", 67.62804540368599439, 0.0),
                            new SingleUnit("teaspoon", 202.88413621105798317, 0.0),
                            new SingleUnit("scruple (℈)", 844.681913473253688, 0.0),
                            new SingleUnit("drachm (ʒ)", 281.560637822832368, 0.0),
                            new SingleUnit("minim (♏)", 16893.63826937945525, 0.0),
                    }),

    };

}
