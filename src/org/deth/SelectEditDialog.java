package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class SelectEditDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class SelectEditDialog extends JDialog {
    private final MapPanel panel;

    private final JComboBox<String> cbEntity = new JComboBox<>(new String[]{"Linedefs", "Things", "Sectors"});


    private final JLabel selHelp = new JLabel(" ");

    private final JTextField liSpecial = new JTextField();
    private final JTextField liTag = new JTextField();
    private final JCheckBox liHasImpass = new JCheckBox("Impassable");
    private final JCheckBox liHasBlockMon = new JCheckBox("Block monsters");
    private final JCheckBox liHasTwoSided = new JCheckBox("Two-sided");
    private final JCheckBox liHasUpperUn = new JCheckBox("Upper unpegged");
    private final JCheckBox liHasLowerUn = new JCheckBox("Lower unpegged");
    private final JCheckBox liHasSecret = new JCheckBox("Secret");
    private final JCheckBox liHasBlockSnd = new JCheckBox("Block sound");
    private final JCheckBox liHasNotMap = new JCheckBox("Not on automap");
    private final JCheckBox liHasOnMap = new JCheckBox("Already on automap");

    private final JCheckBox liNotImpass = new JCheckBox("Impassable");
    private final JCheckBox liNotBlockMon = new JCheckBox("Block monsters");
    private final JCheckBox liNotTwoSided = new JCheckBox("Two-sided");
    private final JCheckBox liNotUpperUn = new JCheckBox("Upper unpegged");
    private final JCheckBox liNotLowerUn = new JCheckBox("Lower unpegged");
    private final JCheckBox liNotSecret = new JCheckBox("Secret");
    private final JCheckBox liNotBlockSnd = new JCheckBox("Block sound");
    private final JCheckBox liNotNotMap = new JCheckBox("Not on automap");
    private final JCheckBox liNotOnMap = new JCheckBox("Already on automap");

    private final JTextField liUpLike = new JTextField();
    private final JTextField liMidLike = new JTextField();
    private final JTextField liLowLike = new JTextField();


    private final JTextField thType = new JTextField();
    private final JTextField thAngle = new JTextField();
    private final JCheckBox thHasEasy = new JCheckBox("Easy");
    private final JCheckBox thHasNormal = new JCheckBox("Normal");
    private final JCheckBox thHasHard = new JCheckBox("Hard");
    private final JCheckBox thHasAmbush = new JCheckBox("Ambush");
    private final JCheckBox thHasNotSingle = new JCheckBox("Not SP");
    private final JCheckBox thHasNotDM = new JCheckBox("Not DM");
    private final JCheckBox thHasNotCoop = new JCheckBox("Not Coop");

    private final JCheckBox thNotEasy = new JCheckBox("Easy");
    private final JCheckBox thNotNormal = new JCheckBox("Normal");
    private final JCheckBox thNotHard = new JCheckBox("Hard");
    private final JCheckBox thNotAmbush = new JCheckBox("Ambush");
    private final JCheckBox thNotNotSingle = new JCheckBox("Not SP");
    private final JCheckBox thNotNotDM = new JCheckBox("Not DM");
    private final JCheckBox thNotNotCoop = new JCheckBox("Not Coop");


    private final JTextField seSpecialS = new JTextField();
    private final JTextField seTagS = new JTextField();
    private final JTextField seLightMin = new JTextField();
    private final JTextField seLightMax = new JTextField();
    private final JTextField seFloorLike = new JTextField();
    private final JTextField seCeilLike = new JTextField();


    private final JTextField leSpecial = new JTextField();
    private final JTextField leTag = new JTextField();
    private final JCheckBox lfImpass = new JCheckBox("Impassable");
    private final JCheckBox lfBlockMon = new JCheckBox("Block monsters");
    private final JCheckBox lfTwoSided = new JCheckBox("Two-sided");
    private final JCheckBox lfUpperUn = new JCheckBox("Upper unpegged");
    private final JCheckBox lfLowerUn = new JCheckBox("Lower unpegged");
    private final JCheckBox lfSecret = new JCheckBox("Secret");
    private final JCheckBox lfBlockSnd = new JCheckBox("Block sound");
    private final JCheckBox lfNotMap = new JCheckBox("Not on automap");
    private final JCheckBox lfOnMap = new JCheckBox("Already on automap");
    private final JCheckBox lfReplaceFlags = new JCheckBox("Replace flags (otherwise leave as-is)", false);


    private final JTextField teType = new JTextField();
    private final JTextField teAngle = new JTextField();
    private final JCheckBox tfEasy = new JCheckBox("Easy");
    private final JCheckBox tfNormal = new JCheckBox("Normal");
    private final JCheckBox tfHard = new JCheckBox("Hard");
    private final JCheckBox tfAmbush = new JCheckBox("Ambush");
    private final JCheckBox tfNotSingle = new JCheckBox("Not SP");
    private final JCheckBox tfNotDM = new JCheckBox("Not DM");
    private final JCheckBox tfNotCoop = new JCheckBox("Not Coop");
    private final JCheckBox tfReplaceFlags = new JCheckBox("Replace flags", false);


    private final JTextField seFloor = new JTextField();
    private final JTextField seCeil = new JTextField();
    private final JTextField seFtex = new JTextField();
    private final JTextField seCtex = new JTextField();
    private final JTextField seLight = new JTextField();
    private final JTextField seSpecial = new JTextField();
    private final JTextField seTag = new JTextField();

    /**
     * Method SelectEditDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public SelectEditDialog(Window owner, MapPanel panel) {
        super(owner, "Select / Mass Edit (DETH-style)", ModalityType.APPLICATION_MODAL);
        this.panel = panel;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(680, 540));
    }

    /**
     * Method emptyToNull.
     *
     * @param s parameter
     * @return result
     */
    private static String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Method parseInt.
     *
     * @param s parameter
     * @return result
     */
    private static Integer parseInt(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            return Integer.parseInt(s.trim());
        }
        /**
         * Constructor for catch.
         * @param ex parameter
         */ catch (Exception ex) {
            return null;
        }
    }

    /**
     * Method anySelected.
     *
     * @param boxes parameter
     * @return result
     */
    private static boolean anySelected(AbstractButton... boxes) {
        if (boxes == null) return false;
        for (AbstractButton b : boxes) if (b != null && b.isSelected()) return true;
        return false;
    }

    /**
     * Method open.
     *
     * @param owner parameter
     * @param panel parameter
     */
    public static void open(Window owner, MapPanel panel) {
        new SelectEditDialog(owner, panel).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.add(new JLabel("Entity:"));
        left.add(cbEntity);
        top.add(left, BorderLayout.WEST);
        selHelp.setForeground(new Color(60, 60, 60));
        top.add(selHelp, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Linedefs", buildLinedefEditor());
        tabs.add("Things", buildThingEditor());
        tabs.add("Sectors", buildSectorEditor());

        root.add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSelect = new JButton("Select");
        JButton btnApply = new JButton("Apply Edits to Selection");
        JButton btnClose = new JButton("Close");
        south.add(btnSelect);
        south.add(btnApply);
        south.add(btnClose);
        root.add(south, BorderLayout.SOUTH);

        btnSelect.addActionListener(e -> onSelect());
        btnApply.addActionListener(e -> onApplyEdits(tabs.getSelectedIndex()));
        btnClose.addActionListener(e -> setVisible(false));

        cbEntity.addActionListener(e -> updateHelp());
        updateHelp();

        setContentPane(root);
    }

    /**
     * Method buildLinedefEditor.
     *
     * @return result
     */
    private JPanel buildLinedefEditor() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        p.add(new JLabel("Special:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(liSpecial, c);
        c.gridx = 2;
        c.weightx = 0;
        p.add(new JLabel("Tag:"), c);
        c.gridx = 3;
        c.weightx = 1;
        p.add(liTag, c);
        y++;


        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        p.add(new JLabel("Upper like:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(liUpLike, c);
        c.gridx = 2;
        c.weightx = 0;
        p.add(new JLabel("Middle like:"), c);
        c.gridx = 3;
        c.weightx = 1;
        p.add(liMidLike, c);
        y++;
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        p.add(new JLabel("Lower like:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(liLowLike, c);
        y++;


        JPanel has = flagsPanel(new JCheckBox[]{liHasImpass, liHasBlockMon, liHasTwoSided, liHasUpperUn, liHasLowerUn, liHasSecret, liHasBlockSnd, liHasNotMap, liHasOnMap});
        JPanel not = flagsPanel(new JCheckBox[]{liNotImpass, liNotBlockMon, liNotTwoSided, liNotUpperUn, liNotLowerUn, liNotSecret, liNotBlockSnd, liNotNotMap, liNotOnMap});
        JPanel both = new JPanel(new GridLayout(1, 2, 8, 8));
        both.setBorder(BorderFactory.createTitledBorder("Flags"));
        both.add(wrap("Must HAVE", has));
        both.add(wrap("Must NOT HAVE", not));
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth = 4;
        c.weightx = 1;
        p.add(both, c);
        c.gridwidth = 1;
        y++;


        JPanel edit = new JPanel(new GridBagLayout());
        GridBagConstraints ec = new GridBagConstraints();
        ec.insets = new Insets(2, 2, 2, 2);
        ec.fill = GridBagConstraints.HORIZONTAL;
        int ey = 0;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Set Special:"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(leSpecial, ec);
        ec.gridx = 2;
        ec.weightx = 0;
        edit.add(new JLabel("Set Tag:"), ec);
        ec.gridx = 3;
        ec.weightx = 1;
        edit.add(leTag, ec);
        ey++;

        JPanel fgrid = flagsPanel(new JCheckBox[]{lfImpass, lfBlockMon, lfTwoSided, lfUpperUn, lfLowerUn, lfSecret, lfBlockSnd, lfNotMap, lfOnMap});
        fgrid.setBorder(BorderFactory.createTitledBorder("New Flags (used if Replace flags is ON)"));
        ec.gridx = 0;
        ec.gridy = ey;
        ec.gridwidth = 4;
        edit.add(fgrid, ec);
        ec.gridwidth = 1;
        ey++;
        ec.gridx = 0;
        ec.gridy = ey;
        ec.gridwidth = 4;
        edit.add(lfReplaceFlags, ec);
        ec.gridwidth = 1;
        ey++;

        p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JPanel outer = new JPanel(new BorderLayout());
        outer.add(p, BorderLayout.NORTH);
        outer.add(edit, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Method flagsPanel.
     *
     * @param boxes parameter
     * @return result
     */
    private JPanel flagsPanel(JCheckBox[] boxes) {
        JPanel g = new JPanel(new GridLayout(0, 3, 6, 2));
        for (JCheckBox b : boxes) g.add(b);
        return g;
    }

    /**
     * Method wrap.
     *
     * @param title parameter
     * @param c     parameter
     * @return result
     */
    private JPanel wrap(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /**
     * Method buildThingEditor.
     *
     * @return result
     */
    private JPanel buildThingEditor() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        p.add(new JLabel("Type:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(thType, c);
        c.gridx = 2;
        c.weightx = 0;
        p.add(new JLabel("Angle:"), c);
        c.gridx = 3;
        c.weightx = 1;
        p.add(thAngle, c);
        y++;

        JPanel has = flagsPanel(new JCheckBox[]{thHasEasy, thHasNormal, thHasHard, thHasAmbush, thHasNotSingle, thHasNotDM, thHasNotCoop});
        JPanel not = flagsPanel(new JCheckBox[]{thNotEasy, thNotNormal, thNotHard, thNotAmbush, thNotNotSingle, thNotNotDM, thNotNotCoop});
        JPanel both = new JPanel(new GridLayout(1, 2, 8, 8));
        both.setBorder(BorderFactory.createTitledBorder("Flags"));
        both.add(wrap("Must HAVE", has));
        both.add(wrap("Must NOT HAVE", not));
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth = 4;
        p.add(both, c);
        c.gridwidth = 1;
        y++;


        JPanel edit = new JPanel(new GridBagLayout());
        GridBagConstraints ec = new GridBagConstraints();
        ec.insets = new Insets(2, 2, 2, 2);
        ec.fill = GridBagConstraints.HORIZONTAL;
        int ey = 0;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Set Type:"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(teType, ec);
        ec.gridx = 2;
        ec.weightx = 0;
        edit.add(new JLabel("Set Angle:"), ec);
        ec.gridx = 3;
        ec.weightx = 1;
        edit.add(teAngle, ec);
        ey++;

        JPanel fgrid = flagsPanel(new JCheckBox[]{tfEasy, tfNormal, tfHard, tfAmbush, tfNotSingle, tfNotDM, tfNotCoop});
        fgrid.setBorder(BorderFactory.createTitledBorder("New Flags (used if Replace flags is ON)"));
        ec.gridx = 0;
        ec.gridy = ey;
        ec.gridwidth = 4;
        edit.add(fgrid, ec);
        ec.gridwidth = 1;
        ey++;
        ec.gridx = 0;
        ec.gridy = ey;
        ec.gridwidth = 4;
        edit.add(tfReplaceFlags, ec);
        ec.gridwidth = 1;
        ey++;

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(p, BorderLayout.NORTH);
        outer.add(edit, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Method buildSectorEditor.
     *
     * @return result
     */
    private JPanel buildSectorEditor() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        p.add(new JLabel("Special:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(seSpecialS, c);
        c.gridx = 2;
        c.weightx = 0;
        p.add(new JLabel("Tag:"), c);
        c.gridx = 3;
        c.weightx = 1;
        p.add(seTagS, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        p.add(new JLabel("Light min:"), c);
        c.gridx = 1;
        p.add(seLightMin, c);
        c.gridx = 2;
        p.add(new JLabel("Light max:"), c);
        c.gridx = 3;
        p.add(seLightMax, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        p.add(new JLabel("Floor like:"), c);
        c.gridx = 1;
        p.add(seFloorLike, c);
        c.gridx = 2;
        p.add(new JLabel("Ceil like:"), c);
        c.gridx = 3;
        p.add(seCeilLike, c);
        y++;


        JPanel edit = new JPanel(new GridBagLayout());
        GridBagConstraints ec = new GridBagConstraints();
        ec.insets = new Insets(2, 2, 2, 2);
        ec.fill = GridBagConstraints.HORIZONTAL;
        int ey = 0;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Floor height:"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(seFloor, ec);
        ec.gridx = 2;
        ec.weightx = 0;
        edit.add(new JLabel("Ceiling height:"), ec);
        ec.gridx = 3;
        ec.weightx = 1;
        edit.add(seCeil, ec);
        ey++;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Floor flat:"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(seFtex, ec);
        ec.gridx = 2;
        ec.weightx = 0;
        edit.add(new JLabel("Ceiling flat:"), ec);
        ec.gridx = 3;
        ec.weightx = 1;
        edit.add(seCtex, ec);
        ey++;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Light (0–255):"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(seLight, ec);
        ec.gridx = 2;
        ec.weightx = 0;
        edit.add(new JLabel("Special:"), ec);
        ec.gridx = 3;
        ec.weightx = 1;
        edit.add(seSpecial, ec);
        ey++;
        ec.gridx = 0;
        ec.gridy = ey;
        edit.add(new JLabel("Tag:"), ec);
        ec.gridx = 1;
        ec.weightx = 1;
        edit.add(seTag, ec);
        ey++;

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(p, BorderLayout.NORTH);
        outer.add(edit, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Method onSelect.
     */
    private void onSelect() {
        String ent = (String) cbEntity.getSelectedItem();
        if ("Linedefs".equals(ent)) {
            Integer sp = parseInt(liSpecial.getText());
            Integer tg = parseInt(liTag.getText());
            int has = buildLineFlags(liHasImpass, liHasBlockMon, liHasTwoSided, liHasUpperUn, liHasLowerUn, liHasSecret, liHasBlockSnd, liHasNotMap, liHasOnMap);
            int not = buildLineFlags(liNotImpass, liNotBlockMon, liNotTwoSided, liNotUpperUn, liNotLowerUn, liNotSecret, liNotBlockSnd, liNotNotMap, liNotOnMap);
            panel.selectLinedefsAdvanced(sp, tg, (has == 0 ? null : has), (not == 0 ? null : not),
                    liUpLike.getText(), liMidLike.getText(), liLowLike.getText());
        } else if ("Things".equals(ent)) {
            Integer type = parseInt(thType.getText());
            Integer ang = parseInt(thAngle.getText());
            int has = buildThingFlags(thHasEasy, thHasNormal, thHasHard, thHasAmbush, thHasNotSingle, thHasNotDM, thHasNotCoop);
            int not = buildThingFlags(thNotEasy, thNotNormal, thNotHard, thNotAmbush, thNotNotSingle, thNotNotDM, thNotNotCoop);
            panel.selectThingsAdvanced(type, ang, (has == 0 ? null : has), (not == 0 ? null : not));
        } else {
            Integer sp = parseInt(seSpecialS.getText());
            Integer tg = parseInt(seTagS.getText());
            Integer lmin = parseInt(seLightMin.getText());
            Integer lmax = parseInt(seLightMax.getText());
            panel.selectSectorsAdvanced(sp, tg, lmin, lmax, seFloorLike.getText(), seCeilLike.getText());
        }
    }

    /**
     * Method onApplyEdits.
     *
     * @param tabIndex parameter
     */
    private void onApplyEdits(int tabIndex) {
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (tabIndex == 0) {
            Integer sp = parseInt(leSpecial.getText());
            Integer tg = parseInt(leTag.getText());
            int flags = buildLineFlags(lfImpass, lfBlockMon, lfTwoSided, lfUpperUn, lfLowerUn, lfSecret, lfBlockSnd, lfNotMap, lfOnMap);
            panel.updateSelectedLinedefs((lfReplaceFlags.isSelected() || anySelected(lfImpass, lfBlockMon, lfTwoSided, lfUpperUn, lfLowerUn, lfSecret, lfBlockSnd, lfNotMap, lfOnMap)) ? flags : null, sp, tg);
        } else if (tabIndex == 1) {
            Integer ty = parseInt(teType.getText());
            Integer ang = parseInt(teAngle.getText());
            int flags = buildThingFlags(tfEasy, tfNormal, tfHard, tfAmbush, tfNotSingle, tfNotDM, tfNotCoop);
            panel.updateSelectedThings(ty, ang, (tfReplaceFlags.isSelected() || anySelected(tfEasy, tfNormal, tfHard, tfAmbush, tfNotSingle, tfNotDM, tfNotCoop)) ? flags : null);
        } else {
            Integer fl = parseInt(seFloor.getText());
            Integer ce = parseInt(seCeil.getText());
            String f = emptyToNull(seFtex.getText());
            String g = emptyToNull(seCtex.getText());
            Integer l = parseInt(seLight.getText());
            Integer sp = parseInt(seSpecial.getText());
            Integer tg = parseInt(seTag.getText());
            panel.updateSelectedSectors(fl, ce, f, g, l, sp, tg);
        }
    }

    /**
     * Method buildLineFlags.
     *
     * @param boxes parameter
     * @return result
     */
    private int buildLineFlags(JCheckBox... boxes) {
        int F_IMPASS = 0x0001, F_BLOCKMON = 0x0002, F_TWOSIDED = 0x0004, F_UPPERUN = 0x0008, F_LOWERUN = 0x0010, F_SECRET = 0x0020, F_BLOCKSND = 0x0040, F_NOTMAP = 0x0080, F_ONMAP = 0x0100;
        JCheckBox[] order = boxes;
        int[] masks = new int[]{F_IMPASS, F_BLOCKMON, F_TWOSIDED, F_UPPERUN, F_LOWERUN, F_SECRET, F_BLOCKSND, F_NOTMAP, F_ONMAP};
        int flags = 0;
        for (int i = 0; i < order.length && i < masks.length; i++) if (order[i].isSelected()) flags |= masks[i];
        return flags;
    }

    /**
     * Method buildThingFlags.
     *
     * @param boxes parameter
     * @return result
     */
    private int buildThingFlags(JCheckBox... boxes) {
        int[] masks = new int[]{0x0001, 0x0002, 0x0004, 0x0008, 0x0010, 0x0020, 0x0040};
        int flags = 0;
        for (int i = 0; i < boxes.length && i < masks.length; i++) if (boxes[i].isSelected()) flags |= masks[i];
        return flags;
    }

    /**
     * Method updateHelp.
     */
    private void updateHelp() {
        String ent = (String) cbEntity.getSelectedItem();
        if ("Linedefs".equals(ent)) {
            selHelp.setText("Select linedefs by Special/Tag, required/forbidden flags, and texture name filters (substring match on upper/mid/lower).");
        } else if ("Things".equals(ent)) {
            selHelp.setText("Select things by Type/Angle plus required/forbidden flags (skill, ambush, game modes).");
        } else {
            selHelp.setText("Select sectors by Special/Tag, light range, and floor/ceiling flat filters.");
        }
    }
}
