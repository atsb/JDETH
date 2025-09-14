package org.deth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class ThingDefs.
 * <p>Auto-generated documentation stub.</p>
 */
public final class ThingDefs {

    public static final List<ThingDef> ALL = new ArrayList<>();
    public static final Map<Integer, ThingDef> BY_ID = new HashMap<>();

    static {

        add(1, "Player 1 start", Category.STARTS);
        add(2, "Player 2 start", Category.STARTS);
        add(3, "Player 3 start", Category.STARTS);
        add(4, "Player 4 start", Category.STARTS);
        add(5, "Blue keycard", Category.KEYS);
        add(6, "Yellow keycard", Category.KEYS);
        add(7, "Spiderdemon", Category.MONSTERS);
        add(8, "Backpack", Category.AMMO);
        add(9, "Shotgun guy", Category.MONSTERS);
        add(10, "Bloody mess", Category.DECOR);
        add(11, "Deathmatch start", Category.STARTS);
        add(12, "Bloody mess (2)", Category.DECOR);
        add(13, "Red keycard", Category.KEYS);
        add(14, "Teleport landing", Category.TELEPORTS);
        add(15, "Dead player", Category.DECOR);
        add(16, "Cyberdemon", Category.MONSTERS);
        add(17, "Energy cell pack", Category.AMMO);
        add(18, "Dead former human", Category.DECOR);
        add(19, "Dead former sergeant", Category.DECOR);
        add(20, "Dead imp", Category.DECOR);
        add(21, "Dead demon", Category.DECOR);
        add(22, "Dead cacodemon", Category.DECOR);
        add(23, "Dead lost soul (invisible)", Category.DECOR);
        add(24, "Pool of blood and flesh", Category.DECOR);
        add(25, "Impaled human", Category.DECOR);
        add(26, "Twitching impaled human", Category.DECOR);
        add(27, "Skull on a pole", Category.DECOR);
        add(28, "Five skulls \"shish kebab\"", Category.DECOR);
        add(29, "Pile of skulls and candles", Category.DECOR);
        add(30, "Tall green pillar", Category.DECOR);
        add(31, "Short green pillar", Category.DECOR);
        add(32, "Tall red pillar", Category.DECOR);
        add(33, "Short red pillar", Category.DECOR);
        add(34, "Candle", Category.LIGHTS);
        add(35, "Candelabra", Category.LIGHTS);
        add(36, "Short green pillar with beating heart", Category.DECOR);
        add(37, "Short red pillar with skull", Category.DECOR);
        add(38, "Red skull key", Category.KEYS);
        add(39, "Yellow skull key", Category.KEYS);
        add(40, "Blue skull key", Category.KEYS);
        add(41, "Evil eye", Category.DECOR);
        add(42, "Floating skull", Category.DECOR);
        add(43, "Burnt tree", Category.DECOR);
        add(44, "Tall blue firestick", Category.LIGHTS);
        add(45, "Tall green firestick", Category.LIGHTS);
        add(46, "Tall red firestick", Category.LIGHTS);
        add(47, "Brown stump", Category.DECOR);
        add(48, "Tall techno column", Category.DECOR);
        add(49, "Hanging victim, twitching (ceiling)", Category.DECOR);
        add(50, "Hanging victim, arms out (ceiling)", Category.DECOR);
        add(51, "Hanging victim, one-legged (ceiling)", Category.DECOR);
        add(52, "Hanging pair of legs (ceiling)", Category.DECOR);
        add(53, "Hanging leg (ceiling)", Category.DECOR);
        add(54, "Large brown tree", Category.DECOR);
        add(55, "Short blue firestick", Category.LIGHTS);
        add(56, "Short green firestick", Category.LIGHTS);
        add(57, "Short red firestick", Category.LIGHTS);
        add(58, "Spectre", Category.MONSTERS);
        add(59, "Hanging victim, arms out (swung)", Category.DECOR);
        add(60, "Hanging pair of legs (swung)", Category.DECOR);
        add(61, "Hanging victim, one-legged (swung)", Category.DECOR);
        add(62, "Hanging leg (swung)", Category.DECOR);
        add(63, "Hanging victim, twitching (swung)", Category.DECOR);
        add(64, "Arch-vile", Category.MONSTERS);
        add(65, "Heavy weapon dude", Category.MONSTERS);
        add(66, "Revenant", Category.MONSTERS);
        add(67, "Mancubus", Category.MONSTERS);
        add(68, "Arachnotron", Category.MONSTERS);
        add(69, "Hell knight", Category.MONSTERS);
        add(70, "Burning barrel", Category.DECOR);
        add(71, "Pain elemental", Category.MONSTERS);
        add(72, "Commander Keen", Category.SPECIAL);
        add(73, "Hanging victim, guts removed", Category.DECOR);
        add(74, "Hanging victim, guts and brain removed", Category.DECOR);
        add(75, "Hanging torso, looking down", Category.DECOR);
        add(76, "Hanging torso, open skull", Category.DECOR);
        add(77, "Hanging torso, looking up", Category.DECOR);
        add(78, "Hanging torso, brain removed", Category.DECOR);
        add(79, "Pool of blood", Category.DECOR);
        add(80, "Pool of blood (narrow)", Category.DECOR);
        add(81, "Pool of brains", Category.DECOR);
        add(82, "Super shotgun", Category.WEAPONS);
        add(83, "Megasphere", Category.POWERUPS);
        add(84, "Wolfenstein SS", Category.MONSTERS);
        add(85, "Tall techno floor lamp", Category.LIGHTS);
        add(86, "Short techno floor lamp", Category.LIGHTS);
        add(87, "Spawn spot", Category.SPECIAL);
        add(88, "Romero's head", Category.SPECIAL);
        add(89, "Monster spawner", Category.SPECIAL);


        add(3001, "Imp", Category.MONSTERS);
        add(3002, "Demon", Category.MONSTERS);
        add(3003, "Baron of Hell", Category.MONSTERS);
        add(3004, "Former human", Category.MONSTERS);
        add(3005, "Cacodemon", Category.MONSTERS);
        add(3006, "Lost soul", Category.MONSTERS);


        add(2001, "Shotgun", Category.WEAPONS);
        add(2002, "Chaingun", Category.WEAPONS);
        add(2003, "Rocket launcher", Category.WEAPONS);
        add(2004, "Plasma gun", Category.WEAPONS);
        add(2005, "Chainsaw", Category.WEAPONS);
        add(2006, "BFG9000", Category.WEAPONS);


        add(2007, "Clip", Category.AMMO);
        add(2008, "4 shotgun shells", Category.AMMO);
        add(2010, "Rocket", Category.AMMO);
        add(2046, "Box of rockets", Category.AMMO);
        add(2047, "Energy cell", Category.AMMO);
        add(2048, "Box of shotgun shells", Category.AMMO);
        add(2049, "Box of bullets", Category.AMMO);
        add(17, "Energy cell pack", Category.AMMO);


        add(2011, "Stimpack", Category.HEALTH_ARMOR);
        add(2012, "Medikit", Category.HEALTH_ARMOR);
        add(2013, "Supercharge", Category.HEALTH_ARMOR);
        add(2014, "Health bonus", Category.HEALTH_ARMOR);
        add(2015, "Armor bonus", Category.HEALTH_ARMOR);
        add(2018, "Armor (green)", Category.HEALTH_ARMOR);
        add(2019, "Megaarmor (blue)", Category.HEALTH_ARMOR);
        add(83, "Megasphere", Category.POWERUPS);
        add(2022, "Invulnerability", Category.POWERUPS);
        add(2023, "Berserk", Category.POWERUPS);
        add(2024, "Partial invisibility", Category.POWERUPS);
        add(2025, "Radiation shielding suit", Category.POWERUPS);
        add(2026, "Computer area map", Category.POWERUPS);
        add(2045, "Light amplification visor", Category.POWERUPS);
        add(8, "Backpack", Category.AMMO);


        add(5, "Blue keycard", Category.KEYS);
        add(6, "Yellow keycard", Category.KEYS);
        add(13, "Red keycard", Category.KEYS);
        add(40, "Blue skull key", Category.KEYS);
        add(39, "Yellow skull key", Category.KEYS);
        add(38, "Red skull key", Category.KEYS);


        add(2028, "Floor lamp", Category.LIGHTS);
        add(2035, "Exploding barrel", Category.DECOR);
    }

