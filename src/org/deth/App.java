package org.deth;

import org.deth.wad.MapData;
import org.deth.wad.TexturePickerDialog;
import org.deth.wad.WadReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Class App.
 * <p>Auto-generated documentation stub.</p>
 */
public class App {
    /**
     * Method main.
     *
     * @param args parameter
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShow);
    }

    /**
     * Method createAndShow.
     */
    private static void createAndShow() {
        JFrame f = new JFrame("DETH - Doom Editor for Total Headcases");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel status = new JLabel("Ready");
        status.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        MapPanel panel = new MapPanel(1280, 800, status);


        final int INSPECTOR_W = 640;

        InspectorPanel inspector = new InspectorPanel(panel);

        inspector.setMinimumSize(new Dimension(INSPECTOR_W, 0));
        inspector.setPreferredSize(new Dimension(INSPECTOR_W, inspector.getPreferredSize().height));
        inspector.setMaximumSize(new Dimension(INSPECTOR_W, Integer.MAX_VALUE));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, inspector);
        split.setResizeWeight(1.0);
        split.setContinuousLayout(true);


        split.setOneTouchExpandable(false);
        split.setEnabled(false);
        split.setDividerSize(0);

        f.setJMenuBar(buildMenuBar(panel));
        f.add(buildToolbar(panel), BorderLayout.NORTH);
        f.add(split, BorderLayout.CENTER);
        f.add(status, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);


