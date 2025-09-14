package org.deth;

import org.deth.wad.MapData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Class MapPanel.
 * <p>Auto-generated documentation stub.</p>
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final java.util.Map<String, java.util.Set<Integer>> THING_CATEGORIES =
            new java.util.HashMap<>();

    static {

        THING_CATEGORIES.put("monsters", cat(
                "3004,9,65,3001,3002,58,3006,3005,69,3003,68,71,66,67,64,7,16,84"
        ));

        THING_CATEGORIES.put("pickups", cat(

                "2001,82,2002,2003,2004,2005,2006," +

                        "2007,2008,2010,2047," +

                        "2011,2012,2013,2014,2018,2019," +

                        "2022,2023,2024,2025,2026"
        ));

        THING_CATEGORIES.put("decor", cat(
                "2035,2028,2029"
        ));


        THING_CATEGORIES.put("monster", THING_CATEGORIES.get("monsters"));
        THING_CATEGORIES.put("pickup", THING_CATEGORIES.get("pickups"));
        THING_CATEGORIES.put("decorations", THING_CATEGORIES.get("decor"));
    }

    private final JLabel status;
    private final Set<Integer> selVertices = new HashSet<>();
    private final Set<Integer> selLines = new HashSet<>();
    private final Set<Integer> selThings = new HashSet<>();
    private final Set<Integer> selSectors = new HashSet<>();
    private final java.util.List<SelectionListener> selectionListeners = new ArrayList<>();
    private final Map<Integer, Point2D.Double> moveOrig = new HashMap<>();
    private final Map<Integer, Point2D.Double> thingMoveOrig = new HashMap<>();
    private final Deque<MapData> undoStack = new ArrayDeque<>();
    private final Deque<MapData> redoStack = new ArrayDeque<>();
    private BufferedImage back;
    private AffineTransform worldToScreen = new AffineTransform();
    private Point lastDrag;
    private MapData map;
    private String mapName = "";
    private boolean showGrid = true;
    private boolean snap = true;
    private boolean filterMode = false;
    private int gridStep = 16;
    private int hoverVertex = -1;
    private int hoverLine = -1;
    private int hoverThing = -1;
    private int thingTemplateType = 2001;
    private int thingTemplateFlags = 0x0007;
    private final java.util.List<MapData.Thing> cbThings = new java.util.ArrayList<>();
    private final java.util.List<MapData.Vertex> cbVertices = new java.util.ArrayList<>();
    private final java.util.List<MapData.Linedef> cbLinedefs = new java.util.ArrayList<>();
    private boolean cbIsLines = false;
    private Tool tool = Tool.SELECT;
    private Mode mode = Mode.IDLE;
    private boolean spacePan = false;
    private Point boxStartScreen = null;
    private Point boxEndScreen = null;
    private Point2D moveStartWorld;
    private Point2D lastMouseWorld = new Point2D.Double();
    private Integer drawStartVertex = null;
    private Point2D drawCurrentWorld = null;
    private boolean measuring = false;
    private Point2D measureStart = null;
    private Point2D measureEnd = null;
    private final AffineTransform[] viewBookmarks = new AffineTransform[4];
    private java.io.File currentPWADFile = null;

    /**
     * Method MapPanel.
     *
     * @param w      parameter
     * @param h      parameter
     * @param status parameter
     * @return result
     */
    public MapPanel(int w, int h, JLabel status) {
        setPreferredSize(new Dimension(w, h));
        back = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        worldToScreen.setToScale(1.0, 1.0);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        setBackground(new Color(30, 33, 36));
        this.status = status;
        setupKeyBindings();
    }

    /**
     * Method copyMap.
     *
     * @param src parameter
     * @return result
     */
    private static MapData copyMap(MapData src) {
        if (src == null) return null;
        MapData d = new MapData();


        for (var v : src.vertices)
            d.vertices.add(new MapData.Vertex(v.x(), v.y()));


        for (var l : src.linedefs)
            d.linedefs.add(new MapData.Linedef(l.v1, l.v2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));


        for (var s : src.sidedefs)
            d.sidedefs.add(new MapData.Sidedef(s.xOffset(), s.yOffset(), s.upperTex(), s.lowerTex(), s.middleTex(), s.sectorIndex()));


        for (var s : src.sectors)
            d.sectors.add(new MapData.Sector(s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(), s.lightLevel(), s.special(), s.tag()));


        for (var t : src.things)
            d.things.add(new MapData.Thing(t.x(), t.y(), t.angle(), t.type(), t.flags()));

        return d;
    }

    /**
     * Method length.
     *
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private static double length(MapData.Vertex a, MapData.Vertex b) {
        double dx = a.getX() - b.getX(), dy = a.getY() - b.getY();
        return Math.hypot(dx, dy);
    }

    /**
     * Method pointToSegment.
     *
     * @param px parameter
     * @param py parameter
     * @param x1 parameter
     * @param y1 parameter
     * @param x2 parameter
     * @param y2 parameter
     * @return result
     */
    private static double pointToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) return Math.hypot(px - x1, py - y1);
        double t = ((px - x1) * dx + (py - y1) * dy) / len2;
        t = Math.max(0, Math.min(1, t));
        double projx = x1 + t * dx, projy = y1 + t * dy;
        return Math.hypot(px - projx, py - projy);
    }

    /**
     * Method paramOnSegment.
     *
     * @param a  parameter
     * @param b  parameter
     * @param px parameter
     * @param py parameter
     * @return result
     */
    private static double paramOnSegment(MapData.Vertex a, MapData.Vertex b, double px, double py) {
        double ax = a.getX(), ay = a.getY(), bx = b.getX(), by = b.getY();
        double dx = bx - ax, dy = by - ay, len2 = dx * dx + dy * dy;
        if (len2 == 0) return 0.0;
        return ((px - ax) * dx + (py - ay) * dy) / len2;
    }

    /**
     * Method unitDir.
     *
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private static double[] unitDir(MapData.Vertex a, MapData.Vertex b) {
        double dx = b.getX() - a.getX(), dy = b.getY() - a.getY(), L = Math.hypot(dx, dy);
        if (L == 0) return new double[]{1, 0};
        return new double[]{dx / L, dy / L};
    }

    /**
     * Method closestPointOnSegment.
     *
     * @param p parameter
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private static Point2D.Double closestPointOnSegment(Point2D p, MapData.Vertex a, MapData.Vertex b) {
        double ax = a.getX(), ay = a.getY(), bx = b.getX(), by = b.getY();
        double dx = bx - ax, dy = by - ay;
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) return new Point2D.Double(ax, ay);
        double t = ((p.getX() - ax) * dx + (p.getY() - ay) * dy) / len2;
        t = Math.max(0, Math.min(1, t));
        return new Point2D.Double(ax + t * dx, ay + t * dy);
    }

    /**
     * Method cat.
     *
     * @param csv parameter
     * @return result
     */
    private static java.util.Set<Integer> cat(String csv) {
        java.util.Set<Integer> s = new java.util.HashSet<>();
        for (String t : csv.split(",")) {
            t = t.trim();
            if (!t.isEmpty()) s.add(Integer.parseInt(t));
        }
        return s;
    }

    /**
     * Method defineThingCategory.
     *
     * @param name   parameter
     * @param ednums parameter
     */
    public static void defineThingCategory(String name, java.util.Set<Integer> ednums) {
        if (name == null || ednums == null) return;
        THING_CATEGORIES.put(name.toLowerCase(java.util.Locale.ROOT), new java.util.HashSet<>(ednums));
    }

    /**
     * Method normLike.
     *
     * @param q parameter
     * @return result
     */
    private static String normLike(String q) {
        if (q == null) return null;
        q = q.trim();
        if (q.isEmpty()) return null;
        return q.toUpperCase(java.util.Locale.ROOT);
    }

    /**
     * Method like.
     *
     * @param val parameter
     * @param q   parameter
     * @return result
     */
    private static boolean like(String val, String q) {
        if (q == null) return true;
        if (val == null) return false;
        return val.toUpperCase(java.util.Locale.ROOT).contains(q);
    }

    /**
     * Method setThingTemplate.
     *
     * @param type  parameter
     * @param flags parameter
     */
    public void setThingTemplate(int type, int flags) {
        if (type > 0) this.thingTemplateType = type;
        this.thingTemplateFlags = flags;
    }

    /**
     * Method getThingTemplateType.
     *
     * @return result
     */
    public int getThingTemplateType() {
        return thingTemplateType;
    }

    /**
     * Method getThingTemplateFlags.
     *
     * @return result
     */
    public int getThingTemplateFlags() {
        return thingTemplateFlags;
    }

    /**
     * Method addSelectionListener.
     *
     * @param l parameter
     */
    public void addSelectionListener(SelectionListener l) {
        selectionListeners.add(l);
    }

    /**
     * Method notifySelection.
     */
    private void notifySelection() {
        for (var l : selectionListeners) l.selectionChanged();
    }

    /**
     * Method getCurrentPWADFile.
     *
     * @return result
     */
    public java.io.File getCurrentPWADFile() {
        return currentPWADFile;
    }

    /**
     * Method setCurrentPWADFile.
     *
     * @param f parameter
     */
    public void setCurrentPWADFile(java.io.File f) {
        this.currentPWADFile = f;
    }

    /**
     * Method getMapData.
     *
     * @return result
     */
    public MapData getMapData() {
        return map;
    }

    /**
     * Method getMapName.
     *
     * @return result
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Method setTool.
     *
     * @param t parameter
     */
    public void setTool(Tool t) {
        this.tool = t;
        mode = Mode.IDLE;
        repaint();
        updateStatus(null);
    }

    /**
     * Method getSelectedLinedefs.
     *
     * @return result
     */
    public List<MapData.Linedef> getSelectedLinedefs() {
        if (map == null) return java.util.Collections.emptyList();
        java.util.List<MapData.Linedef> out = new java.util.ArrayList<>();
        for (int i : selLines) out.add(map.linedefs.get(i));
        return out;
    }

    /**
     * Method connectSelectedLinedefsSplitDonut.
     *
     * @return result
     */
    public boolean connectSelectedLinedefsSplitDonut() {
        if (map == null) return false;

        int[] sel = getSelectedLinedefIndices();
        /**
         * Constructor for if.
         * @param 2 parameter
         */
        if (sel == null || sel.length != 2) {
            warnStatus("Select exactly two linedefs for Split Donut.");
            return false;
        }

        int aIdx = sel[0], bIdx = sel[1];
        MapData.Linedef A = map.linedefs.get(aIdx);
        MapData.Linedef B = map.linedefs.get(bIdx);


        int s1 = sectorOf(A.rightSidedef);
        int s2 = sectorOf(A.leftSidedef);
        int s3 = sectorOf(B.rightSidedef);
        int s4 = sectorOf(B.leftSidedef);


        /**
         * Constructor for if.
         * @param s2 parameter
         */
        if (s1 >= 0 && s1 == s2) {
            warnStatus("First linedef has the same sector on both sides.");
            return false;
        }
        /**
         * Constructor for if.
         * @param s4 parameter
         */
        if (s3 >= 0 && s3 == s4) {
            warnStatus("Second linedef has the same sector on both sides.");
            return false;
        }


        int shared = -1, mode = -1;
        /**
         * Constructor for if.
         * @param s3 parameter
         */
        if (s1 >= 0 && s1 == s3) {
            shared = s1;
            mode = 0;
        }
        /**
         * Method if.
         * @param s4 parameter
         * @return result
         */
        else if (s1 >= 0 && s1 == s4) {
            shared = s1;
            mode = 1;
        }
        /**
         * Method if.
         * @param s3 parameter
         * @return result
         */
        else if (s2 >= 0 && s2 == s3) {
            shared = s2;
            mode = 2;
        }
        /**
         * Method if.
         * @param s4 parameter
         * @return result
         */
        else if (s2 >= 0 && s2 == s4) {
            shared = s2;
            mode = 3;
        } else {
            warnStatus("The two linedefs are not adjacent to the same sector.");
            return false;
        }


        MapData.Sector src = map.sectors.get(shared);
        int newSec = map.sectors.size();
        map.sectors.add(new MapData.Sector(
                src.floorHeight(), src.ceilingHeight(),
                src.floorTex(), src.ceilingTex(),
                src.lightLevel(), src.special(), src.tag()));

        pushUndo();


        final int TWO_SIDED = 0x0004;
        int p1s, p1e, p2s, p2e;
        /**
         * Constructor for switch.
         * @param mode parameter
         */
        switch (mode) {
            case 0 -> {
                p1s = A.v2;
                p1e = B.v1;
                p2s = B.v2;
                p2e = A.v1;
                A.rightSidedef = cloneSidedefWithSector(A.rightSidedef, newSec);
                B.rightSidedef = cloneSidedefWithSector(B.rightSidedef, newSec);
            }
            case 1 -> {
                p1s = A.v2;
                p1e = B.v2;
                p2s = B.v1;
                p2e = A.v1;
                A.rightSidedef = cloneSidedefWithSector(A.rightSidedef, newSec);
                B.leftSidedef = cloneSidedefWithSector(B.leftSidedef, newSec);
            }
            case 2 -> {
                p1s = A.v1;
                p1e = B.v1;
                p2s = B.v2;
                p2e = A.v2;
                A.leftSidedef = cloneSidedefWithSector(A.leftSidedef, newSec);
                B.rightSidedef = cloneSidedefWithSector(B.rightSidedef, newSec);
            }
            case 3 -> {
                p1s = A.v1;
                p1e = B.v2;
                p2s = B.v1;
                p2e = A.v2;
                A.leftSidedef = cloneSidedefWithSector(A.leftSidedef, newSec);
                B.leftSidedef = cloneSidedefWithSector(B.leftSidedef, newSec);
            }
            default -> {
                return false;
            }
        }


        int sd1R = addBlankSidedef(shared);
        int sd1L = addBlankSidedef(newSec);
        int line1 = map.linedefs.size();
        map.linedefs.add(new MapData.Linedef(p1s, p1e, TWO_SIDED, 0, 0, sd1R, sd1L));

        int sd2R = addBlankSidedef(shared);
        int sd2L = addBlankSidedef(newSec);
        int line2 = map.linedefs.size();
        map.linedefs.add(new MapData.Linedef(p2s, p2e, TWO_SIDED, 0, 0, sd2R, sd2L));


        map.linedefs.set(aIdx, A);
        map.linedefs.set(bIdx, B);


        selSectors.clear();
        selLines.clear();
        selSectors.add(newSec);
        selLines.add(line1);
        selLines.add(line2);

        notifySelection();
        repaint();
        infoStatus("Split Donut created.");
        return true;
    }

    /**
     * Method sectorOf.
     *
     * @param sidedefIndex parameter
     * @return result
     */
    private int sectorOf(int sidedefIndex) {
        if (sidedefIndex < 0 || sidedefIndex == 0xFFFF) return -1;
        return map.sidedefs.get(sidedefIndex).sectorIndex();
    }

    /**
     * Method addBlankSidedef.
     *
     * @param sectorIdx parameter
     * @return result
     */
    private int addBlankSidedef(int sectorIdx) {
        map.sidedefs.add(new MapData.Sidedef(0, 0, "-", "-", "-", sectorIdx));
        return map.sidedefs.size() - 1;
    }

    /**
     * Method cloneSidedefWithSector.
     *
     * @param sidedefIndex parameter
     * @param newSector    parameter
     * @return result
     */
    private int cloneSidedefWithSector(int sidedefIndex, int newSector) {
        if (sidedefIndex < 0 || sidedefIndex == 0xFFFF) return sidedefIndex;
        var sd = map.sidedefs.get(sidedefIndex);
        map.sidedefs.add(new MapData.Sidedef(sd.xOffset(), sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), newSector));
        return map.sidedefs.size() - 1;
    }

    /**
     * Method warnStatus.
     *
     * @param msg parameter
     */
    private void warnStatus(String msg) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        javax.swing.JOptionPane.showMessageDialog(this, msg, "Split Donut", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Method infoStatus.
     *
     * @param msg parameter
     */
    private void infoStatus(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg, "Split Donut", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method invertSelection.
     */
    public void invertSelection() {
        if (map == null) return;
        java.util.Set<Integer> newVerts = new java.util.HashSet<>();
        for (int i = 0; i < map.vertices.size(); i++) if (!selVertices.contains(i)) newVerts.add(i);
        java.util.Set<Integer> newLines = new java.util.HashSet<>();
        for (int i = 0; i < map.linedefs.size(); i++) if (!selLines.contains(i)) newLines.add(i);
        java.util.Set<Integer> newThings = new java.util.HashSet<>();
        for (int i = 0; i < map.things.size(); i++) if (!selThings.contains(i)) newThings.add(i);
        selVertices.clear();
        selVertices.addAll(newVerts);
        selLines.clear();
        selLines.addAll(newLines);
        selThings.clear();
        selThings.addAll(newThings);
        notifySelection();
        repaint();
    }

    /**
     * Method updateSelectedSectors.
     *
     * @param floor    parameter
     * @param ceil     parameter
     * @param floorTex parameter
     * @param ceilTex  parameter
     * @param light    parameter
     * @param special  parameter
     * @param tag      parameter
     */
    public void updateSelectedSectors(Integer floor, Integer ceil, String floorTex, String ceilTex, Integer light, Integer special, Integer tag) {
        if (map == null || selSectors.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selSectors)) {
            var s = map.sectors.get(idx);
            int fl = (floor != null) ? floor : s.floorHeight();
            int ch = (ceil != null) ? ceil : s.ceilingHeight();
            String ft = (floorTex != null && !floorTex.isEmpty()) ? floorTex : s.floorTex();
            String ct = (ceilTex != null && !ceilTex.isEmpty()) ? ceilTex : s.ceilingTex();
            int ll = (light != null) ? light : s.lightLevel();
            int sp = (special != null) ? special : s.special();
            int tg = (tag != null) ? tag : s.tag();
            map.sectors.set(idx, new MapData.Sector(fl, ch, ft, ct, ll, sp, tg));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method clearSectorSelection.
     */
    public void clearSectorSelection() {
        selSectors.clear();
        repaint();
        notifySelection();
    }

    /**
     * Method selectFrontSectorsOfSelectedLines.
     */
    public void selectFrontSectorsOfSelectedLines() {
        if (map == null) return;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            if (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size()) {
                int si = map.sidedefs.get(l.rightSidedef).sectorIndex();
                if (si >= 0 && si < map.sectors.size()) selSectors.add(si);
            }
        }
        repaint();
        notifySelection();
    }

    /**
     * Method selectBackSectorsOfSelectedLines.
     */
    public void selectBackSectorsOfSelectedLines() {
        if (map == null) return;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            if (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size()) {
                int si = map.sidedefs.get(l.leftSidedef).sectorIndex();
                if (si >= 0 && si < map.sectors.size()) selSectors.add(si);
            }
        }
        repaint();
        notifySelection();
    }

    /**
     * Method getSelectedThings.
     *
     * @return result
     */
    public java.util.List<MapData.Thing> getSelectedThings() {
        if (map == null) return java.util.Collections.emptyList();
        java.util.List<MapData.Thing> out = new java.util.ArrayList<>();
        for (int i : selThings) out.add(map.things.get(i));
        return out;
    }

    /**
     * Method getSelectedSectors.
     *
     * @return result
     */
    public java.util.List<org.deth.wad.MapData.Sector> getSelectedSectors() {
        if (map == null) return java.util.Collections.emptyList();
        java.util.List<org.deth.wad.MapData.Sector> out = new java.util.ArrayList<>();
        for (int i : selSectors) if (i >= 0 && i < map.sectors.size()) out.add(map.sectors.get(i));
        return out;
    }

    /**
     * Method getSelectedSectorIndices.
     *
     * @return result
     */
    public int[] getSelectedSectorIndices() {
        if (map == null || selSectors.isEmpty()) return new int[0];
        int[] a = new int[selSectors.size()];
        int k = 0;
        for (int i : selSectors) a[k++] = i;
        return a;
    }

    /**
     * Method getSelectedThingIndices.
     *
     * @return result
     */
    public int[] getSelectedThingIndices() {
        return selThings.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Method updateSelectedThings.
     *
     * @param newType  parameter
     * @param newAngle parameter
     * @param newFlags parameter
     */
    public void updateSelectedThings(Integer newType, Integer newAngle, Integer newFlags) {
        if (map == null || selThings.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            int type = (newType != null) ? newType : t.type();
            int angle = (newAngle != null) ? newAngle : t.angle();
            int flags = (newFlags != null) ? newFlags : t.flags();
            map.things.set(idx, new MapData.Thing(t.x(), t.y(), angle, type, flags));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method getSelectedLinedefIndices.
     *
     * @return result
     */
    public int[] getSelectedLinedefIndices() {
        return selLines.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Method updateSelectedLinedefs.
     *
     * @param newFlags   parameter
     * @param newSpecial parameter
     * @param newTag     parameter
     */
    public void updateSelectedLinedefs(Integer newFlags, Integer newSpecial, Integer newTag) {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(idx);
            int flags = (newFlags != null) ? newFlags : l.flags;
            int special = (newSpecial != null) ? newSpecial : l.special;
            int tag = (newTag != null) ? newTag : l.tag;
            map.linedefs.set(idx, new MapData.Linedef(l.v1, l.v2, flags, special, tag, l.rightSidedef, l.leftSidedef));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method deleteSelection.
     */
    public void deleteSelection() {
        if (map == null) return;
        pushUndo();
        if (!selThings.isEmpty()) {
            java.util.List<MapData.Thing> keepT = new java.util.ArrayList<>();
            for (int i = 0; i < map.things.size(); i++) if (!selThings.contains(i)) keepT.add(map.things.get(i));
            map.things.clear();
            map.things.addAll(keepT);
            selThings.clear();
        }
        if (!selLines.isEmpty()) {

            List<MapData.Linedef> keep = new ArrayList<>();
            for (int i = 0; i < map.linedefs.size(); i++) if (!selLines.contains(i)) keep.add(map.linedefs.get(i));
            map.linedefs.clear();
            map.linedefs.addAll(keep);
            selLines.clear();
        }
        if (!selVertices.isEmpty()) {

            boolean[] del = new boolean[map.vertices.size()];
            for (int idx : selVertices) if (idx >= 0 && idx < del.length) del[idx] = true;
            int[] newIndex = new int[map.vertices.size()];
            List<MapData.Vertex> newVerts = new ArrayList<>();
            int c = 0;
            for (int i = 0; i < map.vertices.size(); i++) {
                /**
                 * Constructor for if.
                 * @param deli parameter
                 */
                if (!del[i]) {
                    newIndex[i] = c++;
                    newVerts.add(map.vertices.get(i));
                } else newIndex[i] = -1;
            }
            List<MapData.Linedef> newLines = new ArrayList<>();
            /**
             * Constructor for for.
             * @param maplinedefs parameter
             */
            for (var l : map.linedefs) {
                int nv1 = newIndex[l.v1], nv2 = newIndex[l.v2];
                if (nv1 >= 0 && nv2 >= 0)
                    newLines.add(new MapData.Linedef(nv1, nv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
            }
            map.vertices.clear();
            map.vertices.addAll(newVerts);
            map.linedefs.clear();
            map.linedefs.addAll(newLines);
            selVertices.clear();
        }
        repaint();
        updateHover(lastDrag != null ? lastDrag : new Point(0, 0));
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method undo.
     */
    public void undo() {
        if (undoStack.isEmpty() || map == null) return;
        redoStack.push(copyMap(map));
        map = undoStack.pop();
        clampSelectionToMap();
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method redo.
     */
    public void redo() {
        if (redoStack.isEmpty() || map == null) return;
        undoStack.push(copyMap(map));
        map = redoStack.pop();
        clampSelectionToMap();
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method openProject.
     */
    public void openProject() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try {
            setMap(loadProject(f), f.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to open: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


        try {
            var tc = org.deth.wad.TextureCache.get();

            if (!tc.hasRenderableTextures()) {
                int ok = javax.swing.JOptionPane.showConfirmDialog(
                        this,
                        "No texture resources loaded. Select a base IWAD (doom.wad/doom2.wad) for previews?",
                        "Textures",
                        javax.swing.JOptionPane.YES_NO_OPTION
                );
                /**
                 * Constructor for if.
                 * @param javaxswingJOptionPaneYES_OPTION parameter
                 */
                if (ok == javax.swing.JOptionPane.YES_OPTION) {
                    javax.swing.JFileChooser fc2 = new javax.swing.JFileChooser();
                    if (fc2.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try {
                            tc.addWad(fc2.getSelectedFile());
                        } catch (Throwable ignore) {
                        }
                    }
                }
            }
            org.deth.wad.TextureCache.recomputeUsageFromMap(getMapData());
            try {
                fireSelectionChanged();
            } catch (Throwable ignore) {
                repaint();
            }
        } catch (Throwable ignore) {

        }
    }

    /**
     * Method saveProject.
     */
    public void saveProject() {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (map == null) {
            JOptionPane.showMessageDialog(this, "Nothing to save.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), java.nio.charset.StandardCharsets.UTF_8))) {
            saveProject(out, map);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method pushUndo.
     */
    private void pushUndo() {
        if (map == null) return;
        undoStack.push(copyMap(map));

        redoStack.clear();

        while (undoStack.size() > 100) undoStack.removeLast();
    }

    /**
     * Method selectionCenter.
     *
     * @return result
     */
    private java.awt.geom.Point2D.Double selectionCenter() {
        if (map == null) return new java.awt.geom.Point2D.Double(0, 0);
        double minx = Double.POSITIVE_INFINITY, miny = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;
        boolean any = false;
        /**
         * Constructor for for.
         * @param selVertices parameter
         */
        for (int i : selVertices) {
            var v = map.vertices.get(i);
            minx = Math.min(minx, v.getX());
            maxx = Math.max(maxx, v.getX());
            miny = Math.min(miny, v.getY());
            maxy = Math.max(maxy, v.getY());
            any = true;
        }
        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int i : selThings) {
            var t = map.things.get(i);
            minx = Math.min(minx, t.x());
            maxx = Math.max(maxx, t.x());
            miny = Math.min(miny, t.y());
            maxy = Math.max(maxy, t.y());
            any = true;
        }
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int i : selLines) {
            var l = map.linedefs.get(i);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            minx = Math.min(minx, Math.min(a.getX(), b.getX()));
            maxx = Math.max(maxx, Math.max(a.getX(), b.getX()));
            miny = Math.min(miny, Math.min(a.getY(), b.getY()));
            maxy = Math.max(maxy, Math.max(a.getY(), b.getY()));
            any = true;
        }
        if (!any) return new java.awt.geom.Point2D.Double(0, 0);
        return new java.awt.geom.Point2D.Double((minx + maxx) / 2.0, (miny + maxy) / 2.0);
    }

    /**
     * Method duplicateSelection.
     *
     * @param dx parameter
     * @param dy parameter
     */
    public void duplicateSelection(double dx, double dy) {
        if (map == null) return;
        if (selVertices.isEmpty() && selLines.isEmpty() && selThings.isEmpty()) return;
        pushUndo();


        if (!selThings.isEmpty() && selLines.isEmpty() && selVertices.isEmpty()) {
            java.util.Set<Integer> newSel = new java.util.HashSet<>();
            /**
             * Constructor for for.
             * @param selThings parameter
             */
            for (int idx : selThings) {
                var t = map.things.get(idx);
                map.things.add(new MapData.Thing((int) Math.round(t.x() + dx), (int) Math.round(t.y() + dy), t.angle(), t.type(), t.flags()));
                newSel.add(map.things.size() - 1);
            }
            selThings.clear();
            selThings.addAll(newSel);
            repaint();
            updateStatus(null);
            notifySelection();
            return;
        }


        java.util.Set<Integer> usedVerts = new java.util.LinkedHashSet<>();
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            usedVerts.add(l.v1);
            usedVerts.add(l.v2);
        }
        usedVerts.addAll(selVertices);


        java.util.Map<Integer, Integer> remap = new java.util.HashMap<>();
        int base = map.vertices.size();
        /**
         * Constructor for for.
         * @param usedVerts parameter
         */
        for (int vi : usedVerts) {
            var v = map.vertices.get(vi);
            map.vertices.add(new MapData.Vertex((int) Math.round(v.getX() + dx), (int) Math.round(v.getY() + dy)));
            remap.put(vi, base++);
        }


        java.util.Set<Integer> newLineSel = new java.util.HashSet<>();
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            int nv1 = remap.get(l.v1), nv2 = remap.get(l.v2);
            map.linedefs.add(new MapData.Linedef(nv1, nv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
            newLineSel.add(map.linedefs.size() - 1);
        }


        if (!selThings.isEmpty()) {
            java.util.Set<Integer> newThingsSel = new java.util.HashSet<>();
            /**
             * Constructor for for.
             * @param selThings parameter
             */
            for (int idx : selThings) {
                var t = map.things.get(idx);
                map.things.add(new MapData.Thing((int) Math.round(t.x() + dx), (int) Math.round(t.y() + dy), t.angle(), t.type(), t.flags()));
                newThingsSel.add(map.things.size() - 1);
            }
            selThings.clear();
            selThings.addAll(newThingsSel);
        }

        selVertices.clear();
        selVertices.addAll(remap.values());
        selLines.clear();
        selLines.addAll(newLineSel);
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method rotateSelectedGeometry.
     *
     * @param angleDeg parameter
     */
    public void rotateSelectedGeometry(double angleDeg) {
        if (map == null) return;
        if (selVertices.isEmpty() && selThings.isEmpty() && selLines.isEmpty()) return;
        pushUndo();
        double ang = Math.toRadians(angleDeg);
        var c = selectionCenter();
        double cx = c.x, cy = c.y;

        java.util.function.BiFunction<Double, Double, java.awt.geom.Point2D.Double> rot = (x, y) -> {
            double dx = x - cx, dy = y - cy;
            double rx = dx * Math.cos(ang) - dy * Math.sin(ang);
            double ry = dx * Math.sin(ang) + dy * Math.cos(ang);
            return new java.awt.geom.Point2D.Double(cx + rx, cy + ry);
        };

        for (int idx : new java.util.ArrayList<>(selVertices)) {
            var v = map.vertices.get(idx);
            var p = rot.apply(v.getX(), v.getY());
            map.vertices.set(idx, new MapData.Vertex((int) Math.round(p.x), (int) Math.round(p.y)));
        }
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            var p = rot.apply((double) t.x(), (double) t.y());
            int newAng = Math.floorMod(t.angle() + (int) Math.round(angleDeg), 360);
            map.things.set(idx, new MapData.Thing((int) Math.round(p.x), (int) Math.round(p.y), newAng, t.type(), t.flags()));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method mirrorSelected.
     *
     * @param horizontal parameter
     */
    public void mirrorSelected(boolean horizontal) {
        if (map == null) return;
        if (selVertices.isEmpty() && selThings.isEmpty() && selLines.isEmpty()) return;
        pushUndo();
        var c = selectionCenter();
        double cx = c.x, cy = c.y;

        for (int idx : new java.util.ArrayList<>(selVertices)) {
            var v = map.vertices.get(idx);
            int nx = horizontal ? (int) Math.round(2 * cx - v.getX()) : v.x();
            int ny = horizontal ? v.y() : (int) Math.round(2 * cy - v.getY());
            map.vertices.set(idx, new MapData.Vertex(nx, ny));
        }
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            int nx = horizontal ? (int) Math.round(2 * cx - t.x()) : t.x();
            int ny = horizontal ? t.y() : (int) Math.round(2 * cy - t.y());
            int newAng = horizontal ? Math.floorMod(180 - t.angle(), 360) : Math.floorMod(-t.angle(), 360);
            map.things.set(idx, new MapData.Thing(nx, ny, newAng, t.type(), t.flags()));
        }

        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            map.linedefs.set(li, new MapData.Linedef(l.v1, l.v2, l.flags, l.special, l.tag, l.leftSidedef, l.rightSidedef));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method snapSelectedToGrid.
     */
    public void snapSelectedToGrid() {
        if (map == null) return;
        if (selVertices.isEmpty() && selThings.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selVertices)) {
            var v = map.vertices.get(idx);
            int gx = Math.round(Math.round(v.getX() / (double) gridStep) * gridStep);
            int gy = Math.round(Math.round(v.getY() / (double) gridStep) * gridStep);
            map.vertices.set(idx, new MapData.Vertex(gx, gy));
        }
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            int gx = Math.round(Math.round(t.x() / (double) gridStep) * gridStep);
            int gy = Math.round(Math.round(t.y() / (double) gridStep) * gridStep);
            map.things.set(idx, new MapData.Thing(gx, gy, t.angle(), t.type(), t.flags()));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method cleanupUnusedVertices.
     */
    public void cleanupUnusedVertices() {
        if (map == null) return;
        pushUndo();
        boolean[] used = new boolean[map.vertices.size()];
        /**
         * Constructor for for.
         * @param maplinedefs parameter
         */
        for (var l : map.linedefs) {
            if (l.v1 >= 0 && l.v1 < used.length) used[l.v1] = true;
            if (l.v2 >= 0 && l.v2 < used.length) used[l.v2] = true;
        }
        int[] remap = new int[map.vertices.size()];
        java.util.List<MapData.Vertex> newVerts = new java.util.ArrayList<>();
        for (int i = 0; i < map.vertices.size(); i++) {
            /**
             * Constructor for if.
             * @param usedi parameter
             */
            if (used[i]) {
                remap[i] = newVerts.size();
                newVerts.add(map.vertices.get(i));
            } else remap[i] = -1;
        }
        if (newVerts.size() == map.vertices.size()) return;

        java.util.List<MapData.Linedef> newLines = new java.util.ArrayList<>();
        /**
         * Constructor for for.
         * @param maplinedefs parameter
         */
        for (var l : map.linedefs) {
            int nv1 = (l.v1 >= 0 && l.v1 < remap.length) ? remap[l.v1] : -1;
            int nv2 = (l.v2 >= 0 && l.v2 < remap.length) ? remap[l.v2] : -1;
            if (nv1 >= 0 && nv2 >= 0 && nv1 != nv2)
                newLines.add(new MapData.Linedef(nv1, nv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
        }
        map.vertices.clear();
        map.vertices.addAll(newVerts);
        map.linedefs.clear();
        map.linedefs.addAll(newLines);


        java.util.Set<Integer> ns = new java.util.HashSet<>();
        for (int idx : selVertices) if (idx >= 0 && idx < remap.length && remap[idx] >= 0) ns.add(remap[idx]);
        selVertices.clear();
        selVertices.addAll(ns);
        repaint();
        notifySelection();
    }

    /**
     * Method loadProject.
     *
     * @param f parameter
     * @return result
     * @throws IOException on error
     */
    private MapData loadProject(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), java.nio.charset.StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null || !header.startsWith("# DETH-JAVA-PROJECT v1")) throw new IOException("Bad header");
            MapData d = new MapData();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tok = line.split("\\s+");
                /**
                 * Constructor for switch.
                 * @param tok0 parameter
                 */
                switch (tok[0]) {
                    case "VERTEX":
                        d.vertices.add(new MapData.Vertex(Integer.parseInt(tok[1]), Integer.parseInt(tok[2])));
                        break;
                    case "LINE":
                        d.linedefs.add(new MapData.Linedef(Integer.parseInt(tok[1]), Integer.parseInt(tok[2]),
                                Integer.parseInt(tok[3]), Integer.parseInt(tok[4]), Integer.parseInt(tok[5]),
                                Integer.parseInt(tok[6]), Integer.parseInt(tok[7])));
                        break;
                    default:
                        throw new IOException("Unknown record: " + tok[0]);
                }
            }
            return d;
        }
    }

    /**
     * Method saveProject.
     *
     * @param out parameter
     * @param d   parameter
     */
    private void saveProject(PrintWriter out, MapData d) {
        out.println("# DETH-JAVA-PROJECT v1");
        for (var v : d.vertices) out.printf("VERTEX %d %d%n", v.x(), v.y());
        for (var l : d.linedefs)
            out.printf("LINE %d %d %d %d %d %d %d%n", l.v1, l.v2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
    }

    /**
     * Method setMap.
     *
     * @param map  parameter
     * @param name parameter
     */
    public void setMap(MapData map, String name) {
        this.map = map;
        this.mapName = name;
        fitToView();
        selLines.clear();
        selVertices.clear();
        selThings.clear();
        selSectors.clear();
        undoStack.clear();
        redoStack.clear();
        pushUndo();
        clampSelectionToMap();
        notifySelection();
    }

    /**
     * Method toggleGrid.
     */
    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }

    /**
     * Method fitToView.
     */
    public void fitToView() {
        if (map == null || map.vertices.isEmpty()) {
            repaint();
            return;
        }
        Rectangle bounds = map.getBounds();
        Insets in = getInsets();
        int w = getWidth() - in.left - in.right;
        int h = getHeight() - in.top - in.bottom;
        if (w <= 0 || h <= 0) return;

        double pad = 40.0;
        double sx = (w - 2 * pad) / Math.max(1.0, bounds.getWidth());
        double sy = (h - 2 * pad) / Math.max(1.0, bounds.getHeight());
        double scale = Math.min(sx, sy);

        worldToScreen.setTransform(1, 0, 0, 1, 0, 0);

        worldToScreen.translate(pad - bounds.getMinX() * scale, h - pad + bounds.getMinY() * scale);
        worldToScreen.scale(scale, -scale);
        repaint();
    }

    /**
     * Method setupKeyBindings.
     */
    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "fit");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "grid");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "snap");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0), "gridDown");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0), "gridUp");


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_DOWN_MASK), "zin");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "zin");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zout");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "zout");


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "toolSelect");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "toolDraw");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "toolInsert");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0), "toolThing");


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");


        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "spacePanOn");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "spacePanOff");


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSel");

        am.put("fit", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                fitToView();
            }
        });
        am.put("grid", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleGrid();
            }
        });
        am.put("snap", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                snap = !snap;
                repaint();
                updateStatus(null);
            }
        });
        am.put("gridDown", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustGrid(-1);
            }
        });
        am.put("gridUp", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustGrid(1);
            }
        });
        am.put("zin", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomAt(getWidth() / 2, getHeight() / 2, 1.1);
            }
        });
        am.put("zout", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomAt(getWidth() / 2, getHeight() / 2, 1 / 1.1);
            }
        });

        am.put("toolSelect", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(Tool.SELECT);
            }
        });
        am.put("toolDraw", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(Tool.DRAW_LINE);
            }
        });
        am.put("toolInsert", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(Tool.INSERT_VERTEX);
            }
        });
        am.put("toolThing", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(Tool.THING);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0), "toolSector");
        am.put("toolSector", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(Tool.SECTOR);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "rotateLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "rotateRight");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "rotateReset");
        am.put("rotateLeft", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                MapPanel.this.rotateSelectedThings(-45);
            }
        });
        am.put("rotateRight", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                MapPanel.this.rotateSelectedThings(45);
            }
        });
        am.put("rotateReset", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                MapPanel.this.setSelectedThingsAngle(0);
            }
        });


        am.put("undo", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK), "z1");
        am.put("z1", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(1);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK), "z2");
        am.put("z2", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(2);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK), "z3");
        am.put("z3", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(3);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK), "z4");
        am.put("z4", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(4);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK), "z5");
        am.put("z5", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(5);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK), "z6");
        am.put("z6", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(6);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.ALT_DOWN_MASK), "z7");
        am.put("z7", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(7);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.ALT_DOWN_MASK), "z8");
        am.put("z8", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(8);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.ALT_DOWN_MASK), "z9");
        am.put("z9", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(9);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_DOWN_MASK), "z0");
        am.put("z0", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomPreset(0);
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "toggleDisp");
        am.put("toggleDisp", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDisplayMode();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "floorPlus");
        am.put("floorPlus", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustSectorHeights(8, 0);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "floorMinus");
        am.put("floorMinus", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustSectorHeights(-8, 0);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_DOWN_MASK), "ceilPlus");
        am.put("ceilPlus", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustSectorHeights(0, 8);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.SHIFT_DOWN_MASK), "ceilMinus");
        am.put("ceilMinus", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustSectorHeights(0, -8);
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_DOWN_MASK), "selFrontSectors");
        am.put("selFrontSectors", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFrontSectorsOfSelectedLines();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK), "selBackSectors");
        am.put("selBackSectors", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBackSectorsOfSelectedLines();
            }
        });
        am.put("redo", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "dupSel");
        am.put("dupSel", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateSelection(64, 64);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK), "rotSelNeg");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK), "rotSelPos");
        am.put("rotSelNeg", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateSelectedGeometry(-15.0);
            }
        });
        am.put("rotSelPos", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateSelectedGeometry(15.0);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK), "mirrorH");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK), "mirrorV");
        am.put("mirrorH", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                mirrorSelected(true);
            }
        });
        am.put("mirrorV", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                mirrorSelected(false);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "snapSel");
        am.put("snapSel", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                snapSelectedToGrid();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "cleanupUnusedVerts");
        am.put("cleanupUnusedVerts", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanupUnusedVertices();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "zoomSel");

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "invertSel");
        am.put("invertSel", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                invertSelection();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "flipLines");
        am.put("flipLines", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                flipSelectedLinedefs();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "splitLinesMid");
        am.put("splitLinesMid", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                splitSelectedLinedefsMidpoint();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "weldDupVerts");
        am.put("weldDupVerts", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                weldDuplicateVertices();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "nudgeL");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nudgeR");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "nudgeU");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "nudgeD");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), "nudgeLfine");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), "nudgeRfine");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK), "nudgeUfine");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK), "nudgeDfine");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "nudgeLbig");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "nudgeRbig");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "nudgeUbig");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "nudgeDbig");
        am.put("nudgeL", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(-gridStep, 0);
            }
        });
        am.put("nudgeR", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(gridStep, 0);
            }
        });
        am.put("nudgeU", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, gridStep);
            }
        });
        am.put("nudgeD", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, -gridStep);
            }
        });
        am.put("nudgeLfine", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(-1, 0);
            }
        });
        am.put("nudgeRfine", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(1, 0);
            }
        });
        am.put("nudgeUfine", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, 1);
            }
        });
        am.put("nudgeDfine", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, -1);
            }
        });
        am.put("nudgeLbig", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(-8 * gridStep, 0);
            }
        });
        am.put("nudgeRbig", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(8 * gridStep, 0);
            }
        });
        am.put("nudgeUbig", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, 8 * gridStep);
            }
        });
        am.put("nudgeDbig", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                nudgeSelected(0, -8 * gridStep);
            }
        });

        am.put("zoomSel", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomToSelection();
            }
        });
        am.put("delete", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelection();
            }
        });

        am.put("spacePanOn", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePan = true;
            }
        });
        am.put("spacePanOff", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePan = false;
                if (mode == Mode.PAN) mode = Mode.IDLE;
            }
        });
        am.put("clearSel", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                selVertices.clear();
                selLines.clear();
                selThings.clear();
                selSectors.clear();
                repaint();
                updateStatus(null);
                notifySelection();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "measureToggle");
        am.put("measureToggle", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMeasure();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "growSelLines");
        am.put("growSelLines", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                growSelectedLinedefs();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "shrinkSelLines");
        am.put("shrinkSelLines", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                shrinkSelectedLinedefs();
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.ALT_DOWN_MASK), "scaleDown");
        am.put("scaleDown", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleSelected(0.9, 0.9);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.ALT_DOWN_MASK), "scaleUp");
        am.put("scaleUp", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleSelected(1.1, 1.1);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK), "saveView1");
        am.put("saveView1", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                saveViewBookmark(0);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK), "saveView2");
        am.put("saveView2", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                saveViewBookmark(1);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_DOWN_MASK), "saveView3");
        am.put("saveView3", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                saveViewBookmark(2);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK), "saveView4");
        am.put("saveView4", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                saveViewBookmark(3);
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "goView1");
        am.put("goView1", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                goToViewBookmark(0);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "goView2");
        am.put("goView2", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                goToViewBookmark(1);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "goView3");
        am.put("goView3", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                goToViewBookmark(2);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "goView4");
        am.put("goView4", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                goToViewBookmark(3);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK), "rotNeg");
        am.put("rotNeg", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateSelectedGeometry(-15.0);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK), "rotPos");
        am.put("rotPos", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateSelectedGeometry(15.0);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK), "mirH");
        am.put("mirH", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                mirrorSelected(true);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK), "mirV");
        am.put("mirV", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                mirrorSelected(false);
            }
        });


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "cleanupVerts");
        am.put("cleanupVerts", new AbstractAction() {
            /**
             * Method actionPerformed.
             * @param e parameter
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanupUnusedVertices();
            }
        });

    }

    /**
     * Method adjustGrid.
     *
     * @param dir parameter
     */
    private void adjustGrid(int dir) {
        int[] steps = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512};
        int idx = 4;
        for (int i = 0; i < steps.length; i++)
        /**
         * Constructor for if.
         * @param gridStep parameter
         */
            if (steps[i] == gridStep) {
                idx = i;
                break;
            }
        idx = Math.max(0, Math.min(steps.length - 1, idx + dir));
        gridStep = steps[idx];
        repaint();
        updateStatus(null);
    }

    /**
     * Method paintComponent.
     *
     * @param g0 parameter
     */
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (back.getWidth() != getWidth() || back.getHeight() != getHeight()) {
            back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g = back.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(getBackground());
            g.fillRect(0, 0, back.getWidth(), back.getHeight());


            if (showGrid) drawGrid(g, gridStep, 20000);


            /**
             * Constructor for if.
             * @param null parameter
             */
            if (map != null) {
                Graphics2D gw = (Graphics2D) g.create();
                gw.transform(worldToScreen);

                double px = 1.0 / currentScale();


                for (int i = 0; i < map.linedefs.size(); i++) {
                    var l = map.linedefs.get(i);
                    var v1 = map.vertices.get(l.v1);
                    var v2 = map.vertices.get(l.v2);
                    boolean rightValid = l.rightSidedef != 0xFFFF;
                    boolean leftValid = l.leftSidedef != 0xFFFF;
                    boolean twoSided = rightValid && leftValid;
                    boolean impassable = (l.flags & 0x0001) != 0;

                    Color baseColor;
                    float w = (float) (twoSided ? 1 * px : 2 * px);
                    /**
                     * Constructor for if.
                     * @param impassable parameter
                     */
                    if (impassable) {
                        baseColor = new Color(255, 110, 80);
                        w = (float) (3 * px);
                    } else if (twoSided) {
                        baseColor = new Color(120, 200, 255);
                    } else {
                        baseColor = Color.WHITE;
                    }

                    Color col = baseColor;
                    if (i == hoverLine) col = new Color(140, 255, 140);
                    if (selLines.contains(i)) col = new Color(255, 220, 120);

                    gw.setColor(col);
                    gw.setStroke(new BasicStroke(w));
                    gw.draw(new Line2D.Double(v1.getX(), v1.getY(), v2.getX(), v2.getY()));

                    if (linedefTouchesSelectedSector(l) && !selLines.contains(i)) {
                        Stroke oldSt = gw.getStroke();
                        gw.setStroke(new BasicStroke((float) (3 * px)));
                        gw.setColor(new Color(255, 0, 255, 120));
                        gw.draw(new Line2D.Double(v1.getX(), v1.getY(), v2.getX(), v2.getY()));
                        gw.setStroke(oldSt);
                    }
                }


                double s = currentScale();
                double trPx = Math.max(8.0, Math.min(20.0, 8.0 * s));
                double tr = trPx * px;
                for (int i = 0; i < map.things.size(); i++) {
                    var t = map.things.get(i);
                    Color col = new Color(200, 160, 255);
                    if (i == hoverThing) col = new Color(140, 255, 140);
                    if (selThings.contains(i)) col = new Color(255, 220, 120);
                    gw.setColor(col);
                    java.awt.Polygon poly = new java.awt.Polygon();
                    poly.addPoint((int) Math.round(t.getX()), (int) Math.round(t.getY() - tr));
                    poly.addPoint((int) Math.round(t.getX() + tr), (int) Math.round(t.getY()));
                    poly.addPoint((int) Math.round(t.getX()), (int) Math.round(t.getY() + tr));
                    poly.addPoint((int) Math.round(t.getX() - tr), (int) Math.round(t.getY()));
                    gw.fill(poly);
                    double ang = Math.toRadians(t.angle());
                    double fx = t.getX() + Math.cos(ang) * tr * 1.5;
                    double fy = t.getY() + Math.sin(ang) * tr * 1.5;
                    gw.setColor(new Color(30, 30, 30));
                    gw.setStroke(new BasicStroke((float) (1 * px)));
                    gw.draw(new Line2D.Double(t.getX(), t.getY(), fx, fy));
                }


                double r = 3 * px;
                for (int i = 0; i < map.vertices.size(); i++) {
                    var v = map.vertices.get(i);
                    Color col = new Color(180, 180, 180);
                    if (i == hoverVertex) col = new Color(140, 255, 140);
                    if (selVertices.contains(i)) col = new Color(255, 220, 120);
                    gw.setColor(col);
                    gw.fill(new java.awt.geom.Ellipse2D.Double(v.getX() - r, v.getY() - r, r * 2, r * 2));
                }


                /**
                 * Constructor for if.
                 * @param null parameter
                 */
                if (tool == Tool.DRAW_LINE && mode == Mode.DRAWING && drawStartVertex != null && drawCurrentWorld != null) {
                    gw.setColor(new Color(255, 250, 120));
                    gw.setStroke(new BasicStroke((float) (2 * px)));
                    var v1 = map.vertices.get(drawStartVertex);
                    gw.draw(new Line2D.Double(v1.getX(), v1.getY(), drawCurrentWorld.getX(), drawCurrentWorld.getY()));
                }

                gw.dispose();


                g.setColor(Color.LIGHT_GRAY);
                g.drawString(String.format("%s — V:%d L:%d  |  Snap:%s step=%d  |  Grid:%s  |  Tool:%s  |  Mode:%s",
                        mapName, map.vertices.size(), map.linedefs.size(),
                        snap ? "on" : "off", gridStep, showGrid ? "on" : "off", tool, mode), 10, 20);
                int y = getHeight() - 40;
                g.drawString("Legend:", 10, y);
                drawLegend(g, 60, y - 10);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("Open a WAD (File → Open WAD…) or Open Project", 10, 20);
            }


            /**
             * Constructor for if.
             * @param null parameter
             */
            if (mode == Mode.BOX && boxStartScreen != null && boxEndScreen != null) {
                int x = Math.min(boxStartScreen.x, boxEndScreen.x);
                int y = Math.min(boxStartScreen.y, boxEndScreen.y);
                int w = Math.abs(boxStartScreen.x - boxEndScreen.x);
                int h = Math.abs(boxStartScreen.y - boxEndScreen.y);
                g.setColor(new Color(100, 160, 255, 60));
                g.fillRect(x, y, w, h);
                g.setColor(new Color(100, 160, 255, 180));
                g.drawRect(x, y, w, h);
            }
        } finally {
            g.dispose();
        }


        /**
         * Constructor for if.
         * @param null parameter
         */
        if (measuring && measureStart != null && measureEnd != null) {
            Graphics2D gms = (Graphics2D) g0.create();

            java.awt.geom.Point2D sA = new java.awt.geom.Point2D.Double();
            java.awt.geom.Point2D sB = new java.awt.geom.Point2D.Double();
            worldToScreen.transform(measureStart, sA);
            worldToScreen.transform(measureEnd, sB);
            gms.setColor(new Color(255, 255, 160));
            gms.setStroke(new BasicStroke(1.5f));
            gms.draw(new java.awt.geom.Line2D.Double(sA, sB));
            double dx = measureEnd.getX() - measureStart.getX();
            double dy = measureEnd.getY() - measureStart.getY();
            double dist = Math.hypot(dx, dy);
            String label = String.format(" %.1f ", dist);
            int tx = (int) Math.round((sA.getX() + sB.getX()) / 2);
            int ty = (int) Math.round((sA.getY() + sB.getY()) / 2);
            gms.setColor(new Color(0, 0, 0, 170));
            gms.fillRect(tx - 20, ty - 14, 50, 16);
            gms.setColor(new Color(255, 255, 200));
            gms.drawString(label, tx - 18, ty - 2);
            gms.dispose();
        }

        g0.drawImage(back, 0, 0, null);
    }

    /**
     * Method drawLegend.
     *
     * @param g parameter
     * @param x parameter
     * @param y parameter
     */
    private void drawLegend(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(x, y, 30, 2);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("1-sided", x + 35, y + 5);
        g.setColor(new Color(120, 200, 255));
        g.fillRect(x + 95, y, 30, 1);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("2-sided", x + 130, y + 5);
        g.setColor(new Color(255, 110, 80));
        g.fillRect(x + 190, y - 1, 30, 4);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Impassable", x + 225, y + 5);
        g.setColor(new Color(255, 220, 120));
        g.fillRect(x + 305, y - 1, 30, 4);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Selected", x + 340, y + 5);
        g.setColor(new Color(140, 255, 140));
        g.fillRect(x + 405, y - 1, 30, 4);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Hover", x + 440, y + 5);
        g.setColor(new Color(200, 160, 255));
        g.fillRect(x + 505, y - 1, 30, 4);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Thing", x + 540, y + 5);
    }

    /**
     * Method drawGrid.
     *
     * @param g      parameter
     * @param step   parameter
     * @param extent parameter
     */
    private void drawGrid(Graphics2D g, int step, int extent) {
        Graphics2D gw = (Graphics2D) g.create();
        gw.transform(worldToScreen);

        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < 2; i++) {
            int mul = (i == 0) ? step : step * 8;
            int alpha = (i == 0) ? 40 : 70;
            gw.setColor(new Color(255, 255, 255, alpha));
            for (int x = -extent; x <= extent; x += mul) gw.draw(new Line2D.Double(x, -extent, x, extent));
            for (int y = -extent; y <= extent; y += mul) gw.draw(new Line2D.Double(-extent, y, extent, y));
        }
        gw.dispose();
    }

    /**
     * Method screenToWorld.
     *
     * @param p parameter
     * @return result
     */
    private Point2D screenToWorld(Point p) {
        try {
            AffineTransform inv = worldToScreen.createInverse();
            Point2D.Double out = new Point2D.Double();
            inv.transform(p, out);
            return out;
        } catch (Exception ex) {
            return new Point2D.Double();
        }
    }

    /**
     * Method snapPoint.
     *
     * @param pt parameter
     * @return result
     */
    private Point2D.Double snapPoint(Point2D pt) {
        if (!snap) return new Point2D.Double(pt.getX(), pt.getY());
        double gx = Math.round(pt.getX() / gridStep) * gridStep;
        double gy = Math.round(pt.getY() / gridStep) * gridStep;
        return new Point2D.Double(gx, gy);
    }

    /**
     * Method updateStatus.
     *
     * @param p parameter
     */
    private void updateStatus(Point p) {
        if (status == null) return;
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (p == null) {
            status.setText(String.format("Zoom: %.3f  Sel: V=%d L=%d T=%d S=%d  |  Snap:%s step=%d",
                    currentScale(), selVertices.size(), selLines.size(), selThings.size(), selSectors.size(), snap ? "on" : "off", gridStep));
            return;
        }
        Point2D w = screenToWorld(p);
        String hover = "";
        if (hoverVertex >= 0) hover = String.format("  Hover: Vertex %d", hoverVertex);
        /**
         * Method if.
         * @param 0 parameter
         * @return result
         */
        else if (hoverLine >= 0) {
            var ld = map.linedefs.get(hoverLine);
            double len = length(map.vertices.get(ld.v1), map.vertices.get(ld.v2));
            hover = String.format("  Hover: Linedef %d  flags=0x%04X  special=%d  tag=%d  len=%.1f",
                    hoverLine, ld.flags, ld.special, ld.tag, len);
        }
        status.setText(String.format("World: (%.1f, %.1f)  Zoom: %.3f  Sel: V=%d L=%d T=%d S=%d  |  Snap:%s step=%d%s",
                w.getX(), w.getY(), currentScale(), selVertices.size(), selLines.size(), selThings.size(), selSectors.size(), snap ? "on" : "off", gridStep, hover));
    }

    /**
     * Method currentScale.
     *
     * @return result
     */
    private double currentScale() {
        return Math.hypot(worldToScreen.getScaleX(), worldToScreen.getShearX());
    }

    /**
     * Method zoomAt.
     *
     * @param sx    parameter
     * @param sy    parameter
     * @param scale parameter
     */
    public void zoomAt(int sx, int sy, double scale) {
        AffineTransform at = new AffineTransform();
        at.translate(sx, sy);
        at.scale(scale, scale);
        at.translate(-sx, -sy);
        worldToScreen.preConcatenate(at);
        repaint();
        updateStatus(new Point(sx, sy));
    }

    /**
     * Method updateHover.
     *
     * @param p parameter
     */
    private void updateHover(Point p) {
        if (map == null) return;
        Point2D w = screenToWorld(p);
        double tolPx = Math.max(6.0, Math.min(18.0, 6.0 * currentScale()));
        double tolWorld = tolPx / currentScale();


        int vHit = -1;
        double bestDv = Double.MAX_VALUE;
        for (int i = 0; i < map.vertices.size(); i++) {
            var v = map.vertices.get(i);
            double dv = Math.hypot(v.getX() - w.getX(), v.getY() - w.getY());
            /**
             * Constructor for if.
             * @param bestDv parameter
             */
            if (dv < tolWorld && dv < bestDv) {
                bestDv = dv;
                vHit = i;
            }
        }


        int tHit = -1;
        for (int i = 0; i < map.things.size(); i++) {
            var t = map.things.get(i);
            double dv = Math.hypot(t.getX() - w.getX(), t.getY() - w.getY());
            /**
             * Constructor for if.
             * @param bestDv parameter
             */
            if (dv < tolWorld && dv < bestDv) {
                bestDv = dv;
                tHit = i;
            }
        }


        int lHit = -1;
        double bestDl = tolWorld;
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            double d = pointToSegment(w.getX(), w.getY(), a.getX(), a.getY(), b.getX(), b.getY());
            /**
             * Constructor for if.
             * @param bestDl parameter
             */
            if (d < bestDl) {
                bestDl = d;
                lHit = i;
            }
        }

        hoverVertex = vHit;
        hoverLine = (vHit >= 0 || tHit >= 0) ? -1 : lHit;
        hoverThing = (vHit >= 0) ? -1 : tHit;
    }

    /**
     * Method mousePressed.
     *
     * @param e parameter
     */
    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        lastDrag = e.getPoint();
        updateHover(e.getPoint());
        updateStatus(e.getPoint());

        if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e) || spacePan) {
            mode = Mode.PAN;
            return;
        }
        if (!SwingUtilities.isLeftMouseButton(e)) {
            repaint();
            return;
        }

        /**
         * Constructor for if.
         * @param measuring parameter
         */
        if (measuring) {
            measureStart = screenToWorld(e.getPoint());
            measureEnd = measureStart;
            repaint();
            return;
        }

        boolean ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

        /**
         * Constructor for switch.
         * @param tool parameter
         */
        switch (tool) {
            case SECTOR -> {
                int sec = (hoverLine >= 0) ? sectorOnSide(map.linedefs.get(hoverLine), e.getPoint()) : -1;
                /**
                 * Constructor for if.
                 * @param 0 parameter
                 */
                if (sec >= 0) {
                    /**
                     * Constructor for if.
                     * @param ctrl parameter
                     */
                    if (ctrl) {
                        if (!selSectors.add(sec)) selSectors.remove(sec);
                    } else {
                        selVertices.clear();
                        selLines.clear();
                        selThings.clear();
                        selSectors.clear();
                        selSectors.add(sec);
                    }
                    notifySelection();
                }
            }

            case SELECT -> {
                /**
                 * Constructor for if.
                 * @param 0 parameter
                 */
                if (hoverThing >= 0) {
                    /**
                     * Constructor for if.
                     * @param ctrl parameter
                     */
                    if (!ctrl) {
                        if (!selThings.contains(hoverThing)) {
                            selVertices.clear();
                            selLines.clear();
                            selThings.clear();
                            selSectors.clear();
                            selThings.add(hoverThing);
                            notifySelection();
                        }
                        mode = Mode.MOVE_THINGS;
                        moveStartWorld = screenToWorld(e.getPoint());


                        thingMoveOrig.clear();
                        /**
                         * Constructor for for.
                         * @param selThings parameter
                         */
                        for (int idx : selThings) {
                            var t = map.things.get(idx);
                            thingMoveOrig.put(idx, new java.awt.geom.Point2D.Double(t.x(), t.y()));
                        }
                    }
                } else if (hoverVertex >= 0) {
                    /**
                     * Constructor for if.
                     * @param ctrl parameter
                     */
                    if (!ctrl) {
                        if (!selVertices.contains(hoverVertex)) {
                            selVertices.clear();
                            selLines.clear();
                            selThings.clear();
                            selSectors.clear();
                            selVertices.add(hoverVertex);
                            notifySelection();
                        }
                        mode = Mode.MOVE_VERTS;
                        moveOrig.clear();
                        /**
                         * Constructor for for.
                         * @param selVertices parameter
                         */
                        for (int idx : selVertices) {
                            var v = map.vertices.get(idx);
                            moveOrig.put(idx, new java.awt.geom.Point2D.Double(v.getX(), v.getY()));
                        }
                        moveStartWorld = screenToWorld(e.getPoint());
                    }
                } else if (hoverLine >= 0) {

                    mode = Mode.IDLE;

                } else {

                    mode = Mode.BOX;
                    boxStartScreen = e.getPoint();
                    boxEndScreen = e.getPoint();
                    /**
                     * Constructor for if.
                     * @param ctrl parameter
                     */
                    if (!ctrl) {
                        selVertices.clear();
                        selLines.clear();
                        selThings.clear();
                        selSectors.clear();
                        notifySelection();
                    }
                }
            }

            case DRAW_LINE -> {
                /**
                 * Constructor for if.
                 * @param ModeDRAWING parameter
                 */
                if (mode != Mode.DRAWING) {
                    pushUndo();
                    drawStartVertex = (hoverVertex >= 0) ? hoverVertex : addVertex(screenToWorld(e.getPoint()));
                    mode = Mode.DRAWING;
                } else {
                    int endV = (hoverVertex >= 0) ? hoverVertex : addVertex(screenToWorld(e.getPoint()));
                    addLine(drawStartVertex, endV);
                    drawStartVertex = endV;
                }
            }

            case INSERT_VERTEX -> {
                /**
                 * Constructor for if.
                 * @param 0 parameter
                 */
                if (hoverLine >= 0) {
                    pushUndo();
                    insertVertexOnLine(hoverLine, screenToWorld(e.getPoint()));
                }
            }

            case THING -> {
                pushUndo();
                java.awt.geom.Point2D.Double p = snapPoint(screenToWorld(e.getPoint()));
                map.things.add(new MapData.Thing((int) Math.round(p.x), (int) Math.round(p.y), 0, thingTemplateType, thingTemplateFlags));
                selThings.clear();
                selThings.add(map.things.size() - 1);
                notifySelection();
            }
        }

        repaint();
    }

    /**
     * Method mouseDragged.
     *
     * @param e parameter
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        /**
         * Constructor for if.
         * @param measuring parameter
         */
        if (measuring) {
            measureEnd = screenToWorld(e.getPoint());
            repaint();
            updateStatus(e.getPoint());
            lastDrag = e.getPoint();
            return;
        }
        /**
         * Constructor for switch.
         * @param mode parameter
         */
        switch (mode) {
            case PAN -> {
                double dx = p.x - lastDrag.x, dy = p.y - lastDrag.y;
                worldToScreen.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
            }
            case BOX -> {
                boxEndScreen = p;
            }
            case MOVE_VERTS -> {
                Point2D now = screenToWorld(p);
                double dx = now.getX() - moveStartWorld.getX();
                double dy = now.getY() - moveStartWorld.getY();
                for (var entry : moveOrig.entrySet()) {
                    int idx = entry.getKey();
                    var o = entry.getValue();
                    double nx = o.x + dx, ny = o.y + dy;
                    /**
                     * Constructor for if.
                     * @param snap parameter
                     */
                    if (snap) {
                        nx = Math.round(nx / gridStep) * gridStep;
                        ny = Math.round(ny / gridStep) * gridStep;
                    }
                    map.vertices.set(idx, new MapData.Vertex((int) Math.round(nx), (int) Math.round(ny)));
                }
            }

            case MOVE_THINGS -> {
                java.awt.geom.Point2D now = screenToWorld(p);
                double dx = now.getX() - moveStartWorld.getX();
                double dy = now.getY() - moveStartWorld.getY();

                for (var entry : thingMoveOrig.entrySet()) {
                    int idx = entry.getKey();
                    var o = entry.getValue();

                    double nx = o.x + dx;
                    double ny = o.y + dy;

                    /**
                     * Constructor for if.
                     * @param snap parameter
                     */
                    if (snap) {
                        nx = Math.round(nx / gridStep) * gridStep;
                        ny = Math.round(ny / gridStep) * gridStep;
                    }

                    var t = map.things.get(idx);
                    map.things.set(idx, new MapData.Thing(
                            (int) Math.round(nx),
                            (int) Math.round(ny),
                            t.angle(), t.type(), t.flags()
                    ));
                }
            }

            case DRAWING -> {

                drawCurrentWorld = snapPoint(screenToWorld(p));
            }
            default -> {
            }
        }
        lastDrag = p;
        repaint();
        if (mode != Mode.BOX) updateHover(p);
        updateStatus(p);
    }

    /**
     * Method mouseReleased.
     *
     * @param e parameter
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        /**
         * Constructor for if.
         * @param measuring parameter
         */
        if (measuring) {
            measureEnd = screenToWorld(e.getPoint());
            repaint();
            updateStatus(e.getPoint());
            return;
        }

        /**
         * Constructor for if.
         * @param ModeBOX parameter
         */
        if (mode == Mode.BOX) {
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (boxStartScreen != null && boxEndScreen != null) {
                Rectangle sel = new Rectangle(
                        Math.min(boxStartScreen.x, boxEndScreen.x),
                        Math.min(boxStartScreen.y, boxEndScreen.y),
                        Math.abs(boxStartScreen.x - boxEndScreen.x),
                        Math.abs(boxStartScreen.y - boxEndScreen.y));


                for (int i = 0; i < map.vertices.size(); i++) {

                    var v = map.vertices.get(i);
                    Point2D sp = worldToScreen(new Point2D.Double(v.getX(), v.getY()));
                    if (sel.contains(sp)) selVertices.add(i);
                }


                for (int i = 0; i < map.things.size(); i++) {
                    var t = map.things.get(i);
                    java.awt.geom.Point2D spT = worldToScreen(new java.awt.geom.Point2D.Double(t.getX(), t.getY()));
                    if (sel.contains(spT)) selThings.add(i);
                }


                selLines.clear();
                for (int i = 0; i < map.linedefs.size(); i++) {
                    var l = map.linedefs.get(i);
                    if (selVertices.contains(l.v1) && selVertices.contains(l.v2)) selLines.add(i);
                }
                notifySelection();
            }
            boxStartScreen = boxEndScreen = null;
        }
        /**
         * Constructor for if.
         * @param ModeMOVE_THINGS parameter
         */
        if (mode == Mode.MOVE_VERTS || mode == Mode.MOVE_THINGS) {
            pushUndo();
            moveOrig.clear();
            thingMoveOrig.clear();
        }
        /**
         * Constructor for if.
         * @param ToolDRAW_LINE parameter
         */
        if (mode == Mode.PAN && tool == Tool.DRAW_LINE) {

            mode = Mode.IDLE;
            drawStartVertex = null;
            drawCurrentWorld = null;
        } else {
            mode = (tool == Tool.DRAW_LINE && mode == Mode.DRAWING) ? Mode.DRAWING : Mode.IDLE;
        }
        repaint();
        updateHover(e.getPoint());
        updateStatus(e.getPoint());
    }

    /**
     * Method mouseClicked.
     *
     * @param e parameter
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && hoverLine >= 0) {
            var l = map.linedefs.get(hoverLine);
            if (l.rightSidedef >= 0 && l.rightSidedef != 0xFFFF)
                selSectors.add(map.sidedefs.get(l.rightSidedef).sectorIndex());
            if (l.leftSidedef >= 0 && l.leftSidedef != 0xFFFF)
                selSectors.add(map.sidedefs.get(l.leftSidedef).sectorIndex());
            notifySelection();
            repaint();
            return;
        }

        if (tool != Tool.SELECT) return;
        if (e.getButton() != MouseEvent.BUTTON1) return;
        boolean ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (hoverThing >= 0) {
            /**
             * Constructor for if.
             * @param ctrl parameter
             */
            if (ctrl) {
                if (!selThings.add(hoverThing)) selThings.remove(hoverThing);
            } else {
                selVertices.clear();
                selLines.clear();
                selThings.clear();
                selSectors.clear();
                selThings.add(hoverThing);
            }
        } else if (hoverVertex >= 0) {

            /**
             * Constructor for if.
             * @param ctrl parameter
             */
            if (ctrl) {
                if (!selVertices.add(hoverVertex)) selVertices.remove(hoverVertex);
            } else {
                selVertices.clear();
                selVertices.add(hoverVertex);
                selLines.clear();
            }
        } else if (hoverLine >= 0) {
            /**
             * Constructor for if.
             * @param ctrl parameter
             */
            if (ctrl) {
                if (!selLines.add(hoverLine)) selLines.remove(hoverLine);
            } else {
                selLines.clear();
                selLines.add(hoverLine);
                selVertices.clear();
            }
        } else {
            /**
             * Constructor for if.
             * @param ctrl parameter
             */
            if (!ctrl) {
                selVertices.clear();
                selLines.clear();
                selThings.clear();
                selSectors.clear();
            }
        }
        repaint();
        notifySelection();
        updateStatus(e.getPoint());
    }

    /**
     * Method mouseWheelMoved.
     *
     * @param e parameter
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scale = Math.pow(1.1, -e.getWheelRotation());
        zoomAt(e.getX(), e.getY(), scale);
        updateHover(e.getPoint());
    }

    /**
     * Method mouseMoved.
     *
     * @param e parameter
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        /**
         * Constructor for if.
         * @param measuring parameter
         */
        if (measuring) {
            measureEnd = screenToWorld(e.getPoint());
            repaint();
            updateStatus(e.getPoint());
            return;
        }
        updateHover(e.getPoint());
        lastMouseWorld = screenToWorld(e.getPoint());
        /**
         * Constructor for if.
         * @param ModeDRAWING parameter
         */
        if (tool == Tool.DRAW_LINE && mode == Mode.DRAWING) {
            java.awt.geom.Point2D w = screenToWorld(e.getPoint());
            if (drawStartVertex != null && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                var v0 = map.vertices.get(drawStartVertex);
                double dx = w.getX() - v0.getX();
                double dy = w.getY() - v0.getY();
                double ang = Math.atan2(dy, dx);
                double snapA = Math.PI / 4.0;
                ang = Math.round(ang / snapA) * snapA;
                double len = Math.hypot(dx, dy);
                w = new Point2D.Double(v0.getX() + Math.cos(ang) * len, v0.getY() + Math.sin(ang) * len);
            }
            drawCurrentWorld = snapPoint(w);
        }
        repaint();
        updateStatus(e.getPoint());
    }

    /**
     * Method mouseEntered.
     *
     * @param e parameter
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Method mouseExited.
     *
     * @param e parameter
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Method addVertex.
     *
     * @param world parameter
     * @return result
     */
    private int addVertex(Point2D world) {
        Point2D.Double p = snapPoint(world);
        map.vertices.add(new MapData.Vertex((int) Math.round(p.x), (int) Math.round(p.y)));
        return map.vertices.size() - 1;
    }

    /**
     * Method addLine.
     *
     * @param v1 parameter
     * @param v2 parameter
     */
    private void addLine(int v1, int v2) {
        if (v1 == v2) return;
        map.linedefs.add(new MapData.Linedef(v1, v2, 0, 0, 0, 0xFFFF, 0xFFFF));
        repaint();
        updateStatus(null);
    }

    /**
     * Method insertVertexOnLineGetIndices.
     *
     * @param lineIndex parameter
     * @param world     parameter
     * @return result
     */
    private int[] insertVertexOnLineGetIndices(int lineIndex, java.awt.geom.Point2D world) {
        if (lineIndex < 0 || lineIndex >= map.linedefs.size()) return new int[]{-1, -1};
        var l = map.linedefs.get(lineIndex);
        java.awt.geom.Point2D.Double p = snapPoint(closestPointOnSegment(world, map.vertices.get(l.v1), map.vertices.get(l.v2)));
        int newV = addVertex(p);
        MapData.Linedef a = new MapData.Linedef(l.v1, newV, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
        MapData.Linedef b = new MapData.Linedef(newV, l.v2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
        map.linedefs.set(lineIndex, a);
        map.linedefs.add(b);
        return new int[]{newV, map.linedefs.size() - 1};
    }

    /**
     * Method insertVertexOnLine.
     *
     * @param lineIndex parameter
     * @param world     parameter
     */
    private void insertVertexOnLine(int lineIndex, Point2D world) {
        if (lineIndex < 0 || lineIndex >= map.linedefs.size()) return;
        var l = map.linedefs.get(lineIndex);
        Point2D.Double p = snapPoint(closestPointOnSegment(world, map.vertices.get(l.v1), map.vertices.get(l.v2)));
        int newV = addVertex(p);

        MapData.Linedef a = new MapData.Linedef(l.v1, newV, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
        MapData.Linedef b = new MapData.Linedef(newV, l.v2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);

        map.linedefs.set(lineIndex, a);
        map.linedefs.add(b);
    }

    /**
     * Method worldToScreen.
     *
     * @param w parameter
     * @return result
     */
    private Point2D worldToScreen(Point2D w) {
        Point2D out = new Point2D.Double();
        this.worldToScreen.transform(w, out);
        return out;
    }

    public void makeRectanglePillar(int width,
                                    int height,
                                    int floor,
                                    int ceil,
                                    String ftex,
                                    String ctex,
                                    int light) {


        makeRectangleRoom(width, height, floor, ceil, ftex, ctex, light);
    }

    public void makePillar(int width,
                           int height,
                           int floor,
                           int ceil,
                           String ftex,
                           String ctex,
                           int light) {
        makeRectanglePillar(width, height, floor, ceil, ftex, ctex, light);
    }

    public boolean makeHallwayBetweenSelectedLinedefs(int corridorWidth,
                                                      Integer floorHeight, Integer ceilingHeight,
                                                      String floorTex, String ceilTex, Integer lightLevel) {
        if (map == null) return false;

        int[] sel = getSelectedLinedefIndices();
        /**
         * Constructor for if.
         * @param 2 parameter
         */
        if (sel == null || sel.length != 2) {
            warnStatus("Select exactly two linedefs for Make Hallway.");
            return false;
        }

        final int aIdx = sel[0], bIdx = sel[1];
        MapData.Linedef A = map.linedefs.get(aIdx);
        MapData.Linedef B = map.linedefs.get(bIdx);


        int sAr = sectorOf(A.rightSidedef), sAl = sectorOf(A.leftSidedef);
        int sBr = sectorOf(B.rightSidedef), sBl = sectorOf(B.leftSidedef);


        int shared = -1, mode = -1;
        /**
         * Constructor for if.
         * @param sBr parameter
         */
        if (sAr >= 0 && sAr == sBr) {
            shared = sAr;
            mode = 0;
        } else if (sAr >= 0 && sAr == sBl) {
            shared = sAr;
            mode = 1;
        } else if (sAl >= 0 && sAl == sBr) {
            shared = sAl;
            mode = 2;
        } else if (sAl >= 0 && sAl == sBl) {
            shared = sAl;
            mode = 3;
        } else {
            warnStatus("The two linedefs are not adjacent to the same sector.");
            return false;
        }


        MapData.Vertex aV1 = map.vertices.get(A.v1), aV2 = map.vertices.get(A.v2);
        MapData.Vertex bV1 = map.vertices.get(B.v1), bV2 = map.vertices.get(B.v2);
        double aLen = Math.hypot(aV2.getX() - aV1.getX(), aV2.getY() - aV1.getY());
        double bLen = Math.hypot(bV2.getX() - bV1.getX(), bV2.getY() - bV1.getY());
        int w = Math.max(16, Math.min(corridorWidth, (int) Math.floor(Math.min(aLen, bLen)) - 8));


        MapData.Sector src = map.sectors.get(shared);
        int newFH = (floorHeight != null ? floorHeight : src.floorHeight());
        int newCH = (ceilingHeight != null ? ceilingHeight : src.ceilingHeight());
        String newFT = (floorTex != null ? floorTex : src.floorTex());
        String newCT = (ceilTex != null ? ceilTex : src.ceilingTex());
        int newLL = (lightLevel != null ? lightLevel : src.lightLevel());

        pushUndo();


        int newSec = map.sectors.size();
        map.sectors.add(new MapData.Sector(newFH, newCH, newFT, newCT, newLL, src.special(), src.tag()));


        double amx = (aV1.getX() + aV2.getX()) * 0.5, amy = (aV1.getY() + aV2.getY()) * 0.5;
        double bmx = (bV1.getX() + bV2.getX()) * 0.5, bmy = (bV1.getY() + bV2.getY()) * 0.5;


        double[] ta = unitDir(aV1, aV2);
        double[] tb = unitDir(bV1, bV2);
        double hx = 0.5 * w;


        double ax1 = amx - ta[0] * hx, ay1 = amy - ta[1] * hx;
        double ax2 = amx + ta[0] * hx, ay2 = amy + ta[1] * hx;
        double tA1 = paramOnSegment(aV1, aV2, ax1, ay1);
        double tA2 = paramOnSegment(aV1, aV2, ax2, ay2);
        /**
         * Constructor for if.
         * @param tA1 parameter
         */
        if (tA2 < tA1) {
            double tx = ax1, ty = ay1;
            ax1 = ax2;
            ay1 = ay2;
            ax2 = tx;
            ay2 = ty;
            double tt = tA1;
            tA1 = tA2;
            tA2 = tt;
        }


        double bx1 = bmx - tb[0] * hx, by1 = bmy - tb[1] * hx;
        double bx2 = bmx + tb[0] * hx, by2 = bmy + tb[1] * hx;
        double tB1 = paramOnSegment(bV1, bV2, bx1, by1);
        double tB2 = paramOnSegment(bV1, bV2, bx2, by2);
        /**
         * Constructor for if.
         * @param tB1 parameter
         */
        if (tB2 < tB1) {
            double tx = bx1, ty = by1;
            bx1 = bx2;
            by1 = by2;
            bx2 = tx;
            by2 = ty;
            double tt = tB1;
            tB1 = tB2;
            tB2 = tt;
        }


        int[] resAR = insertVertexOnLineGetIndices(aIdx, new java.awt.geom.Point2D.Double(ax2, ay2));
        int aVtx2 = resAR[0];
        int[] resAL = insertVertexOnLineGetIndices(aIdx, new java.awt.geom.Point2D.Double(ax1, ay1));
        int aVtx1 = resAL[0];
        int aMiddleSegIndex = resAL[1];


        int[] resBR = insertVertexOnLineGetIndices(bIdx, new java.awt.geom.Point2D.Double(bx2, by2));
        int bVtx2 = resBR[0];
        int[] resBL = insertVertexOnLineGetIndices(bIdx, new java.awt.geom.Point2D.Double(bx1, by1));
        int bVtx1 = resBL[0];
        int bMiddleSegIndex = resBL[1];


        MapData.Linedef aMid = map.linedefs.get(aMiddleSegIndex);
        MapData.Linedef bMid = map.linedefs.get(bMiddleSegIndex);
        /**
         * Constructor for switch.
         * @param mode parameter
         */
        switch (mode) {
            case 0 -> {
                aMid.rightSidedef = cloneSidedefWithSector(aMid.rightSidedef, newSec);
                bMid.rightSidedef = cloneSidedefWithSector(bMid.rightSidedef, newSec);
            }
            case 1 -> {
                aMid.rightSidedef = cloneSidedefWithSector(aMid.rightSidedef, newSec);
                bMid.leftSidedef = cloneSidedefWithSector(bMid.leftSidedef, newSec);
            }
            case 2 -> {
                aMid.leftSidedef = cloneSidedefWithSector(aMid.leftSidedef, newSec);
                bMid.rightSidedef = cloneSidedefWithSector(bMid.rightSidedef, newSec);
            }
            case 3 -> {
                aMid.leftSidedef = cloneSidedefWithSector(aMid.leftSidedef, newSec);
                bMid.leftSidedef = cloneSidedefWithSector(bMid.leftSidedef, newSec);
            }
        }
        map.linedefs.set(aMiddleSegIndex, aMid);
        map.linedefs.set(bMiddleSegIndex, bMid);


        final int TWO_SIDED = 0x0004;
        int sdL_right = addBlankSidedef(newSec), sdL_left = addBlankSidedef(shared);
        int sdR_right = addBlankSidedef(newSec), sdR_left = addBlankSidedef(shared);

        MapData.Linedef side1 = new MapData.Linedef(aVtx1, bVtx1, TWO_SIDED, 0, 0,
                (mode == 0 || mode == 1) ? sdL_left : sdL_right,
                (mode == 0 || mode == 1) ? sdL_right : sdL_left);
        MapData.Linedef side2 = new MapData.Linedef(bVtx2, aVtx2, TWO_SIDED, 0, 0,
                (mode == 0 || mode == 1) ? sdR_left : sdR_right,
                (mode == 0 || mode == 1) ? sdR_right : sdR_left);


        map.linedefs.add(side1);
        map.linedefs.add(side2);


        selSectors.clear();
        selLines.clear();
        selSectors.add(newSec);
        selLines.add(aMiddleSegIndex);
        selLines.add(bMiddleSegIndex);
        selLines.add(map.linedefs.size() - 2);
        selLines.add(map.linedefs.size() - 1);

        notifySelection();
        repaint();
        infoStatus("Make Hallway: created new corridor sector.");
        return true;
    }

    public void makeDonut(int innerWidth, int innerHeight, int ringThickness,
                          int ringFloor, int ringCeil, String ringF, String ringC, int ringLight,
                          int innerFloor, int innerCeil, String innerF, String innerC, int innerLight) {
        if (map == null) return;
        if (innerWidth <= 0 || innerHeight <= 0 || ringThickness <= 0) return;

        pushUndo();


        double cx = (lastMouseWorld != null) ? lastMouseWorld.getX() : 0.0;
        double cy = (lastMouseWorld != null) ? lastMouseWorld.getY() : 0.0;

        int hwIn = innerWidth / 2;
        int hhIn = innerHeight / 2;
        int hwOut = hwIn + ringThickness;
        int hhOut = hhIn + ringThickness;


        int v0 = map.vertices.size();
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hwOut), (int) Math.round(cy + hhOut)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hwOut), (int) Math.round(cy + hhOut)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hwOut), (int) Math.round(cy - hhOut)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hwOut), (int) Math.round(cy - hhOut)));

        int u0 = map.vertices.size();
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hwIn), (int) Math.round(cy + hhIn)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hwIn), (int) Math.round(cy + hhIn)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hwIn), (int) Math.round(cy - hhIn)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hwIn), (int) Math.round(cy - hhIn)));


        int secRing = map.sectors.size();
        map.sectors.add(new MapData.Sector(
                ringFloor, ringCeil,
                (ringF != null && !ringF.isEmpty() ? ringF : "FLOOR1"),
                (ringC != null && !ringC.isEmpty() ? ringC : "CEIL1"),
                ringLight, 0, 0));

        int secInner = map.sectors.size();
        map.sectors.add(new MapData.Sector(
                innerFloor, innerCeil,
                (innerF != null && !innerF.isEmpty() ? innerF : "FLOOR1"),
                (innerC != null && !innerC.isEmpty() ? innerC : "CEIL1"),
                innerLight, 0, 0));


        int sOuterBase = map.sidedefs.size();
        for (int i = 0; i < 4; i++)
            map.sidedefs.add(new MapData.Sidedef(0, 0, "-", "-", "-", secRing));


        int sRightInnerBase = map.sidedefs.size();
        for (int i = 0; i < 4; i++)
            map.sidedefs.add(new MapData.Sidedef(0, 0, "-", "-", "-", secInner));
        int sLeftRingBase = map.sidedefs.size();
        for (int i = 0; i < 4; i++)
            map.sidedefs.add(new MapData.Sidedef(0, 0, "-", "-", "-", secRing));


        map.linedefs.add(new MapData.Linedef(v0, v0 + 1, 0, 0, 0, sOuterBase, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 1, v0 + 2, 0, 0, 0, sOuterBase + 1, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 2, v0 + 3, 0, 0, 0, sOuterBase + 2, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 3, v0, 0, 0, 0, sOuterBase + 3, 0xFFFF));


        map.linedefs.add(new MapData.Linedef(u0, u0 + 1, 0, 0, 0, sRightInnerBase, sLeftRingBase));
        map.linedefs.add(new MapData.Linedef(u0 + 1, u0 + 2, 0, 0, 0, sRightInnerBase + 1, sLeftRingBase + 1));
        map.linedefs.add(new MapData.Linedef(u0 + 2, u0 + 3, 0, 0, 0, sRightInnerBase + 2, sLeftRingBase + 2));
        map.linedefs.add(new MapData.Linedef(u0 + 3, u0, 0, 0, 0, sRightInnerBase + 3, sLeftRingBase + 3));


        selVertices.clear();
        selLines.clear();
        selThings.clear();
        selSectors.clear();
        selSectors.add(secRing);
        selSectors.add(secInner);
        int newLineStart = map.linedefs.size() - 8;
        for (int i = 0; i < 8; i++) selLines.add(newLineStart + i);
        selVertices.add(v0);
        selVertices.add(v0 + 1);
        selVertices.add(v0 + 2);
        selVertices.add(v0 + 3);
        selVertices.add(u0);
        selVertices.add(u0 + 1);
        selVertices.add(u0 + 2);
        selVertices.add(u0 + 3);

        repaint();
        notifySelection();
        updateStatus(null);
    }

    /**
     * Method rotateAroundPivot.
     *
     * @param angleDeg parameter
     * @param px       parameter
     * @param py       parameter
     */
    public void rotateAroundPivot(double angleDeg, double px, double py) {
        if (map == null) return;


        java.util.Set<Integer> vset = new java.util.HashSet<>(selVertices);
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            vset.add(l.v1);
            vset.add(l.v2);
        }
        boolean any = !vset.isEmpty() || !selThings.isEmpty();
        if (!any) return;

        pushUndo();

        double ang = Math.toRadians(angleDeg);

        java.util.function.BiFunction<Double, Double, java.awt.geom.Point2D.Double> rot = (x, y) -> {
            double dx = x - px, dy = y - py;
            double rx = dx * Math.cos(ang) - dy * Math.sin(ang);
            double ry = dx * Math.sin(ang) + dy * Math.cos(ang);
            return new java.awt.geom.Point2D.Double(px + rx, py + ry);
        };


        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            var v = map.vertices.get(vi);
            var p = rot.apply(v.getX(), v.getY());
            map.vertices.set(vi, new org.deth.wad.MapData.Vertex((int) Math.round(p.x), (int) Math.round(p.y)));
        }


        for (int ti : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(ti);
            var p = rot.apply((double) t.x(), (double) t.y());
            int newAng = Math.floorMod(t.angle() + (int) Math.round(angleDeg), 360);
            map.things.set(ti, new org.deth.wad.MapData.Thing((int) Math.round(p.x), (int) Math.round(p.y), newAng, t.type(), t.flags()));
        }

        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method mirrorAroundPivot.
     *
     * @param horizontal parameter
     * @param px         parameter
     * @param py         parameter
     */
    public void mirrorAroundPivot(boolean horizontal, double px, double py) {
        if (map == null) return;


        java.util.Set<Integer> vset = new java.util.HashSet<>(selVertices);
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            vset.add(l.v1);
            vset.add(l.v2);
        }
        boolean any = !vset.isEmpty() || !selThings.isEmpty();
        if (!any) return;

        pushUndo();


        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            var v = map.vertices.get(vi);
            int nx = horizontal ? (int) Math.round(2 * px - v.getX()) : v.x();
            int ny = horizontal ? v.y() : (int) Math.round(2 * py - v.getY());
            map.vertices.set(vi, new org.deth.wad.MapData.Vertex(nx, ny));
        }


        for (int ti : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(ti);
            int nx = horizontal ? (int) Math.round(2 * px - t.x()) : t.x();
            int ny = horizontal ? t.y() : (int) Math.round(2 * py - t.y());
            int newAng = horizontal
                    ? Math.floorMod(180 - t.angle(), 360)
                    : Math.floorMod(-t.angle(), 360);
            map.things.set(ti, new org.deth.wad.MapData.Thing(nx, ny, newAng, t.type(), t.flags()));
        }


        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            map.linedefs.set(li, new org.deth.wad.MapData.Linedef(
                    l.v1, l.v2, l.flags, l.special, l.tag, l.leftSidedef, l.rightSidedef
            ));
        }

        repaint();
        updateStatus(null);
        notifySelection();
    }

    public void wizardAssignSpecialAndTagToSelectedLines(Integer specialOrNull,
                                                         Integer tagOrNull,
                                                         WizardSide side) {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (specialOrNull == null) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, "Please enter a valid linedef special number.");
            return;
        }
        wizardAssignSpecialAndTagToSelectedLines(specialOrNull.intValue(), tagOrNull, side);
    }

    /**
     * Method rotateSelectedThings.
     *
     * @param deltaDegrees parameter
     */
    public void rotateSelectedThings(int deltaDegrees) {
        if (map == null || selThings.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            int ang = ((t.angle() + deltaDegrees) % 360 + 360) % 360;
            map.things.set(idx, new MapData.Thing(t.x(), t.y(), ang, t.type(), t.flags()));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method setSelectedThingsAngle.
     *
     * @param angleDegrees parameter
     */
    public void setSelectedThingsAngle(int angleDegrees) {
        if (map == null || selThings.isEmpty()) return;
        pushUndo();
        for (int idx : new java.util.ArrayList<>(selThings)) {
            var t = map.things.get(idx);
            map.things.set(idx, new MapData.Thing(t.x(), t.y(), angleDegrees, t.type(), t.flags()));
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method getGridStep.
     *
     * @return result
     */
    public int getGridStep() {
        return gridStep;
    }

    /**
     * Method setGridStep.
     *
     * @param step parameter
     */
    public void setGridStep(int step) {
        if (step <= 0) return;
        this.gridStep = step;
        repaint();
        updateStatus(null);
    }

    /**
     * Method isSnap.
     *
     * @return result
     */
    public boolean isSnap() {
        return snap;
    }

    /**
     * Method setSnap.
     *
     * @param s parameter
     */
    public void setSnap(boolean s) {
        this.snap = s;
        repaint();
        updateStatus(null);
    }

    /**
     * Method replaceThings.
     *
     * @param fromType    parameter
     * @param toType      parameter
     * @param setMask     parameter
     * @param clearMask   parameter
     * @param inSelection parameter
     * @return result
     */
    public int replaceThings(Integer fromType, Integer toType, int setMask, int clearMask, boolean inSelection) {
        if (map == null) return 0;
        java.util.Set<Integer> scope = null;
        if (inSelection) scope = new java.util.HashSet<>(selThings);

        int changed = 0;
        boolean pushed = false;
        for (int i = 0; i < map.things.size(); i++) {
            if (scope != null && !scope.contains(i)) continue;
            var t = map.things.get(i);
            if (fromType != null && t.type() != fromType.intValue()) continue;

            int newType = (toType != null) ? toType.intValue() : t.type();
            int newFlags = (t.flags() | setMask) & ~clearMask;

            /**
             * Constructor for if.
             * @param tflags parameter
             */
            if (newType != t.type() || newFlags != t.flags()) {
                /**
                 * Constructor for if.
                 * @param pushed parameter
                 */
                if (!pushed) {
                    pushUndo();
                    pushed = true;
                }
                map.things.set(i, new org.deth.wad.MapData.Thing(t.x(), t.y(), t.angle(), newType, newFlags));
                changed++;
            }
        }
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (changed > 0) {
            repaint();
            updateStatus(null);
            notifySelection();
        }
        return changed;
    }

    public int replaceWallTextures(String from, String to,
                                   boolean repUpper, boolean repMiddle, boolean repLower,
                                   boolean inSelection) {
        if (map == null) return 0;
        if (from == null || to == null) return 0;
        String FROM = from.trim().toUpperCase(java.util.Locale.ROOT);
        String TO = to.trim().toUpperCase(java.util.Locale.ROOT);
        if (FROM.isEmpty() || TO.isEmpty()) return 0;

        java.util.Set<Integer> lineScope = null;
        if (inSelection) lineScope = new java.util.HashSet<>(selLines);

        int changed = 0;
        pushUndo();
        for (int i = 0; i < map.sidedefs.size(); i++) {
            /**
             * Constructor for if.
             * @param inSelection parameter
             */
            if (inSelection) {
                boolean belongs = false;
                /**
                 * Constructor for for.
                 * @param lineScope parameter
                 */
                for (int li : lineScope) {
                    var l = map.linedefs.get(li);
                    /**
                     * Constructor for if.
                     * @param i parameter
                     */
                    if (l.rightSidedef == i || l.leftSidedef == i) {
                        belongs = true;
                        break;
                    }
                }
                if (!belongs) continue;
            }
            var sd = map.sidedefs.get(i);
            String up = sd.upperTex();
            String mid = sd.middleTex();
            String low = sd.lowerTex();
            boolean any = false;
            if (repUpper && up != null && up.equalsIgnoreCase(FROM)) {
                up = TO;
                any = true;
            }
            if (repMiddle && mid != null && mid.equalsIgnoreCase(FROM)) {
                mid = TO;
                any = true;
            }
            if (repLower && low != null && low.equalsIgnoreCase(FROM)) {
                low = TO;
                any = true;
            }
            /**
             * Constructor for if.
             * @param any parameter
             */
            if (any) {
                map.sidedefs.set(i, new org.deth.wad.MapData.Sidedef(
                        sd.xOffset(), sd.yOffset(), up, low, mid, sd.sectorIndex()));
                changed++;
            }
        }
        repaint();
        return changed;
    }

    public int replaceFlats(String from, String to,
                            boolean repFloor, boolean repCeil,
                            boolean inSelection) {
        if (map == null) return 0;
        if (from == null || to == null) return 0;
        String FROM = from.trim().toUpperCase(java.util.Locale.ROOT);
        String TO = to.trim().toUpperCase(java.util.Locale.ROOT);
        if (FROM.isEmpty() || TO.isEmpty()) return 0;

        java.util.Set<Integer> sectorScope = null;
        /**
         * Constructor for if.
         * @param inSelection parameter
         */
        if (inSelection) {
            sectorScope = new java.util.HashSet<>(selSectors);

            /**
             * Constructor for for.
             * @param selLines parameter
             */
            for (int li : selLines) {
                var l = map.linedefs.get(li);
                if (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size()) {
                    int si = map.sidedefs.get(l.rightSidedef).sectorIndex();
                    if (si >= 0 && si < map.sectors.size()) sectorScope.add(si);
                }
                if (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size()) {
                    int si = map.sidedefs.get(l.leftSidedef).sectorIndex();
                    if (si >= 0 && si < map.sectors.size()) sectorScope.add(si);
                }
            }
        }

        int changed = 0;
        pushUndo();
        for (int i = 0; i < map.sectors.size(); i++) {
            if (inSelection && (sectorScope == null || !sectorScope.contains(i))) continue;
            var s = map.sectors.get(i);
            String ft = s.floorTex();
            String ct = s.ceilingTex();
            boolean any = false;
            if (repFloor && ft != null && ft.equalsIgnoreCase(FROM)) {
                ft = TO;
                any = true;
            }
            if (repCeil && ct != null && ct.equalsIgnoreCase(FROM)) {
                ct = TO;
                any = true;
            }
            /**
             * Constructor for if.
             * @param any parameter
             */
            if (any) {
                map.sectors.set(i, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), ft, ct, s.lightLevel(), s.special(), s.tag()));
                changed++;
            }
        }
        repaint();
        return changed;
    }

    /**
     * Method distributeSelectedSectorLightsAdvanced.
     *
     * @param start   parameter
     * @param end     parameter
     * @param mode    parameter
     * @param reverse parameter
     */
    public void distributeSelectedSectorLightsAdvanced(int start, int end, String mode, boolean reverse) {
        if (map == null || selSectors == null || selSectors.isEmpty()) return;


        start = Math.max(0, Math.min(255, start));
        end = Math.max(0, Math.min(255, end));

        java.util.List<Integer> order;
        if ("SEQUENCE".equalsIgnoreCase(mode)) {
            order = orderSectorsBySequence(selSectors);
        } else if ("CENTROID_X".equalsIgnoreCase(mode)) {
            order = orderSectorsByCentroid(selSectors, 0);
        } else if ("CENTROID_Y".equalsIgnoreCase(mode)) {
            order = orderSectorsByCentroid(selSectors, 1);
        } else {
            order = orderSectorsByCentroid(selSectors, 2);
        }
        if (order.isEmpty()) return;
        if (reverse) java.util.Collections.reverse(order);

        pushUndo();

        int n = order.size();
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < n; i++) {
            int si = order.get(i);
            if (si < 0 || si >= map.sectors.size()) continue;

            double t = (n <= 1) ? 0.0 : (i / (double) (n - 1));
            int val = (int) Math.round(start + t * (end - start));
            val = Math.max(0, Math.min(255, val));

            var s = map.sectors.get(si);
            map.sectors.set(si, new org.deth.wad.MapData.Sector(
                    s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                    val, s.special(), s.tag()
            ));
        }

        repaint();
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Method orderSectorsBySequence.
     *
     * @param sel parameter
     * @return result
     */
    private java.util.List<Integer> orderSectorsBySequence(java.util.Set<Integer> sel) {
        java.util.Set<Integer> set = new java.util.HashSet<>(sel);
        java.util.Map<Integer, java.util.List<Integer>> adj = buildSectorAdjacency(set);

        java.util.List<Integer> order = new java.util.ArrayList<>();
        java.util.Set<Integer> visited = new java.util.HashSet<>();


        java.util.List<Integer> endpoints = new java.util.ArrayList<>();
        /**
         * Constructor for for.
         * @param set parameter
         */
        for (int s : set) {
            int deg = adj.getOrDefault(s, java.util.Collections.emptyList()).size();
            if (deg <= 1) endpoints.add(s);
        }
        java.util.Collections.sort(endpoints);
        java.util.List<Integer> starts = endpoints.isEmpty() ? new java.util.ArrayList<>(set) : endpoints;
        java.util.Collections.sort(starts);

        /**
         * Constructor for for.
         * @param starts parameter
         */
        for (int start : starts) {
            if (visited.contains(start)) continue;

            java.util.Deque<Integer> dq = new java.util.ArrayDeque<>();
            dq.add(start);
            visited.add(start);
            while (!dq.isEmpty()) {
                int u = dq.removeFirst();
                order.add(u);
                java.util.List<Integer> neigh = new java.util.ArrayList<>(adj.getOrDefault(u, java.util.Collections.emptyList()));
                java.util.Collections.sort(neigh);
                /**
                 * Constructor for for.
                 * @param neigh parameter
                 */
                for (int v : neigh) {
                    if (set.contains(v) && !visited.contains(v)) {
                        visited.add(v);
                        dq.addLast(v);
                    }
                }
            }
        }
        return order;
    }

    private java.util.Map<Integer, java.util.List<Integer>> buildSectorAdjacency(java.util.Set<Integer> scope) {
        java.util.Map<Integer, java.util.List<Integer>> adj = new java.util.HashMap<>();
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            int rs = -1, ls = -1;
            if (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size()) {
                rs = map.sidedefs.get(l.rightSidedef).sectorIndex();
            }
            if (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size()) {
                ls = map.sidedefs.get(l.leftSidedef).sectorIndex();
            }
            /**
             * Constructor for if.
             * @param ls parameter
             */
            if (rs >= 0 && ls >= 0 && rs != ls) {
                if (scope == null || (scope.contains(rs) && scope.contains(ls))) {
                    adj.computeIfAbsent(rs, k -> new java.util.ArrayList<>()).add(ls);
                    adj.computeIfAbsent(ls, k -> new java.util.ArrayList<>()).add(rs);
                }
            }
        }
        return adj;
    }

    /**
     * Method orderSectorsByCentroid.
     *
     * @param sel  parameter
     * @param mode parameter
     * @return result
     */
    private java.util.List<Integer> orderSectorsByCentroid(java.util.Set<Integer> sel, int mode) {
        java.util.List<Integer> list = new java.util.ArrayList<>(sel);

        java.util.Map<Integer, double[]> cent = new java.util.HashMap<>();
        for (int si : list) cent.put(si, computeSectorCentroid(si));

        int useMode = mode;
        /**
         * Constructor for if.
         * @param 2 parameter
         */
        if (mode == 2) {
            double meanX = 0, meanY = 0;
            int count = 0;
            for (double[] c : cent.values()) {
                meanX += c[0];
                meanY += c[1];
                count++;
            }
            if (count == 0) return java.util.Collections.emptyList();
            meanX /= count;
            meanY /= count;
            double varX = 0, varY = 0;
            for (double[] c : cent.values()) {
                varX += (c[0] - meanX) * (c[0] - meanX);
                varY += (c[1] - meanY) * (c[1] - meanY);
            }
            useMode = (varX >= varY) ? 0 : 1;
        }
        final int axis = useMode;

        list.sort((a, b) -> {
            double[] ca = cent.get(a);
            double[] cb = cent.get(b);
            double va = (axis == 0) ? ca[0] : ca[1];
            double vb = (axis == 0) ? cb[0] : cb[1];
            int cmp = Double.compare(va, vb);
            if (cmp != 0) return cmp;
            return Integer.compare(a, b);
        });
        return list;
    }

    /**
     * Method computeSectorCentroid.
     *
     * @param sectorIndex parameter
     * @return result
     */
    private double[] computeSectorCentroid(int sectorIndex) {
        java.util.HashSet<Integer> vset = new java.util.HashSet<>();
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            int rs = -1, ls = -1;
            if (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size()) {
                rs = map.sidedefs.get(l.rightSidedef).sectorIndex();
            }
            if (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size()) {
                ls = map.sidedefs.get(l.leftSidedef).sectorIndex();
            }
            /**
             * Constructor for if.
             * @param sectorIndex parameter
             */
            if (rs == sectorIndex || ls == sectorIndex) {
                if (l.v1 >= 0 && l.v1 < map.vertices.size()) vset.add(l.v1);
                if (l.v2 >= 0 && l.v2 < map.vertices.size()) vset.add(l.v2);
            }
        }
        if (vset.isEmpty()) return new double[]{0, 0};
        double sx = 0, sy = 0;
        int n = 0;
        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            var v = map.vertices.get(vi);
            sx += v.x();
            sy += v.y();
            n++;
        }
        return new double[]{sx / n, sy / n};
    }

    /**
     * Method setSelection.
     *
     * @param verts   parameter
     * @param lines   parameter
     * @param things  parameter
     * @param sectors parameter
     */
    public void setSelection(java.util.Set<Integer> verts, java.util.Set<Integer> lines, java.util.Set<Integer> things, java.util.Set<Integer> sectors) {
        selVertices.clear();
        selLines.clear();
        selThings.clear();
        selSectors.clear();
        if (verts != null) selVertices.addAll(verts);
        if (lines != null) selLines.addAll(lines);
        if (things != null) selThings.addAll(things);
        if (sectors != null) selSectors.addAll(sectors);
        repaint();
        updateStatus(null);
        notifySelection();
    }

    public void createSectorFromSelectedLoop(
            int floor, int ceil, String ftex, String ctex, int light, int special, int tag) {

        if (map == null || selLines.isEmpty()) return;
        pushUndo();


        java.util.Map<Integer, java.util.List<Integer>> vToLines = new java.util.HashMap<>();
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            vToLines.computeIfAbsent(l.v1, k -> new java.util.ArrayList<>()).add(li);
            vToLines.computeIfAbsent(l.v2, k -> new java.util.ArrayList<>()).add(li);
        }
        for (var e : vToLines.entrySet()) {
            if (e.getValue().size() != 2) {
                JOptionPane.showMessageDialog(this, "Selected lines do not form a single closed loop.");
                return;
            }
        }


        java.util.List<Integer> loopVerts = new java.util.ArrayList<>();
        java.util.List<Integer> loopLines = new java.util.ArrayList<>();
        int startLine = selLines.iterator().next();
        var L0 = map.linedefs.get(startLine);
        int currentV = L0.v1, nextV = L0.v2;
        loopVerts.add(currentV);
        loopLines.add(startLine);
        java.util.Set<Integer> used = new java.util.HashSet<>();
        used.add(startLine);

        /**
         * Constructor for while.
         * @param true parameter
         */
        while (true) {
            loopVerts.add(nextV);
            var around = vToLines.get(nextV);
            int prevLi = loopLines.get(loopLines.size() - 1);
            int cand = (around.get(0) == prevLi) ? around.get(1) : around.get(0);
            if (used.contains(cand)) {
                if (!loopVerts.get(0).equals(nextV)) {
                    JOptionPane.showMessageDialog(this, "Loop walk failed (self-intersection?).");
                    return;
                }
                break;
            }
            used.add(cand);
            loopLines.add(cand);
            var ln = map.linedefs.get(cand);
            /**
             * Constructor for if.
             * @param nextV parameter
             */
            if (ln.v1 == nextV) {
                currentV = ln.v1;
                nextV = ln.v2;
            } else if (ln.v2 == nextV) {
                currentV = ln.v2;
                nextV = ln.v1;
            } else {
                JOptionPane.showMessageDialog(this, "Broken loop.");
                return;
            }
        }


        double area = 0.0;
        for (int i = 0; i < loopVerts.size() - 1; i++) {
            var a = map.vertices.get(loopVerts.get(i));
            var b = map.vertices.get(loopVerts.get(i + 1));
            area += a.getX() * b.getY() - a.getY() * b.getX();
        }
        boolean interiorOnRight = (area < 0.0);


        var A = map.vertices.get(loopVerts.get(0));
        var B = map.vertices.get(loopVerts.get(1));
        double ex = B.getX() - A.getX(), ey = B.getY() - A.getY();
        double len = Math.hypot(ex, ey);
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (len == 0) {
            JOptionPane.showMessageDialog(this, "Zero-length edge.");
            return;
        }
        ex /= len;
        ey /= len;
        double nx = interiorOnRight ? -ey : ey;
        double ny = interiorOnRight ? ex : -ex;
        double mx = (A.getX() + B.getX()) * 0.5 + nx * 4.0;
        double my = (A.getY() + B.getY()) * 0.5 + ny * 4.0;
        Integer outerSec = findSectorByRay(mx, my, nx, ny, selLines);


        int newSecIdx = map.sectors.size();
        map.sectors.add(new org.deth.wad.MapData.Sector(
                floor, ceil,
                (ftex != null && !ftex.isEmpty() ? ftex : "FLOOR1"),
                (ctex != null && !ctex.isEmpty() ? ctex : "CEIL1"),
                light, special, tag));


        for (int i = 0; i < loopLines.size(); i++) {
            int li = loopLines.get(i);
            var l = map.linedefs.get(li);

            int vi0 = loopVerts.get(i);
            int vi1 = loopVerts.get(i + 1);
            boolean edgeSameDir = (l.v1 == vi0 && l.v2 == vi1);
            boolean innerOnRightHere = interiorOnRight == edgeSameDir;

            int right = l.rightSidedef, left = l.leftSidedef;

            /**
             * Constructor for if.
             * @param innerOnRightHere parameter
             */
            if (innerOnRightHere) {

                if (right < 0 || right == 0xFFFF || right >= map.sidedefs.size()) {
                    int sd = map.sidedefs.size();
                    map.sidedefs.add(new org.deth.wad.MapData.Sidedef(0, 0, "-", "-", "-", newSecIdx));
                    right = sd;
                } else {
                    var sd = map.sidedefs.get(right);
                    map.sidedefs.set(right, new org.deth.wad.MapData.Sidedef(sd.xOffset(), sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), newSecIdx));
                }

                if ((left < 0 || left == 0xFFFF || left >= map.sidedefs.size()) && outerSec != null) {
                    int sd = map.sidedefs.size();
                    map.sidedefs.add(new org.deth.wad.MapData.Sidedef(0, 0, "-", "-", "-", outerSec.intValue()));
                    left = sd;
                }
            } else {

                if (left < 0 || left == 0xFFFF || left >= map.sidedefs.size()) {
                    int sd = map.sidedefs.size();
                    map.sidedefs.add(new org.deth.wad.MapData.Sidedef(0, 0, "-", "-", "-", newSecIdx));
                    left = sd;
                } else {
                    var sd = map.sidedefs.get(left);
                    map.sidedefs.set(left, new org.deth.wad.MapData.Sidedef(sd.xOffset(), sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), newSecIdx));
                }

                if ((right < 0 || right == 0xFFFF || right >= map.sidedefs.size()) && outerSec != null) {
                    int sd = map.sidedefs.size();
                    map.sidedefs.add(new org.deth.wad.MapData.Sidedef(0, 0, "-", "-", "-", outerSec.intValue()));
                    right = sd;
                }
            }


            int newFlags = l.flags;
            boolean twoSided = (right != 0xFFFF && right >= 0 && right < map.sidedefs.size()) &&
                    (left != 0xFFFF && left >= 0 && left < map.sidedefs.size());
            if (twoSided) newFlags |= 0x0004;
            else newFlags &= ~0x0004;

            map.linedefs.set(li, new org.deth.wad.MapData.Linedef(
                    l.v1, l.v2, newFlags, l.special, l.tag, right, left));
        }

        selSectors.clear();
        selSectors.add(newSecIdx);
        repaint();
        notifySelection();
        updateStatus(null);
    }

    /**
     * Method findSectorByRay.
     *
     * @param ox          parameter
     * @param oy          parameter
     * @param dx          parameter
     * @param dy          parameter
     * @param ignoreLines parameter
     * @return result
     */
    private Integer findSectorByRay(double ox, double oy, double dx, double dy, java.util.Set<Integer> ignoreLines) {
        double bestT = Double.POSITIVE_INFINITY;
        int bestLine = -1;
        for (int i = 0; i < map.linedefs.size(); i++) {
            if (ignoreLines != null && ignoreLines.contains(i)) continue;
            var l = map.linedefs.get(i);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            double ax = a.getX(), ay = a.getY(), bx = b.getX(), by = b.getY();
            double rx = dx, ry = dy, sx = bx - ax, sy = by - ay;
            double denom = (-rx * sy + ry * sx);
            if (Math.abs(denom) < 1e-6) continue;
            double t = (-sy * (ax - ox) + sx * (ay - oy)) / denom;
            double u = (-ry * (ax - ox) + rx * (ay - oy)) / denom;
            /**
             * Constructor for if.
             * @param 10 parameter
             */
            if (t > 1e-6 && u >= 0.0 && u <= 1.0) {
                /**
                 * Constructor for if.
                 * @param bestT parameter
                 */
                if (t < bestT) {
                    bestT = t;
                    bestLine = i;
                }
            }
        }
        if (bestLine < 0) return null;
        var l = map.linedefs.get(bestLine);
        var a = map.vertices.get(l.v1);
        var b = map.vertices.get(l.v2);
        double cross = (b.getX() - a.getX()) * (oy - a.getY()) - (b.getY() - a.getY()) * (ox - a.getX());
        boolean originIsOnRight = (cross <= 0);
        int sd = originIsOnRight ? l.rightSidedef : l.leftSidedef;
        if (sd >= 0 && sd != 0xFFFF && sd < map.sidedefs.size()) return map.sidedefs.get(sd).sectorIndex();
        int od = originIsOnRight ? l.leftSidedef : l.rightSidedef;
        if (od >= 0 && od != 0xFFFF && od < map.sidedefs.size()) return map.sidedefs.get(od).sectorIndex();
        return null;
    }

    /**
     * Method copySelection.
     */
    public void copySelection() {
        cbThings.clear();
        cbVertices.clear();
        cbLinedefs.clear();
        cbIsLines = false;
        if (map == null) return;
        if (!selThings.isEmpty()) {
            for (int idx : selThings) cbThings.add(map.things.get(idx));
        } else if (!selLines.isEmpty()) {
            java.util.Set<Integer> usedV = new java.util.LinkedHashSet<>();
            /**
             * Constructor for for.
             * @param selLines parameter
             */
            for (int li : selLines) {
                var l = map.linedefs.get(li);
                usedV.add(l.v1);
                usedV.add(l.v2);
            }
            java.util.Map<Integer, Integer> remap = new java.util.LinkedHashMap<>();
            cbVertices.clear();
            int i = 0;
            /**
             * Constructor for for.
             * @param usedV parameter
             */
            for (int vi : usedV) {
                remap.put(vi, i++);
                cbVertices.add(map.vertices.get(vi));
            }
            cbLinedefs.clear();
            /**
             * Constructor for for.
             * @param selLines parameter
             */
            for (int li : selLines) {
                var l = map.linedefs.get(li);
                cbLinedefs.add(new MapData.Linedef(remap.get(l.v1), remap.get(l.v2), l.flags, l.special, l.tag, 0xFFFF, 0xFFFF));
            }
            cbIsLines = true;
        }
    }

    /**
     * Method pasteClipboard.
     */
    public void pasteClipboard() {
        if (map == null) return;
        if (cbThings.isEmpty() && !cbIsLines) return;
        pushUndo();
        double ox = 0, oy = 0;
        boolean haveTarget = lastMouseWorld != null;
        /**
         * Constructor for if.
         * @param haveTarget parameter
         */
        if (haveTarget) {
            ox = lastMouseWorld.getX();
            oy = lastMouseWorld.getY();
        }

        /**
         * Constructor for if.
         * @param snap parameter
         */
        if (snap) {
            ox = Math.round(ox / gridStep) * gridStep;
            oy = Math.round(oy / gridStep) * gridStep;
        }
        selVertices.clear();
        selLines.clear();
        selThings.clear();
        selSectors.clear();

        /**
         * Constructor for if.
         * @param cbIsLines parameter
         */
        if (!cbIsLines) {

            if (cbThings.isEmpty()) return;
            int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE;
            /**
             * Constructor for for.
             * @param cbThings parameter
             */
            for (var t : cbThings) {
                if (t.x() < minx) minx = t.x();
                if (t.y() < miny) miny = t.y();
            }
            double dx = (haveTarget ? ox - minx : 64.0);
            double dy = (haveTarget ? oy - miny : 64.0);
            /**
             * Constructor for for.
             * @param cbThings parameter
             */
            for (var t : cbThings) {
                int nx = (int) Math.round(t.x() + dx);
                int ny = (int) Math.round(t.y() + dy);
                map.things.add(new MapData.Thing(nx, ny, t.angle(), t.type(), t.flags()));
                selThings.add(map.things.size() - 1);
            }
        } else {

            if (cbVertices.isEmpty()) return;
            int minx = 10_000_000, miny = 10_000_000;
            /**
             * Constructor for for.
             * @param cbVertices parameter
             */
            for (var v : cbVertices) {
                if (v.getX() < minx) minx = (int) v.getX();
                if (v.getY() < miny) miny = (int) v.getY();
            }
            double dx = (haveTarget ? ox - minx : 64.0);
            double dy = (haveTarget ? oy - miny : 64.0);
            int baseIndex = map.vertices.size();

            /**
             * Constructor for for.
             * @param cbVertices parameter
             */
            for (var v : cbVertices) {
                int nx = (int) Math.round(v.getX() + dx);
                int ny = (int) Math.round(v.getY() + dy);
                map.vertices.add(new MapData.Vertex(nx, ny));
                selVertices.add(map.vertices.size() - 1);
            }

            /**
             * Constructor for for.
             * @param cbLinedefs parameter
             */
            for (var l : cbLinedefs) {
                int v1 = baseIndex + l.v1;
                int v2 = baseIndex + l.v2;
                map.linedefs.add(new MapData.Linedef(v1, v2, l.flags, l.special, l.tag, 0xFFFF, 0xFFFF));
                selLines.add(map.linedefs.size() - 1);
            }
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method linedefTouchesSelectedSector.
     *
     * @param l parameter
     * @return result
     */
    private boolean linedefTouchesSelectedSector(org.deth.wad.MapData.Linedef l) {
        if (map == null || selSectors.isEmpty()) return false;
        if (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size()) {
            int si = map.sidedefs.get(l.rightSidedef).sectorIndex();
            if (si >= 0 && si < map.sectors.size() && selSectors.contains(si)) return true;
        }
        if (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size()) {
            int si = map.sidedefs.get(l.leftSidedef).sectorIndex();
            return si >= 0 && si < map.sectors.size() && selSectors.contains(si);
        }
        return false;
    }

    /**
     * Method selectLinedefsBy.
     *
     * @param special parameter
     * @param tag     parameter
     */
    public void selectLinedefsBy(Integer special, Integer tag) {
        if (map == null) return;
        selLines.clear();
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            boolean ok = special == null || l.special == special;
            if (tag != null && l.tag != tag) ok = false;
            if (ok) selLines.add(i);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method selectThingsBy.
     *
     * @param type parameter
     */
    public void selectThingsBy(Integer type) {
        if (map == null) return;
        selThings.clear();
        for (int i = 0; i < map.things.size(); i++) {
            var t = map.things.get(i);
            if (type == null || t.type() == type) selThings.add(i);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method selectSectorsByLight.
     *
     * @param minL parameter
     * @param maxL parameter
     */
    public void selectSectorsByLight(Integer minL, Integer maxL) {
        if (map == null) return;
        selSectors.clear();
        for (int i = 0; i < map.sectors.size(); i++) {
            var s = map.sectors.get(i);
            boolean ok = minL == null || s.lightLevel() >= minL;
            if (maxL != null && s.lightLevel() > maxL) ok = false;
            if (ok) selSectors.add(i);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method selectThingsByCategory.
     *
     * @param name parameter
     */
    public void selectThingsByCategory(String name) {
        if (map == null || name == null) return;
        String key = name.toLowerCase(java.util.Locale.ROOT);
        java.util.Set<Integer> set = THING_CATEGORIES.get(key);
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (set == null) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, "Unknown thing category: " + name,
                    "Thing filters", javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        selThings.clear();
        for (int i = 0; i < map.things.size(); i++) {
            if (set.contains(map.things.get(i).type())) selThings.add(i);
        }

        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method distributeSelectedSectorHeights.
     *
     * @param floor parameter
     * @param start parameter
     * @param end   parameter
     */
    public void distributeSelectedSectorHeights(boolean floor, int start, int end) {
        if (map == null || selSectors == null || selSectors.isEmpty()) return;

        pushUndo();

        java.util.List<Integer> idx = new java.util.ArrayList<>(selSectors);
        java.util.Collections.sort(idx);

        int n = idx.size();
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < n; i++) {
            int si = idx.get(i);
            if (si < 0 || si >= map.sectors.size()) continue;


            double t = (n <= 1) ? 0.0 : (i / (double) (n - 1));
            int h = (int) Math.round(start + t * (end - start));

            var s = map.sectors.get(si);
            /**
             * Constructor for if.
             * @param floor parameter
             */
            if (floor) {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        h, s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), s.tag()
                ));
            } else {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), h, s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), s.tag()
                ));
            }
        }

        repaint();
        notifySelection();
    }

    /**
     * Method setFilterMode.
     *
     * @param enable parameter
     */
    public void setFilterMode(boolean enable) {
        this.filterMode = enable;
        repaint();
    }

    /**
     * Method updateSelectedSidedefs.
     *
     * @param up   parameter
     * @param mid  parameter
     * @param low  parameter
     * @param xOff parameter
     * @param yOff parameter
     */
    public void updateSelectedSidedefs(String up, String mid, String low, Integer xOff, Integer yOff) {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            int[] sides = new int[]{l.rightSidedef, l.leftSidedef};
            /**
             * Constructor for for.
             * @param sides parameter
             */
            for (int sidx : sides) {
                if (sidx >= 0 && sidx < map.sidedefs.size()) {
                    var sd = map.sidedefs.get(sidx);
                    String U = (up != null) ? up : sd.upperTex();
                    String M = (mid != null) ? mid : sd.middleTex();
                    String L = (low != null) ? low : sd.lowerTex();
                    int xo = (xOff != null) ? xOff : sd.xOffset();
                    int yo = (yOff != null) ? yOff : sd.yOffset();
                    map.sidedefs.set(
                            sidx,
                            new org.deth.wad.MapData.Sidedef(xo, yo, U, L, M, sd.sectorIndex())
                    );
                }
            }
        }
        repaint();
        notifySelection();
    }

    /**
     * Method swapSelectedSidedefs.
     */
    public void swapSelectedSidedefs() {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            map.linedefs.set(li, new org.deth.wad.MapData.Linedef(l.v1, l.v2, l.flags, l.special, l.tag, l.leftSidedef, l.rightSidedef));
        }
        repaint();
        notifySelection();
    }

    /**
     * Method alignSelectedSidedefs.
     *
     * @param xAxis parameter
     */
    public void alignSelectedSidedefs(boolean xAxis) {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        Integer common = null;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            int sidx = (l.rightSidedef >= 0 && l.rightSidedef != 0xFFFF) ? l.rightSidedef : l.leftSidedef;
            if (sidx >= 0 && sidx < map.sidedefs.size()) {
                var sd = map.sidedefs.get(sidx);
                common = xAxis ? sd.xOffset() : sd.yOffset();
                break;
            }
        }
        if (common == null) return;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            int[] sides = new int[]{l.rightSidedef, l.leftSidedef};
            /**
             * Constructor for for.
             * @param sides parameter
             */
            for (int sidx : sides) {
                if (sidx >= 0 && sidx < map.sidedefs.size()) {
                    var sd = map.sidedefs.get(sidx);
                    map.sidedefs.set(sidx, xAxis ? new org.deth.wad.MapData.Sidedef(common, sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), sd.sectorIndex())
                            : new org.deth.wad.MapData.Sidedef(sd.xOffset(), common, sd.upperTex(), sd.lowerTex(), sd.middleTex(), sd.sectorIndex()));
                }
            }
        }
        repaint();
        notifySelection();
    }

    /**
     * Method addSectorBehindSelectedOneSided.
     *
     * @param floor parameter
     * @param ceil  parameter
     * @param ftex  parameter
     * @param ctex  parameter
     * @param light parameter
     */
    public void addSectorBehindSelectedOneSided(int floor, int ceil, String ftex, String ctex, int light) {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            boolean rightOnly = (l.rightSidedef != 0xFFFF) && (l.leftSidedef == 0xFFFF);
            boolean leftOnly = (l.leftSidedef != 0xFFFF) && (l.rightSidedef == 0xFFFF);
            if (rightOnly == leftOnly) continue;
            int secIdx = map.sectors.size();
            map.sectors.add(new org.deth.wad.MapData.Sector(floor, ceil, ftex, ctex, light, 0, 0));
            int sdIdx = map.sidedefs.size();
            map.sidedefs.add(new org.deth.wad.MapData.Sidedef(0, 0, "-", "-", "-", secIdx));
            org.deth.wad.MapData.Linedef nl = rightOnly
                    ? new org.deth.wad.MapData.Linedef(l.v1, l.v2, l.flags, l.special, l.tag, l.rightSidedef, sdIdx)
                    : new org.deth.wad.MapData.Linedef(l.v1, l.v2, l.flags, l.special, l.tag, sdIdx, l.leftSidedef);
            map.linedefs.set(li, nl);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method scaleAroundPivot.
     *
     * @param sx parameter
     * @param sy parameter
     * @param px parameter
     * @param py parameter
     */
    public void scaleAroundPivot(double sx, double sy, double px, double py) {
        if (map == null) return;

        java.util.Set<Integer> vset = new java.util.HashSet<>(selVertices);
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            if (li < 0 || li >= map.linedefs.size()) continue;
            var l = map.linedefs.get(li);
            vset.add(l.v1);
            vset.add(l.v2);
        }
        boolean any = !vset.isEmpty() || !selThings.isEmpty();
        if (!any) return;

        pushUndo();


        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            if (vi < 0 || vi >= map.vertices.size()) continue;
            var v = map.vertices.get(vi);
            double nx = px + (v.getX() - px) * sx;
            double ny = py + (v.getY() - py) * sy;
            map.vertices.set(vi, new org.deth.wad.MapData.Vertex(
                    (int) Math.round(nx), (int) Math.round(ny)));
        }


        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int ti : selThings) {
            if (ti < 0 || ti >= map.things.size()) continue;
            var t = map.things.get(ti);
            double nx = px + (t.x() - px) * sx;
            double ny = py + (t.y() - py) * sy;
            map.things.set(ti, new org.deth.wad.MapData.Thing(
                    (int) Math.round(nx), (int) Math.round(ny), t.angle(), t.type(), t.flags()));
        }


        repaint();
        try {
            notifySelection();
        } catch (Throwable ignore) {
        }
    }

    public int wizardMakeTeleport(int special, Integer tagOrNull, boolean useOnlySelectedLines,
                                  boolean createDestination, int destAngleDeg) {
        if (map == null) return 0;
        int changes = 0;
        int tag = (tagOrNull == null || tagOrNull.intValue() == 0)
                ? findFirstFreeTagNumber() : tagOrNull.intValue();

        pushUndo();


        java.util.Set<Integer> targets = new java.util.LinkedHashSet<>();
        if (useOnlySelectedLines && selLines != null && !selLines.isEmpty()) {
            targets.addAll(selLines);
        } else if (selSectors != null && !selSectors.isEmpty()) {
            targets = boundaryLinedefsForSectors(selSectors);
        } else if (selLines != null && !selLines.isEmpty()) {
            targets.addAll(selLines);
        }

        if (!targets.isEmpty()) {
            applySpecialToLinedefs(targets, special, tag);
            changes += targets.size();
        }


        if (selSectors != null && !selSectors.isEmpty()) {
            for (int si : new java.util.ArrayList<>(selSectors)) {
                var s = map.sectors.get(si);
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), tag
                ));
                changes++;
                /**
                 * Constructor for if.
                 * @param createDestination parameter
                 */
                if (createDestination) {
                    double[] c = computeSectorCentroid(si);
                    int cx = (int) Math.round(c[0]);
                    int cy = (int) Math.round(c[1]);

                    map.things.add(new org.deth.wad.MapData.Thing(cx, cy, destAngleDeg, 14, 0x0001 | 0x0002 | 0x0004));
                    changes++;
                }
            }
        }

        repaint();
        updateStatus(null);
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
        return changes;
    }

    /**
     * Method joinSelectedSectors.
     *
     * @return result
     */
    public int joinSelectedSectors() {
        if (map == null || selSectors == null || selSectors.size() < 2) return 0;
        pushUndo();


        java.util.List<Integer> picked = new java.util.ArrayList<>(selSectors);
        java.util.Collections.sort(picked);
        int target = picked.get(0);


        for (int i = 0; i < map.sidedefs.size(); i++) {
            var sd = map.sidedefs.get(i);
            if (selSectors.contains(sd.sectorIndex())) {
                /**
                 * Constructor for if.
                 * @param target parameter
                 */
                if (sd.sectorIndex() != target) {
                    map.sidedefs.set(i, new org.deth.wad.MapData.Sidedef(sd.xOffset(), sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), target));
                }
            }
        }


        picked.remove(0);
        picked.sort(java.util.Collections.reverseOrder());
        /**
         * Constructor for for.
         * @param picked parameter
         */
        for (int idx : picked) {
            map.sectors.remove(idx);
        }


        for (int i = 0; i < map.sidedefs.size(); i++) {
            var sd = map.sidedefs.get(i);
            int old = sd.sectorIndex();
            int newIndex = old;
            /**
             * Constructor for for.
             * @param picked parameter
             */
            for (int rem : picked) {
                if (old > rem) newIndex--;
            }
            /**
             * Constructor for if.
             * @param old parameter
             */
            if (newIndex != old) {
                map.sidedefs.set(i, new org.deth.wad.MapData.Sidedef(
                        sd.xOffset(), sd.yOffset(),
                        sd.upperTex(), sd.lowerTex(), sd.middleTex(),
                        newIndex
                ));
            }
        }


        int newTarget = target;
        /**
         * Constructor for for.
         * @param picked parameter
         */
        for (int rem : picked) {
            if (target > rem) newTarget--;
        }


        selSectors.clear();
        selSectors.add(newTarget);

        repaint();
        notifySelection();
        return picked.size();
    }

    /**
     * Method mergeSelectedVertices.
     */
    public void mergeSelectedVertices() {
        if (map == null || selVertices.size() < 2) return;
        pushUndo();
        int target = selVertices.iterator().next();
        for (int i = map.linedefs.size() - 1; i >= 0; i--) {
            var l = map.linedefs.get(i);
            int nv1 = selVertices.contains(l.v1) ? target : l.v1;
            int nv2 = selVertices.contains(l.v2) ? target : l.v2;
            /**
             * Constructor for if.
             * @param nv2 parameter
             */
            if (nv1 == nv2) {
                map.linedefs.remove(i);
            } else
                map.linedefs.set(i, new org.deth.wad.MapData.Linedef(nv1, nv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
        }
        java.util.List<Integer> others = new java.util.ArrayList<>(selVertices);
        others.remove(Integer.valueOf(target));
        others.sort(java.util.Collections.reverseOrder());
        for (int idx : others) map.vertices.remove(idx);

        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            int rv1 = l.v1 - countLess(others, l.v1);
            int rv2 = l.v2 - countLess(others, l.v2);
            map.linedefs.set(i, new org.deth.wad.MapData.Linedef(rv1, rv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
        }
        selVertices.clear();
        selVertices.add(target);
        repaint();
        notifySelection();
    }

    /**
     * Method countLess.
     *
     * @param descRemoved parameter
     * @param v           parameter
     * @return result
     */
    private int countLess(java.util.List<Integer> descRemoved, int v) {
        int c = 0;
        for (int r : descRemoved) if (v > r) c++;
        return c;
    }

    /**
     * Method joinColinearSelectedLinedefs.
     */
    public void joinColinearSelectedLinedefs() {
        if (map == null || selLines.size() < 2) return;
        pushUndo();
        boolean changed = true;
        /**
         * Constructor for while.
         * @param changed parameter
         */
        while (changed) {
            changed = false;
            outer:
            for (int i = 0; i < map.linedefs.size(); i++) {
                if (!selLines.contains(i)) continue;
                var a = map.linedefs.get(i);
                for (int j = i + 1; j < map.linedefs.size(); j++) {
                    if (!selLines.contains(j)) continue;
                    var b = map.linedefs.get(j);
                    int shared = sharedVertex(a, b);
                    if (shared >= 0 && colinear(a, b)) {
                        int aOther = (a.v1 == shared) ? a.v2 : a.v1;
                        int bOther = (b.v1 == shared) ? b.v2 : b.v1;
                        map.linedefs.set(i, new org.deth.wad.MapData.Linedef(aOther, bOther, a.flags, a.special, a.tag, a.rightSidedef, a.leftSidedef));
                        map.linedefs.remove(j);
                        java.util.Set<Integer> newSel = new java.util.HashSet<>();
                        for (int idx : selLines) newSel.add(idx > j ? idx - 1 : idx);
                        selLines.clear();
                        selLines.addAll(newSel);
                        changed = true;
                        break outer;
                    }
                }
            }
        }
        repaint();
        notifySelection();
    }

    /**
     * Method sharedVertex.
     *
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private int sharedVertex(org.deth.wad.MapData.Linedef a, org.deth.wad.MapData.Linedef b) {
        if (a.v1 == b.v1 || a.v1 == b.v2) return a.v1;
        if (a.v2 == b.v1 || a.v2 == b.v2) return a.v2;
        return -1;
    }

    /**
     * Method colinear.
     *
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private boolean colinear(org.deth.wad.MapData.Linedef a, org.deth.wad.MapData.Linedef b) {
        var av1 = map.vertices.get(a.v1);
        var av2 = map.vertices.get(a.v2);
        var bv1 = map.vertices.get(b.v1);
        var bv2 = map.vertices.get(b.v2);
        double ax = av2.getX() - av1.getX(), ay = av2.getY() - av1.getY();
        double bx = bv2.getX() - bv1.getX(), by = bv2.getY() - bv1.getY();
        double cross = ax * by - ay * bx;
        return Math.abs(cross) < 1e-6;
    }

    /**
     * Method zoomPreset.
     *
     * @param idx parameter
     */
    public void zoomPreset(int idx) {

        double[] scales = new double[]{0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0, 6.0, 8.0, 12.0};
        int i = (idx == 0) ? 9 : Math.max(1, Math.min(9, idx)) - 1;
        double s = scales[i];
        java.awt.Rectangle r = getVisibleRect();
        java.awt.Point center = new java.awt.Point(r.x + r.width / 2, r.y + r.height / 2);
        java.awt.geom.Point2D worldC = screenToWorld(center);
        worldToScreen.setTransform(1, 0, 0, 1, 0, 0);
        worldToScreen.translate(getWidth() / 2.0, getHeight() / 2.0);
        worldToScreen.scale(s, -s);
        worldToScreen.translate(-worldC.getX(), -worldC.getY());
        repaint();
        updateStatus(null);
    }

    /**
     * Method toggleDisplayMode.
     */
    public void toggleDisplayMode() {
        showGrid = !showGrid;
        repaint();
    }

    /**
     * Method adjustSectorHeights.
     *
     * @param dFloor parameter
     * @param dCeil  parameter
     */
    public void adjustSectorHeights(int dFloor, int dCeil) {
        if (map == null || selSectors.isEmpty()) return;
        pushUndo();
        for (int si : new java.util.ArrayList<>(selSectors)) {
            var s = map.sectors.get(si);
            map.sectors.set(si, new org.deth.wad.MapData.Sector(s.floorHeight() + dFloor, s.ceilingHeight() + dCeil, s.floorTex(), s.ceilingTex(), s.lightLevel(), s.special(), s.tag()));
        }
        repaint();
        notifySelection();
    }

    /**
     * Method sectorOnSide.
     *
     * @param l        parameter
     * @param screenPt parameter
     * @return result
     */
    private int sectorOnSide(org.deth.wad.MapData.Linedef l, java.awt.Point screenPt) {
        java.awt.geom.Point2D W = screenToWorld(screenPt);
        var a = map.vertices.get(l.v1);
        var b = map.vertices.get(l.v2);
        double ax = a.getX(), ay = a.getY(), bx = b.getX(), by = b.getY();
        double cross = (bx - ax) * (W.getY() - ay) - (by - ay) * (W.getX() - ax);

        int sd = cross <= 0 ? l.rightSidedef : l.leftSidedef;
        if (sd < 0 || sd == 0xFFFF || sd >= map.sidedefs.size()) return -1;
        int sec = map.sidedefs.get(sd).sectorIndex();
        if (sec < 0 || sec >= map.sectors.size()) return -1;
        return sec;
    }

    /**
     * Method clampSelectionToMap.
     */
    private void clampSelectionToMap() {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (map == null) {
            selVertices.clear();
            selLines.clear();
            selThings.clear();
            selSectors.clear();
            return;
        }
        selVertices.removeIf(i -> i < 0 || i >= map.vertices.size());
        selLines.removeIf(i -> i < 0 || i >= map.linedefs.size());
        selThings.removeIf(i -> i < 0 || i >= map.things.size());
        selSectors.removeIf(i -> i < 0 || i >= map.sectors.size());
    }

    /**
     * Method zoomToSelection.
     */
    public void zoomToSelection() {
        if (map == null) return;
        java.awt.geom.Rectangle2D.Double box = new java.awt.geom.Rectangle2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0);
        final boolean[] any = {false};
        java.util.function.BiConsumer<Double, Double> add = (x, y) -> {
            /**
             * Constructor for if.
             * @param any0 parameter
             */
            if (!any[0]) {
                box.x = x;
                box.y = y;
                box.width = 0;
                box.height = 0;
                any[0] = true;
            } else {
                double minx = Math.min(box.x, x), miny = Math.min(box.y, y);
                double maxx = Math.max(box.x + box.width, x), maxy = Math.max(box.y + box.height, y);
                box.x = minx;
                box.y = miny;
                box.width = maxx - minx;
                box.height = maxy - miny;
            }
        };
        /**
         * Constructor for for.
         * @param selVertices parameter
         */
        for (int i : selVertices) {
            var v = map.vertices.get(i);
            add.accept(v.getX(), v.getY());
        }
        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int i : selThings) {
            var t = map.things.get(i);
            add.accept((double) t.x(), (double) t.y());
        }
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int i : selLines) {
            var l = map.linedefs.get(i);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            add.accept(a.getX(), a.getY());
            add.accept(b.getX(), b.getY());
        }
        if (!any[0]) return;
        double pad = 32;
        double sx = (getWidth() - 80) / (box.width + 2 * pad);
        double sy = (getHeight() - 80) / (box.height + 2 * pad);
        double s = Math.max(0.1, Math.min(sx, sy));
        worldToScreen.setTransform(1, 0, 0, 1, 0, 0);
        worldToScreen.translate(getWidth() / 2.0, getHeight() / 2.0);
        worldToScreen.scale(s, -s);
        worldToScreen.translate(-(box.x + box.width / 2.0), -(box.y + box.height / 2.0));
        repaint();
        updateStatus(null);
    }

    /**
     * Method nudgeSelected.
     *
     * @param dx parameter
     * @param dy parameter
     */
    private void nudgeSelected(double dx, double dy) {
        if (map == null) return;
        boolean any = !selVertices.isEmpty() || !selThings.isEmpty();
        if (!any) return;
        pushUndo();
        if (!selVertices.isEmpty()) {
            for (int idx : new java.util.ArrayList<>(selVertices)) {
                var v = map.vertices.get(idx);
                int nx = (int) Math.round(v.getX() + dx);
                int ny = (int) Math.round(v.getY() + dy);
                map.vertices.set(idx, new MapData.Vertex(nx, ny));
            }
        }
        if (!selThings.isEmpty()) {
            for (int idx : new java.util.ArrayList<>(selThings)) {
                var t = map.things.get(idx);
                int nx = (int) Math.round(t.x() + dx);
                int ny = (int) Math.round(t.y() + dy);
                map.things.set(idx, new MapData.Thing(nx, ny, t.angle(), t.type(), t.flags()));
            }
        }
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method flipSelectedLinedefs.
     */
    public void flipSelectedLinedefs() {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            map.linedefs.set(li, new MapData.Linedef(l.v2, l.v1, l.flags, l.special, l.tag, l.leftSidedef, l.rightSidedef));
        }
        repaint();
        notifySelection();
    }

    /**
     * Method splitSelectedLinedefsMidpoint.
     */
    public void splitSelectedLinedefsMidpoint() {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        java.util.Set<Integer> newSel = new java.util.HashSet<>();
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            double mx = (a.getX() + b.getX()) / 2.0;
            double my = (a.getY() + b.getY()) / 2.0;
            int midV = addVertex(new java.awt.geom.Point2D.Double(mx, my));
            MapData.Linedef A = new MapData.Linedef(l.v1, midV, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
            MapData.Linedef B = new MapData.Linedef(midV, l.v2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef);
            map.linedefs.set(li, A);
            map.linedefs.add(B);
            newSel.add(li);
            newSel.add(map.linedefs.size() - 1);
        }
        selLines.clear();
        selLines.addAll(newSel);
        repaint();
        notifySelection();
    }

    /**
     * Method weldDuplicateVertices.
     */
    public void weldDuplicateVertices() {
        if (map == null || map.vertices.isEmpty()) return;
        pushUndo();
        java.util.Map<Long, Integer> seen = new java.util.HashMap<>();
        int[] remap = new int[map.vertices.size()];
        java.util.List<MapData.Vertex> newVerts = new java.util.ArrayList<>();
        for (int i = 0; i < map.vertices.size(); i++) {
            var v = map.vertices.get(i);
            long key = (((long) v.x()) << 32) ^ (v.y() & 0xffffffffL);
            Integer at = seen.get(key);
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (at == null) {
                remap[i] = newVerts.size();
                seen.put(key, remap[i]);
                newVerts.add(v);
            } else {
                remap[i] = at;
            }
        }

        java.util.List<MapData.Linedef> newLines = new java.util.ArrayList<>();
        /**
         * Constructor for for.
         * @param maplinedefs parameter
         */
        for (var l : map.linedefs) {
            int nv1 = remap[l.v1], nv2 = remap[l.v2];
            if (nv1 != nv2)
                newLines.add(new MapData.Linedef(nv1, nv2, l.flags, l.special, l.tag, l.rightSidedef, l.leftSidedef));
        }
        map.vertices.clear();
        map.vertices.addAll(newVerts);
        map.linedefs.clear();
        map.linedefs.addAll(newLines);

        java.util.Set<Integer> ns = new java.util.HashSet<>();
        for (int idx : selVertices) ns.add(remap[idx]);
        selVertices.clear();
        selVertices.addAll(ns);
        repaint();
        notifySelection();
    }

    /**
     * Method fireSelectionChanged.
     */
    public void fireSelectionChanged() {
        notifySelection();
        repaint();
        updateStatus(null);
    }

    /**
     * Method ensureTagOnSelectedSectors.
     *
     * @param tagToSet parameter
     * @return result
     */
    private int ensureTagOnSelectedSectors(int tagToSet) {
        if (map == null) return tagToSet;
        for (int si : new java.util.ArrayList<>(selSectors)) {
            if (si >= 0 && si < map.sectors.size()) {
                var s = map.sectors.get(si);
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), tagToSet));
            }
        }
        return tagToSet;
    }

    /**
     * Method boundaryLinedefsForSectors.
     *
     * @param secSet parameter
     * @return result
     */
    private java.util.Set<Integer> boundaryLinedefsForSectors(java.util.Set<Integer> secSet) {
        java.util.Set<Integer> out = new java.util.LinkedHashSet<>();
        if (map == null || secSet == null || secSet.isEmpty()) return out;
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            int rs = (l.rightSidedef >= 0 && l.rightSidedef < map.sidedefs.size())
                    ? map.sidedefs.get(l.rightSidedef).sectorIndex() : -1;
            int ls = (l.leftSidedef >= 0 && l.leftSidedef < map.sidedefs.size())
                    ? map.sidedefs.get(l.leftSidedef).sectorIndex() : -1;
            boolean rIn = rs >= 0 && secSet.contains(rs);
            boolean lIn = ls >= 0 && secSet.contains(ls);
            if (rIn ^ lIn) out.add(i);
        }
        return out;
    }

    /**
     * Method applySpecialToLinedefs.
     *
     * @param lines   parameter
     * @param special parameter
     * @param tag     parameter
     */
    private void applySpecialToLinedefs(java.util.Set<Integer> lines, int special, int tag) {
        /**
         * Constructor for for.
         * @param lines parameter
         */
        for (int li : lines) {
            if (li < 0 || li >= map.linedefs.size()) continue;
            var l = map.linedefs.get(li);
            map.linedefs.set(li, new org.deth.wad.MapData.Linedef(
                    l.v1, l.v2, l.flags, special, tag, l.rightSidedef, l.leftSidedef));
        }
    }

    public void makeDoorFromSelectedSectors(int special,
                                            boolean assignTagToSectors,
                                            int tagToUse,
                                            boolean useOnlySelectedLines) {
        if (map == null || selSectors.isEmpty()) return;
        pushUndo();


        int tag = tagToUse;
        if (assignTagToSectors) tag = ensureTagOnSelectedSectors(tagToUse);


        java.util.Set<Integer> targets = new java.util.LinkedHashSet<>();
        if (useOnlySelectedLines && !selLines.isEmpty()) {
            targets.addAll(selLines);
        } else {
            targets = boundaryLinedefsForSectors(selSectors);
        }

        applySpecialToLinedefs(targets, special, tag);
        repaint();
        updateStatus(null);
        notifySelection();
    }

    public void makeLiftFromSelectedSectors(int special,
                                            boolean assignTagToSectors,
                                            int tagToUse,
                                            boolean useOnlySelectedLines) {
        if (map == null || selSectors.isEmpty()) return;
        pushUndo();

        int tag = tagToUse;
        if (assignTagToSectors) tag = ensureTagOnSelectedSectors(tagToUse);

        java.util.Set<Integer> targets = new java.util.LinkedHashSet<>();
        if (useOnlySelectedLines && !selLines.isEmpty()) {
            targets.addAll(selLines);
        } else {
            targets = boundaryLinedefsForSectors(selSectors);
        }

        applySpecialToLinedefs(targets, special, tag);
        repaint();
        updateStatus(null);
        notifySelection();
    }

    /**
     * Method getNextFreeSectorTag.
     *
     * @return result
     */
    public int getNextFreeSectorTag() {
        if (map == null || map.sectors == null || map.sectors.isEmpty()) return 1;
        int max = 0;
        /**
         * Constructor for for.
         * @param mapsectors parameter
         */
        for (org.deth.wad.MapData.Sector s : map.sectors) {
            if (s != null && s.tag() > max) max = s.tag();
        }
        return Math.max(1, max + 1);
    }

    /**
     * Method toggleMeasure.
     */
    public void toggleMeasure() {
        measuring = !measuring;
        /**
         * Constructor for if.
         * @param measuring parameter
         */
        if (!measuring) {
            measureStart = null;
            measureEnd = null;
        }
        repaint();
        updateStatus(null);
    }

    /**
     * Method clearMeasure.
     */
    private void clearMeasure() {
        measuring = false;
        measureStart = null;
        measureEnd = null;
    }

    /**
     * Method saveViewBookmark.
     *
     * @param slot parameter
     */
    public void saveViewBookmark(int slot) {
        if (slot < 0 || slot >= viewBookmarks.length) return;
        viewBookmarks[slot] = (AffineTransform) worldToScreen.clone();
    }

    /**
     * Method goToViewBookmark.
     *
     * @param slot parameter
     */
    public void goToViewBookmark(int slot) {
        if (slot < 0 || slot >= viewBookmarks.length) return;
        if (viewBookmarks[slot] == null) return;
        worldToScreen = (AffineTransform) viewBookmarks[slot].clone();
        repaint();
        updateStatus(null);
    }

    /**
     * Method makeRectangleRoom.
     *
     * @param width  parameter
     * @param height parameter
     * @param floor  parameter
     * @param ceil   parameter
     * @param ftex   parameter
     * @param ctex   parameter
     * @param light  parameter
     */
    public void makeRectangleRoom(int width, int height, int floor, int ceil, String ftex, String ctex, int light) {
        if (width <= 0 || height <= 0) return;
        if (map == null) return;
        pushUndo();

        double cx = (lastMouseWorld != null) ? lastMouseWorld.getX() : 0.0;
        double cy = (lastMouseWorld != null) ? lastMouseWorld.getY() : 0.0;
        int hw = width / 2, hh = height / 2;

        int v0 = map.vertices.size();
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hw), (int) Math.round(cy + hh)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hw), (int) Math.round(cy + hh)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx + hw), (int) Math.round(cy - hh)));
        map.vertices.add(new MapData.Vertex((int) Math.round(cx - hw), (int) Math.round(cy - hh)));

        int secIdx = map.sectors.size();
        map.sectors.add(new MapData.Sector(floor, ceil, (ftex != null ? ftex : "FLOOR1"), (ctex != null ? ctex : "CEIL1"), light, 0, 0));

        int s0 = map.sidedefs.size();
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < 4; i++) {
            map.sidedefs.add(new MapData.Sidedef(0, 0, "-", "-", "-", secIdx));
        }

        map.linedefs.add(new MapData.Linedef(v0, v0 + 1, 0, 0, 0, s0, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 1, v0 + 2, 0, 0, 0, s0 + 1, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 2, v0 + 3, 0, 0, 0, s0 + 2, 0xFFFF));
        map.linedefs.add(new MapData.Linedef(v0 + 3, v0, 0, 0, 0, s0 + 3, 0xFFFF));

        selVertices.clear();
        selLines.clear();
        selThings.clear();
        selSectors.clear();
        selVertices.add(v0);
        selVertices.add(v0 + 1);
        selVertices.add(v0 + 2);
        selVertices.add(v0 + 3);
        int baseL = map.linedefs.size() - 4;
        selLines.add(baseL);
        selLines.add(baseL + 1);
        selLines.add(baseL + 2);
        selLines.add(baseL + 3);
        selSectors.add(secIdx);
        repaint();
        notifySelection();
        updateStatus(null);
    }

    /**
     * Method growSelectedLinedefs.
     */
    public void growSelectedLinedefs() {
        if (map == null || selLines.isEmpty()) return;
        java.util.Set<Integer> add = new java.util.HashSet<>();
        for (int i = 0; i < map.linedefs.size(); i++) {
            if (selLines.contains(i)) continue;
            var l = map.linedefs.get(i);
            /**
             * Constructor for for.
             * @param selLines parameter
             */
            for (int j : selLines) {
                var a = map.linedefs.get(j);
                /**
                 * Constructor for if.
                 * @param av2 parameter
                 */
                if (l.v1 == a.v1 || l.v1 == a.v2 || l.v2 == a.v1 || l.v2 == a.v2) {
                    add.add(i);
                    break;
                }
            }
        }
        selLines.addAll(add);
        repaint();
        notifySelection();
    }

    /**
     * Method shrinkSelectedLinedefs.
     */
    public void shrinkSelectedLinedefs() {
        if (map == null || selLines.isEmpty()) return;

        int n = map.linedefs.size();
        java.util.Set<Integer> remove = new java.util.HashSet<>();
        java.util.Map<Integer, Integer> deg = new java.util.HashMap<>();
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int i : selLines) {
            var l = map.linedefs.get(i);
            deg.put(l.v1, deg.getOrDefault(l.v1, 0) + 1);
            deg.put(l.v2, deg.getOrDefault(l.v2, 0) + 1);
        }
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int i : selLines) {
            var l = map.linedefs.get(i);
            int d1 = deg.getOrDefault(l.v1, 0);
            int d2 = deg.getOrDefault(l.v2, 0);
            if (d1 <= 1 || d2 <= 1) remove.add(i);
        }
        if (remove.isEmpty()) return;
        selLines.removeAll(remove);
        repaint();
        notifySelection();
    }

    /**
     * Method scaleSelected.
     *
     * @param sx parameter
     * @param sy parameter
     */
    public void scaleSelected(double sx, double sy) {
        if (map == null) return;

        java.util.Set<Integer> vset = new java.util.HashSet<>(selVertices);
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            var l = map.linedefs.get(li);
            vset.add(l.v1);
            vset.add(l.v2);
        }
        if (vset.isEmpty() && selThings.isEmpty()) return;
        pushUndo();
        double cx = 0, cy = 0;
        int count = 0;
        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            var v = map.vertices.get(vi);
            cx += v.getX();
            cy += v.getY();
            count++;
        }
        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int ti : selThings) {
            var t = map.things.get(ti);
            cx += t.x();
            cy += t.y();
            count++;
        }
        if (count == 0) return;
        cx /= count;
        cy /= count;
        /**
         * Constructor for for.
         * @param vset parameter
         */
        for (int vi : vset) {
            var v = map.vertices.get(vi);
            double nx = cx + (v.getX() - cx) * sx;
            double ny = cy + (v.getY() - cy) * sy;
            map.vertices.set(vi, new MapData.Vertex((int) Math.round(nx), (int) Math.round(ny)));
        }
        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int ti : selThings) {
            var t = map.things.get(ti);
            double nx = cx + (t.x() - cx) * sx;
            double ny = cy + (t.y() - cy) * sy;
            map.things.set(ti, new MapData.Thing((int) Math.round(nx), (int) Math.round(ny), t.angle(), t.type(), t.flags()));
        }
        repaint();
        notifySelection();
        updateStatus(null);
    }

    /**
     * Method renumberSelectedLinedefsLowest.
     */
    public void renumberSelectedLinedefsLowest() {
        if (map == null || selLines.isEmpty()) return;
        pushUndo();
        java.util.List<org.deth.wad.MapData.Linedef> old = map.linedefs;
        int n = old.size();
        java.util.List<Integer> selected = new java.util.ArrayList<>(selLines);
        java.util.Collections.sort(selected);
        java.util.List<Integer> others = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) if (!selLines.contains(i)) others.add(i);

        int[] newIndexOf = new int[n];
        java.util.List<org.deth.wad.MapData.Linedef> reordered = new java.util.ArrayList<>(n);
        int k = 0;
        /**
         * Constructor for for.
         * @param selected parameter
         */
        for (int idx : selected) {
            reordered.add(old.get(idx));
            newIndexOf[idx] = k++;
        }
        /**
         * Constructor for for.
         * @param others parameter
         */
        for (int idx : others) {
            reordered.add(old.get(idx));
            newIndexOf[idx] = k++;
        }


        old.clear();
        old.addAll(reordered);


        java.util.Set<Integer> newSel = new java.util.HashSet<>();
        for (int idx : selLines) newSel.add(newIndexOf[idx]);
        selLines.clear();
        selLines.addAll(newSel);

        repaint();
    }

    /**
     * Method renumberSelectedSectorsLowest.
     */
    public void renumberSelectedSectorsLowest() {
        if (map == null || selSectors.isEmpty()) return;
        pushUndo();
        java.util.List<org.deth.wad.MapData.Sector> oldSec = map.sectors;
        int n = oldSec.size();
        java.util.List<Integer> selected = new java.util.ArrayList<>(selSectors);
        java.util.Collections.sort(selected);
        java.util.List<Integer> others = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) if (!selSectors.contains(i)) others.add(i);

        int[] newIndexOf = new int[n];
        java.util.List<org.deth.wad.MapData.Sector> reordered = new java.util.ArrayList<>(n);
        int k = 0;
        /**
         * Constructor for for.
         * @param selected parameter
         */
        for (int idx : selected) {
            reordered.add(oldSec.get(idx));
            newIndexOf[idx] = k++;
        }
        /**
         * Constructor for for.
         * @param others parameter
         */
        for (int idx : others) {
            reordered.add(oldSec.get(idx));
            newIndexOf[idx] = k++;
        }


        oldSec.clear();
        oldSec.addAll(reordered);


        for (int i = 0; i < map.sidedefs.size(); i++) {
            var sd = map.sidedefs.get(i);
            int newSi = (sd.sectorIndex() >= 0 && sd.sectorIndex() < n) ? newIndexOf[sd.sectorIndex()] : sd.sectorIndex();
            /**
             * Constructor for if.
             * @param sdsectorIndex parameter
             */
            if (newSi != sd.sectorIndex()) {
                map.sidedefs.set(i, new org.deth.wad.MapData.Sidedef(sd.xOffset(), sd.yOffset(), sd.upperTex(), sd.lowerTex(), sd.middleTex(), newSi));
            }
        }


        java.util.Set<Integer> newSel = new java.util.HashSet<>();
        for (int idx : selSectors) newSel.add(newIndexOf[idx]);
        selSectors.clear();
        selSectors.addAll(newSel);

        repaint();
    }

    /**
     * Method findFirstFreeTagNumber.
     *
     * @return result
     */
    public int findFirstFreeTagNumber() {
        if (map == null) return 1;
        java.util.HashSet<Integer> used = new java.util.HashSet<>();
        for (var s : map.sectors) if (s.tag() > 0) used.add(s.tag());
        for (var l : map.linedefs) if (l.tag > 0) used.add(l.tag);
        int t = 1;
        while (used.contains(t)) t++;
        return t;
    }

    /**
     * Method distributeSelectedSectorLights.
     *
     * @param start parameter
     * @param end   parameter
     */
    public void distributeSelectedSectorLights(int start, int end) {
        if (map == null || selSectors == null || selSectors.isEmpty()) return;


        start = Math.max(0, Math.min(255, start));
        end = Math.max(0, Math.min(255, end));

        pushUndo();

        java.util.List<Integer> idx = new java.util.ArrayList<>(selSectors);
        java.util.Collections.sort(idx);

        int n = idx.size();
        /**
         * Constructor for for.
         * @param i parameter
         */
        for (int i = 0; i < n; i++) {
            int si = idx.get(i);
            if (si < 0 || si >= map.sectors.size()) continue;

            double t = (n <= 1) ? 0.0 : (i / (double) (n - 1));
            int val = (int) Math.round(start + t * (end - start));
            val = Math.max(0, Math.min(255, val));

            var s = map.sectors.get(si);
            map.sectors.set(si, new org.deth.wad.MapData.Sector(
                    s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                    val, s.special(), s.tag()
            ));
        }

        repaint();
        notifySelection();
    }

    public int wizardSetupInGameStairs(int special, Integer tagOrNull,
                                       String floorFlatOrNull,
                                       String riserWallTexOrNull,
                                       boolean applyRisersLowerTex) {
        if (map == null) return 0;
        int changes = 0;
        int tag = (tagOrNull == null || tagOrNull.intValue() == 0) ? findFirstFreeTagNumber() : tagOrNull.intValue();

        pushUndo();


        if (!selLines.isEmpty()) {
            updateSelectedLinedefs(null, special, tag);
            changes += selLines.size();
        }


        if (selSectors != null && !selSectors.isEmpty()) {
            for (int idx : new java.util.ArrayList<>(selSectors)) {
                var s = map.sectors.get(idx);
                String floorFlat = (floorFlatOrNull != null && !floorFlatOrNull.isEmpty()) ? floorFlatOrNull : s.floorTex();

                map.sectors.set(idx, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), floorFlat, s.ceilingTex(),
                        s.lightLevel(), s.special(), tag
                ));
                changes++;
            }
        }


        if (applyRisersLowerTex && riserWallTexOrNull != null && !riserWallTexOrNull.isEmpty()
                && selSectors != null && !selSectors.isEmpty()) {
            for (int i = 0; i < map.sidedefs.size(); i++) {
                var sd = map.sidedefs.get(i);
                if (selSectors.contains(sd.sectorIndex())) {

                    map.sidedefs.set(i, new org.deth.wad.MapData.Sidedef(
                            sd.xOffset(), sd.yOffset(),
                            sd.upperTex(),
                            riserWallTexOrNull,
                            sd.middleTex(),
                            sd.sectorIndex()
                    ));
                    changes++;
                }
            }
        }

        repaint();
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
        return changes;
    }

    /**
     * Method makeExitFromSelectedSectors.
     *
     * @param special              parameter
     * @param useOnlySelectedLines parameter
     */
    public void makeExitFromSelectedSectors(int special, boolean useOnlySelectedLines) {
        if (map == null || (selSectors.isEmpty() && (selLines == null || selLines.isEmpty()))) return;
        pushUndo();
        java.util.Set<Integer> targets = new java.util.LinkedHashSet<>();
        if (useOnlySelectedLines && selLines != null && !selLines.isEmpty()) {
            targets.addAll(selLines);
        } else if (!selSectors.isEmpty()) {
            targets = boundaryLinedefsForSectors(selSectors);
        }
        if (!targets.isEmpty()) {
            applySpecialToLinedefs(targets, special, 0);
            repaint();
            updateStatus(null);
            notifySelection();
        }
    }

    /**
     * Method setTagForSelectedSectors.
     *
     * @param newTag parameter
     * @return result
     */
    public int setTagForSelectedSectors(int newTag) {
        if (map == null || selSectors == null || selSectors.isEmpty()) return 0;
        pushUndo();
        int changes = 0;
        for (int si : new java.util.ArrayList<>(selSectors)) {
            var s = map.sectors.get(si);
            /**
             * Constructor for if.
             * @param newTag parameter
             */
            if (s.tag() != newTag) {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), newTag
                ));
                changes++;
            }
        }
        repaint();
        updateStatus(null);
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
        return changes;
    }

    /**
     * Method wizardMakeCrusher.
     *
     * @param special              parameter
     * @param tagOrNull            parameter
     * @param useOnlySelectedLines parameter
     * @return result
     */
    public int wizardMakeCrusher(int special, Integer tagOrNull, boolean useOnlySelectedLines) {
        if (map == null) return 0;
        if ((selSectors == null || selSectors.isEmpty()) && (selLines == null || selLines.isEmpty())) return 0;
        int changes = 0;
        int tag = (tagOrNull == null || tagOrNull.intValue() == 0) ? findFirstFreeTagNumber() : tagOrNull.intValue();

        pushUndo();


        java.util.Set<Integer> targets = new java.util.LinkedHashSet<>();
        if (useOnlySelectedLines && selLines != null && !selLines.isEmpty()) {
            targets.addAll(selLines);
        } else if (selSectors != null && !selSectors.isEmpty()) {
            targets = boundaryLinedefsForSectors(selSectors);
        } else if (selLines != null && !selLines.isEmpty()) {
            targets.addAll(selLines);
        }

        if (!targets.isEmpty()) {
            applySpecialToLinedefs(targets, special, tag);
            changes += targets.size();
        }


        if (selSectors != null && !selSectors.isEmpty()) {
            for (int si : new java.util.ArrayList<>(selSectors)) {
                var s = map.sectors.get(si);
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        s.lightLevel(), s.special(), tag
                ));
                changes++;
            }
        }

        repaint();
        updateStatus(null);
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
        return changes;
    }

    /**
     * Method applySectorLightingEffect.
     *
     * @param special          parameter
     * @param lightLevelOrNull parameter
     * @return result
     */
    public int applySectorLightingEffect(int special, Integer lightLevelOrNull) {
        if (map == null || selSectors == null || selSectors.isEmpty()) return 0;
        pushUndo();
        int changes = 0;
        for (int si : new java.util.ArrayList<>(selSectors)) {
            var s = map.sectors.get(si);
            int newLight = (lightLevelOrNull != null) ? lightLevelOrNull.intValue() : s.lightLevel();
            /**
             * Constructor for if.
             * @param newLight parameter
             */
            if (s.special() != special || s.lightLevel() != newLight) {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                        newLight, special, s.tag()
                ));
                changes++;
            }
        }
        repaint();
        updateStatus(null);
        try {
            notifySelection();
        } catch (Throwable ignored) {
        }
        return changes;
    }

    /**
     * Method autoTagSectors.
     *
     * @param src          parameter
     * @param startTag     parameter
     * @param step         parameter
     * @param onlyUntagged parameter
     * @return result
     */
    public int autoTagSectors(TagSource src, int startTag, int step, boolean onlyUntagged) {
        if (map == null) return 0;
        java.util.LinkedHashSet<Integer> targetSectors = new java.util.LinkedHashSet<>();

        switch (src) {
            case SELECTED_SECTORS -> {
                for (int s : selSectors) targetSectors.add(s);
            }
            case LINES_FRONT, LINES_BACK, LINES_BOTH -> {
                for (int li : selLines) {
                    var l = map.linedefs.get(li);
                    if (src == TagSource.LINES_FRONT || src == TagSource.LINES_BOTH) {
                        int rs = l.rightSidedef;
                        if (rs >= 0 && rs != 0xFFFF && rs < map.sidedefs.size()) {
                            int sec = map.sidedefs.get(rs).sectorIndex();
                            if (sec >= 0 && sec < map.sectors.size()) targetSectors.add(sec);
                        }
                    }
                    if (src == TagSource.LINES_BACK || src == TagSource.LINES_BOTH) {
                        int ls = l.leftSidedef;
                        if (ls >= 0 && ls != 0xFFFF && ls < map.sidedefs.size()) {
                            int sec = map.sidedefs.get(ls).sectorIndex();
                            if (sec >= 0 && sec < map.sectors.size()) targetSectors.add(sec);
                        }
                    }
                }
            }
        }

        if (targetSectors.isEmpty()) return 0;
        pushUndo();
        int tag = startTag;
        int changed = 0;
        /**
         * Constructor for for.
         * @param targetSectors parameter
         */
        for (int secIdx : targetSectors) {
            var s = map.sectors.get(secIdx);
            if (onlyUntagged && s.tag() != 0) continue;
            map.sectors.set(secIdx, new org.deth.wad.MapData.Sector(
                    s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(),
                    s.lightLevel(), s.special(), tag));
            changed++;
            tag += step;
        }
        repaint();
        updateStatus(null);
        notifySelection();
        return changed;
    }

    /**
     * Method bindSelectedLinedefsTags.
     *
     * @param mode            parameter
     * @param onlyZeroLineTag parameter
     * @return result
     */
    public int bindSelectedLinedefsTags(BindMode mode, boolean onlyZeroLineTag) {
        if (map == null || selLines.isEmpty()) return 0;
        pushUndo();
        int changed = 0;
        for (int li : new java.util.ArrayList<>(selLines)) {
            var l = map.linedefs.get(li);
            if (onlyZeroLineTag && l.tag != 0) continue;
            Integer tagFrom = null;

            int rs = l.rightSidedef;
            Integer rTag = null;
            if (rs >= 0 && rs != 0xFFFF && rs < map.sidedefs.size()) {
                int rsec = map.sidedefs.get(rs).sectorIndex();
                if (rsec >= 0 && rsec < map.sectors.size()) rTag = map.sectors.get(rsec).tag();
            }
            int ls = l.leftSidedef;
            Integer lTag = null;
            if (ls >= 0 && ls != 0xFFFF && ls < map.sidedefs.size()) {
                int lsec = map.sidedefs.get(ls).sectorIndex();
                if (lsec >= 0 && lsec < map.sectors.size()) lTag = map.sectors.get(lsec).tag();
            }

            /**
             * Constructor for switch.
             * @param mode parameter
             */
            switch (mode) {
                case FRONT -> tagFrom = rTag;
                case BACK -> tagFrom = lTag;
                case PREFER_FRONT -> tagFrom = (rTag != null && rTag != 0) ? rTag : lTag;
            }
            if (tagFrom == null) continue;

            map.linedefs.set(li, new org.deth.wad.MapData.Linedef(
                    l.v1, l.v2, l.flags, l.special, tagFrom, l.rightSidedef, l.leftSidedef));
            changed++;
        }
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (changed > 0) {
            repaint();
            updateStatus(null);
            notifySelection();
        }
        return changed;
    }

    /**
     * Method applyQuickScroller.
     *
     * @param enable      parameter
     * @param onlyZeroTag parameter
     * @return result
     */
    public int applyQuickScroller(boolean enable, boolean onlyZeroTag) {
        if (map == null) return 0;
        int count = 0;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int idx : selLines) {
            if (idx < 0 || idx >= map.linedefs.size()) continue;
            var l = map.linedefs.get(idx);
            if (onlyZeroTag && l.tag != 0) continue;
            int newSp = enable ? 48 : 0;
            /**
             * Constructor for if.
             * @param newSp parameter
             */
            if (l.special != newSp) {
                l.special = newSp;
                count++;
            }
        }
        repaint();
        return count;
    }

    /**
     * Method getCommonLineFlagsOrZero.
     *
     * @return result
     */
    public int getCommonLineFlagsOrZero() {
        if (selLines.isEmpty()) return 0;
        int first = selLines.iterator().next();
        if (first < 0 || first >= map.linedefs.size()) return 0;
        int f = map.linedefs.get(first).flags;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int idx : selLines) {
            if (idx < 0 || idx >= map.linedefs.size()) continue;
            var l = map.linedefs.get(idx);
            f &= l.flags;
        }
        return f;
    }

    /**
     * Method getCommonThingFlagsOrZero.
     *
     * @return result
     */
    public int getCommonThingFlagsOrZero() {
        if (selThings.isEmpty()) return 0;
        int first = selThings.iterator().next();
        if (first < 0 || first >= map.things.size()) return 0;
        int f = map.things.get(first).flags();
        /**
         * Constructor for for.
         * @param selThings parameter
         */
        for (int idx : selThings) {
            if (idx < 0 || idx >= map.things.size()) continue;
            var t = map.things.get(idx);
            f &= t.flags();
        }
        return f;
    }

    public void selectLinedefsAdvanced(Integer special, Integer tag,
                                       Integer flagsHas, Integer flagsNot,
                                       String upLike, String midLike, String lowLike) {
        if (map == null) return;
        String upQ = normLike(upLike);
        String midQ = normLike(midLike);
        String lowQ = normLike(lowLike);
        selLines.clear();
        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            if (special != null && l.special != special) continue;
            if (tag != null && l.tag != tag) continue;
            if (flagsHas != null && (l.flags & flagsHas) != flagsHas) continue;
            if (flagsNot != null && (l.flags & flagsNot) != 0) continue;

            if ((upQ != null || midQ != null || lowQ != null)) {
                boolean ok = false;

                int[] sides = {l.rightSidedef, l.leftSidedef};
                /**
                 * Constructor for for.
                 * @param sides parameter
                 */
                for (int sidx : sides) {
                    if (sidx >= 0 && sidx != 0xFFFF && sidx < map.sidedefs.size()) {
                        var sd = map.sidedefs.get(sidx);
                        if (like(sd.upperTex(), upQ) && like(sd.middleTex(), midQ) && like(sd.lowerTex(), lowQ)) {
                            ok = true;
                            break;
                        }
                    }
                }
                if (!ok) continue;
            }
            selLines.add(i);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method selectThingsAdvanced.
     *
     * @param type     parameter
     * @param angle    parameter
     * @param flagsHas parameter
     * @param flagsNot parameter
     */
    public void selectThingsAdvanced(Integer type, Integer angle, Integer flagsHas, Integer flagsNot) {
        if (map == null) return;
        selThings.clear();
        for (int i = 0; i < map.things.size(); i++) {
            var t = map.things.get(i);
            if (type != null && t.type() != type) continue;
            if (angle != null && t.angle() != angle) continue;
            if (flagsHas != null && (t.flags() & flagsHas) != flagsHas) continue;
            if (flagsNot != null && (t.flags() & flagsNot) != 0) continue;
            selThings.add(i);
        }
        repaint();
        notifySelection();
    }

    public void selectSectorsAdvanced(Integer special, Integer tag,
                                      Integer lightMin, Integer lightMax,
                                      String floorLike, String ceilLike) {
        if (map == null) return;
        String fQ = normLike(floorLike);
        String cQ = normLike(ceilLike);
        selSectors.clear();
        for (int i = 0; i < map.sectors.size(); i++) {
            var s = map.sectors.get(i);
            if (special != null && s.special() != special) continue;
            if (tag != null && s.tag() != tag) continue;
            if (lightMin != null && s.lightLevel() < lightMin) continue;
            if (lightMax != null && s.lightLevel() > lightMax) continue;
            if (!like(s.floorTex(), fQ)) continue;
            if (!like(s.ceilingTex(), cQ)) continue;
            selSectors.add(i);
        }
        repaint();
        notifySelection();
    }

    /**
     * Method splitSelectedSectorsQuick.
     */
    public void splitSelectedSectorsQuick() {
        if (map == null) return;

        Integer refSec = null;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            if (li < 0 || li >= map.linedefs.size()) continue;
            var ld = map.linedefs.get(li);
            int[] sds = new int[]{ld.rightSidedef, ld.leftSidedef};
            /**
             * Constructor for for.
             * @param sds parameter
             */
            for (int sdi : sds) {
                if (sdi >= 0 && sdi != 0xFFFF && sdi < map.sidedefs.size()) {
                    int sec = map.sidedefs.get(sdi).sectorIndex();
                    if (sec >= 0 && sec < map.sectors.size()) {
                        refSec = sec;
                        break;
                    }
                }
            }
            if (refSec != null) break;
        }
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (refSec == null) {
            warnStatus("Split: select lines that border an existing sector.");
            return;
        }
        var s = map.sectors.get(refSec);

        createSectorFromSelectedLoop(s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(), s.lightLevel(), s.special(), s.tag());
    }

    /**
     * Method clearAllSecretSectors.
     *
     * @return result
     */
    public int clearAllSecretSectors() {
        if (map == null || map.sectors == null || map.sectors.isEmpty()) {
            warnStatus("No sectors to clean.");
            return 0;
        }
        pushUndo();
        int changed = 0;
        for (int si = 0; si < map.sectors.size(); si++) {
            var s = map.sectors.get(si);
            /**
             * Constructor for if.
             * @param 9 parameter
             */
            if (s.special() == 9) {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), s.floorTex(), s.ceilingTex(), s.lightLevel(), 0, s.tag()));
                changed++;
            }
        }
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (changed > 0) {
            repaint();
            notifySelection();
            infoStatus("Cleared " + changed + " secret sector(s).");
        } else {
            infoStatus("No secret sectors found.");
        }
        return changed;
    }

    /**
     * Method sectorsAdjacentToSelectedLines.
     *
     * @param lineSpecials parameter
     * @param includeRight parameter
     * @param includeLeft  parameter
     * @return result
     */
    private java.util.Set<Integer> sectorsAdjacentToSelectedLines(java.util.Set<Integer> lineSpecials, boolean includeRight, boolean includeLeft) {
        java.util.Set<Integer> secs = new java.util.HashSet<>();
        if (map == null) return secs;
        /**
         * Constructor for for.
         * @param selLines parameter
         */
        for (int li : selLines) {
            if (li < 0 || li >= map.linedefs.size()) continue;
            var l = map.linedefs.get(li);
            if (!lineSpecials.contains(l.special)) continue;
            /**
             * Constructor for if.
             * @param includeRight parameter
             */
            if (includeRight) {
                int rsd = l.rightSidedef;
                if (rsd >= 0 && rsd != 0xFFFF && rsd < map.sidedefs.size()) {
                    int sec = map.sidedefs.get(rsd).sectorIndex();
                    if (sec >= 0 && sec < map.sectors.size()) secs.add(sec);
                }
            }
            /**
             * Constructor for if.
             * @param includeLeft parameter
             */
            if (includeLeft) {
                int lsd = l.leftSidedef;
                if (lsd >= 0 && lsd != 0xFFFF && lsd < map.sidedefs.size()) {
                    int sec = map.sidedefs.get(lsd).sectorIndex();
                    if (sec >= 0 && sec < map.sectors.size()) secs.add(sec);
                }
            }
        }
        return secs;
    }

    /**
     * Method changeTeleportTextures.
     *
     * @param floorTex    parameter
     * @param ceilTex     parameter
     * @param affectRight parameter
     * @param affectLeft  parameter
     * @return result
     */
    public int changeTeleportTextures(String floorTex, String ceilTex, boolean affectRight, boolean affectLeft) {
        if (map == null) return 0;
        java.util.Set<Integer> tele = new java.util.HashSet<>();
        tele.add(39);
        tele.add(97);
        tele.add(125);
        tele.add(126);
        java.util.Set<Integer> targets = sectorsAdjacentToSelectedLines(tele, affectRight, affectLeft);
        if (targets.isEmpty()) {
            warnStatus("No teleport linedefs selected.");
            return 0;
        }
        pushUndo();
        int changed = 0;
        /**
         * Constructor for for.
         * @param targets parameter
         */
        for (int si : targets) {
            var s = map.sectors.get(si);
            String ft = (floorTex != null && !floorTex.isBlank()) ? floorTex : s.floorTex();
            String ct = (ceilTex != null && !ceilTex.isBlank()) ? ceilTex : s.ceilingTex();
            if (!ft.equals(s.floorTex()) || !ct.equals(s.ceilingTex())) {
                map.sectors.set(si, new org.deth.wad.MapData.Sector(
                        s.floorHeight(), s.ceilingHeight(), ft, ct, s.lightLevel(), s.special(), s.tag()));
                changed++;
            }
        }
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (changed > 0) {
            repaint();
            notifySelection();
            infoStatus("Teleport textures applied to " + changed + " sector(s).");
        } else {
            infoStatus("Teleport textures unchanged.");
        }
        return changed;
    }

    /**
     * Enum WizardSide.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum WizardSide {BACK, FRONT, BOTH}

    /**
     * Enum Tool.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Tool {SELECT, DRAW_LINE, INSERT_VERTEX, THING, SECTOR}


    /**
     * Enum Mode.
     * <p>Auto-generated documentation stub.</p>
     */
    private enum Mode {IDLE, PAN, BOX, MOVE_VERTS, MOVE_THINGS, DRAWING}


    /**
     * Enum TagSource.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum TagSource {SELECTED_SECTORS, LINES_FRONT, LINES_BACK, LINES_BOTH}


    /**
     * Enum BindMode.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum BindMode {FRONT, BACK, PREFER_FRONT}


    /**
     * Interface SelectionListener.
     * <p>Auto-generated documentation stub.</p>
     */
    public interface SelectionListener {
        void selectionChanged();
    }
}