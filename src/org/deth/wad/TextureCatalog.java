package org.deth.wad;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Class TextureCatalog.
 * <p>Auto-generated documentation stub.</p>
 */
public final class TextureCatalog implements Closeable {
    public final List<WadReader.Lump> lumps = new ArrayList<>();
    public final Map<String, Entry> textures = new TreeMap<>();
    public final Map<String, Entry> flats = new TreeMap<>();
    public final String type;
    private final FileChannel ch;
    /**
     * Method TextureCatalog.
     *
     * @param wadFile parameter
     * @return result
     * @throws IOException on error
     */
    public TextureCatalog(File wadFile) throws IOException {
        ch = FileChannel.open(wadFile.toPath());

        ByteBuffer hdr = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        ch.read(hdr, 0);
        hdr.flip();
        byte[] magic = new byte[4];
        hdr.get(magic);
        type = new String(magic, StandardCharsets.US_ASCII).trim();
        int num = hdr.getInt();
        int dirOfs = hdr.getInt();
        ByteBuffer dir = ByteBuffer.allocate(num * 16).order(ByteOrder.LITTLE_ENDIAN);
        ch.read(dir, dirOfs);
        dir.flip();
        /**
         * Constructor for for.
         * @param i0inumi parameter
         */
        for (int i = 0; i < num; i++) {
            int off = dir.getInt();
            int size = dir.getInt();
            byte[] nameBytes = new byte[8];
            dir.get(nameBytes);
            int len = 0;
            while (len < 8 && nameBytes[len] != 0) len++;
            String nm = new String(nameBytes, 0, len, StandardCharsets.US_ASCII).toUpperCase(Locale.ROOT);
            lumps.add(new WadReader.Lump(nm, off, size));
        }
        readTextures();
        readFlats();
    }

    /**
     * Method equalsAny.
     *
     * @param s   parameter
     * @param arr parameter
     * @return result
     */
    private static boolean equalsAny(String s, String[] arr) {
        for (String a : arr) if (s.equalsIgnoreCase(a)) return true;
        return false;
    }

    /**
     * Method bump.
     *
     * @param m   parameter
     * @param key parameter
     */
    private static void bump(Map<String, Entry> m, String key) {
        if (key == null) return;
        String k = key.toUpperCase(Locale.ROOT);
        var e = m.get(k);
        if (e != null) e.uses++;
    }

    /**
     * Method read.
     *
     * @param l parameter
     * @return result
     * @throws IOException on error
     */
    private ByteBuffer read(WadReader.Lump l) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(l.size).order(ByteOrder.LITTLE_ENDIAN);
        ch.read(buf, l.offset);
        buf.flip();
        return buf;
    }

    /**
     * Method find.
     *
     * @param name parameter
     * @return result
     */
    private WadReader.Lump find(String name) {
        for (var l : lumps) if (l.name.equalsIgnoreCase(name)) return l;
        return null;
    }

    /**
     * Method readName.
     *
     * @param buf parameter
     * @param max parameter
     * @return result
     */
    private String readName(ByteBuffer buf, int max) {
        byte[] b = new byte[max];
        buf.get(b);
        int len = 0;
        while (len < max && b[len] != 0) len++;
        return new String(b, 0, len, StandardCharsets.US_ASCII).toUpperCase(Locale.ROOT);
    }

    /**
     * Method readTextures.
     *
     * @throws IOException on error
     */
    private void readTextures() throws IOException {
        Set<String> names = new HashSet<>();
        for (String nm : List.of("TEXTURE1", "TEXTURE2")) {
            var lump = find(nm);
            if (lump == null) continue;
            ByteBuffer buf = read(lump);
            if (buf.remaining() < 4) continue;
            int num = buf.getInt();
            int[] offsets = new int[num];
            for (int i = 0; i < num && buf.remaining() >= 4; i++) offsets[i] = buf.getInt();
            /**
             * Constructor for for.
             * @param i0inumi parameter
             */
            for (int i = 0; i < num; i++) {
                int ofs = offsets[i];
                if (ofs < 0 || ofs >= lump.size) continue;
                int oldPos = buf.position();
                buf.position(ofs);
                String name = readName(buf, 8);
                if (!name.isBlank() && !name.equals("-")) names.add(name);
                buf.position(oldPos);
            }
        }
        for (String n : names) textures.put(n, new Entry(n, false));
    }

    /**
     * Method readFlats.
     *
     * @throws IOException on error
     */
    private void readFlats() throws IOException {

        String[] starts = new String[]{"FF_START", "F_START"};
        String[] ends = new String[]{"FF_END", "F_END"};
        boolean in = false;
        /**
         * Constructor for for.
         * @param lumps parameter
         */
        for (var l : lumps) {
            if (equalsAny(l.name, starts)) {
                in = true;
                continue;
            }
            if (equalsAny(l.name, ends)) {
                in = false;
                continue;
            }
            /**
             * Constructor for if.
             * @param 4096 parameter
             */
            if (in && l.size >= 4096) {
                String nm = l.name.toUpperCase(Locale.ROOT);
                if (!nm.isBlank() && !nm.equals("-")) flats.put(nm, new Entry(nm, true));
            }
        }
    }

    /**
     * Method listTextures.
     *
     * @return result
     */
    public List<Entry> listTextures() {
        return new ArrayList<>(textures.values());
    }

    /**
     * Method listFlats.
     *
     * @return result
     */
    public List<Entry> listFlats() {
        return new ArrayList<>(flats.values());
    }

    /**
     * Method computeUsage.
     *
     * @param map parameter
     */
    public void computeUsage(org.deth.wad.MapData map) {
        /**
         * Constructor for for.
         * @param mapsidedefs parameter
         */
        for (var sd : map.sidedefs) {
            bump(textures, sd.upperTex());
            bump(textures, sd.middleTex());
            bump(textures, sd.lowerTex());
        }
        /**
         * Constructor for for.
         * @param mapsectors parameter
         */
        for (var sc : map.sectors) {
            bump(flats, sc.floorTex());
            bump(flats, sc.ceilingTex());
        }
    }

    @Override
    public void close() throws IOException {
        ch.close();
    }

    /**
     * Class Entry.
     * <p>Auto-generated documentation stub.</p>
     */
    public static final class Entry {
        public final String name;
        public final boolean isFlat;
        public int uses = 0;

        /**
         * Method Entry.
         *
         * @param n parameter
         * @param f parameter
         * @return result
         */
        public Entry(String n, boolean f) {
            name = n;
            isFlat = f;
        }

        @Override
        public String toString() {
            return name + (uses > 0 ? "  (" + uses + ")" : "");
        }
    }
}