        SwingUtilities.invokeLater(() ->
                split.setDividerLocation(split.getWidth() - INSPECTOR_W)
        );
    }

    /**
     * Method buildMenuBar.
     *
     * @param panel parameter
     * @return result
     */
    private static JMenuBar buildMenuBar(MapPanel panel) {
        JMenuBar bar = new JMenuBar();


        JMenu file = new JMenu("File");

        file.add(new AbstractAction("Open WAD…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWad(panel);
            }
        });
        file.add(new AbstractAction("Open Project…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.openProject();
            }
        });
        file.add(new AbstractAction("Save Project…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveProject();
            }
        });
        file.addSeparator();
        file.add(new AbstractAction("Export to PWAD…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportWad(panel);
            }
        });
        file.add(new AbstractAction("Export & Build Nodes…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportWadAndBuild(panel);
            }
        });
        file.addSeparator();
        file.add(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bar.add(file);


        JMenu edit = new JMenu("Edit");
        edit.add(new AbstractAction("Undo (Ctrl+Z)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.undo();
            }
        });
        edit.add(new AbstractAction("Redo (Ctrl+Y)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.redo();
            }
        });
        edit.addSeparator();
        edit.add(new AbstractAction("Copy (Ctrl+C)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.copySelection();
            }
        });
        edit.add(new AbstractAction("Paste (Ctrl+V)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.pasteClipboard();
            }
        });
        edit.add(new AbstractAction("Find / Filter…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindFilterDialog dlg = new FindFilterDialog(
                        SwingUtilities.getWindowAncestor(panel),
                        new FindFilterDialog.Callback() {
                            /**
                             * Method selectLinedefs.
                             * @param sp parameter
                             * @param tg parameter
                             */
                            public void selectLinedefs(Integer sp, Integer tg) {
                                panel.selectLinedefsBy(sp, tg);
                            }

                            /**
                             * Method selectThings.
                             * @param type parameter
                             */
                            public void selectThings(Integer type) {
                                panel.selectThingsBy(type);
                            }

                            /**
                             * Method selectSectors.
                             * @param a parameter
                             * @param b parameter
                             */
                            public void selectSectors(Integer a, Integer b) {
                                panel.selectSectorsByLight(a, b);
                            }

                            /**
                             * Method setFilterMode.
                             * @param f parameter
                             */
                            public void setFilterMode(boolean f) {
                                panel.setFilterMode(f);
                            }
                        });
                dlg.setVisible(true);
            }
        });
        edit.add(new AbstractAction("Delete (Del)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.deleteSelection();
            }
        });
        bar.add(edit);


        JMenu view = new JMenu("View");

        JMenu bm = new JMenu("Bookmarks");
        bm.add(new AbstractAction("Save View 1 (Ctrl+F1)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveViewBookmark(0);
            }
        });
        bm.add(new AbstractAction("Save View 2 (Ctrl+F2)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveViewBookmark(1);
            }
        });
        bm.add(new AbstractAction("Save View 3 (Ctrl+F3)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveViewBookmark(2);
            }
        });
        bm.add(new AbstractAction("Save View 4 (Ctrl+F4)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveViewBookmark(3);
            }
        });
        bm.addSeparator();
        bm.add(new AbstractAction("Go to View 1 (F1)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.goToViewBookmark(0);
            }
        });
        bm.add(new AbstractAction("Go to View 2 (F2)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.goToViewBookmark(1);
            }
        });
        bm.add(new AbstractAction("Go to View 3 (F3)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.goToViewBookmark(2);
            }
        });
        bm.add(new AbstractAction("Go to View 4 (F4)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.goToViewBookmark(3);
            }
        });
        view.add(bm);

        view.add(new AbstractAction("Fit to View (F)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.fitToView();
            }
        });
        view.add(new AbstractAction("Toggle Grid (G)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.toggleGrid();
            }
        });
        view.add(new AbstractAction("Zoom In (+)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.zoomAt(panel.getWidth() / 2, panel.getHeight() / 2, 1.1);
            }
        });
        view.add(new AbstractAction("Zoom Out (-)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.zoomAt(panel.getWidth() / 2, panel.getHeight() / 2, 1 / 1.1);
            }
        });
        bar.add(view);


        JMenu linedefs = new JMenu("LineDefs");

        linedefs.add(new AbstractAction("Flip LineDef (X)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.flipSelectedLinedefs();
            }
        });
        linedefs.add(new AbstractAction("Split LineDef And Add A Vertex (M)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.splitSelectedLinedefsMidpoint();
            }
        });
        linedefs.addSeparator();
        linedefs.add(new AbstractAction("Swap Sidedefs (Front/Back)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.swapSelectedSidedefs();
            }
        });
        linedefs.add(new AbstractAction("Align Textures Intelligently On X") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.alignSelectedSidedefs(true);
            }
        });
        linedefs.add(new AbstractAction("Align Textures Intelligently On Y") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.alignSelectedSidedefs(false);
            }
        });
        linedefs.addSeparator();
        linedefs.add(new AbstractAction("Texture Picker…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(panel), null);
                dlg.setVisible(true);
                String tex = dlg.getResult();
                if (tex != null) panel.updateSelectedSidedefs(tex, tex, tex, null, null);
            }
        });

        bar.add(linedefs);


        JMenu sectors = new JMenu("Sectors");

        sectors.add(new AbstractAction("Make Sector From Selected Lines…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = SwingUtilities.getWindowAncestor(panel);
                SectorTools.Params p = SectorTools.prompt(owner);
                if (p != null)
                    panel.createSectorFromSelectedLoop(p.floor(), p.ceil(), p.ftex(), p.ctex(), p.light(), p.special(), p.tag());
            }
        });


        JMenu mfs = new JMenu("Make From Sector");
        JMenuItem doorFromSector = new JMenuItem("Make Door From Sector");
        JMenuItem liftFromSector = new JMenuItem("Make Lift From Sector");
        mfs.add(doorFromSector);
        mfs.add(liftFromSector);
        JMenuItem exitFromSector = new JMenuItem("Make Exit From Sector");
        mfs.add(exitFromSector);
        JMenuItem crusherFromSector = new JMenuItem("Make Crusher From Sector");
        mfs.add(crusherFromSector);
        JMenuItem wizTeleport = new JMenuItem("Make Teleport From Sector");
        mfs.add(wizTeleport);
        wizTeleport.addActionListener(e -> {
            TeleportWizardDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });
        JMenuItem lockedDoorFromSector = new JMenuItem("Make Locked Door From Sector");
        mfs.add(lockedDoorFromSector);
        lockedDoorFromSector.addActionListener(e -> {
            LockedDoorWizardDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });

        sectors.addSeparator();
        sectors.add(mfs);


        doorFromSector.addActionListener(e -> {
            if (panel.getSelectedSectorIndices().length == 0) {
                JOptionPane.showMessageDialog(panel, "Select one or more sectors first.", "Door Wizard", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DoorLiftWizardDialog.openForDoor(SwingUtilities.getWindowAncestor(panel), panel);
        });
        liftFromSector.addActionListener(e -> {
            if (panel.getSelectedSectorIndices().length == 0) {
                JOptionPane.showMessageDialog(panel, "Select one or more sectors first.", "Lift Wizard", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DoorLiftWizardDialog.openForLift(SwingUtilities.getWindowAncestor(panel), panel);
        });

        exitFromSector.addActionListener(e -> {
            ExitWizardDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });

        crusherFromSector.addActionListener(e -> {
            CrusherWizardDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });


        JMenu heights = new JMenu("Distribute heights");
        JMenuItem miDistFloor = new JMenuItem("Distribute floor…");
        JMenuItem miDistCeil = new JMenuItem("Distribute ceiling…");
        heights.add(miDistFloor);
        heights.add(miDistCeil);
        sectors.addSeparator();
        sectors.add(heights);

        sectors.add(new AbstractAction("Split Selected Sector(s) (quick)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.splitSelectedSectorsQuick();
            }
        });


        miDistFloor.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(panel),
                    "Enter: startHeight,endHeight", "0,128");
            if (s == null) return;
            String[] parts = s.split(",");
            /**
             * Constructor for if.
             * @param 2 parameter
             */
            if (parts.length != 2) {
                JOptionPane.showMessageDialog(panel, "Please enter two integers separated by a comma.");
                return;
            }
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                panel.distributeSelectedSectorHeights(true, a, b);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        miDistCeil.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(panel),
                    "Enter: startHeight,endHeight", "128,256");
            if (s == null) return;
            String[] parts = s.split(",");
            /**
             * Constructor for if.
             * @param 2 parameter
             */
            if (parts.length != 2) {
                JOptionPane.showMessageDialog(panel, "Please enter two integers separated by a comma.");
                return;
            }
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                panel.distributeSelectedSectorHeights(false, a, b);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JMenuItem miDistLight = new JMenuItem("Distribute light…");
        sectors.add(miDistLight);
        miDistLight.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(panel),
                    "Enter: startLight,endLight (0..255)", "160,96");
            if (s == null) return;
            String[] parts = s.split(",");
            /**
             * Constructor for if.
             * @param 2 parameter
             */
            if (parts.length != 2) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel), "Please enter two integers separated by a comma.");
                return;
            }
            try {
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                panel.distributeSelectedSectorLights(a, b);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel), "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JMenuItem sectorLighting = new JMenuItem("Lighting…");
        sectors.add(sectorLighting);
        sectorLighting.addActionListener(e -> {
            LightEffectsDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });
        bar.add(sectors);


        JMenu things = new JMenu("Things");
        things.add(new AbstractAction("Thing Palette…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ThingPaletteDialog dlg = new ThingPaletteDialog(SwingUtilities.getWindowAncestor(panel));
                dlg.setVisible(true);
                int t = dlg.getResultType();
                if (t > 0) panel.updateSelectedThings(t, null, null);
            }
        });

        JMenu thingFilters = new JMenu("Filters");
        JMenuItem tfMon = new JMenuItem("Monsters");
        JMenuItem tfPick = new JMenuItem("Pickups");
        JMenuItem tfDec = new JMenuItem("Decorations");
        tfMon.addActionListener(e -> panel.selectThingsByCategory("monsters"));
        tfPick.addActionListener(e -> panel.selectThingsByCategory("pickups"));
        tfDec.addActionListener(e -> panel.selectThingsByCategory("decor"));
        thingFilters.add(tfMon);
        thingFilters.add(tfPick);
        thingFilters.add(tfDec);
        things.add(thingFilters);

        bar.add(things);


        JMenu wiz = new JMenu("Wizards");
        JMenuItem wizDoor = new JMenuItem("Door…");
        JMenuItem wizLift = new JMenuItem("Lift…");
        wiz.add(wizDoor);
        wiz.add(wizLift);


        java.util.function.BiConsumer<String, Integer> runWizard = (title, defaultSpecial) -> {
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            JTextField tfSpecial = new JTextField(defaultSpecial > 0 ? Integer.toString(defaultSpecial) : "");
            JTextField tfTag = new JTextField("0");
            JComboBox<String> sideBox = new JComboBox<>(new String[]{
                    "Back (left side / behind the line)",
                    "Front (right side / in front of the line)",
                    "Both sides"
            });

            c.gridx = 0;
            c.gridy = 0;
            p.add(new JLabel("Linedef special (number):"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(tfSpecial, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0;
            p.add(new JLabel("Tag (0 = auto):"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(tfTag, c);
            c.gridx = 0;
            c.gridy = 2;
            c.weightx = 0;
            p.add(new JLabel("Affect sector on:"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(sideBox, c);

            int res = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(panel),
                    p, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            Integer special = null;
            try {
                special = Integer.parseInt(tfSpecial.getText().trim());
            } catch (Exception ignored) {
            }
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (special == null) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid linedef special number.");
                return;
            }

            Integer tag = null;
            try {
                String t = tfTag.getText().trim();
                tag = t.isEmpty() ? null : Integer.parseInt(t);
            }
            /**
             * Constructor for catch.
             * @param ignored parameter
             */ catch (Exception ignored) {
                tag = null;
            }

            org.deth.MapPanel.WizardSide side =
                    switch (sideBox.getSelectedIndex()) {
                        case 1 -> org.deth.MapPanel.WizardSide.FRONT;
                        case 2 -> org.deth.MapPanel.WizardSide.BOTH;
                        default -> org.deth.MapPanel.WizardSide.BACK;
                    };

            panel.wizardAssignSpecialAndTagToSelectedLines(special, tag, side);
        };

        wizDoor.addActionListener(e -> runWizard.accept("Door Wizard", 0));
        wizLift.addActionListener(e -> runWizard.accept("Lift Wizard", 0));


        JMenuItem wizStairs = new JMenuItem("Stairs (in-game)…");
        wiz.add(wizStairs);
        wizStairs.addActionListener(e -> {
            java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
            int suggested = panel.findFirstFreeTagNumber();
            StairBuilderDialog dlg = new StairBuilderDialog(owner, suggested);
            dlg.setVisible(true);
            if (!dlg.isAccepted()) return;
            int changes = panel.wizardSetupInGameStairs(
                    dlg.getSpecial(),
                    dlg.getTagOrNull(),
                    dlg.getFlatOrNull(),
                    dlg.getRiserWallTexOrNull(),
                    dlg.isApplyRisersLowerTex()
            );
            javax.swing.JOptionPane.showMessageDialog(panel,
                    "Stair setup complete. Changes applied: " + changes,
                    "Stairs (in-game)", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });


        JMenuItem wizCrusher = new JMenuItem("Crusher…");
        wiz.add(wizCrusher);
        wizCrusher.addActionListener(e -> {
            CrusherWizardDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
        });
        bar.add(wiz);


        JMenu tools = new JMenu("Tools");
        tools.add(new AbstractAction("Toggle Measure (Ctrl+M)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.toggleMeasure();
            }
        });
        tools.addSeparator();
        tools.add(new AbstractAction("Duplicate Selection (+64,+64)  Ctrl+D") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.duplicateSelection(64, 64);
            }
        });
        tools.add(new AbstractAction("Mirror Selection Horiz. (Alt+H)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.mirrorSelected(true);
            }
        });
        tools.add(new AbstractAction("Mirror Selection Vert. (Alt+V)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.mirrorSelected(false);
            }
        });
        tools.add(new AbstractAction("Rotate Selection -15°  Alt+Q") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.rotateSelectedGeometry(-15.0);
            }
        });
        tools.add(new AbstractAction("Rotate Selection +15°  Alt+E") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.rotateSelectedGeometry(15.0);
            }
        });
        tools.add(new AbstractAction("Snap Selected to Grid  Ctrl+Shift+S") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.snapSelectedToGrid();
            }
        });
        tools.add(new AbstractAction("Cleanup Unused Vertices  Ctrl+Shift+U") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.cleanupUnusedVertices();
            }
        });
        tools.add(new AbstractAction("Weld Duplicate Vertices (Ctrl+W)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.weldDuplicateVertices();
            }
        });


        tools.add(new AbstractAction("Clear all Secret Sectors") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = panel.clearAllSecretSectors();
                if (n == 0)
                    JOptionPane.showMessageDialog(panel, "No secret sectors (special=9) found.", "Clear Secrets", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        tools.add(new AbstractAction("Split LineDefs And Add A Sector…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.splitSelectedSectorsQuick();
            }
        });


        tools.add(new AbstractAction("Change Teleport Texture…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel p = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.insets = new Insets(4, 4, 4, 4);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                JTextField tfFloor = new JTextField("FLOOR0_1");
                JTextField tfCeil = new JTextField("");
                JComboBox<String> which = new JComboBox<>(new String[]{"Right side sectors", "Left side sectors", "Both sides"});
                c.gridx = 0;
                c.gridy = 0;
                p.add(new JLabel("Floor flat:"), c);
                c.gridx = 1;
                c.weightx = 1;
                p.add(tfFloor, c);
                c.gridx = 0;
                c.gridy = 1;
                c.weightx = 0;
                p.add(new JLabel("Ceiling flat (optional):"), c);
                c.gridx = 1;
                c.weightx = 1;
                p.add(tfCeil, c);
                c.gridx = 0;
                c.gridy = 2;
                c.gridwidth = 2;
                p.add(which, c);
                int r = JOptionPane.showConfirmDialog(panel, p, "Change Teleport Texture", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r != JOptionPane.OK_OPTION) return;
                String ff = tfFloor.getText().trim();
                String cc = tfCeil.getText().trim();
                int idx = which.getSelectedIndex();
                boolean affectRight = (idx == 0 || idx == 2);
                boolean affectLeft = (idx == 1 || idx == 2);
                int n = panel.changeTeleportTextures(ff, cc, affectRight, affectLeft);
                if (n == 0)
                    JOptionPane.showMessageDialog(panel, "No matching teleport linedefs in current selection.", "Teleport Texture", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tools.addSeparator();

        tools.addSeparator();

        tools.add(new AbstractAction("Search & Replace Textures…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                SearchReplaceTexturesDialog dlg = new SearchReplaceTexturesDialog(owner);
                dlg.setVisible(true);
                if (!dlg.isAccepted()) return;
                boolean inSel = dlg.isSelectionOnly();

                int changed = 0;

                String fw = dlg.getFromWall(), tw = dlg.getToWall();
                if (fw != null && tw != null && (dlg.repUpper() || dlg.repMiddle() || dlg.repLower())) {
                    changed += panel.replaceWallTextures(fw, tw, dlg.repUpper(), dlg.repMiddle(), dlg.repLower(), inSel);
                }

                String ff = dlg.getFromFlat(), tf = dlg.getToFlat();
                if (ff != null && tf != null && (dlg.repFloor() || dlg.repCeil())) {
                    changed += panel.replaceFlats(ff, tf, dlg.repFloor(), dlg.repCeil(), inSel);
                }
                javax.swing.JOptionPane.showMessageDialog(panel, changed + " texture reference(s) replaced.", "Search & Replace", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });

        tools.add(new AbstractAction("Search & Replace Things…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                ThingReplaceDialogDETH dlg = new ThingReplaceDialogDETH(owner);
                dlg.setVisible(true);
                if (!dlg.isAccepted()) return;

                Integer fromT = dlg.getFromType();
                Integer toT = dlg.getToType();
                int setMask = dlg.getSetMask();
                int clrMask = dlg.getClearMask();
                boolean inSel = dlg.isSelectionOnly();

                int changed = panel.replaceThings(fromT, toT, setMask, clrMask, inSel);
                javax.swing.JOptionPane.showMessageDialog(panel,
                        "Things changed: " + changed,
                        "Search & Replace Things",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });


        tools.addSeparator();
        tools.add(new AbstractAction("Renumber Selected Linedefs Lowest") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.renumberSelectedLinedefsLowest();
            }
        });
        tools.add(new AbstractAction("Renumber Selected Sectors Lowest") {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.renumberSelectedSectorsLowest();
            }
        });
        tools.add(new AbstractAction("Find First Free Tag…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int t = panel.findFirstFreeTagNumber();
                try {
                    java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(Integer.toString(t));
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                } catch (Throwable ignore) {
                }
                javax.swing.JOptionPane.showMessageDialog(panel, "First free tag: " + t + "\n(Copied to clipboard)", "Free Tag", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tools.add(new AbstractAction("Error Checker…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                ErrorChecker.showDialog(owner, panel);
            }
        });
        tools.add(new AbstractAction("Preferences…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                NodeBuilder.showPreferences(panel);
            }
        });

        tools.add(new AbstractAction("Auto-Tag & Bind Triggers…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                AutoTagBindDialog.open(owner, panel);
            }
        });
        tools.add(new AbstractAction("Quick Scrollers…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuickScrollerDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
            }
        });
        tools.add(new AbstractAction("Select by Property / Mass Edit…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectEditDialog.open(SwingUtilities.getWindowAncestor(panel), panel);
            }
        });


        bar.add(tools);


        JMenu resources = new JMenu("Resources");
        resources.add(new AbstractAction("Add Resource WAD…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addResourceWad(panel);
            }
        });
        resources.add(new AbstractAction("Clear Resource WADs") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResourceWads(panel);
            }
        });
        bar.add(resources);


        JMenu extended = new JMenu("Misc");


        JMenu construct = new JMenu("Construction");
        construct.add(new AbstractAction("Rectangle room…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(panel,
                        "width,height,floor,ceil,floorTex,ceilTex,light",
                        "256,256,0,128,FLOOR1,CEIL1,160");
                if (s == null) return;
                try {
                    String[] t = s.split(",");
                    int w = Integer.parseInt(t[0].trim());
                    int h = Integer.parseInt(t[1].trim());
                    int fl = Integer.parseInt(t[2].trim());
                    int cl = Integer.parseInt(t[3].trim());
                    String ft = t.length > 4 ? t[4].trim() : "FLOOR1";
                    String ct = t.length > 5 ? t[5].trim() : "CEIL1";
                    int li = (t.length > 6) ? Integer.parseInt(t[6].trim()) : 160;
                    panel.makeRectangleRoom(w, h, fl, cl, ft, ct, li);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Bad input.", "Rectangle room", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        construct.add(new AbstractAction("Make hallway…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(panel,
                        "width,floor,ceil,floorTex,ceilTex,light (leave blanks to copy from shared)",
                        "128,,,FLOOR1,CEIL1,");
                if (s == null) return;
                try {
                    String[] t = s.split(",", -1);
                    int width = Integer.parseInt(t[0].trim());
                    Integer fh = t[1].isBlank() ? null : Integer.valueOf(t[1].trim());
                    Integer ch = t[2].isBlank() ? null : Integer.valueOf(t[2].trim());
                    String ft = t[3].isBlank() ? null : t[3].trim();
                    String ct = t[4].isBlank() ? null : t[4].trim();
                    Integer ll = (t.length > 5 && !t[5].isBlank()) ? Integer.valueOf(t[5].trim()) : null;
                    if (!panel.makeHallwayBetweenSelectedLinedefs(width, fh, ch, ft, ct, ll)) {
                        JOptionPane.showMessageDialog(panel, "Couldn’t make hallway for the current selection.",
                                "Make hallway", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Bad input.", "Make hallway", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        construct.add(new AbstractAction("Pillar (centered)…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(panel,
                        "width,height,floor,ceil,floorTex,ceilTex,light",
                        "128,128,0,128,FLOOR1,CEIL1,160");
                if (s == null) return;
                try {
                    String[] t = s.split(",");
                    int w = Integer.parseInt(t[0].trim());
                    int h = Integer.parseInt(t[1].trim());
                    int fl = Integer.parseInt(t[2].trim());
                    int cl = Integer.parseInt(t[3].trim());
                    String ft = t.length > 4 ? t[4].trim() : "FLOOR1";
                    String ct = t.length > 5 ? t[5].trim() : "CEIL1";
                    int li = (t.length > 6) ? Integer.parseInt(t[6].trim()) : 160;
                    panel.makeRectanglePillar(w, h, fl, cl, ft, ct, li);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Bad input.", "Pillar", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        construct.add(new AbstractAction("Donut (ring + pillar)…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(panel,
                        "innerW,innerH,ringThick, ringFloor,ringCeil,ringF,ringC,ringLight, innerFloor,innerCeil,innerF,innerC,innerLight",
                        "128,128,32, 0,128,FLOOR1,CEIL1,144, 24,128,FLOOR1,CEIL1,160");
                if (s == null) return;
                try {
                    String[] t = s.split(",");
                    int innerW = Integer.parseInt(t[0].trim());
                    int innerH = Integer.parseInt(t[1].trim());
                    int ring = Integer.parseInt(t[2].trim());
                    int rf = Integer.parseInt(t[3].trim());
                    int rc = Integer.parseInt(t[4].trim());
                    String rF = t[5].trim();
                    String rC = t[6].trim();
                    int rL = Integer.parseInt(t[7].trim());
                    int ifl = Integer.parseInt(t[8].trim());
                    int icl = Integer.parseInt(t[9].trim());
                    String iF = t[10].trim();
                    String iC = t[11].trim();
                    int iL = Integer.parseInt(t[12].trim());
                    panel.makeDonut(innerW, innerH, ring, rf, rc, rF, rC, rL, ifl, icl, iF, iC, iL);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Bad input.", "Donut", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        extended.add(construct);


        JMenu pivot = new JMenu("Pivot transform");
        JMenuItem rotP = new JMenuItem("Rotate around pivot…");
        JMenuItem mirHX = new JMenuItem("Mirror H around pivot…");
        JMenuItem mirVY = new JMenuItem("Mirror V around pivot…");
        pivot.add(rotP);
        pivot.add(mirHX);
        pivot.add(mirVY);
        extended.add(pivot);

        rotP.addActionListener(e -> {
            java.awt.Window owner = SwingUtilities.getWindowAncestor(panel);
            String s = JOptionPane.showInputDialog(owner, "Enter: angle,px,py", "45,0,0");
            if (s == null) return;
            String[] t = s.split(",");
            /**
             * Constructor for if.
             * @param 3 parameter
             */
            if (t.length != 3) {
                JOptionPane.showMessageDialog(panel, "Please enter three numbers: angle,px,py");
                return;
            }
            try {
                double a = Double.parseDouble(t[0].trim());
                double px = Double.parseDouble(t[1].trim());
                double py = Double.parseDouble(t[2].trim());
                panel.rotateAroundPivot(a, px, py);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        mirHX.addActionListener(e -> {
            java.awt.Window owner = SwingUtilities.getWindowAncestor(panel);
            String s = JOptionPane.showInputDialog(owner, "Enter pivot: px,py", "0,0");
            if (s == null) return;
            String[] t = s.split(",");
            /**
             * Constructor for if.
             * @param 2 parameter
             */
            if (t.length != 2) {
                JOptionPane.showMessageDialog(panel, "Please enter two numbers: px,py");
                return;
            }
            try {
                double px = Double.parseDouble(t[0].trim());
                double py = Double.parseDouble(t[1].trim());
                panel.mirrorAroundPivot(true, px, py);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        mirVY.addActionListener(e -> {
            java.awt.Window owner = SwingUtilities.getWindowAncestor(panel);
            String s = JOptionPane.showInputDialog(owner, "Enter pivot: px,py", "0,0");
            if (s == null) return;
            String[] t = s.split(",");
            /**
             * Constructor for if.
             * @param 2 parameter
             */
            if (t.length != 2) {
                JOptionPane.showMessageDialog(panel, "Please enter two numbers: px,py");
                return;
            }
            try {
                double px = Double.parseDouble(t[0].trim());
                double py = Double.parseDouble(t[1].trim());
                panel.mirrorAroundPivot(false, px, py);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        extended.add(new AbstractAction("Texture Browser…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                java.io.File wad = chooseWadFile(owner);
                if (wad == null) return;
                new TextureBrowserDialog(owner, wad, panel.getMapData(), panel).setVisible(true);
            }
        });
        extended.add(new AbstractAction("Map Format: Toggle DOOM/HEXEN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HexenFormat.Format f = HexenFormat.get();
                HexenFormat.set(f == HexenFormat.Format.DOOM ? HexenFormat.Format.HEXEN : HexenFormat.Format.DOOM);
                JOptionPane.showMessageDialog(panel, "Map format now: " + HexenFormat.get());
            }
        });


        extended.add(new AbstractAction("Distribute light (advanced)…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
                DistributeLightAdvancedDialog dlg = new DistributeLightAdvancedDialog(owner);
                dlg.setVisible(true);
                if (!dlg.isAccepted()) return;
                panel.distributeSelectedSectorLightsAdvanced(
                        dlg.getStart(), dlg.getEnd(), dlg.getMode(), dlg.isReverse());
            }
        });


        extended.add(new AbstractAction("Join selected sectors") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = panel.joinSelectedSectors();
                /**
                 * Constructor for if.
                 * @param 0 parameter
                 */
                if (n <= 0) {
                    JOptionPane.showMessageDialog(panel, "Select two or more sectors to join.", "Join sectors", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "Joined " + (n + 1) + " sectors into one.", "Join sectors", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        bar.add(extended);


        JMenu help = new JMenu("Help");
        help.add(new AbstractAction("Legends…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLegendsDialog(SwingUtilities.getWindowAncestor(panel));
            }
        });
        help.add(new AbstractAction("About…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog(SwingUtilities.getWindowAncestor(panel));
            }
        });
        bar.add(help);

        return bar;
    }

    /**
     * Method showLegendsDialog.
     *
     * @param owner parameter
     */
    private static void showLegendsDialog(java.awt.Window owner) {
        JDialog d = new JDialog(owner, "Legends: R.I.P", Dialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        d.setResizable(false);
        d.setLayout(new BorderLayout());

        Color bg = Color.LIGHT_GRAY;
        Color fg = Color.BLACK;

        JTextPane tp = new JTextPane();
        tp.setEditable(false);
        tp.setOpaque(true);
        tp.setBackground(bg);
        tp.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        tp.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane sp = new JScrollPane(tp);
        sp.setOpaque(true);
        sp.setBackground(bg);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(bg);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getHorizontalScrollBar().setBackground(bg);
        sp.getVerticalScrollBar().setBackground(bg);

        String text =
                "Since the last release of DETH, some important members have since passed.\n\n" +
                        "* Ty Halderman.\n" +
                        "* Jim F. Flynn.\n" +
                        "* John W. Anderson.\n" +
                        "Thank you for your work and efforts in preserving DOOM, rest easy.\n\n" +
                        "Special Thanks to the following:\n\n" +
                        "DOOM Community — testers, documenters, fellow developers, port authors, moderators and modders.\n\n" +
                        "DETH 5.0 aims to preserve how older DOOM editors functioned, for new and old Doomers alike.\n" +
                        "We will do our best to keep the torch lit, in your honor.\n\n" +
                        "With love <3 - Gibbon";


        tp.setText(text);


        javax.swing.text.StyledDocument doc = tp.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attrs, fg);
        doc.setCharacterAttributes(0, doc.getLength(), attrs, false);

        d.add(sp, BorderLayout.CENTER);

        d.setSize(new Dimension(780, 620));
        d.setLocationRelativeTo(owner);
        d.setVisible(true);
    }

    /**
     * Method showAboutDialog.
     *
     * @param owner parameter
     */
    private static void showAboutDialog(java.awt.Window owner) {
        JOptionPane.showMessageDialog(owner,
                "DETH 5.0 — Doom Editor for Total Headcases\n" +
                        "© 2020 - 2025 Gibbon",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method addResourceWad.
     *
     * @param panel parameter
     */
    private static void addResourceWad(MapPanel panel) {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(panel);
        if (r != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try {
            org.deth.wad.TextureCache.get().addWad(f);
            JOptionPane.showMessageDialog(panel,
                    "Added resource WAD:\n" + f.getName(),
                    "Resources", JOptionPane.INFORMATION_MESSAGE);


            try {
                panel.fireSelectionChanged();
            } catch (Throwable t) {
                panel.repaint();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Failed to add resource WAD: " + ex.getMessage(),
                    "Resources", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method clearResourceWads.
     *
     * @param panel parameter
     */
    private static void clearResourceWads(MapPanel panel) {

        org.deth.wad.TextureCache.get().clear();
        JOptionPane.showMessageDialog(panel, "Cleared resource WADs.", "Resources",
                JOptionPane.INFORMATION_MESSAGE);
        panel.repaint();
    }

    /**
     * Method openWad.
     *
     * @param panel parameter
     */
    private static void openWad(MapPanel panel) {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(panel);
        if (r != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        panel.setCurrentPWADFile(file);

        try (WadReader wad = new WadReader(file)) {
            var maps = wad.listMaps();
            if (maps.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No maps found in WAD.", "Open WAD", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String choice = (String) JOptionPane.showInputDialog(
                    panel, "Select a map to load:", "Open WAD",
                    JOptionPane.PLAIN_MESSAGE, null, maps.toArray(), maps.get(0));
            if (choice == null) return;

            MapData data = wad.readMap(choice);
            panel.setMap(data, choice);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Failed to open WAD: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        try {
            var tc = org.deth.wad.TextureCache.get();


            tc.addWad(file);

            org.deth.wad.TextureCache.recomputeUsageFromMap(panel.getMapData());
            try {
                panel.fireSelectionChanged();
            } catch (Throwable ignore) {
                panel.repaint();
            }


            if (!tc.hasRenderableTextures()) {
                int ok = JOptionPane.showConfirmDialog(
                        panel,
                        "This WAD doesn’t include texture data.\nSelect a base IWAD (doom.wad/doom2.wad) for previews?",
                        "Textures",
                        JOptionPane.YES_NO_OPTION
                );
                /**
                 * Constructor for if.
                 * @param JOptionPaneYES_OPTION parameter
                 */
                if (ok == JOptionPane.YES_OPTION) {
                    JFileChooser fc2 = new JFileChooser();
                    if (fc2.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                        tc.addWad(fc2.getSelectedFile());
                    }
                }
            }


            org.deth.wad.TextureCache.recomputeUsageFromMap(panel.getMapData());


            try {
                panel.fireSelectionChanged();
            } catch (Throwable ignore) {
                panel.repaint();
            }

        } catch (Exception ignore) {

        }
    }

    /**
     * Method buildToolbar.
     *
     * @param panel parameter
     * @return result
     */
    private static JToolBar buildToolbar(MapPanel panel) {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        ButtonGroup grp = new ButtonGroup();

        JToggleButton sel = new JToggleButton("Select (1)");
        sel.addActionListener(e -> panel.setTool(MapPanel.Tool.SELECT));
        tb.add(sel);
        grp.add(sel);

        JToggleButton draw = new JToggleButton("Draw Line (2)");
        draw.addActionListener(e -> panel.setTool(MapPanel.Tool.DRAW_LINE));
        tb.add(draw);
        grp.add(draw);

        JToggleButton ins = new JToggleButton("Insert Vertex (3)");
        ins.addActionListener(e -> panel.setTool(MapPanel.Tool.INSERT_VERTEX));

        tb.add(ins);
        grp.add(ins);

        JToggleButton thing = new JToggleButton("Thing (4)");
        thing.addActionListener(e -> panel.setTool(MapPanel.Tool.THING));
        tb.add(thing);
        grp.add(thing);

        JToggleButton sector = new JToggleButton("Sector (5)");
        sector.addActionListener(e -> panel.setTool(MapPanel.Tool.SECTOR));
        tb.add(sector);
        grp.add(sector);
        tb.addSeparator();
        javax.swing.JButton umapBtn = new javax.swing.JButton("UMAPINFO Editor");
        umapBtn.setToolTipText("UMapinfo Editor");
        umapBtn.addActionListener(e -> {
            java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
            new UmapinfoEditorDialog((java.awt.Frame) owner, panel.getCurrentPWADFile(), panel.getMapName())
                    .setVisible(true);
        });
        tb.add(umapBtn);
        javax.swing.JButton dehBtn = new javax.swing.JButton("DeHacked Editor");
        dehBtn.setToolTipText("DeHacked Editor");
        dehBtn.addActionListener(e -> {
            java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(panel);
            new DehackedEditorDialog((java.awt.Frame) owner, panel.getCurrentPWADFile())
                    .setVisible(true);
        });
        tb.add(dehBtn);
        tb.addSeparator();
        JToggleButton snapT = new JToggleButton("Snap");
        snapT.setSelected(true);
        snapT.addActionListener(e -> panel.setSnap(snapT.isSelected()));
        tb.add(snapT);
        tb.add(new JLabel(" Grid: "));
        JComboBox<Integer> grid = new JComboBox<>(new Integer[]{8, 16, 32, 64});
        grid.setSelectedItem(16);
        grid.addActionListener(e -> panel.setGridStep((Integer) grid.getSelectedItem()));
        tb.add(grid);

        sel.setSelected(true);
        return tb;

    }


    /**
     * Method exportWad.
     *
     * @param panel parameter
     */
    private static void exportWad(MapPanel panel) {
        if (panel.getMapData() == null) {
            JOptionPane.showMessageDialog(panel, "No map loaded.", "Export", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String defaultName = panel.getMapName();
        if (defaultName == null || defaultName.isBlank()) defaultName = "MAP01";
        String mapName = JOptionPane.showInputDialog(panel, "Map name (E1M1 or MAP01):", defaultName);
        if (mapName == null) return;
        mapName = mapName.trim().toUpperCase();
        if (!mapName.matches("E[1-4]M[1-9]|MAP\\d\\d")) {
            JOptionPane.showMessageDialog(panel, "Invalid map name. Use E1M1 to E4M9 or MAP01..MAP99.", "Export", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        int r = fc.showSaveDialog(panel);
        if (r != JFileChooser.APPROVE_OPTION) return;
        java.io.File f = fc.getSelectedFile();
        try {
            org.deth.wad.WadWriter.writeSingleMap(f, mapName, panel.getMapData());
            JOptionPane.showMessageDialog(panel, "Exported PWAD: " + f.getName() +
                    "\nNote: nodes are not built; some ports require an external node builder.", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Export failed: " + ex.getMessage(), "Export", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Method exportWadAndBuild.
     *
     * @param panel parameter
     */
    private static void exportWadAndBuild(MapPanel panel) {
        if (panel.getMapData() == null) {
            JOptionPane.showMessageDialog(panel, "No map loaded.", "Export", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String defaultName = panel.getMapName();
        if (defaultName == null || defaultName.isBlank()) defaultName = "MAP01";
        String mapName = JOptionPane.showInputDialog(panel, "Map name (E1M1 or MAP01):", defaultName);
        if (mapName == null) return;
        mapName = mapName.trim().toUpperCase();
        if (!mapName.matches("E[1-4]M[1-9]|MAP\\d\\d")) {
            JOptionPane.showMessageDialog(panel, "Invalid map name. Use E1M1 to E4M9 or MAP01..MAP99.", "Export", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        int r = fc.showSaveDialog(panel);
        if (r != JFileChooser.APPROVE_OPTION) return;
        java.io.File f = fc.getSelectedFile();
        try {
            org.deth.wad.WadWriter.writeSingleMap(f, mapName, panel.getMapData());
            int exit = NodeBuilder.runBuilder(f, panel);
            /**
             * Constructor for if.
             * @param 0 parameter
             */
            if (exit >= 0) {
                JOptionPane.showMessageDialog(panel, "Exported and built nodes. Exit code: " + exit, "Export", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Export/build failed: " + ex.getMessage(), "Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method chooseWadFile.
     *
     * @param owner parameter
     * @return result
     */
    private static java.io.File chooseWadFile(java.awt.Window owner) {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        int r = fc.showOpenDialog(owner);
        if (r != javax.swing.JFileChooser.APPROVE_OPTION) return null;
        return fc.getSelectedFile();
    }
}