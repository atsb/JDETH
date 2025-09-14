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
 * Class UmapinfoEditorDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class UmapinfoEditorDialog extends JDialog {
    private final SimpleSyntaxPane editor;
    private File targetPWAD;

    /**
     * Method UmapinfoEditorDialog.
     *
     * @param owner       parameter
     * @param defaultPWAD parameter
     * @param mapName     parameter
     * @return result
     */
    public UmapinfoEditorDialog(Frame owner, File defaultPWAD, String mapName) {
        super(owner, "UMAPINFO Editor", true);
        this.targetPWAD = defaultPWAD;

        var scheme = new SimpleSyntaxPane.Scheme(
                Set.of(
                        "map", "episode", "cluster", "skill", "levelname", "label", "author", "intertext", "intertextsecret",
                        "music", "skytexture", "next", "nextsecret", "endgame", "par", "partime", "levelpic", "levelselect",
                        "exitpic", "nointermission", "lightning", "allowjump", "allowfreelook", "nosoundclipping"
                ),
                Set.of("[episode]", "[map]", "[cluster]")
        );

        String template = "# UMAPINFO template\n" +
                "[episode]\n" +
                "label = \"E1\"\n" +
                "name  = \"Knee-Deep\"\n" +
                "start = \"E1M1\"\n\n" +
                "[map " + (mapName == null ? "MAP01" : mapName) + "]\n" +
                "levelname   = \"New Level\"\n" +
                "next        = \"" + (mapName != null && mapName.startsWith("E") ? "E1M2" : "MAP02") + "\"\n" +
                "skytexture  = \"SKY1\"\n" +
                "music       = \"D_RUNNIN\"\n" +
                "partime     = 30\n";

        String existing = null;
        if (targetPWAD != null && targetPWAD.exists()) {
            try {
                byte[] b = WadLumpRW.readLump(targetPWAD, "UMAPINFO");
                if (b != null) existing = new String(b, StandardCharsets.US_ASCII);
            } catch (Exception ignore) {
            }
        }
        editor = new SimpleSyntaxPane(scheme, existing != null ? existing : template);

        JButton btnImport = new JButton("Import Umapinfo file");
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
            WadLumpRW.writeOrReplaceLump(targetPWAD, "UMAPINFO", editor.getAllText().getBytes(StandardCharsets.US_ASCII));
            JOptionPane.showMessageDialog(this, "UMAPINFO saved into " + targetPWAD.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
