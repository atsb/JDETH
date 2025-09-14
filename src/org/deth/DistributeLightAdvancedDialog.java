package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class DistributeLightAdvancedDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class DistributeLightAdvancedDialog extends JDialog {
    private final JSpinner spStart = new JSpinner(new SpinnerNumberModel(160, 0, 255, 1));
    private final JSpinner spEnd = new JSpinner(new SpinnerNumberModel(96, 0, 255, 1));

    private final JRadioButton rbSeq = new JRadioButton("Sequence (adjacent path)", true);
    private final JRadioButton rbCentA = new JRadioButton("Centroid (auto axis)");
    private final JRadioButton rbCentX = new JRadioButton("Centroid (X axis)");
    private final JRadioButton rbCentY = new JRadioButton("Centroid (Y axis)");

    private final JCheckBox cbReverse = new JCheckBox("Reverse order");

    private boolean accepted = false;

    /**
     * Method DistributeLightAdvancedDialog.
     *
     * @param owner parameter
     * @return result
     */
    public DistributeLightAdvancedDialog(Window owner) {
        super(owner, "Distribute light (advanced)", ModalityType.APPLICATION_MODAL);

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbSeq);
        grp.add(rbCentA);
        grp.add(rbCentX);
        grp.add(rbCentY);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Start:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(spStart, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        p.add(new JLabel("End:"), c);
        c.gridx = 1;
        c.weightx = 1;
        p.add(spEnd, c);

        JPanel modes = new JPanel(new GridLayout(0, 1, 4, 4));
        modes.setBorder(BorderFactory.createTitledBorder("Order mode"));
        modes.add(rbSeq);
        modes.add(rbCentA);
        modes.add(rbCentX);
        modes.add(rbCentY);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Apply");
        south.add(cbReverse);
        south.add(cancel);
        south.add(ok);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(p, BorderLayout.NORTH);
        root.add(modes, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);

        cancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });
        ok.addActionListener(e -> {
            accepted = true;
            setVisible(false);
        });

        setSize(400, 320);
        setLocationRelativeTo(owner);
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
     * Method getStart.
     *
     * @return result
     */
    public int getStart() {
        return (Integer) spStart.getValue();
    }

    /**
     * Method getEnd.
     *
     * @return result
     */
    public int getEnd() {
        return (Integer) spEnd.getValue();
    }

    /**
     * Method getMode.
     *
     * @return result
     */
    public String getMode() {
        if (rbSeq.isSelected()) return "SEQUENCE";
        if (rbCentX.isSelected()) return "CENTROID_X";
        if (rbCentY.isSelected()) return "CENTROID_Y";
        return "CENTROID_AUTO";
    }

    /**
     * Method isReverse.
     *
     * @return result
     */
    public boolean isReverse() {
        return cbReverse.isSelected();
    }
}
