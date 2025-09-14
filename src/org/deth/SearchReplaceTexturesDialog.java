package org.deth;

import org.deth.wad.TexturePickerDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Class SearchReplaceTexturesDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class SearchReplaceTexturesDialog extends JDialog {
    private final JTextField tfFromWall = new JTextField();
    private final JTextField tfToWall = new JTextField();
    private final JCheckBox cbUp = new JCheckBox("Upper", true);
    private final JCheckBox cbMid = new JCheckBox("Middle", true);
    private final JCheckBox cbLow = new JCheckBox("Lower", true);

    private final JTextField tfFromFlat = new JTextField();
    private final JTextField tfToFlat = new JTextField();
    private final JCheckBox cbFloor = new JCheckBox("Floor", true);
    private final JCheckBox cbCeil = new JCheckBox("Ceiling", true);

    private final JRadioButton rbSel = new JRadioButton("Selection only", true);
    private final JRadioButton rbAll = new JRadioButton("Whole map");

    private boolean accepted = false;

    /**
     * Method SearchReplaceTexturesDialog.
     *
     * @param owner parameter
     * @return result
     */
    public SearchReplaceTexturesDialog(Window owner) {
        super(owner, "Search & Replace Textures", ModalityType.APPLICATION_MODAL);
        setSize(560, 380);
        setLocationRelativeTo(owner);

        JPanel wall = new JPanel(new GridBagLayout());
        wall.setBorder(BorderFactory.createTitledBorder("Wall Textures"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        JButton pickFromWall = new JButton("…");
        JButton pickToWall = new JButton("…");

        c.gridx = 0;
        c.gridy = 0;
        wall.add(new JLabel("From:"), c);
        c.gridx = 1;
        c.weightx = 1;
        wall.add(tfFromWall, c);
        c.gridx = 2;
        c.weightx = 0;
        wall.add(pickFromWall, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        wall.add(new JLabel("To:"), c);
        c.gridx = 1;
        c.weightx = 1;
        wall.add(tfToWall, c);
        c.gridx = 2;
        c.weightx = 0;
        wall.add(pickToWall, c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        JPanel wflags = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        wflags.add(cbUp);
        wflags.add(cbMid);
        wflags.add(cbLow);
        wall.add(wflags, c);

        JPanel flat = new JPanel(new GridBagLayout());
        flat.setBorder(BorderFactory.createTitledBorder("Sector Flats"));
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(4, 4, 4, 4);
        f.fill = GridBagConstraints.HORIZONTAL;
        JButton pickFromFlat = new JButton("…");
        JButton pickToFlat = new JButton("…");
        f.gridx = 0;
        f.gridy = 0;
        flat.add(new JLabel("From:"), f);
        f.gridx = 1;
        f.weightx = 1;
        flat.add(tfFromFlat, f);
        f.gridx = 2;
        f.weightx = 0;
        flat.add(pickFromFlat, f);
        f.gridx = 0;
        f.gridy = 1;
        f.weightx = 0;
        flat.add(new JLabel("To:"), f);
        f.gridx = 1;
        f.weightx = 1;
        flat.add(tfToFlat, f);
        f.gridx = 2;
        f.weightx = 0;
        flat.add(pickToFlat, f);
        f.gridx = 1;
        f.gridy = 2;
        f.gridwidth = 2;
        JPanel fflags = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fflags.add(cbFloor);
        fflags.add(cbCeil);
        flat.add(fflags, f);

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbSel);
        grp.add(rbAll);
        JPanel scope = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        scope.add(new JLabel("Scope:"));
        scope.add(rbSel);
        scope.add(rbAll);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(wall);
        center.add(Box.createVerticalStrut(8));
        center.add(flat);
        center.add(Box.createVerticalStrut(8));
        center.add(scope);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Replace");
        south.add(cancel);
        south.add(ok);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(center, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        setContentPane(main);


        pickFromWall.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(owner, null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfFromWall.setText(r);
        });
        pickToWall.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(owner, null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfToWall.setText(r);
        });
        pickFromFlat.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(owner, null, TexturePickerDialog.Mode.FLATS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfFromFlat.setText(r);
        });
        pickToFlat.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(owner, null, TexturePickerDialog.Mode.FLATS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            if (r != null) tfToFlat.setText(r);
        });

        cancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });
        ok.addActionListener(e -> {
            accepted = true;
            setVisible(false);
        });
    }

    /**
     * Method textOrNull.
     *
     * @param s parameter
     * @return result
     */
    private static String textOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
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
        return rbSel.isSelected();
    }

    /**
     * Method getFromWall.
     *
     * @return result
     */
    public String getFromWall() {
        return textOrNull(tfFromWall.getText());
    }

    /**
     * Method getToWall.
     *
     * @return result
     */
    public String getToWall() {
        return textOrNull(tfToWall.getText());
    }

    /**
     * Method repUpper.
     *
     * @return result
     */
    public boolean repUpper() {
        return cbUp.isSelected();
    }

    /**
     * Method repMiddle.
     *
     * @return result
     */
    public boolean repMiddle() {
        return cbMid.isSelected();
    }

    /**
     * Method repLower.
     *
     * @return result
     */
    public boolean repLower() {
        return cbLow.isSelected();
    }

    /**
     * Method getFromFlat.
     *
     * @return result
     */
    public String getFromFlat() {
        return textOrNull(tfFromFlat.getText());
    }

    /**
     * Method getToFlat.
     *
     * @return result
     */
    public String getToFlat() {
        return textOrNull(tfToFlat.getText());
    }

    /**
     * Method repFloor.
     *
     * @return result
     */
    public boolean repFloor() {
        return cbFloor.isSelected();
    }

    /**
     * Method repCeil.
     *
     * @return result
     */
    public boolean repCeil() {
        return cbCeil.isSelected();
    }
}
