package org.deth.wad;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;


/**
 * Class TextureCache.
 * <p>Auto-generated documentation stub.</p>
 */
public class TextureCache {
    private static final TextureCache INSTANCE = new TextureCache();
    private final Set<String> allTextureNames = new HashSet<>();
    private final Map<String, Integer> usageMap = new HashMap<>();
    private final Map<String, WadLump> lumpIndex = new HashMap<>();
    private final Map<String, TextureDef> textures = new LinkedHashMap<>();
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private Usage lastUsage = new Usage();
    private int[] palette;

    /**
     * Method get.
     *
     * @return result
     */
    public static TextureCache get() {
        return INSTANCE;
    }

    /**
     * Method inc.
     *
     * @param m    parameter
     * @param name parameter
     */
    private static void inc(Map<String, Integer> m, String name) {
        if (name == null) return;
        String n = name.trim().toUpperCase(Locale.ROOT);
        if (n.isEmpty() || "-".equals(n)) return;
        m.put(n, m.getOrDefault(n, 0) + 1);
    }

    /**
     * Method computeUsage.
     *
     * @param map parameter
     * @return result
     */
    public static Usage computeUsage(MapData map) {
        Usage u = new Usage();
        if (map == null) return u;
        /**
         * Constructor for for.
         * @param mapsidedefs parameter
         */
        for (MapData.Sidedef sd : map.sidedefs) {
            if (sd == null) continue;
            inc(u.upper, sd.upperTex());
            inc(u.middle, sd.middleTex());
            inc(u.lower, sd.lowerTex());
        }
        /**
         * Constructor for for.
         * @param mapsectors parameter
         */
        for (MapData.Sector s : map.sectors) {
            if (s == null) continue;
            inc(u.floor, s.floorTex());
            inc(u.ceiling, s.ceilingTex());
        }
        for (Map<String, Integer> layer : List.of(u.upper, u.middle, u.lower, u.floor, u.ceiling)) {
            for (var e : layer.entrySet()) {
                u.total.merge(e.getKey(), e.getValue(), Integer::sum);
                u.textures.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }
        return u;
    }

    /**
     * Method getLastUsage.
     *
     * @return result
     */
    public static Usage getLastUsage() {
        TextureCache tc = get();
        Usage u = new Usage();
        u.textures.putAll(tc.lastUsage.textures);
        u.flats.putAll(tc.lastUsage.flats);
        u.total.putAll(tc.lastUsage.total);
        u.upper.putAll(tc.lastUsage.upper);
        u.middle.putAll(tc.lastUsage.middle);
        u.lower.putAll(tc.lastUsage.lower);
        u.floor.putAll(tc.lastUsage.floor);
        u.ceiling.putAll(tc.lastUsage.ceiling);
        return u;
    }

    /**
     * Method recomputeUsageFromMap.
     *
     * @param map parameter
     * @return result
     */
    public static Usage recomputeUsageFromMap(MapData map) {
        TextureCache tc = get();
        tc.usageMap.clear();
        tc.allTextureNames.clear();
        Usage u = computeUsage(map);
        tc.usageMap.putAll(u.total);
        tc.allTextureNames.addAll(u.total.keySet());
        tc.lastUsage = u;
        return getLastUsage();
    }

    /**
     * Method makeThumb.
     *
     * @param src parameter
     * @param w   parameter
     * @param h   parameter
     * @return result
     */
    public static Image makeThumb(Image src, int w, int h) {
        if (src == null) return null;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private static Map<String, Lump> readWadDirectory(File wad) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            byte[] hdr = new byte[12];
            if (raf.read(hdr) != 12) throw new IOException("Bad WAD header");
            String ident = new String(hdr, 0, 4, StandardCharsets.US_ASCII);
            if (!"IWAD".equals(ident) && !"PWAD".equals(ident)) throw new IOException("Not a WAD file");
            int numLumps = le32(hdr, 4), dirOff = le32(hdr, 8);
            if (numLumps < 0 || dirOff < 0) throw new IOException("Corrupt WAD directory");
            Map<String, Lump> map = new HashMap<>();
            raf.seek(dirOff);
            byte[] entry = new byte[16];
            for (int i = 0; i < numLumps; i++) {
                if (raf.read(entry) != 16) throw new IOException("Short WAD directory");
                int off = le32(entry, 0), size = le32(entry, 4);
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
        return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8) | ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
    }

    /**
     * Method u16.
     *
     * @param b parameter
     * @param o parameter
     * @return result
     */
    private static int u16(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8);
    }

    /**
     * Method s16.
     *
     * @param b parameter
     * @param o parameter
     * @return result
     */
    private static short s16(byte[] b, int o) {
        return (short) ((b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8));
    }

