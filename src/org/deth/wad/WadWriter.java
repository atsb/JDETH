package org.deth.wad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Class WadWriter.
 * <p>Auto-generated documentation stub.</p>
 */
public final class WadWriter {

    /**
     * Method leShort.
     *
     * @param v parameter
     * @return result
     */
    private static byte[] leShort(int v) {
        ByteBuffer b = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        b.putShort((short) v);
        return b.array();
    }

    /**
     * Method leInt.
     *
     * @param v parameter
     * @return result
     */
    private static byte[] leInt(int v) {
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(v);
        return b.array();
    }

    /**
     * Method name8.
     *
     * @param s parameter
     * @return result
     */
    private static byte[] name8(String s) {

        String t = (s == null || s.isBlank()) ? "-" : s.trim().toUpperCase();
        byte[] raw = t.getBytes(StandardCharsets.US_ASCII);
        byte[] out = new byte[8];
        int n = Math.min(8, raw.length);
        System.arraycopy(raw, 0, out, 0, n);

        return out;
    }

    /**
     * Method writeSingleMap.
     *
     * @param out     parameter
     * @param mapName parameter
     * @param d       parameter
     * @throws IOException on error
     */
    public static void writeSingleMap(File out, String mapName, MapData d) throws IOException {
        if (d == null) throw new IOException("No map data.");
        List<Lump> lumps = new ArrayList<>();


        lumps.add(new Lump(mapName, new byte[0]));


        ByteArrayOutputStream th = new ByteArrayOutputStream();
        /**
         * Constructor for for.
         * @param dthings parameter
         */
        for (var t : d.things) {
            th.write(leShort(t.x()));
            th.write(leShort(t.y()));
            th.write(leShort(t.angle()));
            th.write(leShort(t.type()));
            th.write(leShort(t.flags()));
        }
        lumps.add(new Lump("THINGS", th.toByteArray()));


        ByteArrayOutputStream ln = new ByteArrayOutputStream();
        /**
         * Constructor for for.
         * @param dlinedefs parameter
         */
        for (var l : d.linedefs) {
            ln.write(leShort(l.v1));
            ln.write(leShort(l.v2));
            ln.write(leShort(l.flags));
            ln.write(leShort(l.special));
            ln.write(leShort(l.tag));

            ln.write(leShort(l.rightSidedef));
            ln.write(leShort(l.leftSidedef));
        }
        lumps.add(new Lump("LINEDEFS", ln.toByteArray()));


        ByteArrayOutputStream sd = new ByteArrayOutputStream();
        /**
         * Constructor for for.
         * @param dsidedefs parameter
         */
        for (var s : d.sidedefs) {
            sd.write(leShort(s.xOffset()));
            sd.write(leShort(s.yOffset()));
            sd.write(name8(s.upperTex()));
            sd.write(name8(s.lowerTex()));
            sd.write(name8(s.middleTex()));
            sd.write(leShort(s.sectorIndex()));
        }
        lumps.add(new Lump("SIDEDEFS", sd.toByteArray()));


        ByteArrayOutputStream vx = new ByteArrayOutputStream();
        /**
         * Constructor for for.
         * @param dvertices parameter
         */
        for (var v : d.vertices) {
            vx.write(leShort(v.x()));
            vx.write(leShort(v.y()));
        }
        lumps.add(new Lump("VERTEXES", vx.toByteArray()));


        ByteArrayOutputStream se = new ByteArrayOutputStream();
        /**
         * Constructor for for.
         * @param dsectors parameter
         */
        for (var s : d.sectors) {
            se.write(leShort(s.floorHeight()));
            se.write(leShort(s.ceilingHeight()));
            se.write(name8(s.floorTex()));
            se.write(name8(s.ceilingTex()));
            se.write(leShort(s.lightLevel()));
            se.write(leShort(s.special()));
            se.write(leShort(s.tag()));
        }
        lumps.add(new Lump("SECTORS", se.toByteArray()));


        try (RandomAccessFile raf = new RandomAccessFile(out, "rw")) {
            raf.setLength(0);

            raf.write("PWAD".getBytes(StandardCharsets.US_ASCII));
            raf.write(leInt(lumps.size()));
            int dirOffsetPos = (int) raf.getFilePointer();
            raf.write(leInt(0));


            List<int[]> dir = new ArrayList<>();
            /**
             * Constructor for for.
             * @param lumps parameter
             */
            for (Lump L : lumps) {
                int offset = (int) raf.getFilePointer();
                raf.write(L.data);
                dir.add(new int[]{offset, L.data.length, L.name.toUpperCase().getBytes(StandardCharsets.US_ASCII).length});
            }


            int dirOffset = (int) raf.getFilePointer();
            for (int i = 0; i < lumps.size(); i++) {
                Lump L = lumps.get(i);
                int[] meta = dir.get(i);
                raf.write(leInt(meta[0]));
                raf.write(leInt(meta[1]));
                byte[] n8 = new byte[8];
                byte[] src = L.name.toUpperCase().getBytes(StandardCharsets.US_ASCII);
                System.arraycopy(src, 0, n8, 0, Math.min(8, src.length));
                raf.write(n8);
            }


            raf.seek(8);
            raf.write(leInt(dirOffset));
        }
    }

    /**
     * Method Lump.
     *
     * @param name parameter
     * @param data parameter
     * @return result
     */
    private record Lump(String name, byte[] data) {
    }
}
