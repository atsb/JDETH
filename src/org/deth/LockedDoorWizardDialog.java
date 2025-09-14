package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class LockedDoorWizardDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class LockedDoorWizardDialog extends JDialog {

    private final MapPanel panel;


    private final JRadioButton rbVanilla = new JRadioButton("Vanilla (Doom / Doom II)", true);
    private final JRadioButton rbBoom = new JRadioButton("Boom generalized");


    private final JRadioButton rbVNormal = new JRadioButton("Normal speed (D1 / DR)", true);
    private final JRadioButton rbVFast = new JRadioButton("Fast (Doom II) (S1 / SR)");
    private final JRadioButton rbVOnce = new JRadioButton("One-time", true);
    private final JRadioButton rbVRepeat = new JRadioButton("Repeatable");
    private final JComboBox<String> cbVColor = new JComboBox<>(new String[]{"Blue", "Yellow", "Red"});


    private final JComboBox<String> cbBTrigger = new JComboBox<>(new String[]{"Use once (P1)", "Use repeat (PR)"});
    private final JComboBox<String> cbBSpeed = new JComboBox<>(new String[]{"Slow", "Normal", "Fast", "Turbo"});
    private final JCheckBox cbBStayOpen = new JCheckBox("Stay open (otherwise: open, wait, then close)");
    private final JComboBox<String> cbBColor = new JComboBox<>(new String[]{"Blue", "Yellow", "Red", "Any color", "All keys"});
    private final JComboBox<String> cbBKeyForm = new JComboBox<>(new String[]{"Card", "Skull", "Card or Skull"});


    private final JRadioButton rbTargetBack = new JRadioButton("Operate on sector behind each linedef (tag 0)", true);
    private final JRadioButton rbTargetTag = new JRadioButton("Operate on sectors with tag:");
    private final JSpinner spTag = new JSpinner(new SpinnerNumberModel(0, 0, 32767, 1));
    private final JButton btnAutoTag = new JButton("Auto");
    private final JCheckBox cbRetagSectors = new JCheckBox("Also set this tag on currently selected sectors");


    private final JCheckBox cbUseOnlySelectedLines =
            new JCheckBox("Affect only currently selected linedefs (otherwise use sector boundary)");

    /**
     * Method LockedDoorWizardDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public LockedDoorWizardDialog(Window owner, MapPanel panel) {
        super(owner, "Make Locked Door From Sector", ModalityType.APPLICATION_MODAL);
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
        new LockedDoorWizardDialog(owner, panel).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int ns = panel.getSelectedSectorIndices().length;
        int nl = panel.getSelectedLinedefIndices().length;
        JLabel info = new JLabel("Selected sectors: " + ns + "   |   Selected linedefs: " + nl);


        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(info);
        JPanel comp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ButtonGroup gCompat = new ButtonGroup();
        gCompat.add(rbVanilla);
        gCompat.add(rbBoom);
        comp.add(rbVanilla);
        comp.add(rbBoom);
        top.add(Box.createVerticalStrut(6));
        top.add(comp);
        root.add(top, BorderLayout.NORTH);


        JPanel cards = new JPanel(new CardLayout());
        cards.add(buildVanillaForm(), "vanilla");
        cards.add(buildBoomForm(), "boom");

        rbVanilla.addActionListener(e -> ((CardLayout) cards.getLayout()).show(cards, "vanilla"));
        rbBoom.addActionListener(e -> ((CardLayout) cards.getLayout()).show(cards, "boom"));

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(cards, BorderLayout.CENTER);
        center.add(buildTargetingPanel(nl), BorderLayout.EAST);
        root.add(center, BorderLayout.CENTER);


        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JPanel scope = new JPanel(new BorderLayout());
        cbUseOnlySelectedLines.setSelected(nl > 0);
        scope.add(cbUseOnlySelectedLines, BorderLayout.CENTER);
        bottom.add(scope);
        bottom.add(Box.createVerticalStrut(6));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Apply");
        btns.add(cancel);
        btns.add(ok);
        bottom.add(btns);

        cancel.addActionListener(e -> setVisible(false));
        ok.addActionListener(e -> onApply());

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        ((CardLayout) cards.getLayout()).show(cards, rbVanilla.isSelected() ? "vanilla" : "boom");
    }

    /**
     * Method buildTargetingPanel.
     *
     * @param nl parameter
     * @return result
     */
    private JPanel buildTargetingPanel(int nl) {
        JPanel target = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(4, 6, 4, 6);
        c2.anchor = GridBagConstraints.WEST;
        ButtonGroup gTarget = new ButtonGroup();
        gTarget.add(rbTargetBack);
        gTarget.add(rbTargetTag);
        c2.gridx = 0;
        c2.gridy = 0;
        target.add(new JLabel("Target:"), c2);
        c2.gridx = 0;
        c2.gridy = 1;
        target.add(rbTargetBack, c2);
        c2.gridy = 2;
        target.add(rbTargetTag, c2);

        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tagRow.add(new JLabel("Tag:"));
        spTag.setEnabled(false);
        btnAutoTag.setEnabled(false);
        cbRetagSectors.setEnabled(false);
        tagRow.add(spTag);
        tagRow.add(btnAutoTag);
        tagRow.add(cbRetagSectors);
        c2.gridx = 0;
        c2.gridy = 3;
        target.add(tagRow, c2);

        rbTargetTag.addActionListener(e -> {
            boolean en = rbTargetTag.isSelected();
            spTag.setEnabled(en);
            btnAutoTag.setEnabled(en);
            cbRetagSectors.setEnabled(en);
        });
        rbTargetBack.addActionListener(e -> {
            boolean en = rbTargetTag.isSelected();
            spTag.setEnabled(en);
            btnAutoTag.setEnabled(en);
            cbRetagSectors.setEnabled(en);
        });
        btnAutoTag.addActionListener(e -> {
            int free = panel.findFirstFreeTagNumber();
            spTag.setValue(free > 0 ? free : 1);
        });

        return target;
    }

    /**
     * Method buildVanillaForm.
     *
     * @return result
     */
    private JPanel buildVanillaForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;


        ButtonGroup gSpeed = new ButtonGroup();
        gSpeed.add(rbVNormal);
        gSpeed.add(rbVFast);
        form.add(new JLabel("Speed:"), c);
        c.gridx = 1;
        form.add(rbVNormal, c);
        c.gridx = 2;
        form.add(rbVFast, c);


        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Activation:"), c);
        ButtonGroup gAct = new ButtonGroup();
        gAct.add(rbVOnce);
        gAct.add(rbVRepeat);
        c.gridx = 1;
        form.add(rbVOnce, c);
        c.gridx = 2;
        form.add(rbVRepeat, c);


        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Key color:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        form.add(cbVColor, c);
        c.gridwidth = 1;

        return form;
    }

    /**
     * Method buildBoomForm.
     *
     * @return result
     */
    private JPanel buildBoomForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;


        form.add(new JLabel("Trigger:"), c);
        c.gridx = 1;
        form.add(cbBTrigger, c);
        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Speed:"), c);
        c.gridx = 1;
        form.add(cbBSpeed, c);
        c.gridx = 2;
        form.add(cbBStayOpen, c);


        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Key color:"), c);
        c.gridx = 1;
        form.add(cbBColor, c);
        c.gridx = 2;
        form.add(new JLabel("Key form:"), c);
        c.gridx = 3;
        form.add(cbBKeyForm, c);


        cbBColor.addActionListener(e -> {
            String col = (String) cbBColor.getSelectedItem();
            boolean allowForm = !("Any color".equals(col) || "All keys".equals(col));
            cbBKeyForm.setEnabled(allowForm);
        });
        cbBColor.setSelectedIndex(0);
        cbBKeyForm.setSelectedIndex(2);

        return form;
    }

    /**
     * Method onApply.
     */
    private void onApply() {
        if (panel.getSelectedSectorIndices().length == 0 && panel.getSelectedLinedefs().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select sector(s) or linedefs first.", "Locked Door", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final boolean useOnly = cbUseOnlySelectedLines.isSelected();
        int special;

        if (rbVanilla.isSelected()) {
            special = computeVanillaSpecial();
        } else {
            special = computeBoomGeneralized();
        }

        int tag = 0;
        if (rbTargetTag.isSelected()) {
            tag = ((Integer) spTag.getValue()).intValue();
            /**
             * Constructor for if.
             * @param 0 parameter
             */
            if (tag <= 0) {
                tag = panel.findFirstFreeTagNumber();
                if (tag <= 0) tag = 1;
            }
            if (cbRetagSectors.isSelected()) {
                try {
                    panel.setTagForSelectedSectors(tag);
                } catch (Throwable ignored) {
                }
            }
        }

        panel.makeDoorFromSelectedSectors(special, false, tag, useOnly);
        JOptionPane.showMessageDialog(this, "Applied locked door special " + special + (tag > 0 ? (" with tag " + tag) : "") + ".", "Locked Door", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }


    /**
     * Method computeVanillaSpecial.
     *
     * @return result
     */
    private int computeVanillaSpecial() {
        String color = (String) cbVColor.getSelectedItem();
        boolean fast = rbVFast.isSelected();
        boolean repeat = rbVRepeat.isSelected();

        /**
         * Constructor for if.
         * @param fast parameter
         */
        if (!fast) {

            /**
             * Constructor for if.
             * @param repeat parameter
             */
            if (repeat) {

                if ("Blue".equals(color)) return 26;
                if ("Yellow".equals(color)) return 27;
                return 28;
            } else {

                if ("Blue".equals(color)) return 32;
                if ("Red".equals(color)) return 33;
                return 34;
            }
        } else {

            /**
             * Constructor for if.
             * @param repeat parameter
             */
            if (repeat) {

                if ("Blue".equals(color)) return 99;
                if ("Red".equals(color)) return 134;
                return 136;
            } else {

                if ("Blue".equals(color)) return 133;
                if ("Red".equals(color)) return 135;
                return 137;
            }
        }
    }


    /**
     * Method computeBoomGeneralized.
     *
     * @return result
     */
    private int computeBoomGeneralized() {

        int type = 0x3800;


        int trigIndex = cbBTrigger.getSelectedIndex();
        int triggerBits = (trigIndex == 0) ? 6 : 7;
        type |= triggerBits;


        int speed = cbBSpeed.getSelectedIndex();
        type |= (speed << 3);


        int kind = cbBStayOpen.isSelected() ? 1 : 0;
        type |= (kind << 5);


        String color = (String) cbBColor.getSelectedItem();
        String form = (String) cbBKeyForm.getSelectedItem();
        int lockIndex;
        int skEqualsCard = 0;

        if ("Any color".equals(color)) {
            lockIndex = 0;
            skEqualsCard = 0;
        } else if ("All keys".equals(color)) {
            lockIndex = 7;
            skEqualsCard = 0;
        } else {
            boolean red = "Red".equals(color);
            boolean blue = "Blue".equals(color);
            boolean yellow = "Yellow".equals(color);
            if ("Card".equals(form)) {

                lockIndex = red ? 1 : blue ? 2 : 3;
                skEqualsCard = 0;
            } else if ("Skull".equals(form)) {

                lockIndex = red ? 4 : blue ? 5 : 6;
                skEqualsCard = 0;
            } else {

                lockIndex = red ? 1 : blue ? 2 : 3;
                skEqualsCard = 1;
            }
        }
        type |= (lockIndex << 6);
        type |= (skEqualsCard << 9);

        return type;
    }
}
