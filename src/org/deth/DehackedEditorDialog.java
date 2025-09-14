package org.deth;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Class DehackedEditorDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class DehackedEditorDialog extends JDialog {
    private final SimpleSyntaxPane editor;
    private File targetPWAD;

    /**
     * Method DehackedEditorDialog.
     *
     * @param owner       parameter
     * @param defaultPWAD parameter
     * @return result
     */
    public DehackedEditorDialog(Frame owner, File defaultPWAD) {
        super(owner, "DEHACKED Editor", true);
        this.targetPWAD = defaultPWAD;

        var scheme = new SimpleSyntaxPane.Scheme(
                Set.of("thing", "frame", "pointer", "ammo", "weapon", "sound", "text", "id", "health", "speed", "damage", "flags", "mbf21", "codepointer"),
                Set.of("Thing", "Frame", "Pointer", "Ammo", "Weapon", "Sound", "Text")
        );

        String template = String.join("\n",
                "Patch File for DeHackEd v3.0",
                "# Created by DETH",
                "",
                "Thing 1 (Player)",
                "Speed = 1",
                "",
                "Frame 12",
                "Sprite number = 123",
                ""
        );

        String existing = null;
        if (targetPWAD != null && targetPWAD.exists()) {
            try {
                byte[] b = WadLumpRW.readLump(targetPWAD, "DEHACKED");
                if (b != null) existing = new String(b, StandardCharsets.US_ASCII);
            } catch (Exception ignore) {
            }
        }
        editor = new SimpleSyntaxPane(scheme, existing != null ? existing : template);

        JButton btnImport = new JButton("Import DeHacked file");
        JButton btnExport = new JButton("Export to file");
        JButton btnSave = new JButton("Save into PWAD");
        JButton btnClose = new JButton("Close");

        btnImport.addActionListener(e -> importFromFile());
        btnExport.addActionListener(e -> exportToFile());
        btnSave.addActionListener(e -> saveToPWAD());
        btnClose.addActionListener(e -> dispose());

        JScrollPane sp = new JScrollPane(editor);
        sp.setPreferredSize(new Dimension(800, 560));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnImport);
        south.add(btnExport);
        south.add(btnSave);
        south.add(btnClose);

        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Method importFromFile.
     */
    private void importFromFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try {
            String txt = new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            editor.setText(txt);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to read: " + ex.getMessage(), "Import", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method exportToFile.
     */
    private void exportToFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (OutputStream out = new FileOutputStream(f)) {
            out.write(editor.getAllText().getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "Exported.", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to write: " + ex.getMessage(), "Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method saveToPWAD.
     */
    private void saveToPWAD() {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (targetPWAD == null) {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                targetPWAD = fc.getSelectedFile();
            } else return;
        }
        try {
            WadLumpRW.writeOrReplaceLump(targetPWAD, "DEHACKED", editor.getAllText().getBytes(StandardCharsets.US_ASCII));
            JOptionPane.showMessageDialog(this, "DEHACKED saved into " + targetPWAD.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
