package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class ThingReplaceDialogDETH.
 * <p>Auto-generated documentation stub.</p>
 */
public class ThingReplaceDialogDETH extends JDialog {
    private final JTextField tfFromType = new JTextField();
    private final JTextField tfToType = new JTextField();

    private final JCheckBox cbSelOnly = new JCheckBox("Only within current selection", true);


    private final JCheckBox sEasy = new JCheckBox("Set Easy");
    private final JCheckBox sNorm = new JCheckBox("Set Normal");
    private final JCheckBox sHard = new JCheckBox("Set Hard");
    private final JCheckBox sAmbush = new JCheckBox("Set Ambush");
    private final JCheckBox sNS = new JCheckBox("Set Not single");
    private final JCheckBox sNDM = new JCheckBox("Set Not DM");
    private final JCheckBox sNCoop = new JCheckBox("Set Not coop");


    private final JCheckBox cEasy = new JCheckBox("Clear Easy");
    private final JCheckBox cNorm = new JCheckBox("Clear Normal");
    private final JCheckBox cHard = new JCheckBox("Clear Hard");
    private final JCheckBox cAmbush = new JCheckBox("Clear Ambush");
    private final JCheckBox cNS = new JCheckBox("Clear Not single");
    private final JCheckBox cNDM = new JCheckBox("Clear Not DM");
    private final JCheckBox cNCoop = new JCheckBox("Clear Not coop");

    private boolean accepted = false;

    /**
     * Method ThingReplaceDialogDETH.
     *
     * @param owner parameter
     * @return result
     */
    public ThingReplaceDialogDETH(Window owner) {
        super(owner, "Search & Replace Things (DETH)…", ModalityType.APPLICATION_MODAL);
        buildUI();
        pack();
        setLocationRelativeTo(owner);
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
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        form.add(new JLabel("From type (id, blank=any):"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tfFromType, c);

        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("To type (id, blank=keep):"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tfToType, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        form.add(new JSeparator(), c);


        JPanel flags = new JPanel(new GridLayout(0, 2, 8, 4));
        JPanel left = new JPanel(new GridLayout(0, 1, 4, 2));
        JPanel right = new JPanel(new GridLayout(0, 1, 4, 2));
        left.add(sEasy);
        left.add(sNorm);
        left.add(sHard);
        left.add(sAmbush);
        left.add(sNS);
        left.add(sNDM);
        left.add(sNCoop);
        right.add(cEasy);
        right.add(cNorm);
        right.add(cHard);
        right.add(cAmbush);
        right.add(cNS);
        right.add(cNDM);
        right.add(cNCoop);
        flags.add(left);
        flags.add(right);

        c.gridy++;
        form.add(flags, c);

        c.gridy++;
        form.add(cbSelOnly, c);

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
            accepted = true;
            setVisible(false);
        });
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(420, 420));
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
     * Method isSelectionOnly.
     *
     * @return result
     */
    public boolean isSelectionOnly() {
        return cbSelOnly.isSelected();
    }

    /**
     * Method getSetMask.
     *
     * @return result
     */
    public int getSetMask() {
        int m = 0;
        if (sEasy.isSelected()) m |= 0x0001;
        if (sNorm.isSelected()) m |= 0x0002;
        if (sHard.isSelected()) m |= 0x0004;
        if (sAmbush.isSelected()) m |= 0x0008;
        if (sNS.isSelected()) m |= 0x0010;
        if (sNDM.isSelected()) m |= 0x0020;
        if (sNCoop.isSelected()) m |= 0x0040;
        return m;
    }

    /**
     * Method getClearMask.
     *
     * @return result
     */
    public int getClearMask() {
        int m = 0;
        if (cEasy.isSelected()) m |= 0x0001;
        if (cNorm.isSelected()) m |= 0x0002;
        if (cHard.isSelected()) m |= 0x0004;
        if (cAmbush.isSelected()) m |= 0x0008;
        if (cNS.isSelected()) m |= 0x0010;
        if (cNDM.isSelected()) m |= 0x0020;
        if (cNCoop.isSelected()) m |= 0x0040;
        return m;
    }
}
