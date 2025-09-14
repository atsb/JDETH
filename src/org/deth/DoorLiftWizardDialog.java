package org.deth;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class DoorLiftWizardDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class DoorLiftWizardDialog extends JDialog {
    private final MapPanel panel;
    private final Mode mode;
    private JComboBox<ComboItem> cbSpecial;
    private JSpinner spTag;
    private JCheckBox chkAutoAssignTag;
    private JCheckBox chkUseOnlySelectedLines;
    /**
     * Method DoorLiftWizardDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @param mode  parameter
     * @return result
     */
    public DoorLiftWizardDialog(Window owner, MapPanel panel, Mode mode) {
        super(owner, (mode == Mode.DOOR ? "Make Door from Sector…" : "Make Lift from Sector…"),
                Dialog.ModalityType.APPLICATION_MODAL);
        this.panel = panel;
        this.mode = mode;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Method openForDoor.
     *
     * @param owner parameter
     * @param panel parameter
     */
    public static void openForDoor(Window owner, MapPanel panel) {
        new DoorLiftWizardDialog(owner, panel, Mode.DOOR).setVisible(true);
    }

    /**
     * Method openForLift.
     *
     * @param owner parameter
     * @param panel parameter
     */
    public static void openForLift(Window owner, MapPanel panel) {
        new DoorLiftWizardDialog(owner, panel, Mode.LIFT).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        int selSecs = panel.getSelectedSectorIndices().length;
        int selLines = panel.getSelectedLinedefIndices().length;
        JLabel summary = new JLabel(
                String.format("Selected sectors: %d   |   Selected linedefs: %d", selSecs, selLines));
        root.add(summary, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;


        form.add(new JLabel("Linedef special:"), c);
        c.gridx = 1;
        c.weightx = 1;
        cbSpecial = new JComboBox<>(buildSpecials());
        form.add(cbSpecial, c);


        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Sector tag:"), c);
        c.gridx = 1;
        c.weightx = 1;
        int nextTag = panel.getNextFreeSectorTag();
        spTag = new JSpinner(new SpinnerNumberModel(nextTag, 1, 65535, 1));
        form.add(spTag, c);

        c.gridx = 1;
        c.gridy++;
        chkAutoAssignTag = new JCheckBox("Assign this tag to all selected sectors");
        chkAutoAssignTag.setSelected(true);
        form.add(chkAutoAssignTag, c);


        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Activator lines:"), c);
        c.gridx = 1;
        c.weightx = 1;
        chkUseOnlySelectedLines = new JCheckBox("Use only selected linedefs (otherwise, sector boundary)");
        chkUseOnlySelectedLines.setSelected(selLines > 0);
        form.add(chkUseOnlySelectedLines, c);

        root.add(form, BorderLayout.CENTER);


        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Create");
        buttons.add(cancel);
        buttons.add(ok);
        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> onCreate());

        setContentPane(root);
    }

    /**
     * Method onCreate.
     */
    private void onCreate() {
        int tag = (Integer) spTag.getValue();
        ComboItem it = (ComboItem) cbSpecial.getSelectedItem();
        int special = (it != null) ? it.value : 0;
        boolean assign = chkAutoAssignTag.isSelected();
        boolean useOnlySel = chkUseOnlySelectedLines.isSelected();

        if (panel.getSelectedSectorIndices().length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one sector first.", "Wizard", JOptionPane.WARNING_MESSAGE);
            return;
        }
        /**
         * Constructor for if.
         * @param ModeDOOR parameter
         */
        if (mode == Mode.DOOR) {
            panel.makeDoorFromSelectedSectors(special, assign, tag, useOnlySel);
        } else {
            panel.makeLiftFromSelectedSectors(special, assign, tag, useOnlySel);
        }
        dispose();
    }

    /**
     * Method buildSpecials.
     *
     * @return result
     */
    private DefaultComboBoxModel<ComboItem> buildSpecials() {

        Map<String, Integer> entries = new LinkedHashMap<>();
        /**
         * Constructor for if.
         * @param ModeDOOR parameter
         */
        if (mode == Mode.DOOR) {
            entries.put("DR Door Open-Wait-Close", 1);
            entries.put("S1 Door Open-Wait-Close", 28);
            entries.put("SR Door Open-Wait-Close", 29);
            entries.put("W1 Door Open-Wait-Close", 31);
            entries.put("W1 Door Open (stays open)", 26);
            entries.put("WR Door Open (stays open)", 27);
            entries.put("S1 Door Fast Open-Wait-Close", 117);
            entries.put("SR Door Fast Open-Wait-Close", 118);
        } else {

            entries.put("S1 Lift (lower-wait-rise)", 62);
            entries.put("SR Lift (lower-wait-rise)", 88);
            entries.put("W1 Lift (lower-wait-rise)", 121);
            entries.put("WR Lift (lower-wait-rise)", 122);
        }
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();
        for (var e : entries.entrySet()) m.addElement(new ComboItem(e.getKey(), e.getValue()));
        return m;
    }


    /**
     * Enum Mode.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Mode {DOOR, LIFT}

    /**
         * Class ComboItem.
         * <p>Auto-generated documentation stub.</p>
         */
        private record ComboItem(String label, int value) {
        /**
         * Constructor for ComboItem.
         *
         * @param label parameter
         * @param value parameter
         */
        private ComboItem {
        }

            /**
             * Method toString.
             *
             * @return result
             */
            public String toString() {
                return label + "  (" + value + ")";
            }
        }
}
