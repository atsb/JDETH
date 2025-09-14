package org.deth.wad;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class WadReader.
 * <p>Auto-generated documentation stub.</p>
 */
public final class WadReader implements Closeable {
    private static final Pattern MAP_MARKER = Pattern.compile("E[1-4]M[1-9]|MAP\\d\\d");
    public final List<Lump> lumps = new ArrayList<>();
    public final String type;
    private final FileChannel ch;
    /**
     * Method WadReader.
     *
     * @param f parameter
     * @return result
     * @throws IOException on error
     */
    public WadReader(File f) throws IOException {
        ch = FileChannel.open(f.toPath());
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
        for (int i = 0; i < num; i++) {
            int off = dir.getInt();
            int size = dir.getInt();
            byte[] nameBytes = new byte[8];
            dir.get(nameBytes);
            int len = 0;
            while (len < 8 && nameBytes[len] != 0) len++;
            String nm = new String(nameBytes, 0, len, StandardCharsets.US_ASCII).toUpperCase(Locale.ROOT);
            lumps.add(new Lump(nm, off, size));
        }
    }

    /**
     * Method readName.
     *
     * @param b   parameter
     * @param len parameter
     * @return result
     */
    private static String readName(ByteBuffer b, int len) {
        byte[] arr = new byte[len];
        b.get(arr);
        int n = 0;
        while (n < len && arr[n] != 0) n++;
        return new String(arr, 0, n, StandardCharsets.US_ASCII);
    }

    /**
     * Method listMaps.
     *
     * @return result
     */
    public List<String> listMaps() {
        List<String> maps = new ArrayList<>();
        for (var l : lumps) if (MAP_MARKER.matcher(l.name).matches()) maps.add(l.name);
        return maps;
    }

    /**
     * Method readMap.
     *
     * @param mapName parameter
     * @return result
     * @throws IOException on error
     */
    public MapData readMap(String mapName) throws IOException {
        int idx = -1;
        for (int i = 0; i < lumps.size(); i++)
            if (lumps.get(i).name.equalsIgnoreCase(mapName)) {
                idx = i;
                break;
            }
        if (idx < 0) throw new IOException("Map marker not found: " + mapName);

        Lump vtx = null, ldef = null, sdef = null, sect = null, things = null;
        for (int i = idx + 1; i < lumps.size(); i++) {
            Lump l = lumps.get(i);
            if (MAP_MARKER.matcher(l.name).matches()) break;
            /**
             * Constructor for switch.
             * @param lname parameter
             */
            switch (l.name) {
                case "VERTEXES" -> vtx = l;
                case "LINEDEFS" -> ldef = l;
                case "SIDEDEFS" -> sdef = l;
                case "THINGS" -> things = l;
                case "SECTORS" -> sect = l;
            }
        }
        if (vtx == null || ldef == null) throw new IOException("Missing VERTEXES or LINEDEFS lumps.");

        MapData map = new MapData();
        if (sect != null) readSectors(map, sect);
        if (things != null) readThings(map, things);
        if (sdef != null) readSidedefs(map, sdef);
        readVertices(map, vtx);
        readLinedefs(map, ldef);
        return map;
    }

    /**
     * Method readVertices.
     *
     * @param map parameter
     * @param vtx parameter
     * @throws IOException on error
     */
    private void readVertices(MapData map, Lump vtx) throws IOException {
        ByteBuffer buf = read(vtx);
        while (buf.remaining() >= 4) {
            short x = buf.getShort();
            short y = buf.getShort();
            map.vertices.add(new MapData.Vertex(x, y));
        }
    }

    /**
     * Method readLinedefs.
     *
     * @param map  parameter
     * @param ldef parameter
     * @throws IOException on error
     */
    private void readLinedefs(MapData map, Lump ldef) throws IOException {
        ByteBuffer buf = read(ldef);
        while (buf.remaining() >= 14) {
            int v1 = Short.toUnsignedInt(buf.getShort());
            int v2 = Short.toUnsignedInt(buf.getShort());
            int flags = Short.toUnsignedInt(buf.getShort());
            int special = Short.toUnsignedInt(buf.getShort());
            int tag = Short.toUnsignedInt(buf.getShort());
            int rightSd = Short.toUnsignedInt(buf.getShort());
            int leftSd = Short.toUnsignedInt(buf.getShort());
            map.linedefs.add(new MapData.Linedef(v1, v2, flags, special, tag, rightSd, leftSd));
        }
    }

    /**
     * Method readSidedefs.
     *
     * @param map  parameter
     * @param sdef parameter
     * @throws IOException on error
     */
    private void readSidedefs(MapData map, Lump sdef) throws IOException {
        ByteBuffer buf = read(sdef);
        while (buf.remaining() >= 30) {
            int xOff = Short.toUnsignedInt(buf.getShort());
            int yOff = Short.toUnsignedInt(buf.getShort());
            String up = readName(buf, 8);
            String lo = readName(buf, 8);
            String mid = readName(buf, 8);
            int sectorIndex = Short.toUnsignedInt(buf.getShort());
            map.sidedefs.add(new MapData.Sidedef(xOff, yOff, up, lo, mid, sectorIndex));
        }
    }

    /**
     * Method readSectors.
     *
     * @param map  parameter
     * @param sect parameter
     * @throws IOException on error
     */
    private void readSectors(MapData map, Lump sect) throws IOException {
        ByteBuffer buf = read(sect);
        while (buf.remaining() >= 26) {
            int fh = buf.getShort();
            int ch = buf.getShort();
            String ft = readName(buf, 8);
            String ct = readName(buf, 8);
            int ll = Short.toUnsignedInt(buf.getShort());
            int sp = Short.toUnsignedInt(buf.getShort());
            int tg = Short.toUnsignedInt(buf.getShort());
            map.sectors.add(new MapData.Sector(fh, ch, ft, ct, ll, sp, tg));
        }
    }

    /**
     * Method readThings.
     *
     * @param map    parameter
     * @param things parameter
     * @throws IOException on error
     */
    private void readThings(MapData map, Lump things) throws IOException {
        ByteBuffer buf = read(things);
        while (buf.remaining() >= 10) {
            int x = buf.getShort();
            int y = buf.getShort();
            int angle = Short.toUnsignedInt(buf.getShort());
            int type = Short.toUnsignedInt(buf.getShort());
            int flags = Short.toUnsignedInt(buf.getShort());
            map.things.add(new MapData.Thing(x, y, angle, type, flags));
        }
    }

    /**
     * Method read.
     *
     * @param l parameter
     * @return result
     * @throws IOException on error
     */
    private ByteBuffer read(Lump l) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(l.size).order(ByteOrder.LITTLE_ENDIAN);
        ch.read(buf, l.offset);
        buf.flip();
        return buf;
    }

    @Override
    public void close() throws IOException {
        ch.close();
    }

    /**
     * Class Lump.
     * <p>Auto-generated documentation stub.</p>
     */
    public static final class Lump {
        public final String name;
        public final int offset, size;

        Lump(String n, int o, int s) {
            name = n;
            offset = o;
            size = s;
        }
    }
}
