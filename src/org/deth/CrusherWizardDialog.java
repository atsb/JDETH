package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class CrusherWizardDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class CrusherWizardDialog extends JDialog {
    private final MapPanel panel;
    private final JComboBox<ComboItem> cbMode = new JComboBox<>();
    private final JCheckBox cbUseOnlySelected = new JCheckBox("Use only selected linedefs (otherwise, sector boundary)", false);
    private final JCheckBox cbAutoTag = new JCheckBox("Auto tag", true);
    private final JSpinner spTag = new JSpinner(new SpinnerNumberModel(0, 0, 32767, 1));
    private boolean accepted = false;

    /**
     * Method CrusherWizardDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public CrusherWizardDialog(Window owner, MapPanel panel) {
        super(owner, "Make Crusher From Sector…", ModalityType.APPLICATION_MODAL);
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
        CrusherWizardDialog d = new CrusherWizardDialog(owner, panel);
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

        form.add(new JLabel("Crusher action:"), c);
        c.gridx = 1;
        c.weightx = 1;
        cbMode.setModel(buildModes());
        form.add(cbMode, c);


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
            JOptionPane.showMessageDialog(this, "Select sectors and/or linedefs first.", "Crusher Wizard", JOptionPane.WARNING_MESSAGE);
            accepted = false;
            return;
        }
        ComboItem it = (ComboItem) cbMode.getSelectedItem();
        Integer tag = cbAutoTag.isSelected() ? null : (Integer) spTag.getValue();
        boolean onlySel = cbUseOnlySelected.isSelected();
        int changed = panel.wizardMakeCrusher(it.value, tag, onlySel);
        JOptionPane.showMessageDialog(this, "Crusher setup complete. Changes: " + changed, "Crusher Wizard", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }

    /**
     * Method buildModes.
     *
     * @return result
     */
    private DefaultComboBoxModel<ComboItem> buildModes() {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();

        m.addElement(new ComboItem("Start (W1 slow)", 6));
        m.addElement(new ComboItem("Start (S1 slow)", 25));
        m.addElement(new ComboItem("Start (WR fast)", 73));
        m.addElement(new ComboItem("Stop (W1)", 46));
        m.addElement(new ComboItem("Stop (S1)", 49));
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