    /**
     * Method name8.
     *
     * @param b   parameter
     * @param off parameter
     * @return result
     */
    private static String name8(byte[] b, int off) {
        int end = off + 8, len = 0;
        for (int i = off; i < end; i++) {
            if (b[i] == 0) break;
            len++;
        }
        if (len <= 0) return "";
        return new String(b, off, len, StandardCharsets.US_ASCII).trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Method loadPaletteRGB.
     *
     * @param wad parameter
     * @param dir parameter
     * @return result
     * @throws IOException on error
     */
    private static int[] loadPaletteRGB(File wad, Map<String, Lump> dir) throws IOException {
        Lump l = dir.get("PLAYPAL");
        if (l == null) return null;
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            raf.seek(l.offset);
            byte[] pal = new byte[256 * 3];
            if (raf.read(pal) != pal.length) return null;
            int[] out = new int[256];
            /**
             * Constructor for for.
             * @param i0i256i parameter
             */
            for (int i = 0; i < 256; i++) {
                int r = pal[i * 3] & 0xFF, g = pal[i * 3 + 1] & 0xFF, b = pal[i * 3 + 2] & 0xFF;
                out[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
            return out;
        }
    }

    /**
     * Method loadPNAMES.
     *
     * @param wad parameter
     * @param dir parameter
     * @return result
     * @throws IOException on error
     */
    private static List<String> loadPNAMES(File wad, Map<String, Lump> dir) throws IOException {
        Lump l = dir.get("PNAMES");
        if (l == null) return Collections.emptyList();
        byte[] data = new byte[l.size];
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            raf.seek(l.offset);
            raf.readFully(data);
        }
        int n = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
        List<String> names = new ArrayList<>(n);
        /**
         * Constructor for for.
         * @param i0ini parameter
         */
        for (int i = 0; i < n; i++) {
            int off = 4 + i * 8;
            if (off + 8 <= data.length) names.add(readName8(data, off));
        }
        return names;
    }

    /**
     * Method readName8.
     *
     * @param data parameter
     * @param off  parameter
     * @return result
     */
    private static String readName8(byte[] data, int off) {
        int end = Math.min(off + 8, data.length), len = 0;
        for (int i = off; i < end; i++) {
            if (data[i] == 0) break;
            len++;
        }
        if (len <= 0) return "";
        return new String(data, off, len, StandardCharsets.US_ASCII).trim().toUpperCase(Locale.ROOT);
    }

    private static Map<String, TextureDef> loadTextures(File wad, Map<String, Lump> dir, List<String> pnames) throws IOException {
        Map<String, TextureDef> out = new LinkedHashMap<>();
        loadTextureLumpInto(wad, dir.get("TEXTURE1"), pnames, out);
        loadTextureLumpInto(wad, dir.get("TEXTURE2"), pnames, out);
        return out;
    }

    /**
     * Method loadTextureLumpInto.
     *
     * @param wad    parameter
     * @param lump   parameter
     * @param pnames parameter
     * @param out    parameter
     * @throws IOException on error
     */
    private static void loadTextureLumpInto(File wad, Lump lump, List<String> pnames, Map<String, TextureDef> out) throws IOException {
        if (lump == null) return;
        byte[] data = new byte[lump.size];
        try (RandomAccessFile raf = new RandomAccessFile(wad, "r")) {
            raf.seek(lump.offset);
            raf.readFully(data);
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        if (data.length < 4) return;
        int num = bb.getInt();
        if (num <= 0 || data.length < 4 + 4L * num) return;

        int[] offs = new int[num];
        for (int i = 0; i < num; i++) offs[i] = bb.getInt();

        /**
         * Constructor for for.
         * @param offs parameter
         */
        for (int off : offs) {
            if (off < 0 || off + 22 > data.length) continue;
            String name = readName8(data, off);
            int width = u16(data, off + 12);
            int height = u16(data, off + 14);
            int patchCount = u16(data, off + 20);
            int pOff = off + 22;

            TextureDef def = new TextureDef(name, width, height);
            /**
             * Constructor for for.
             * @param i0ipatchCounti parameter
             */
            for (int i = 0; i < patchCount; i++) {
                if (pOff + 10 > data.length) break;
                int ox = s16(data, pOff);
                int oy = s16(data, pOff + 2);
                int pIdx = u16(data, pOff + 4);
                pOff += 10;
                String patchName = (pIdx >= 0 && pIdx < pnames.size()) ? pnames.get(pIdx) : null;
                if (patchName != null && !patchName.isEmpty()) def.patches.add(new PatchRef(ox, oy, patchName));
            }
            out.put(def.name, def);
        }
    }

    /**
     * Method listFlats.
     *
     * @param dir parameter
     * @return result
     */
    private static Set<String> listFlats(Map<String, Lump> dir) {
        List<String> names = new ArrayList<>(dir.keySet());
        Collections.sort(names);
        Set<String> flats = new HashSet<>();
        boolean in = false;
        /**
         * Constructor for for.
         * @param names parameter
         */
        for (String n : names) {
            if (n.endsWith("_START") && (n.startsWith("F") || n.startsWith("FF"))) {
                in = true;
                continue;
            }
            if (n.endsWith("_END") && (n.startsWith("F") || n.startsWith("FF"))) {
                in = false;
                continue;
            }
            if (in) flats.add(n);
        }
        return flats;
    }

    /**
     * Method isIndexed.
     *
     * @return result
     */
    public boolean isIndexed() {
        return !allTextureNames.isEmpty();
    }

    /**
     * Method clear.
     */
    public void clear() {
        allTextureNames.clear();
        usageMap.clear();
        lastUsage = new Usage();
        clearWads();
    }

    /**
     * Method getAllTextureNames.
     *
     * @return result
     */
    public List<String> getAllTextureNames() {
        List<String> out = new ArrayList<>(allTextureNames);
        Collections.sort(out);
        return out;
    }

    /**
     * Method getAllFlatNames.
     *
     * @return result
     */
    public List<String> getAllFlatNames() {
        Set<String> flats = new HashSet<>(allTextureNames);
        flats.removeAll(textures.keySet());
        List<String> out = new ArrayList<>(flats);
        Collections.sort(out);
        return out;
    }

    /**
     * Method getAllWallTextureNames.
     *
     * @return result
     */
    public List<String> getAllWallTextureNames() {
        List<String> out = new ArrayList<>(textures.keySet());
        Collections.sort(out);
        return out;
    }

    public Map<String, Integer> getUsageMap() {
        return new LinkedHashMap<>(usageMap);
    }

    /**
     * Method indexWad.
     *
     * @param wad parameter
     * @throws IOException on error
     */
    public void indexWad(File wad) throws IOException {
        clearWads();
        addWad(wad);
    }

    /**
     * Method indexWads.
     *
     * @param wads parameter
     * @throws IOException on error
     */
    public void indexWads(List<File> wads) throws IOException {
        clearWads();
        if (wads != null) for (File f : wads) if (f != null) addWad(f);
    }

    /**
     * Method addWad.
     *
     * @param wad parameter
     * @throws IOException on error
     */
    public void addWad(File wad) throws IOException {
        if (wad == null) return;
        Map<String, Lump> dir = readWadDirectory(wad);

        if (palette == null) palette = loadPaletteRGB(wad, dir);


        for (var e : dir.entrySet()) lumpIndex.put(e.getKey(), new WadLump(wad, e.getValue()));


        List<String> pnames = loadPNAMES(wad, dir);


        Map<String, TextureDef> defs = loadTextures(wad, dir, pnames);

        for (var e : defs.entrySet()) textures.put(e.getKey(), e.getValue());


        allTextureNames.addAll(defs.keySet());
        allTextureNames.addAll(listFlats(dir));

        imageCache.clear();
    }

    /**
     * Method hasRenderableTextures.
     *
     * @return result
     */
    public boolean hasRenderableTextures() {
        if (!textures.isEmpty()) return true;

        for (WadLump wl : lumpIndex.values()) if (wl.lump.size == 4096) return true;
        return false;
    }

    /**
     * Method getTextureImage.
     *
     * @param name parameter
     * @return result
     */
    public BufferedImage getTextureImage(String name) {
        if (name == null) return null;
        String key = name.trim().toUpperCase(Locale.ROOT);
        if (key.isEmpty() || "-".equals(key)) return null;

        BufferedImage cached = imageCache.get(key);
        if (cached != null) return cached;

        BufferedImage img = null;

        TextureDef def = textures.get(key);
        if (def != null) img = renderComposite(def);
        if (img == null) img = tryLoadFlat(key);
        if (img == null) img = tryLoadPatch(key);

        if (img != null) imageCache.put(key, img);
        return img;
    }

    /**
     * Method clearWads.
     */
    private void clearWads() {
        lumpIndex.clear();
        textures.clear();
        imageCache.clear();
        palette = null;
    }

    /**
     * Method ensurePalette.
     *
     * @return result
     */
    private int[] ensurePalette() {
        if (palette != null) return palette;
        int[] p = new int[256];
        /**
         * Constructor for for.
         * @param i0i256i parameter
         */
        for (int i = 0; i < 256; i++) {
            int g = i;
            p[i] = 0xFF000000 | (g << 16) | (g << 8) | g;
        }
        return p;
    }

    /**
     * Method renderComposite.
     *
     * @param def parameter
     * @return result
     */
    private BufferedImage renderComposite(TextureDef def) {
        BufferedImage out = new BufferedImage(Math.max(def.width, 1), Math.max(def.height, 1), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        /**
         * Constructor for for.
         * @param defpatches parameter
         */
        for (PatchRef pr : def.patches) {
            BufferedImage p = loadPatchImage(pr.patch);
            if (p != null) g.drawImage(p, pr.ox, pr.oy, null);
        }
        g.dispose();
        return out;
    }

    /**
     * Method tryLoadFlat.
     *
     * @param name parameter
     * @return result
     */
    private BufferedImage tryLoadFlat(String name) {
        WadLump wl = lumpIndex.get(name);
        if (wl == null || wl.lump.size != 4096) return null;
        try (RandomAccessFile raf = new RandomAccessFile(wl.file, "r")) {
            raf.seek(wl.lump.offset);
            byte[] pix = new byte[4096];
            raf.readFully(pix);
            int[] pal = ensurePalette();
            BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            int k = 0;
            for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) img.setRGB(x, y, pal[pix[k++] & 0xFF]);
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method tryLoadPatch.
     *
     * @param name parameter
     * @return result
     */
    private BufferedImage tryLoadPatch(String name) {
        return loadPatchImage(name);
    }

    /**
     * Method loadPatchImage.
     *
     * @param lumpName parameter
     * @return result
     */
    private BufferedImage loadPatchImage(String lumpName) {
        WadLump wl = lumpIndex.get(lumpName);
        if (wl == null) return null;
        try (RandomAccessFile raf = new RandomAccessFile(wl.file, "r")) {
            raf.seek(wl.lump.offset);
            byte[] head = new byte[8];
            if (raf.read(head) != 8) return null;
            int width = u16(head, 0);
            int height = u16(head, 2);
            if (width <= 0 || height <= 0 || width > 4096 || height > 4096) return null;


            byte[] colbuf = new byte[width * 4];
            if (raf.read(colbuf) != colbuf.length) return null;
            int[] colofs = new int[width];
            for (int i = 0; i < width; i++) colofs[i] = le32(colbuf, i * 4);

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int[] pal = ensurePalette();

            /**
             * Constructor for for.
             * @param x0xwidthx parameter
             */
            for (int x = 0; x < width; x++) {
                int pos = colofs[x];
                if (pos <= 0 || pos >= wl.lump.size) continue;
                raf.seek(wl.lump.offset + pos);
                /**
                 * Constructor for while.
                 * @param true parameter
                 */
                while (true) {
                    int top = raf.read();
                    if (top == -1 || top == 0xFF) break;
                    int len = raf.read();
                    if (len < 0) break;
                    raf.read();
                    /**
                     * Constructor for for.
                     * @param i0ileni parameter
                     */
                    for (int i = 0; i < len; i++) {
                        int y = top + i;
                        int idx = raf.read();
                        if (idx < 0) idx = 0;
                        if (y >= 0 && y < height) img.setRGB(x, y, pal[idx & 0xFF]);
                    }
                    raf.read();
                }
            }
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Class Usage.
     * <p>Auto-generated documentation stub.</p>
     */
    public static final class Usage {
        public final Map<String, Integer> textures = new HashMap<>();
        public final Map<String, Integer> flats = new HashMap<>();
        public final Map<String, Integer> total = new HashMap<>();
        public final Map<String, Integer> upper = new HashMap<>();
        public final Map<String, Integer> middle = new HashMap<>();
        public final Map<String, Integer> lower = new HashMap<>();
        public final Map<String, Integer> floor = new HashMap<>();
        public final Map<String, Integer> ceiling = new HashMap<>();
    }

    /**
         * Class Lump.
         * <p>Auto-generated documentation stub.</p>
         */
        private record Lump(int offset, int size) {
    }

    /**
         * Class WadLump.
         * <p>Auto-generated documentation stub.</p>
         */
        private record WadLump(File file, Lump lump) {
    }

    /**
         * Class PatchRef.
         * <p>Auto-generated documentation stub.</p>
         */
        private record PatchRef(int ox, int oy, String patch) {
    }

    /**
     * Class TextureDef.
     * <p>Auto-generated documentation stub.</p>
     */
    private static final class TextureDef {
        final String name;
        final int width, height;
        final List<PatchRef> patches = new ArrayList<>();

        /**
         * Constructor for TextureDef.
         *
         * @param n parameter
         * @param w parameter
         * @param h parameter
         */
        TextureDef(String n, int w, int h) {
            name = n;
            width = w;
            height = h;
        }
    }
}
