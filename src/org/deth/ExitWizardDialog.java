package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class ExitWizardDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class ExitWizardDialog extends JDialog {
    private final MapPanel panel;
    private final JComboBox<ComboItem> cbSpecial = new JComboBox<>();
    private final JCheckBox cbUseOnlySelected = new JCheckBox("Use only selected linedefs (otherwise, sector boundary)", false);
    private boolean accepted = false;

    /**
     * Method ExitWizardDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public ExitWizardDialog(Window owner, MapPanel panel) {
        super(owner, "Make Exit From Sector…", ModalityType.APPLICATION_MODAL);
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
        ExitWizardDialog d = new ExitWizardDialog(owner, panel);
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

        form.add(new JLabel("Exit type:"), c);
        c.gridx = 1;
        c.weightx = 1;
        cbSpecial.setModel(buildSpecials());
        form.add(cbSpecial, c);

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
            JOptionPane.showMessageDialog(this, "Select sectors or linedefs first.", "Wizard", JOptionPane.WARNING_MESSAGE);
            accepted = false;
            return;
        }
        int special = ((ComboItem) cbSpecial.getSelectedItem()).value;
        boolean useOnlySel = cbUseOnlySelected.isSelected();
        panel.makeExitFromSelectedSectors(special, useOnlySel);
        setVisible(false);
    }

    /**
     * Method isAccepted.
     *
     * @return result
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Method buildSpecials.
     *
     * @return result
     */
    private DefaultComboBoxModel<ComboItem> buildSpecials() {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();

        m.addElement(new ComboItem("Exit (S1)", 11));
        m.addElement(new ComboItem("Exit (W1)", 52));
        m.addElement(new ComboItem("Secret Exit (S1)", 51));
        m.addElement(new ComboItem("Secret Exit (W1)", 124));
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
