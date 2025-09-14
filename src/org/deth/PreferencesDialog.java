package org.deth;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Class PreferencesDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class PreferencesDialog extends JDialog {
    private final JTextField tfNodeCmd = new JTextField();
    private final JTextField tfNodeArgs = new JTextField();
    private boolean ok = false;

    /**
     * Method PreferencesDialog.
     *
     * @param owner parameter
     * @return result
     */
    public PreferencesDialog(Window owner) {
        super(owner, "Preferences", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Node builder command:"), c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        form.add(tfNodeCmd, c);
        JButton browse = new JButton("Browse…");
        browse.addActionListener(e -> browseCmd());
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        form.add(browse, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Arguments:"), c);
        tfNodeArgs.setToolTipText("Use %WAD% for the wad path, %DIR% for its folder, %NAME% for file name");
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        form.add(tfNodeArgs, c);

        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        btns.add(cancelBtn);
        btns.add(okBtn);
        add(btns, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> {
            ok = true;
            setVisible(false);
        });
        cancelBtn.addActionListener(e -> {
            ok = false;
            setVisible(false);
        });

        setSize(640, 180);
        setLocationRelativeTo(owner);
    }

    /**
     * Method browseCmd.
     */
    private void browseCmd() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        /**
         * Constructor for if.
         * @param JFileChooserAPPROVE_OPTION parameter
         */
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            tfNodeCmd.setText(f.getAbsolutePath());
        }
    }

    /**
     * Method getNodeBuilderCommand.
     *
     * @return result
     */
    public String getNodeBuilderCommand() {
        return tfNodeCmd.getText().trim();
    }

    /**
     * Method setNodeBuilderCommand.
     *
     * @param s parameter
     */
    public void setNodeBuilderCommand(String s) {
        tfNodeCmd.setText(s == null ? "" : s);
    }

    /**
     * Method getNodeBuilderArgs.
     *
     * @return result
     */
    public String getNodeBuilderArgs() {
        return tfNodeArgs.getText().trim();
    }

    /**
     * Method setNodeBuilderArgs.
     *
     * @param s parameter
     */
    public void setNodeBuilderArgs(String s) {
        tfNodeArgs.setText(s == null ? "" : s);
    }

    /**
     * Method isOk.
     *
     * @return result
     */
    public boolean isOk() {
        return ok;
    }
}
