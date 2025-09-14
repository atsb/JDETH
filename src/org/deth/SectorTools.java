package org.deth;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Class SectorTools.
 * <p>Auto-generated documentation stub.</p>
 */
public final class SectorTools {
    /**
     * Method prompt.
     *
     * @param owner parameter
     * @return result
     */
    public static Params prompt(Window owner) {
        JTextField tfFloor = new JTextField("0", 6);
        JTextField tfCeil = new JTextField("128", 6);
        JTextField tfFtex = new JTextField("FLOOR1", 8);
        JTextField tfCtex = new JTextField("CEIL1", 8);
        JTextField tfLight = new JTextField("160", 6);
        JTextField tfSpec = new JTextField("0", 6);
        JTextField tfTag = new JTextField("0", 6);

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Floor height:"));
        p.add(tfFloor);
        p.add(new JLabel("Ceiling height:"));
        p.add(tfCeil);
        p.add(new JLabel("Floor texture:"));
        p.add(tfFtex);
        p.add(new JLabel("Ceiling texture:"));
        p.add(tfCtex);
        p.add(new JLabel("Light level:"));
        p.add(tfLight);
        p.add(new JLabel("Special:"));
        p.add(tfSpec);
        p.add(new JLabel("Tag:"));
        p.add(tfTag);

        int r = JOptionPane.showConfirmDialog(owner, p, "Make Sector from Selected Loop",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return null;

        try {
            int floor = Integer.parseInt(tfFloor.getText().trim());
            int ceil = Integer.parseInt(tfCeil.getText().trim());
            int light = Integer.parseInt(tfLight.getText().trim());
            int spec = Integer.parseInt(tfSpec.getText().trim());
            int tag = Integer.parseInt(tfTag.getText().trim());

            String ftex = tfFtex.getText().trim().toUpperCase(Locale.ROOT);
            String ctex = tfCtex.getText().trim().toUpperCase(Locale.ROOT);
            if (ftex.isEmpty()) ftex = "-";
            if (ctex.isEmpty()) ctex = "-";

            return new Params(floor, ceil, ftex, ctex, light, spec, tag);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner, "Please enter valid numbers.", "Make Sector", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
         * Class Params.
         * <p>Auto-generated documentation stub.</p>
         */
        public record Params(int floor, int ceil, String ftex, String ctex, int light, int special, int tag) {
        /**
         * Method Params.
         *
         * @param floor   parameter
         * @param ceil    parameter
         * @param ftex    parameter
         * @param ctex    parameter
         * @param light   parameter
         * @param special parameter
         * @param tag     parameter
         * @return result
         */
        public Params {
        }
        }
}
