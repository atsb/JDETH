package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class TeleportWizardDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class TeleportWizardDialog extends JDialog {
    private final MapPanel panel;
    private final JComboBox<ComboItem> cbSpecial = new JComboBox<>();
    private final JCheckBox cbUseOnlySelected = new JCheckBox("Use only selected linedefs (otherwise, sector boundary)", false);
    private final JCheckBox cbCreateDest = new JCheckBox("Create Teleport Destination in selected sectors", true);
    private final JSpinner spAngle = new JSpinner(new SpinnerNumberModel(0, 0, 359, 45));
    private final JCheckBox cbAutoTag = new JCheckBox("Auto tag", true);
    private final JSpinner spTag = new JSpinner(new SpinnerNumberModel(0, 0, 32767, 1));
    private boolean accepted = false;

    /**
     * Method TeleportWizardDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public TeleportWizardDialog(Window owner, MapPanel panel) {
        super(owner, "Make Teleport From Sector…", ModalityType.APPLICATION_MODAL);
        this.panel = panel;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Method open.
     *
     * @param owner parameter
     * @param panel parameter
     */
    public static void open(Window owner, MapPanel panel) {
        TeleportWizardDialog d = new TeleportWizardDialog(owner, panel);
        d.setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int selSecs = panel.getSelectedSectorIndices().length;
        int selLines = panel.getSelectedLinedefIndices().length;
        JLabel summary = new JLabel(String.format("Selected sectors: %d   |   Selected linedefs: %d", selSecs, selLines));
        root.add(summary, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;

        form.add(new JLabel("Teleport type:"), c);
        c.gridx = 1;
        c.weightx = 1;
        cbSpecial.setModel(buildSpecials());
        form.add(cbSpecial, c);


        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Tag:"), c);
        JPanel tagBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        spTag.setEnabled(false);
        cbAutoTag.addActionListener(e -> spTag.setEnabled(!cbAutoTag.isSelected()));
        tagBox.add(cbAutoTag);
        tagBox.add(spTag);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tagBox, c);


        c.gridx = 1;
        c.gridy++;
        c.weightx = 1;
        cbUseOnlySelected.setSelected(selLines > 0);
        form.add(cbUseOnlySelected, c);


        JPanel dest = new JPanel(new GridBagLayout());
        dest.setBorder(BorderFactory.createTitledBorder("Destination"));
        GridBagConstraints dc = new GridBagConstraints();
        dc.insets = new Insets(4, 4, 4, 4);
        dc.fill = GridBagConstraints.HORIZONTAL;
        dc.gridx = 0;
        dc.gridy = 0;
        dest.add(cbCreateDest, dc);
        dc.gridx = 0;
        dc.gridy = 1;
        dest.add(new JLabel("Angle:"), dc);
        dc.gridx = 1;
        dc.weightx = 1;
        dest.add(spAngle, dc);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        form.add(dest, c);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Apply");
        btns.add(cancel);
        btns.add(ok);
        root.add(btns, BorderLayout.SOUTH);

        cancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });
        ok.addActionListener(e -> {
            accepted = true;
            onApply();
        });

        setContentPane(root);
    }

    /**
     * Method onApply.
     */
    private void onApply() {
        if (!accepted) return;
        if (panel.getSelectedSectorIndices().length == 0 && panel.getSelectedLinedefIndices().length == 0) {
            JOptionPane.showMessageDialog(this, "Select sectors and/or linedefs first.", "Teleport Wizard", JOptionPane.WARNING_MESSAGE);
            accepted = false;
            return;
        }
        int special = ((ComboItem) cbSpecial.getSelectedItem()).value;
        boolean onlySel = cbUseOnlySelected.isSelected();
        boolean createDest = cbCreateDest.isSelected();
        int angle = (Integer) spAngle.getValue();
        Integer tag = cbAutoTag.isSelected() ? null : (Integer) spTag.getValue();
        int changed = panel.wizardMakeTeleport(special, tag, onlySel, createDest, angle);
        JOptionPane.showMessageDialog(this, "Teleport setup complete. Changes: " + changed, "Teleport Wizard", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }

    /**
     * Method buildSpecials.
     *
     * @return result
     */
    private DefaultComboBoxModel<ComboItem> buildSpecials() {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();

        m.addElement(new ComboItem("Teleport (W1)", 39));
        m.addElement(new ComboItem("Teleport (WR)", 97));
        m.addElement(new ComboItem("Teleport Silent (SR)", 125));
        m.addElement(new ComboItem("Teleport Silent (WR)", 126));
        return m;
    }

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
