package org.deth.wad;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


/**
 * Class SpriteCache.
 * <p>Auto-generated documentation stub.</p>
 */
public final class SpriteCache {

    private static final SpriteCache INSTANCE = new SpriteCache();
    private static final Map<Integer, String> TYPE_TO_PREFIX = buildTypeMap();
    private final Map<String, BufferedImage> firstByPrefix = new HashMap<>();


    /**
     * Method SpriteCache.
     *
     * @return result
     */
    private SpriteCache() {
    }

    /**
     * Method get.
     *
     * @return result
     */
    public static SpriteCache get() {
        return INSTANCE;
    }

    private static Map<Integer, String> buildTypeMap() {
        Map<Integer, String> m = new HashMap<>();


        m.put(1, "PLAY");
        m.put(2, "PLAY");
        m.put(3, "PLAY");
        m.put(4, "PLAY");
        m.put(11, "TFOG");
        m.put(14, "TFOG");
        m.put(87, "TFOG");
        m.put(89, "TFOG");
        m.put(88, "BBRN");


        m.put(5, "BKEY");
        m.put(13, "RKEY");
        m.put(6, "YKEY");
        m.put(40, "BSKU");
        m.put(38, "RSKU");
        m.put(39, "YSKU");


        m.put(2001, "SHOT");
        m.put(2002, "MGUN");
        m.put(2003, "LAUN");
        m.put(2004, "PLAS");
        m.put(2005, "CSAW");
        m.put(2006, "BFUG");
        m.put(82, "SGN2");


        m.put(2007, "CLIP");
        m.put(2008, "SHEL");
        m.put(2010, "ROCK");
        m.put(2048, "AMMO");
        m.put(2046, "BROK");
        m.put(2049, "SBOX");
        m.put(2047, "CELL");
        m.put(17, "CELP");


        m.put(2015, "BON2");
        m.put(2014, "BON1");
        m.put(2013, "SOUL");
        m.put(2023, "PSTR");
        m.put(2026, "PMAP");
        m.put(2022, "PINV");
        m.put(2024, "PINS");
        m.put(2045, "PVIS");
        m.put(83, "MEGA");


        m.put(2011, "STIM");
        m.put(2012, "MEDI");
        m.put(2018, "ARM1");
        m.put(2019, "ARM2");
        m.put(2025, "SUIT");
        m.put(8, "BPAK");


        m.put(3004, "POSS");
        m.put(9, "SPOS");
        m.put(65, "CPOS");
        m.put(3001, "TROO");
        m.put(3002, "SARG");
        m.put(58, "SARG");
        m.put(3006, "SKUL");
        m.put(3005, "HEAD");
        m.put(69, "BOS2");
        m.put(3003, "BOSS");
        m.put(68, "FATT");
        m.put(66, "SKEL");
        m.put(67, "BSPI");
        m.put(64, "VILE");
        m.put(16, "CYBR");
        m.put(7, "SPID");
        m.put(71, "PAIN");
        m.put(72, "KEEN");
        m.put(84, "SSWV");


        m.put(47, "SMIT");
        m.put(70, "FCAN");
        m.put(43, "TRE1");
        m.put(35, "CBRA");
        m.put(41, "CEYE");
        m.put(2035, "BAR1");
        m.put(28, "POL2");
        m.put(42, "FSKU");
        m.put(2028, "COLU");
        m.put(53, "GOR5");
        m.put(52, "GOR4");
        m.put(78, "HDB6");
        m.put(75, "HDB3");
        m.put(77, "HDB5");
        m.put(76, "HDB4");
        m.put(50, "GOR2");
        m.put(74, "HDB2");
        m.put(73, "HDB1");
        m.put(51, "GOR3");
        m.put(49, "GOR1");
        m.put(25, "POL1");
        m.put(54, "TRE2");
        m.put(29, "POL3");
        m.put(55, "SMBT");
        m.put(56, "SMGT");
        m.put(31, "COL2");
        m.put(36, "COL5");
        m.put(57, "SMRT");
        m.put(33, "COL4");
        m.put(37, "COL6");
        m.put(86, "TLP2");
        m.put(27, "POL4");
        m.put(44, "TBLU");
        m.put(45, "TGRN");
        m.put(30, "COL1");
        m.put(46, "TRED");
        m.put(32, "COL3");
        m.put(48, "ELEC");
        m.put(85, "TLMP");
        m.put(26, "POL6");


        m.put(10, "PLAY");
        m.put(12, "PLAY");
        m.put(34, "CAND");
        m.put(22, "HEAD");
        m.put(21, "SARG");
        m.put(18, "POSS");
        m.put(19, "SPOS");
        m.put(20, "TROO");
        m.put(23, "SKUL");
        m.put(15, "PLAY");
        m.put(62, "GOR5");
        m.put(60, "GOR4");
        m.put(59, "GOR2");
        m.put(61, "GOR3");
        m.put(63, "GOR1");
        m.put(79, "POB1");
        m.put(80, "POB2");
        m.put(24, "POL5");
        m.put(81, "BRS1");

        return m;
    }

    /**
     * Method clear.
     */
    public void clear() {
        firstByPrefix.clear();
    }

    /**
     * Method indexSpriteLump.
     *
     * @param lumpName parameter
     * @param img      parameter
     */
    public void indexSpriteLump(String lumpName, BufferedImage img) {
        if (lumpName == null || img == null) return;
        String name = lumpName.trim().toUpperCase();
        if (name.length() < 4) return;
        String pfx = name.substring(0, 4);

        firstByPrefix.putIfAbsent(pfx, img);
    }

    /**
     * Method getThingSprite.
     *
     * @param doomEdNum       parameter
     * @param angleDegIgnored parameter
     * @return result
     */
    public BufferedImage getThingSprite(int doomEdNum, int angleDegIgnored) {
        String pfx = TYPE_TO_PREFIX.get(doomEdNum);
        if (pfx == null) return null;

        BufferedImage hit = firstByPrefix.get(pfx);
        if (hit != null) return hit;

        TextureCache tc = TextureCache.get();
        BufferedImage img = tc.getTextureImage(pfx + "A0");
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (img == null) {
            /**
             * Constructor for for.
             * @param i parameter
             */
            for (int i = 1; i <= 8 && img == null; i++) {
                img = tc.getTextureImage(pfx + "A" + i);
            }
        }
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (img != null) {
            firstByPrefix.put(pfx, img);
        }
        return img;
    }

    /**
     * Method getSpriteByLump.
     *
     * @param lumpName parameter
     * @return result
     */
    public BufferedImage getSpriteByLump(String lumpName) {
        return (lumpName == null) ? null : TextureCache.get().getTextureImage(lumpName);
    }
}
