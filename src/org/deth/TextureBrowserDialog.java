package org.deth;

import org.deth.wad.MapData;
import org.deth.wad.TextureCatalog;
import org.deth.wad.TextureCatalog.Entry;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.Locale;

/**
 * Class TextureBrowserDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class TextureBrowserDialog extends JDialog {
    private final JTextField filter = new JTextField();
    private final DefaultListModel<Entry> modelTex = new DefaultListModel<>();
    private final DefaultListModel<Entry> modelFlat = new DefaultListModel<>();
    private final JList<Entry> listTex = new JList<>(modelTex);
    private final JList<Entry> listFlat = new JList<>(modelFlat);

    /**
     * Method TextureBrowserDialog.
     *
     * @param owner   parameter
     * @param wadFile parameter
     * @param map     parameter
     * @param panel   parameter
     * @return result
     */
    public TextureBrowserDialog(Window owner, File wadFile, MapData map, MapPanel panel) {
        super(owner, "Texture Browser", ModalityType.APPLICATION_MODAL);
        setSize(720, 520);
        setLocationRelativeTo(owner);

        JPanel north = new JPanel(new BorderLayout(6, 6));
        north.add(new JLabel("Filter:"), BorderLayout.WEST);
        north.add(filter, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Wall Textures", new JScrollPane(listTex));
        tabs.addTab("Flats", new JScrollPane(listFlat));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyUpper = new JButton("Apply → Upper");
        JButton applyMiddle = new JButton("Apply → Middle");
        JButton applyLower = new JButton("Apply → Lower");
        JButton applyFloor = new JButton("Apply → Floor");
        JButton applyCeil = new JButton("Apply → Ceiling");
        buttons.add(applyUpper);
        buttons.add(applyMiddle);
        buttons.add(applyLower);
        buttons.add(new JSeparator(SwingConstants.VERTICAL));
        buttons.add(applyFloor);
        buttons.add(applyCeil);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(north, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);
        setContentPane(main);


        try (var cat = new TextureCatalog(wadFile)) {
            modelTex.clear();
            modelFlat.clear();
            cat.computeUsage(map);
            java.util.List<Entry> tex = cat.listTextures();
            tex.sort(Comparator.comparingInt((Entry e) -> -e.uses).thenComparing(e -> e.name));
            java.util.List<Entry> flats = cat.listFlats();
            flats.sort(Comparator.comparingInt((Entry e) -> -e.uses).thenComparing(e -> e.name));
            for (Entry e : tex) modelTex.addElement(e);
            for (Entry e : flats) modelFlat.addElement(e);

            var filterApply = new javax.swing.event.DocumentListener() {
                /**
                 * Method run.
                 */
                private void run() {
                    String q = filter.getText().trim().toUpperCase(Locale.ROOT);
                    modelTex.clear();
                    modelFlat.clear();
                    for (Entry e : tex) if (q.isEmpty() || e.name.contains(q)) modelTex.addElement(e);
                    for (Entry e : flats) if (q.isEmpty() || e.name.contains(q)) modelFlat.addElement(e);
                }

                /**
                 * Method insertUpdate.
                 * @param e parameter
                 */
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    run();
                }

                /**
                 * Method removeUpdate.
                 * @param e parameter
                 */
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    run();
                }

                /**
                 * Method changedUpdate.
                 * @param e parameter
                 */
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    run();
                }
            };
            filter.getDocument().addDocumentListener(filterApply);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load textures: " + ex.getMessage(), "Texture Browser", JOptionPane.ERROR_MESSAGE);
        }


        applyUpper.addActionListener(e -> {
            var sel = selectedTexture();
            if (sel == null) return;
            panel.updateSelectedSidedefs(sel.name, null, null, null, null);
        });
        applyMiddle.addActionListener(e -> {
            var sel = selectedTexture();
            if (sel == null) return;
            panel.updateSelectedSidedefs(null, sel.name, null, null, null);
        });
        applyLower.addActionListener(e -> {
            var sel = selectedTexture();
            if (sel == null) return;
            panel.updateSelectedSidedefs(null, null, sel.name, null, null);
        });
        applyFloor.addActionListener(e -> {
            var sel = selectedFlat();
            if (sel == null) return;
            panel.updateSelectedSectors(null, null, sel.name, null, null, null, null);
        });
        applyCeil.addActionListener(e -> {
            var sel = selectedFlat();
            if (sel == null) return;
            panel.updateSelectedSectors(null, null, null, sel.name, null, null, null);
        });

    }

    /**
     * Method selectedTexture.
     *
     * @return result
     */
    private Entry selectedTexture() {
        return listTex.getSelectedValue();
    }

    /**
     * Method selectedFlat.
     *
     * @return result
     */
    private Entry selectedFlat() {
        return listFlat.getSelectedValue();
    }
}
