package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class TransformDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class TransformDialog extends JDialog {
    private final MapPanel panel;
    private final JSpinner spAngle = new JSpinner(new SpinnerNumberModel(0.0, -3600.0, 3600.0, 1.0));
    private final JSpinner spScaleX = new JSpinner(new SpinnerNumberModel(100.0, 1.0, 1000.0, 1.0));
    private final JSpinner spScaleY = new JSpinner(new SpinnerNumberModel(100.0, 1.0, 1000.0, 1.0));
    private final JCheckBox cbUniform = new JCheckBox("Uniform", true);
    private final JRadioButton rbCentroid = new JRadioButton("Centroid", true);
    private final JRadioButton rbPivot = new JRadioButton("Explicit pivot:");
    private final JSpinner spPx = new JSpinner(new SpinnerNumberModel(0.0, -1_000_000.0, 1_000_000.0, 1.0));
    private final JSpinner spPy = new JSpinner(new SpinnerNumberModel(0.0, -1_000_000.0, 1_000_000.0, 1.0));
    private boolean accepted = false;

    /**
     * Method TransformDialog.
     *
     * @param owner parameter
     * @param panel parameter
     * @return result
     */
    public TransformDialog(Window owner, MapPanel panel) {
        super(owner, "Transform Selection…", ModalityType.APPLICATION_MODAL);
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
        new TransformDialog(owner, panel).setVisible(true);
    }

    /**
     * Method buildUI.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        grid.add(new JLabel("Rotate (°):"), c);
        c.gridx = 1;
        c.weightx = 1;
        grid.add(spAngle, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        grid.add(new JLabel("Scale X (%):"), c);
        c.gridx = 1;
        c.weightx = 1;
        grid.add(spScaleX, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        grid.add(new JLabel("Scale Y (%):"), c);
        c.gridx = 1;
        c.weightx = 1;
        grid.add(spScaleY, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        grid.add(cbUniform, c);
        cbUniform.addActionListener(e -> {
            spScaleY.setEnabled(!cbUniform.isSelected());
            if (cbUniform.isSelected()) spScaleY.setValue(spScaleX.getValue());
        });
        spScaleX.addChangeListener(e -> {
            if (cbUniform.isSelected()) spScaleY.setValue(spScaleX.getValue());
        });
        y++;


        ButtonGroup grp = new ButtonGroup();
        grp.add(rbCentroid);
        grp.add(rbPivot);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        d.insets = new Insets(6, 6, 6, 6);
        d.fill = GridBagConstraints.HORIZONTAL;

        int yy = 0;
        d.gridx = 0;
        d.gridy = yy;
        d.weightx = 0;
        center.add(new JLabel("Center:"), d);
        d.gridx = 1;
        d.weightx = 1;
        center.add(rbCentroid, d);
        yy++;
        d.gridx = 1;
        d.gridy = yy;
        d.weightx = 1;
        center.add(rbPivot, d);
        d.gridx = 2;
        d.weightx = 0;
        center.add(new JLabel("X:"), d);
        d.gridx = 3;
        d.weightx = 1;
        center.add(spPx, d);
        d.gridx = 4;
        d.weightx = 0;
        center.add(new JLabel("Y:"), d);
        d.gridx = 5;
        d.weightx = 1;
        center.add(spPy, d);
        yy++;

        root.add(grid, BorderLayout.CENTER);
        root.add(center, BorderLayout.NORTH);

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
            applyNow();
        });
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /**
     * Method applyNow.
     */
    private void applyNow() {
        double ang = ((Number) spAngle.getValue()).doubleValue();
        double sx = ((Number) spScaleX.getValue()).doubleValue() / 100.0;
        double sy = ((Number) spScaleY.getValue()).doubleValue() / 100.0;
        if (rbCentroid.isSelected()) {
            if (Math.abs(sx - 1.0) > 1e-9 || Math.abs(sy - 1.0) > 1e-9) panel.scaleSelected(sx, sy);
            if (Math.abs(ang) > 1e-9) panel.rotateSelectedGeometry(ang);
        } else {
            double px = ((Number) spPx.getValue()).doubleValue();
            double py = ((Number) spPy.getValue()).doubleValue();
            if (Math.abs(sx - 1.0) > 1e-9 || Math.abs(sy - 1.0) > 1e-9) panel.scaleAroundPivot(sx, sy, px, py);
            if (Math.abs(ang) > 1e-9) panel.rotateAroundPivot(px, py, ang);
        }
        setVisible(false);
    }
}
