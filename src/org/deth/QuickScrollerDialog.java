package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class QuickScrollerDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class QuickScrollerDialog extends JDialog {
    private final MapPanel panel;
    private final JRadioButton rbEnable = new JRadioButton("Enable scroll (vanilla: left) — set special 48");
    private final JRadioButton rbDisable = new JRadioButton("Disable scroll — set special 0");
    private final JCheckBox cbOnlyZeroTag = new JCheckBox("Only affect linedefs with tag = 0", false);

    /**
     * Method QuickScrollerDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public QuickScrollerDialog(Window owner, MapPanel panel) {
        super(owner, "Quick Scrollers", ModalityType.APPLICATION_MODAL);
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
        new QuickScrollerDialog(owner, panel).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel("<html>Applies to <b>selected linedefs</b>.<br>" +
                "Vanilla DOOM supports a single scroll direction (type 48: left).</html>");
        root.add(info, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbEnable);
        grp.add(rbDisable);
        rbEnable.setSelected(true);

        c.gridx = 0;
        c.gridy = 0;
        center.add(rbEnable, c);
        c.gridy = 1;
        center.add(rbDisable, c);
        c.gridy = 2;
        center.add(cbOnlyZeroTag, c);

        root.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton apply = new JButton("Apply");
        south.add(cancel);
        south.add(apply);
        root.add(south, BorderLayout.SOUTH);

        cancel.addActionListener(e -> setVisible(false));
        apply.addActionListener(e -> onApply());

        setContentPane(root);
    }

    /**
     * Method onApply.
     */
    private void onApply() {
        boolean enable = rbEnable.isSelected();
        int changed = panel.applyQuickScroller(enable, cbOnlyZeroTag.isSelected());
        JOptionPane.showMessageDialog(this, "Linedefs changed: " + changed, "Quick Scrollers",
                JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }
}