    static {
        for (ThingDef d : ALL) BY_ID.put(d.id, d);
    }

    /**
     * Method ThingDefs.
     *
     * @return result
     */
    private ThingDefs() {
    }

    /**
     * Method add.
     *
     * @param id   parameter
     * @param name parameter
     * @param c    parameter
     */
    private static void add(int id, String name, Category c) {
        ALL.add(new ThingDef(id, name, c));
    }

    /**
     * Method get.
     *
     * @param id parameter
     * @return result
     */
    public static ThingDef get(int id) {
        return BY_ID.get(id);
    }

    /**
     * Method filterBy.
     *
     * @param c parameter
     * @return result
     */
    public static List<ThingDef> filterBy(Category c) {
        if (c == null) return ALL;
        List<ThingDef> out = new ArrayList<>();
        for (ThingDef d : ALL) if (d.category == c) out.add(d);
        return out;
    }


    /**
     * Enum Category.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Category {
        STARTS, KEYS, WEAPONS, AMMO, HEALTH_ARMOR, POWERUPS, MONSTERS, DECOR, LIGHTS, SPECIAL, TELEPORTS, OTHER
    }

    /**
         * Class ThingDef.
         * <p>Auto-generated documentation stub.</p>
         */
        public record ThingDef(int id, String name, Category category) {
        /**
         * Method ThingDef.
         *
         * @param id       parameter
         * @param name     parameter
         * @param category parameter
         * @return result
         */
        public ThingDef {
        }

            @Override
            public String toString() {
                return id + "  " + name;
            }
        }
}
