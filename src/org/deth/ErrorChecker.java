package org.deth;

import org.deth.wad.MapData;

import javax.swing.*;
import java.awt.*;

/**
 * Class ErrorChecker.
 * <p>Auto-generated documentation stub.</p>
 */
public final class ErrorChecker {
    /**
     * Method analyze.
     *
     * @param map parameter
     * @return result
     */
    public static java.util.List<Issue> analyze(MapData map) {
        java.util.List<Issue> issues = new java.util.ArrayList<>();

        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            var a = map.vertices.get(l.v1);
            var b = map.vertices.get(l.v2);
            if (a.x() == b.x() && a.y() == b.y()) {
                issues.add(new Issue(Kind.ZERO_LENGTH_LINE, "Zero-length linedef #" + i, i, -1, -1));
            }
        }

        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            if (l.special > 0 && l.tag == 0)
                issues.add(new Issue(Kind.UNTAGGED_SPECIAL, "Special with tag=0 on linedef #" + i, i, -1, -1));
        }

        for (int i = 0; i < map.linedefs.size(); i++) {
            var l = map.linedefs.get(i);
            int[] sides = new int[]{l.rightSidedef, l.leftSidedef};
            /**
             * Constructor for for.
             * @param sides parameter
             */
            for (int sidx : sides) {
                if (sidx == 0xFFFF || sidx < 0) continue;
                if (sidx >= map.sidedefs.size()) {
                    issues.add(new Issue(Kind.INVALID_SIDEDEF_SECTOR, "Invalid sidedef index on linedef #" + i + " -> sidedef #" + sidx, i, sidx, -1));
                } else {
                    int sec = map.sidedefs.get(sidx).sectorIndex();
                    if (sec < 0 || sec >= map.sectors.size()) {
                        issues.add(new Issue(Kind.MISSING_SIDEDEF_SECTOR, "Sidedef #" + sidx + " points to invalid sector #" + sec, i, sidx, sec));
                    }
                }
            }
            boolean rightValid = l.rightSidedef != 0xFFFF && l.rightSidedef >= 0;
            boolean leftValid = l.leftSidedef != 0xFFFF && l.leftSidedef >= 0;
            /**
             * Constructor for if.
             * @param leftValid parameter
             */
            if (rightValid ^ leftValid) {
                int sidx = rightValid ? l.rightSidedef : l.leftSidedef;
                if (sidx >= 0 && sidx < map.sidedefs.size()) {
                    var sd = map.sidedefs.get(sidx);
                    if (sd.middleTex() == null || sd.middleTex().equals("-") || sd.middleTex().isBlank()) {
                        issues.add(new Issue(Kind.ONE_SIDED_NO_MIDDLE, "One-sided line with empty middle texture at linedef #" + i, i, sidx, sd.sectorIndex()));
                    }
                }
            }
        }
        return issues;
    }

    /**
     * Method showDialog.
     *
     * @param owner parameter
     * @param panel parameter
     */
    public static void showDialog(Window owner, MapPanel panel) {
        MapData map = panel.getMapData();
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (map == null) {
            JOptionPane.showMessageDialog(owner, "No map loaded.");
            return;
        }
        java.util.List<Issue> issues = analyze(map);
        DefaultListModel<Issue> model = new DefaultListModel<>();
        for (Issue i : issues) model.addElement(i);
        JList<Issue> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton select = new JButton("Select");
        JButton close = new JButton("Close");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(select);
        south.add(close);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(new JLabel("Issues found: " + issues.size()), BorderLayout.NORTH);
        main.add(new JScrollPane(list), BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        JDialog dlg = new JDialog(owner, "Error Checker", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setContentPane(main);
        dlg.setSize(640, 420);
        dlg.setLocationRelativeTo(owner);

        select.addActionListener(e -> {
            Issue sel = list.getSelectedValue();
            if (sel == null) return;
            java.util.Set<Integer> v = new java.util.HashSet<>();
            java.util.Set<Integer> l = new java.util.HashSet<>();
            java.util.Set<Integer> t = new java.util.HashSet<>();
            java.util.Set<Integer> s = new java.util.HashSet<>();
            if (sel.linedef >= 0) l.add(sel.linedef);
            if (sel.sector >= 0) s.add(sel.sector);
            panel.setSelection(v, l, t, s);
            dlg.toFront();
        });
        close.addActionListener(e -> dlg.setVisible(false));
        dlg.setVisible(true);
    }

    /**
     * Enum Kind.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Kind {ZERO_LENGTH_LINE, UNTAGGED_SPECIAL, MISSING_SIDEDEF_SECTOR, ONE_SIDED_NO_MIDDLE, INVALID_SIDEDEF_SECTOR}

    /**
         * Class Issue.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Issue(Kind kind, String message, int linedef, int sidedef, int sector) {
        /**
         * Method Issue.
         *
         * @param kind    parameter
         * @param message parameter
         * @param linedef parameter
         * @param sidedef parameter
         * @param sector  parameter
         * @return result
         */
        public Issue {
        }

            @Override
            public String toString() {
                return message;
            }
        }
}
