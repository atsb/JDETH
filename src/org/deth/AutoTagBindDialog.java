package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class AutoTagBindDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class AutoTagBindDialog extends JDialog {
    private final MapPanel panel;


    private final JCheckBox cbDoTag = new JCheckBox("Auto-tag sectors", true);
    private final JSpinner spStart = new JSpinner(new SpinnerNumberModel(1, 0, 32767, 1));
    private final JSpinner spStep = new JSpinner(new SpinnerNumberModel(1, 1, 1024, 1));
    private final JCheckBox cbOnlyUntagged = new JCheckBox("Only untagged sectors", true);
    private final JRadioButton rbTagSelectedSectors = new JRadioButton("Selected sectors", true);
    private final JRadioButton rbTagFromLinedefsFront = new JRadioButton("Sectors on linedefs (front)");
    private final JRadioButton rbTagFromLinedefsBack = new JRadioButton("Sectors on linedefs (back)");
    private final JRadioButton rbTagFromLinedefsBoth = new JRadioButton("Sectors on linedefs (both)");


    private final JCheckBox cbDoBind = new JCheckBox("Bind linedefs to sector tags", true);
    private final JCheckBox cbOnlyZeroLineTag = new JCheckBox("Only linedefs with tag=0", true);
    private final JRadioButton rbBindFront = new JRadioButton("Use front sector tag", true);
    private final JRadioButton rbBindBack = new JRadioButton("Use back sector tag");
    private final JRadioButton rbBindPreferF = new JRadioButton("Prefer front (fallback to back)");

    /**
     * Method AutoTagBindDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public AutoTagBindDialog(Window owner, MapPanel panel) {
        super(owner, "Auto-Tag & Bind Triggers", ModalityType.APPLICATION_MODAL);
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
    public static void open(java.awt.Window owner, MapPanel panel) {
        new AutoTagBindDialog(owner, panel).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel tagPanel = new JPanel(new GridBagLayout());
        tagPanel.setBorder(BorderFactory.createTitledBorder("Auto-tag sectors"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        tagPanel.add(cbDoTag, c);

        ButtonGroup grpTagSrc = new ButtonGroup();
        grpTagSrc.add(rbTagSelectedSectors);
        grpTagSrc.add(rbTagFromLinedefsFront);
        grpTagSrc.add(rbTagFromLinedefsBack);
        grpTagSrc.add(rbTagFromLinedefsBoth);

        c.gridy++;
        tagPanel.add(rbTagSelectedSectors, c);
        c.gridy++;
        tagPanel.add(rbTagFromLinedefsFront, c);
        c.gridy++;
        tagPanel.add(rbTagFromLinedefsBack, c);
        c.gridy++;
        tagPanel.add(rbTagFromLinedefsBoth, c);

        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tagRow.add(new JLabel("Start tag:"));
        tagRow.add(spStart);
        tagRow.add(new JLabel("Step:"));
        tagRow.add(spStep);
        tagRow.add(cbOnlyUntagged);
        c.gridy++;
        tagPanel.add(tagRow, c);


        JPanel bindPanel = new JPanel(new GridBagLayout());
        bindPanel.setBorder(BorderFactory.createTitledBorder("Bind linedefs to sector tags"));
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(4, 4, 4, 4);
        b.fill = GridBagConstraints.HORIZONTAL;
        b.gridx = 0;
        b.gridy = 0;
        b.weightx = 1;
        bindPanel.add(cbDoBind, b);
        ButtonGroup grpBind = new ButtonGroup();
        grpBind.add(rbBindFront);
        grpBind.add(rbBindBack);
        grpBind.add(rbBindPreferF);
        b.gridy++;
        bindPanel.add(rbBindFront, b);
        b.gridy++;
        bindPanel.add(rbBindBack, b);
        b.gridy++;
        bindPanel.add(rbBindPreferF, b);
        b.gridy++;
        bindPanel.add(cbOnlyZeroLineTag, b);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(tagPanel);
        center.add(bindPanel);
        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton apply = new JButton("Apply");
        buttons.add(cancel);
        buttons.add(apply);
        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> setVisible(false));
        apply.addActionListener(e -> onApply());

        setContentPane(root);
    }

    /**
     * Method onApply.
     */
    private void onApply() {
        int start = (Integer) spStart.getValue();
        int step = (Integer) spStep.getValue();
        int tagged = 0, bound = 0;

        if (cbDoTag.isSelected()) {
            MapPanel.TagSource ts =
                    rbTagSelectedSectors.isSelected() ? MapPanel.TagSource.SELECTED_SECTORS :
                            rbTagFromLinedefsFront.isSelected() ? MapPanel.TagSource.LINES_FRONT :
                                    rbTagFromLinedefsBack.isSelected() ? MapPanel.TagSource.LINES_BACK :
                                            MapPanel.TagSource.LINES_BOTH;
            tagged = panel.autoTagSectors(ts, start, step, cbOnlyUntagged.isSelected());
        }
        if (cbDoBind.isSelected()) {
            MapPanel.BindMode bm =
                    rbBindFront.isSelected() ? MapPanel.BindMode.FRONT :
                            rbBindBack.isSelected() ? MapPanel.BindMode.BACK :
                                    MapPanel.BindMode.PREFER_FRONT;
            bound = panel.bindSelectedLinedefsTags(bm, cbOnlyZeroLineTag.isSelected());
        }
        JOptionPane.showMessageDialog(this,
                "Auto-tagged sectors: " + tagged + "\nBound linedefs: " + bound,
                "Auto-Tag & Bind", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
    }
}
