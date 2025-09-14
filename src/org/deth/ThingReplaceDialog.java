package org.deth;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class ThingReplaceDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class ThingReplaceDialog extends JDialog {
    private final MapPanel panel;
    private final JTextField tfFromType = new JTextField();
    private final JTextField tfToType = new JTextField();
    private final JCheckBox cbSelectionOnly = new JCheckBox("Only within current selection", true);
    private final Map<String, JComboBox<Tri>> flagBoxes = new LinkedHashMap<>();
    private boolean accepted = false;

    /**
     * Method ThingReplaceDialog.
     *
     * @param panel parameter
     * @return result
     */
    public ThingReplaceDialog(MapPanel panel) {
        this.panel = panel;
    }

    /**
     * Method parseOrNull.
     *
     * @param s parameter
     * @return result
     */
    private static Integer parseOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        form.add(new JLabel("From type (id):"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tfFromType, c);

        c.gridx = 2;
        c.weightx = 0;
        JButton btnFromPal = new JButton("Palette…");
        form.add(btnFromPal, c);

        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("To type (id):"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tfToType, c);

        c.gridx = 2;
        c.weightx = 0;
        JButton btnToPal = new JButton("Palette…");
        form.add(btnToPal, c);


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        form.add(new JSeparator(), c);

        c.gridy++;
        c.gridwidth = 1;
        form.add(new JLabel("Flags:"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(new JLabel("No change / Set / Clear"), c);

        c.weightx = 0;

        addFlagRow(form, c, "Easy");
        addFlagRow(form, c, "Normal");
        addFlagRow(form, c, "Hard");
        addFlagRow(form, c, "Ambush");
        addFlagRow(form, c, "Not single");
        addFlagRow(form, c, "Not DM");
        addFlagRow(form, c, "Not coop");


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        form.add(new JSeparator(), c);
        c.gridy++;
        form.add(cbSelectionOnly, c);

        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Apply");
        btns.add(cancel);
        btns.add(ok);
        cancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });
        ok.addActionListener(e -> {
            if (getToType() == null && getFlagsSetMask() == 0 && getFlagsClearMask() == 0 && getFromType() == null) {
                JOptionPane.showMessageDialog(this, "Specify a change: to-type and/or flags.", "Nothing to do", JOptionPane.WARNING_MESSAGE);
                return;
            }
            accepted = true;
            setVisible(false);
        });
        root.add(btns, BorderLayout.SOUTH);


        java.awt.event.ActionListener openFrom = e -> {
            if (panel == null) return;
            java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(this);
            ThingPaletteDialog dlg = new ThingPaletteDialog(owner, panel.getThingTemplateType(), panel.getThingTemplateFlags());
            dlg.setLocationRelativeTo(owner);
            dlg.setVisible(true);
            Integer selType = dlg.getResultType();
            if (selType != null) tfFromType.setText(String.valueOf(selType));
        };
        java.awt.event.ActionListener openTo = e -> {
            if (panel == null) return;
            java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(this);
            ThingPaletteDialog dlg = new ThingPaletteDialog(owner, panel.getThingTemplateType(), panel.getThingTemplateFlags());
            dlg.setLocationRelativeTo(owner);
            dlg.setVisible(true);
            Integer selType = dlg.getResultType();
            if (selType != null) tfToType.setText(String.valueOf(selType));
        };
        btnFromPal.addActionListener(openFrom);
        btnToPal.addActionListener(openTo);

        setContentPane(root);
        setMinimumSize(new Dimension(420, 420));
    }

    /**
     * Method addFlagRow.
     *
     * @param form parameter
     * @param base parameter
     * @param name parameter
     */
    private void addFlagRow(JPanel form, GridBagConstraints base, String name) {
        GridBagConstraints c = (GridBagConstraints) base.clone();
        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel(name + ":"), c);
        c.gridx = 1;
        c.weightx = 1;
        JComboBox<Tri> cb = new JComboBox<>(Tri.values());
        cb.setSelectedItem(Tri.NO_CHANGE);
        flagBoxes.put(name, cb);
        form.add(cb, c);
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
     * Method isSelectionOnly.
     *
     * @return result
     */
    public boolean isSelectionOnly() {
        return cbSelectionOnly.isSelected();
    }

    /**
     * Method getFromType.
     *
     * @return result
     */
    public Integer getFromType() {
        return parseOrNull(tfFromType.getText());
    }

    /**
     * Method getToType.
     *
     * @return result
     */
    public Integer getToType() {
        return parseOrNull(tfToType.getText());
    }

    /**
     * Method getFlagsSetMask.
     *
     * @return result
     */
    public int getFlagsSetMask() {
        int m = 0;
        if (flagBoxes.get("Easy").getSelectedItem() == Tri.SET) m |= 0x0001;
        if (flagBoxes.get("Normal").getSelectedItem() == Tri.SET) m |= 0x0002;
        if (flagBoxes.get("Hard").getSelectedItem() == Tri.SET) m |= 0x0004;
        if (flagBoxes.get("Ambush").getSelectedItem() == Tri.SET) m |= 0x0008;
        if (flagBoxes.get("Not single").getSelectedItem() == Tri.SET) m |= 0x0010;
        if (flagBoxes.get("Not DM").getSelectedItem() == Tri.SET) m |= 0x0020;
        if (flagBoxes.get("Not coop").getSelectedItem() == Tri.SET) m |= 0x0040;
        return m;
    }

    /**
     * Method getFlagsClearMask.
     *
     * @return result
     */
    public int getFlagsClearMask() {
        int m = 0;
        if (flagBoxes.get("Easy").getSelectedItem() == Tri.CLEAR) m |= 0x0001;
        if (flagBoxes.get("Normal").getSelectedItem() == Tri.CLEAR) m |= 0x0002;
        if (flagBoxes.get("Hard").getSelectedItem() == Tri.CLEAR) m |= 0x0004;
        if (flagBoxes.get("Ambush").getSelectedItem() == Tri.CLEAR) m |= 0x0008;
        if (flagBoxes.get("Not single").getSelectedItem() == Tri.CLEAR) m |= 0x0010;
        if (flagBoxes.get("Not DM").getSelectedItem() == Tri.CLEAR) m |= 0x0020;
        if (flagBoxes.get("Not coop").getSelectedItem() == Tri.CLEAR) m |= 0x0040;
        return m;
    }

    /**
     * Enum Tri.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Tri {NO_CHANGE, SET, CLEAR}
}
