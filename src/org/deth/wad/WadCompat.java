package org.deth.wad;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Class WadCompat.
 * <p>Auto-generated documentation stub.</p>
 */
public final class WadCompat {
    /**
     * Method WadCompat.
     *
     * @return result
     */
    private WadCompat() {
    }


    /**
     * Method readLumpOrNull.
     *
     * @param wr       parameter
     * @param lumpName parameter
     * @return result
     */
    public static byte[] readLumpOrNull(WadReader wr, String lumpName) {
        if (wr == null || lumpName == null) return null;


        String[] candidates = {
                "readLump", "readLumpBytes", "getLumpBytes", "getLump", "readBytesForLump"
        };
        /**
         * Constructor for for.
         * @param candidates parameter
         */
        for (String m : candidates) {
            try {
                var meth = wr.getClass().getMethod(m, String.class);
                Object out = meth.invoke(wr, lumpName);
                if (out instanceof byte[]) return (byte[]) out;
            } catch (Exception ignored) {
            }
        }


        File wadFile = null;
        try {
            var gm = wr.getClass().getMethod("getFile");
            Object f = gm.invoke(wr);
            if (f instanceof File) wadFile = (File) f;
        } catch (Exception ignored) {
        }
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (wadFile == null) {
            try {
                var gm = wr.getClass().getMethod("getPath");
                Object p = gm.invoke(wr);
                if (p instanceof String) wadFile = new File((String) p);
            } catch (Exception ignored) {
            }
        }


        if (wadFile != null && wadFile.isFile()) {
            try {
                return readLumpFromFile(wadFile, lumpName);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * Method readLumpFromFile.
     *
     * @param wad      parameter
     * @param lumpName parameter
     * @return result
     * @throws IOException on error
     */
    private static byte[] readLumpFromFile(File wad, String lumpName) throws IOException {
        Map<String, Lump> dir = readWadDirectory(wad);
        Lump l = dir.get(lumpName.toUpperCase(Locale.ROOT));
        if (l == null || l.size <= 0) return null;
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            byte[] data = new byte[l.size];
            raf.seek(l.offset);
            int n = raf.read(data);
            if (n != l.size) throw new IOException("Short read for lump " + lumpName);
            return data;
        }
    }

    private static Map<String, Lump> readWadDirectory(File wad) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            byte[] hdr = new byte[12];
            if (raf.read(hdr) != 12) throw new IOException("Bad WAD header");
            String ident = new String(hdr, 0, 4, StandardCharsets.US_ASCII);
            if (!"IWAD".equals(ident) && !"PWAD".equals(ident)) throw new IOException("Not a WAD");

            int numLumps = le32(hdr, 4);
            int dirOff = le32(hdr, 8);
            if (numLumps < 0 || dirOff < 0) throw new IOException("Corrupt directory");

            Map<String, Lump> map = new HashMap<>();
            raf.seek(dirOff);
            byte[] entry = new byte[16];
            /**
             * Constructor for for.
             * @param i parameter
             */
            for (int i = 0; i < numLumps; i++) {
                if (raf.read(entry) != 16) throw new IOException("Short directory");
                int off = le32(entry, 0);
                int size = le32(entry, 4);
                String name = name8(entry, 8);
                if (!name.isEmpty()) map.put(name, new Lump(off, size));
            }
            return map;
        }
    }

    /**
     * Method le32.
     *
     * @param b   parameter
     * @param off parameter
     * @return result
     */
    private static int le32(byte[] b, int off) {
        return (b[off] & 0xFF) |
                ((b[off + 1] & 0xFF) << 8) |
                ((b[off + 2] & 0xFF) << 16) |
                ((b[off + 3] & 0xFF) << 24);
    }

    /**
     * Method name8.
     *
     * @param b   parameter
     * @param off parameter
     * @return result
     */
    private static String name8(byte[] b, int off) {
        int end = off + 8;
        int len = 0;
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = off; i < end; i++) {
            if (b[i] == 0) break;
            len++;
        }
        if (len <= 0) return "";
        return new String(b, off, len, StandardCharsets.US_ASCII).trim().toUpperCase(Locale.ROOT);
    }

    /**
         * Class Lump.
         * <p>Auto-generated documentation stub.</p>
         */
        private record Lump(int offset, int size) {
        /**
         * Constructor for Lump.
         *
         * @param offset parameter
         * @param size   parameter
         */
        private Lump {
        }
        }
}
