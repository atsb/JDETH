package org.deth;

import java.util.*;


/**
 * Class ThingCatalog.
 * <p>Auto-generated documentation stub.</p>
 */
public final class ThingCatalog {

    public static final List<Def> ALL;
    public static final Map<Integer, Def> BY_EDNUM;
    public static final String[] CATEGORIES = new String[]{
            "All",
            "Monsters",
            "Weapons",
            "Ammo",
            "Health/Armor",
            "Powerups",
            "Keys",
            "Starts/Teleports",
            "Decor/Props",
            "Decor/Props (Ceiling)",
            "Boss/Special",
            "Other"
    };

    static {
        List<Def> L = new ArrayList<>(150);


        L.add(d(1, "Player 1 start", "Starts/Teleports"));
        L.add(d(2, "Player 2 start", "Starts/Teleports"));
        L.add(d(3, "Player 3 start", "Starts/Teleports"));
        L.add(d(4, "Player 4 start", "Starts/Teleports"));
        L.add(d(11, "Deathmatch start", "Starts/Teleports"));
        L.add(d(14, "Teleport landing", "Starts/Teleports"));


        L.add(d(5, "Blue keycard", "Keys"));
        L.add(d(6, "Yellow keycard", "Keys"));
        L.add(d(13, "Red keycard", "Keys"));
        L.add(d(38, "Red skull key", "Keys"));
        L.add(d(39, "Yellow skull key", "Keys"));
        L.add(d(40, "Blue skull key", "Keys"));


        L.add(d(82, "Super shotgun", "Weapons"));
        L.add(d(2001, "Shotgun", "Weapons"));
        L.add(d(2002, "Chaingun", "Weapons"));
        L.add(d(2003, "Rocket launcher", "Weapons"));
        L.add(d(2004, "Plasma gun", "Weapons"));
        L.add(d(2005, "Chainsaw", "Weapons"));
        L.add(d(2006, "BFG9000", "Weapons"));


        L.add(d(8, "Backpack", "Ammo"));
        L.add(d(17, "Energy cell pack", "Ammo"));
        L.add(d(2007, "Clip", "Ammo"));
        L.add(d(2008, "4 shotgun shells", "Ammo"));
        L.add(d(2010, "Rocket", "Ammo"));
        L.add(d(2046, "Box of rockets", "Ammo"));
        L.add(d(2047, "Energy cell", "Ammo"));


        L.add(d(2011, "Stimpack", "Health/Armor"));
        L.add(d(2012, "Medikit", "Health/Armor"));
        L.add(d(2013, "Supercharge", "Health/Armor"));
        L.add(d(2014, "Health bonus", "Health/Armor"));
        L.add(d(2015, "Armor bonus", "Health/Armor"));
        L.add(d(2018, "Armor (green)", "Health/Armor"));
        L.add(d(2019, "Megaarmor (blue)", "Health/Armor"));


        L.add(d(2022, "Invulnerability", "Powerups"));
        L.add(d(2023, "Berserk", "Powerups"));
        L.add(d(2024, "Partial invisibility", "Powerups"));
        L.add(d(2025, "Radiation suit", "Powerups"));
        L.add(d(2026, "Computer area map", "Powerups"));
        L.add(d(2045, "Light amp visor", "Powerups"));
        L.add(d(83, "Megasphere", "Powerups"));


        L.add(d(9, "Shotgun guy", "Monsters"));
        L.add(d(58, "Spectre", "Monsters"));
        L.add(d(64, "Arch-vile", "Monsters"));
        L.add(d(65, "Heavy weapon dude", "Monsters"));
        L.add(d(66, "Revenant", "Monsters"));
        L.add(d(67, "Mancubus", "Monsters"));
        L.add(d(68, "Arachnotron", "Monsters"));
        L.add(d(69, "Hell knight", "Monsters"));
        L.add(d(71, "Pain elemental", "Monsters"));


        L.add(d(3004, "Zombieman (former human)", "Monsters"));
        L.add(d(65, "Heavy weapon dude", "Monsters"));
        L.add(d(3001, "Imp", "Monsters"));
        L.add(d(3002, "Demon", "Monsters"));
        L.add(d(3006, "Spectre (alt)", "Monsters"));
        L.add(d(3005, "Cacodemon", "Monsters"));
        L.add(d(3003, "Baron of Hell", "Monsters"));
        L.add(d(3009, "Lost soul", "Monsters"));


        L.add(d(7, "Spiderdemon", "Boss/Special"));
        L.add(d(16, "Cyberdemon", "Boss/Special"));
        L.add(d(84, "Wolfenstein SS", "Boss/Special"));
        L.add(d(88, "Romero's head (Icon of Sin target)", "Boss/Special"));
        L.add(d(89, "Monster spawner (Boss brain)", "Boss/Special"));
        L.add(d(57, "Spawn spot (cube target)", "Boss/Special"));
        L.add(d(72, "Commander Keen", "Boss/Special"));


        L.add(d(2035, "Exploding barrel", "Decor/Props"));
        L.add(d(70, "Burning barrel", "Decor/Props"));
        L.add(d(2028, "Floor lamp", "Decor/Props"));
        L.add(d(85, "Tall techno floor lamp", "Decor/Props"));
        L.add(d(86, "Short techno floor lamp", "Decor/Props"));


        L.add(d(10, "Bloody mess 1", "Decor/Props"));
        L.add(d(12, "Bloody mess 2", "Decor/Props"));
        L.add(d(15, "Dead player", "Decor/Props"));
        L.add(d(18, "Dead former human", "Decor/Props"));
        L.add(d(19, "Dead former sergeant", "Decor/Props"));
        L.add(d(20, "Dead imp", "Decor/Props"));
        L.add(d(21, "Dead demon", "Decor/Props"));
        L.add(d(22, "Dead cacodemon", "Decor/Props"));
        L.add(d(23, "Dead lost soul (invisible)", "Decor/Props"));
        L.add(d(24, "Pool of blood and flesh", "Decor/Props"));
        L.add(d(25, "Impaled human", "Decor/Props"));
        L.add(d(26, "Twitching impaled human", "Decor/Props"));
        L.add(d(27, "Skull on a pole", "Decor/Props"));
        L.add(d(28, "Five skulls 'shish kebab'", "Decor/Props"));
        L.add(d(29, "Pile of skulls and candles", "Decor/Props"));
        L.add(d(30, "Tall green pillar", "Decor/Props"));
        L.add(d(31, "Short green pillar", "Decor/Props"));
        L.add(d(32, "Tall red pillar", "Decor/Props"));
        L.add(d(33, "Short red pillar", "Decor/Props"));
        L.add(d(34, "Candle", "Decor/Props"));
        L.add(d(35, "Candelabra", "Decor/Props"));
        L.add(d(36, "Short green pillar w/ heart", "Decor/Props"));
        L.add(d(37, "Short red pillar w/ skull", "Decor/Props"));
        L.add(d(41, "Evil eye", "Decor/Props"));
        L.add(d(42, "Floating skull", "Decor/Props"));
        L.add(d(43, "Burnt tree", "Decor/Props"));
        L.add(d(44, "Tall blue firestick", "Decor/Props"));
        L.add(d(45, "Tall green firestick", "Decor/Props"));
        L.add(d(46, "Tall red firestick", "Decor/Props"));
        L.add(d(47, "Brown stump", "Decor/Props"));
        L.add(d(48, "Tall techno column", "Decor/Props"));
        L.add(d(55, "Short blue firestick", "Decor/Props"));
        L.add(d(56, "Short green firestick", "Decor/Props"));
        L.add(d(57, "Short red firestick", "Decor/Props"));
        L.add(d(49, "Hanging victim, twitching", "Decor/Props (Ceiling)"));
        L.add(d(50, "Hanging victim, arms out", "Decor/Props (Ceiling)"));
        L.add(d(51, "Hanging victim, one-legged", "Decor/Props (Ceiling)"));
        L.add(d(52, "Hanging pair of legs", "Decor/Props (Ceiling)"));
        L.add(d(53, "Hanging leg", "Decor/Props (Ceiling)"));
        L.add(d(59, "Hanging victim, arms out (alt)", "Decor/Props (Ceiling)"));
        L.add(d(60, "Hanging pair of legs (alt)", "Decor/Props (Ceiling)"));
        L.add(d(61, "Hanging victim, one-legged (alt)", "Decor/Props (Ceiling)"));
        L.add(d(62, "Hanging leg (alt)", "Decor/Props (Ceiling)"));
        L.add(d(63, "Hanging victim, twitching (alt)", "Decor/Props (Ceiling)"));
        L.add(d(79, "Pool of blood", "Decor/Props"));
        L.add(d(80, "Pool of blood (alt)", "Decor/Props"));
        L.add(d(81, "Pool of brains", "Decor/Props"));


        ALL = Collections.unmodifiableList(L);

        Map<Integer, Def> map = new HashMap<>();
        for (Def def : L) map.put(def.ednum, def);
        BY_EDNUM = Collections.unmodifiableMap(map);
    }

    /**
     * Method d.
     *
     * @param n    parameter
     * @param name parameter
     * @param cat  parameter
     * @return result
     */
    private static Def d(int n, String name, String cat) {
        return new Def(n, name, cat);
    }

    /**
     * Method byEdNum.
     *
     * @param ednum parameter
     * @return result
     */
    public static Def byEdNum(int ednum) {
        return BY_EDNUM.get(ednum);
    }

    /**
         * Class Def.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Def(int ednum, String name, String category) {
        /**
         * Method Def.
         *
         * @param ednum    parameter
         * @param name     parameter
         * @param category parameter
         * @return result
         */
        public Def {
        }

            @Override
            public String toString() {
                return ednum + " — " + name;
            }
        }
}
