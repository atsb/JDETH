package org.deth;

import org.deth.wad.TexturePickerDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Class StairBuilderDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class StairBuilderDialog extends JDialog {
    private final JRadioButton rbStep8 = new JRadioButton("Step height 8", true);
    private final JRadioButton rbStep16 = new JRadioButton("Step height 16");
    private final JRadioButton rbW1 = new JRadioButton("W1 (walk over, once)", true);
    private final JRadioButton rbS1 = new JRadioButton("S1 (use, once)");

    private final JCheckBox cbAutoTag = new JCheckBox("Auto tag", true);
    private final JSpinner spTag = new JSpinner(new SpinnerNumberModel(0, 0, 32767, 1));


    private final JTextField tfFlat = new JTextField();
    private final JButton btnPickFlat = new JButton("…");
    private final JTextField tfRiser = new JTextField();
    private final JButton btnPickRiser = new JButton("…");
    private final JCheckBox cbApplyRisers = new JCheckBox("Apply riser wall texture to LOWER", true);

    private boolean accepted = false;

    /**
     * Method StairBuilderDialog.
     *
     * @param owner        parameter
     * @param suggestedTag parameter
     * @return result
     */
    public StairBuilderDialog(Window owner, int suggestedTag) {
        super(owner, "Stairs (in-game)", ModalityType.APPLICATION_MODAL);

        ButtonGroup gStep = new ButtonGroup();
        gStep.add(rbStep8);
        gStep.add(rbStep16);
        ButtonGroup gTrig = new ButtonGroup();
        gTrig.add(rbW1);
        gTrig.add(rbS1);

        spTag.setValue(suggestedTag);
        spTag.setEnabled(false);
        cbAutoTag.addActionListener(e -> spTag.setEnabled(!cbAutoTag.isSelected()));
        if (suggestedTag > 0) {
            cbAutoTag.setSelected(false);
            spTag.setEnabled(true);
        }

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;


        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Step:"), c);
        JPanel stepBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stepBox.add(rbStep8);
        stepBox.add(rbStep16);
        c.gridx = 1;
        c.weightx = 1;
        p.add(stepBox, c);


        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        p.add(new JLabel("Trigger:"), c);
        JPanel trigBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        trigBox.add(rbW1);
        trigBox.add(rbS1);
        c.gridx = 1;
        c.weightx = 1;
        p.add(trigBox, c);


        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        p.add(new JLabel("Tag:"), c);
        JPanel tagBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tagBox.add(cbAutoTag);
        tagBox.add(spTag);
        c.gridx = 1;
        c.weightx = 1;
        p.add(tagBox, c);


        JPanel textures = new JPanel(new GridBagLayout());
        textures.setBorder(BorderFactory.createTitledBorder("Textures (optional)"));
        GridBagConstraints tc = new GridBagConstraints();
        tc.insets = new Insets(4, 4, 4, 4);
        tc.fill = GridBagConstraints.HORIZONTAL;

        tc.gridx = 0;
        tc.gridy = 0;
        textures.add(new JLabel("Step floor flat:"), tc);
        tc.gridx = 1;
        tc.weightx = 1;
        textures.add(tfFlat, tc);
        tc.gridx = 2;
        tc.weightx = 0;
        textures.add(btnPickFlat, tc);

        tc.gridx = 0;
        tc.gridy = 1;
        tc.weightx = 0;
        textures.add(new JLabel("Riser wall texture:"), tc);
        tc.gridx = 1;
        tc.weightx = 1;
        textures.add(tfRiser, tc);
        tc.gridx = 2;
        tc.weightx = 0;
        textures.add(btnPickRiser, tc);

        tc.gridx = 1;
        tc.gridy = 2;
        tc.gridwidth = 2;
        textures.add(cbApplyRisers, tc);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        p.add(textures, c);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Apply");
        south.add(cancel);
        south.add(ok);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(p, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);


        btnPickFlat.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.FLATS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfFlat.setText(r);
        });
        btnPickRiser.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfRiser.setText(r);
        });

        cancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });
        ok.addActionListener(e -> {
            accepted = true;
            setVisible(false);
        });

        setSize(520, 320);
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
     * Method getSpecial.
     *
     * @return result
     */
    public int getSpecial() {
        boolean step16 = rbStep16.isSelected();
        boolean s1 = rbS1.isSelected();

        if (step16) return s1 ? 104 : 100;
        else return s1 ? 8 : 7;
    }

    /**
     * Method getTagOrNull.
     *
     * @return result
     */
    public Integer getTagOrNull() {
        if (cbAutoTag.isSelected()) return null;
        return (Integer) spTag.getValue();
    }

    /**
     * Method getFlatOrNull.
     *
     * @return result
     */
    public String getFlatOrNull() {
        String t = tfFlat.getText().trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Method getRiserWallTexOrNull.
     *
     * @return result
     */
    public String getRiserWallTexOrNull() {
        String t = tfRiser.getText().trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Method isApplyRisersLowerTex.
     *
     * @return result
     */
    public boolean isApplyRisersLowerTex() {
        return cbApplyRisers.isSelected();
    }
}
