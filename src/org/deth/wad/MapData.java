package org.deth.wad;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class MapData.
 * <p>Auto-generated documentation stub.</p>
 */
public class MapData {
    public final List<Vertex> vertices = new ArrayList<>();
    public final List<Linedef> linedefs = new ArrayList<>();
    public final List<Sidedef> sidedefs = new ArrayList<>();
    public final List<Sector> sectors = new ArrayList<>();
    public final List<Thing> things = new ArrayList<>();

    /**
     * Method getBounds.
     *
     * @return result
     */
    public Rectangle getBounds() {
        if (vertices.isEmpty()) return new Rectangle(0, 0, 1, 1);
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        /**
         * Constructor for for.
         * @param vertices parameter
         */
        for (var v : vertices) {
            if (v.x < minX) minX = v.x;
            if (v.y < minY) minY = v.y;
            if (v.x > maxX) maxX = v.x;
            if (v.y > maxY) maxY = v.y;
        }
        return new Rectangle(minX, minY, Math.max(1, maxX - minX), Math.max(1, maxY - minY));
    }

    /**
         * Class Thing.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Thing(int x, int y, int angle, int type, int flags) {
        /**
         * Method Thing.
         *
         * @param x     parameter
         * @param y     parameter
         * @param angle parameter
         * @param type  parameter
         * @param flags parameter
         * @return result
         */
        public Thing {
        }

            /**
             * Method getX.
             *
             * @return result
             */
            public double getX() {
                return x;
            }

            /**
             * Method getY.
             *
             * @return result
             */
            public double getY() {
                return y;
            }
        }

    /**
         * Class Vertex.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Vertex(int x, int y) {
        /**
         * Method Vertex.
         *
         * @param x parameter
         * @param y parameter
         * @return result
         */
        public Vertex {
        }

            /**
             * Method getX.
             *
             * @return result
             */
            public double getX() {
                return x;
            }

            /**
             * Method getY.
             *
             * @return result
             */
            public double getY() {
                return y;
            }
        }

    /**
     * Class Linedef.
     * <p>Auto-generated documentation stub.</p>
     */
    public static final class Linedef {
        public final int tag;
        public int v1, v2;
        public int flags;
        public int special;
        public int rightSidedef, leftSidedef;

        /**
         * Method Linedef.
         *
         * @param v1           parameter
         * @param v2           parameter
         * @param flags        parameter
         * @param special      parameter
         * @param tag          parameter
         * @param rightSidedef parameter
         * @param leftSidedef  parameter
         * @return result
         */
        public Linedef(int v1, int v2, int flags, int special, int tag, int rightSidedef, int leftSidedef) {
            this.v1 = v1;
            this.v2 = v2;
            this.flags = flags;
            this.special = special;
            this.tag = tag;
            this.rightSidedef = rightSidedef;
            this.leftSidedef = leftSidedef;
        }
    }

    /**
         * Class Sidedef.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Sidedef(int xOffset, int yOffset, String upperTex, String lowerTex, String middleTex,
                              int sectorIndex) {
        /**
         * Method Sidedef.
         *
         * @param xOffset     parameter
         * @param yOffset     parameter
         * @param upperTex    parameter
         * @param lowerTex    parameter
         * @param middleTex   parameter
         * @param sectorIndex parameter
         * @return result
         */
        public Sidedef {
        }
        }

    /**
         * Class Sector.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Sector(int floorHeight, int ceilingHeight, String floorTex, String ceilingTex, int lightLevel,
                             int special, int tag) {
        /**
         * Method Sector.
         *
         * @param floorHeight   parameter
         * @param ceilingHeight parameter
         * @param floorTex      parameter
         * @param ceilingTex    parameter
         * @param lightLevel    parameter
         * @param special       parameter
         * @param tag           parameter
         * @return result
         */
        public Sector {
        }
        }
}
