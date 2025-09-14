package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class LightEffectsDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class LightEffectsDialog extends JDialog {
    private final MapPanel panel;
    private final JComboBox<ComboItem> cbEffect = new JComboBox<>();
    private final JCheckBox cbSetLevel = new JCheckBox("Set light level to:", false);
    private final JSpinner spLevel = new JSpinner(new SpinnerNumberModel(160, 0, 255, 16));
    private boolean accepted = false;

    /**
     * Method LightEffectsDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public LightEffectsDialog(Window owner, MapPanel panel) {
        super(owner, "Sector Lighting…", ModalityType.APPLICATION_MODAL);
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
        LightEffectsDialog d = new LightEffectsDialog(owner, panel);
        d.setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int selSecs = panel.getSelectedSectorIndices().length;
        JLabel summary = new JLabel(String.format("Selected sectors: %d", selSecs));
        root.add(summary, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;

        form.add(new JLabel("Effect:"), c);
        c.gridx = 1;
        c.weightx = 1;
        cbEffect.setModel(buildEffects());
        form.add(cbEffect, c);

        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(cbSetLevel, c);
        c.gridx = 1;
        c.weightx = 1;
        spLevel.setEnabled(false);
        cbSetLevel.addActionListener(e -> spLevel.setEnabled(cbSetLevel.isSelected()));
        form.add(spLevel, c);

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
        if (panel.getSelectedSectorIndices().length == 0) {
            JOptionPane.showMessageDialog(this, "Select sectors first.", "Sector Lighting", JOptionPane.WARNING_MESSAGE);
            accepted = false;
            return;
        }
        ComboItem it = (ComboItem) cbEffect.getSelectedItem();
        Integer level = cbSetLevel.isSelected() ? (Integer) spLevel.getValue() : null;
        int changed = panel.applySectorLightingEffect(it.value, level);
        JOptionPane.showMessageDialog(this, "Applied to sectors: " + changed, "Sector Lighting", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }

    /**
     * Method buildEffects.
     *
     * @return result
     */
    private DefaultComboBoxModel<ComboItem> buildEffects() {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();

        m.addElement(new ComboItem("Normal (0)", 0));
        m.addElement(new ComboItem("Blink random (1)", 1));
        m.addElement(new ComboItem("Blink 0.5s (2)", 2));
        m.addElement(new ComboItem("Blink 1.0s (3)", 3));
        m.addElement(new ComboItem("Oscillate (8)", 8));
        m.addElement(new ComboItem("Blink 1.0s sync (12)", 12));
        m.addElement(new ComboItem("Blink 0.5s sync (13)", 13));
        m.addElement(new ComboItem("Flicker random (17)", 17));

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
