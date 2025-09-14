package org.deth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Class Prefs.
 * <p>Auto-generated documentation stub.</p>
 */
public class Prefs {
    private static final String ROOT = System.getProperty("user.home") + File.separator + ".deth-java";
    private static final String FILE = ROOT + File.separator + "config.properties";
    private final Properties p = new Properties();

    /**
     * Method Prefs.
     *
     * @return result
     */
    public Prefs() {
        load();
    }

    /**
     * Method load.
     */
    private void load() {
        try {
            File dir = new File(ROOT);
            if (!dir.exists()) dir.mkdirs();
            File f = new File(FILE);
            if (f.exists()) try (FileInputStream in = new FileInputStream(f)) {
                p.load(in);
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Method save.
     */
    public void save() {
        try (FileOutputStream out = new FileOutputStream(FILE)) {
            p.store(out, "DETH-Java preferences");
        } catch (IOException ignored) {
        }
    }

    /**
     * Method get.
     *
     * @param k   parameter
     * @param def parameter
     * @return result
     */
    public String get(String k, String def) {
        return p.getProperty(k, def);
    }

    /**
     * Method set.
     *
     * @param k parameter
     * @param v parameter
     */
    public void set(String k, String v) {
        p.setProperty(k, v);
    }

    /**
     * Method getInt.
     *
     * @param k   parameter
     * @param def parameter
     * @return result
     */
    public int getInt(String k, int def) {
        try {
            return Integer.parseInt(p.getProperty(k));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Method setInt.
     *
     * @param k parameter
     * @param v parameter
     */
    public void setInt(String k, int v) {
        p.setProperty(k, Integer.toString(v));
    }
}
