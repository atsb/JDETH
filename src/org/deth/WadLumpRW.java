package org.deth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class WadLumpRW.
 * <p>Auto-generated documentation stub.</p>
 */
public class WadLumpRW {
    /**
     * Method readLump.
     *
     * @param wadFile  parameter
     * @param lumpName parameter
     * @return result
     * @throws IOException on error
     */
    public static byte[] readLump(File wadFile, String lumpName) throws IOException {
        lumpName = lumpName.toUpperCase();
        try (RandomAccessFile raf = new RandomAccessFile(wadFile, "r")) {
            Header h = readHeader(raf);
            List<DirEntry> dir = readDirectory(raf, h);
            for (DirEntry de : dir) {
                if (de.name.equals(lumpName)) {
                    byte[] data = new byte[de.size];
                    raf.seek(de.filepos);
                    raf.readFully(data);
                    return data;
                }
            }
            return null;
        }
    }

    /**
     * Method writeOrReplaceLump.
     *
     * @param wadFile  parameter
     * @param lumpName parameter
     * @param data     parameter
     * @throws IOException on error
     */
    public static void writeOrReplaceLump(File wadFile, String lumpName, byte[] data) throws IOException {
        lumpName = lumpName.toUpperCase();
        File tmp = new File(wadFile.getParentFile(), wadFile.getName() + ".tmp___");
        try (RandomAccessFile raf = new RandomAccessFile(wadFile, "r");
             RandomAccessFile out = new RandomAccessFile(tmp, "rw")) {

            Header h = readHeader(raf);
            List<DirEntry> dir = readDirectory(raf, h);


            List<DirEntry> newDir = new ArrayList<>();
            int numLumps = dir.size();
            boolean replaced = false;


            out.write(h.ident);
            out.writeInt(0);
            out.writeInt(0);


            /**
             * Constructor for for.
             * @param dir parameter
             */
            for (DirEntry de : dir) {
                DirEntry nd = new DirEntry();
                nd.name = de.name;
                if (de.name.equals(lumpName)) {
                    nd.filepos = (int) out.getFilePointer();
                    nd.size = data.length;
                    out.write(data);
                    replaced = true;
                } else {
                    byte[] buf = new byte[de.size];
                    raf.seek(de.filepos);
                    raf.readFully(buf);
                    nd.filepos = (int) out.getFilePointer();
                    nd.size = buf.length;
                    out.write(buf);
                }
                newDir.add(nd);
            }

            /**
             * Constructor for if.
             * @param replaced parameter
             */
            if (!replaced) {
                DirEntry nd = new DirEntry();
                nd.name = lumpName;
                nd.filepos = (int) out.getFilePointer();
                nd.size = data.length;
                out.write(data);
                newDir.add(nd);
            }

            int diroff = (int) out.getFilePointer();

            /**
             * Constructor for for.
             * @param newDir parameter
             */
            for (DirEntry nd : newDir) {
                out.write(intLE(nd.filepos));
                out.write(intLE(nd.size));
                byte[] name8 = new byte[8];
                byte[] src = nd.name.getBytes(StandardCharsets.US_ASCII);
                int n = Math.min(8, src.length);
                System.arraycopy(src, 0, name8, 0, n);
                out.write(name8);
            }


            out.seek(4);
            out.write(intLE(newDir.size()));
            out.write(intLE(diroff));
        }


        if (!tmp.renameTo(wadFile)) {

            try (InputStream in = new FileInputStream(tmp);
                 OutputStream o = new FileOutputStream(wadFile)) {
                in.transferTo(o);
            }
            tmp.delete();
        }
    }

    /**
     * Method readHeader.
     *
     * @param raf parameter
     * @return result
     * @throws IOException on error
     */
    private static Header readHeader(RandomAccessFile raf) throws IOException {
        Header h = new Header();
        raf.readFully(h.ident);
        h.numlumps = readIntLE(raf);
        h.diroffset = readIntLE(raf);
        return h;
    }

    /**
     * Method readDirectory.
     *
     * @param raf parameter
     * @param h   parameter
     * @return result
     * @throws IOException on error
     */
    private static List<DirEntry> readDirectory(RandomAccessFile raf, Header h) throws IOException {
        raf.seek(h.diroffset);
        List<DirEntry> dir = new ArrayList<>(h.numlumps);
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < h.numlumps; i++) {
            DirEntry de = new DirEntry();
            de.filepos = readIntLE(raf);
            de.size = readIntLE(raf);
            byte[] name = new byte[8];
            raf.readFully(name);
            int len = 0;
            /**
             * Constructor for for.
             * @param j parameter
             */
            for (int j = 0; j < 8; j++) {
                if (name[j] == 0) break;
                len++;
            }
            de.name = new String(name, 0, len, StandardCharsets.US_ASCII).toUpperCase();
            dir.add(de);
        }
        return dir;
    }

    /**
     * Method readIntLE.
     *
     * @param raf parameter
     * @return result
     * @throws IOException on error
     */
    private static int readIntLE(RandomAccessFile raf) throws IOException {
        int b1 = raf.read();
        int b2 = raf.read();
        int b3 = raf.read();
        int b4 = raf.read();
        if ((b1 | b2 | b3 | b4) < 0) throw new EOFException();
        return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    /**
     * Method intLE.
     *
     * @param v parameter
     * @return result
     */
    private static byte[] intLE(int v) {
        return new byte[]{(byte) (v), (byte) (v >>> 8), (byte) (v >>> 16), (byte) (v >>> 24)};
    }

    /**
     * Class DirEntry.
     * <p>Auto-generated documentation stub.</p>
     */
    static class DirEntry {
        int filepos;
        int size;
        String name;
    }

    /**
     * Class Header.
     * <p>Auto-generated documentation stub.</p>
     */
    static class Header {
        byte[] ident = new byte[4];
        int numlumps;
        int diroffset;
    }
}
