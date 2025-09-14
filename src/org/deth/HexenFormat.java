package org.deth;

import java.util.prefs.Preferences;

/**
 * Class HexenFormat.
 * <p>Auto-generated documentation stub.</p>
 */
public final class HexenFormat {
    private static final String PREF_KEY = "map.format";

    /**
     * Method HexenFormat.
     *
     * @return result
     */
    private HexenFormat() {
    }

    /**
     * Method get.
     *
     * @return result
     */
    public static Format get() {
        String s = Preferences.userNodeForPackage(HexenFormat.class).get(PREF_KEY, "DOOM");
        return "HEXEN".equalsIgnoreCase(s) ? Format.HEXEN : Format.DOOM;
    }

    /**
     * Method set.
     *
     * @param f parameter
     */
    public static void set(Format f) {
        Preferences.userNodeForPackage(HexenFormat.class).put(PREF_KEY, f == null ? "DOOM" : f.name());
    }

    /**
     * Enum Format.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Format {DOOM, HEXEN}
}
