package org.deth;

import org.deth.wad.SpriteCache;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class ThingPaletteDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class ThingPaletteDialog extends JDialog {
    private final JTextField search = new JTextField();
    private final JComboBox<ThingDefs.Category> cbCat = new JComboBox<>();
    private final DefaultListModel<ThingDefs.ThingDef> model = new DefaultListModel<>();
    private final JList<ThingDefs.ThingDef> list = new JList<>(model);

    private final JLabel preview = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblName = new JLabel();
    private final JLabel lblId = new JLabel();
    private final JCheckBox cbEasy = new JCheckBox("Easy");
    private final JCheckBox cbNormal = new JCheckBox("Normal");
    private final JCheckBox cbHard = new JCheckBox("Hard");
    private final JCheckBox cbAmbush = new JCheckBox("Ambush");
    private final JCheckBox cbNotSP = new JCheckBox("Not SP");
    private final JCheckBox cbNotDM = new JCheckBox("Not DM");
    private final JCheckBox cbNotCoop = new JCheckBox("Not Coop");

    private Integer resultType = null;
    private Integer resultFlags = null;

    /**
     * Method ThingPaletteDialog.
     *
     * @param owner        parameter
     * @param initialType  parameter
     * @param initialFlags parameter
     * @return result
     */
    public ThingPaletteDialog(Window owner, int initialType, int initialFlags) {
        super(owner, "Thing Palette", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(720, 520));
        setLayout(new BorderLayout(10, 10));


        JPanel left = new JPanel(new BorderLayout(6, 6));
        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(new JLabel("Search:"), BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);
        JPanel catp = new JPanel(new BorderLayout(6, 6));
        catp.add(new JLabel("Category:"), BorderLayout.WEST);

        DefaultComboBoxModel<ThingDefs.Category> catModel = new DefaultComboBoxModel<>();
        cbCat.setModel(catModel);
        cbCat.addItem(null);
        for (ThingDefs.Category c : ThingDefs.Category.values()) cbCat.addItem(c);
        catp.add(cbCat, BorderLayout.CENTER);
        JPanel topp = new JPanel(new GridLayout(1, 2, 6, 6));
        topp.add(top);
        topp.add(catp);
        left.add(topp, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new Renderer());
        JScrollPane sp = new JScrollPane(list);
        left.add(sp, BorderLayout.CENTER);


        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel prevBox = new JPanel(new BorderLayout());
        prevBox.setBorder(new TitledBorder("Preview"));
        preview.setPreferredSize(new Dimension(160, 160));
        preview.setOpaque(true);
        preview.setBackground(Color.WHITE);
        prevBox.add(preview, BorderLayout.CENTER);
        right.add(prevBox);

        JPanel meta = new JPanel(new GridLayout(0, 1));
        meta.setBorder(new TitledBorder("Thing"));
        meta.add(lblName);
        meta.add(lblId);
        right.add(meta);

        JPanel flags = new JPanel(new GridLayout(0, 1));
        flags.setBorder(new TitledBorder("Spawn flags"));
        flags.add(cbEasy);
        flags.add(cbNormal);
        flags.add(cbHard);
        flags.add(cbAmbush);
        flags.add(cbNotSP);
        flags.add(cbNotDM);
        flags.add(cbNotCoop);
        right.add(flags);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        btns.add(cancel);
        btns.add(ok);
        right.add(btns);

        add(left, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);


        refreshList();


        if (ThingDefs.get(initialType) != null) {
            list.setSelectedValue(ThingDefs.get(initialType), true);
        } else if (!ThingDefs.ALL.isEmpty()) {
            list.setSelectedIndex(0);
        }
        setFlags(initialFlags);


        search.getDocument().addDocumentListener(new SimpleDoc(() -> refreshList()));
        cbCat.addActionListener(e -> refreshList());
        list.addListSelectionListener(e -> updatePreview());
        ok.addActionListener(e -> {
            ThingDefs.ThingDef d = list.getSelectedValue();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (d != null) {
                resultType = d.id();
                resultFlags = getFlags();
            }
            dispose();
        });
        cancel.addActionListener(e -> {
            resultType = null;
            resultFlags = null;
            dispose();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Method ThingPaletteDialog.
     *
     * @param owner parameter
     * @return result
     */
    public ThingPaletteDialog(java.awt.Window owner) {

        this(owner, -1, 0);
    }

    /**
     * Method refreshList.
     */
    private void refreshList() {
        String q = search.getText().trim().toLowerCase();
        ThingDefs.Category cat = (ThingDefs.Category) cbCat.getSelectedItem();
        model.clear();
        /**
         * Constructor for for.
         * @param ThingDefsALL parameter
         */
        for (ThingDefs.ThingDef d : ThingDefs.ALL) {
            if (cat != null && d.category() != cat) continue;
            if (!q.isEmpty()) {
                String s = (d.id() + " " + d.name()).toLowerCase();
                if (!s.contains(q)) continue;
            }
            model.addElement(d);
        }
        if (!model.isEmpty() && list.getSelectedIndex() < 0) list.setSelectedIndex(0);
        updatePreview();
    }

    /**
     * Method updatePreview.
     */
    private void updatePreview() {
        ThingDefs.ThingDef d = list.getSelectedValue();
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (d == null) {
            preview.setIcon(null);
            preview.setText("");
            lblName.setText("");
            lblId.setText("");
            return;
        }
        lblName.setText(d.name());
        lblId.setText("ID: " + d.id() + "   (" + d.category() + ")");


        BufferedImage im = null;
        try {
            im = SpriteCache.get().getThingSprite(d.id(), 0);
        } catch (Throwable ignored) {
        }
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (im != null) {
            int w = im.getWidth(), h = im.getHeight();
            int max = 160;
            double s = (double) max / Math.max(w, h);
            Image scaled = im.getScaledInstance((int) Math.round(w * s), (int) Math.round(h * s), Image.SCALE_SMOOTH);
            preview.setIcon(new ImageIcon(scaled));
            preview.setText("");
        } else {
            preview.setIcon(null);
            preview.setText(Integer.toString(d.id()));
        }
    }

    /**
     * Method getFlags.
     *
     * @return result
     */
    private int getFlags() {
        int f = 0;
        if (cbEasy.isSelected()) f |= 0x0001;
        if (cbNormal.isSelected()) f |= 0x0002;
        if (cbHard.isSelected()) f |= 0x0004;
        if (cbAmbush.isSelected()) f |= 0x0008;
        if (cbNotSP.isSelected()) f |= 0x0010;
        if (cbNotDM.isSelected()) f |= 0x0020;
        if (cbNotCoop.isSelected()) f |= 0x0040;
        return f;
    }

    /**
     * Method setFlags.
     *
     * @param flags parameter
     */
    private void setFlags(int flags) {
        cbEasy.setSelected((flags & 0x0001) != 0);
        cbNormal.setSelected((flags & 0x0002) != 0);
        cbHard.setSelected((flags & 0x0004) != 0);
        cbAmbush.setSelected((flags & 0x0008) != 0);
        cbNotSP.setSelected((flags & 0x0010) != 0);
        cbNotDM.setSelected((flags & 0x0020) != 0);
        cbNotCoop.setSelected((flags & 0x0040) != 0);
    }

    /**
     * Method getResultType.
     *
     * @return result
     */
    public Integer getResultType() {
        return resultType;
    }

    /**
     * Method getResultFlags.
     *
     * @return result
     */
    public Integer getResultFlags() {
        return resultFlags;
    }

    /**
     * Interface Change.
     * <p>Auto-generated documentation stub.</p>
     */
    private interface Change {
        void run();
    }

    /**
         * Class SimpleDoc.
         * <p>Auto-generated documentation stub.</p>
         */
        private record SimpleDoc(Change c) implements DocumentListener {
        /**
         * Constructor for SimpleDoc.
         *
         * @param c parameter
         */
        private SimpleDoc {
        }

            @Override
            public void insertUpdate(DocumentEvent e) {
                c.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                c.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                c.run();
            }
        }

    /**
     * Class Renderer.
     * <p>Auto-generated documentation stub.</p>
     */
    private static class Renderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ThingDefs.ThingDef d) {
                l.setText(d.id() + "  " + d.name());
                try {
                    BufferedImage im = SpriteCache.get().getThingSprite(d.id(), 0);
                    if (im != null) {
                        int w = im.getWidth(), h = im.getHeight();
                        int max = 28;
                        double s = (double) max / Math.max(w, h);
                        Image scaled = im.getScaledInstance((int) Math.round(w * s), (int) Math.round(h * s), Image.SCALE_SMOOTH);
                        l.setIcon(new ImageIcon(scaled));
                    } else {
                        l.setIcon(null);
                    }
                } catch (Throwable ignored) {
                    l.setIcon(null);
                }
            }
            return l;
        }
    }
}
