package org.deth.wad;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class TexturePickerDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class TexturePickerDialog extends JDialog {
    private final Mode mode;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final JTextField tfFilter = new JTextField();
    private final JCheckBox cbUsedOnly = new JCheckBox("Used only");
    private final JComboBox<String> cbSort = new JComboBox<>(new String[]{"Sort: Name (A→Z)", "Sort: Usage (high→low)"});
    private final JLabel infoLabel = new JLabel(" ");
    private String result = null;
    private List<String> allNames = new ArrayList<>();
    private Map<String, Integer> usage = new HashMap<>();
    /**
     * Method TexturePickerDialog.
     *
     * @param owner       parameter
     * @param suggestions parameter
     * @return result
     */
    public TexturePickerDialog(Window owner, java.util.Collection<String> suggestions) {
        this(owner, suggestions, Mode.ANY);
    }

    /**
     * Method TexturePickerDialog.
     *
     * @param owner       parameter
     * @param suggestions parameter
     * @param mode        parameter
     * @return result
     */
    public TexturePickerDialog(Window owner, java.util.Collection<String> suggestions, Mode mode) {
        super(owner, "Texture Picker", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(8, 8));


        JPanel top = new JPanel(new BorderLayout(6, 6));
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.add(new JLabel("Filter:"), BorderLayout.WEST);
        left.add(tfFilter, BorderLayout.CENTER);
        top.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.add(cbUsedOnly);
        right.add(cbSort);
        JButton btnUseTyped = new JButton("Use typed");
        right.add(btnUseTyped);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);


        list.setVisibleRowCount(20);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            /**
             * Method getListCellRendererComponent.
             * @param l parameter
             * @param value parameter
             * @param index parameter
             * @param isSelected parameter
             * @param cellHasFocus parameter
             * @return result
             */
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                String name = String.valueOf(value);
                int u = usage.getOrDefault(name, 0);

                lbl.setText(u > 0 ? (name + "   (used " + u + ")") : name);
                lbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, lbl.getFont().getSize()));
                return lbl;
            }
        });
        add(new JScrollPane(list), BorderLayout.CENTER);


        JPanel bottom = new JPanel(new BorderLayout(6, 6));
        infoLabel.setForeground(new Color(80, 80, 80));
        bottom.add(infoLabel, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("OK");
        buttons.add(cancel);
        buttons.add(ok);
        bottom.add(buttons, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        this.mode = mode == null ? Mode.ANY : mode;


        seedFromCacheOrDefaults(suggestions);


        tfFilter.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            /**
             * Method apply.
             */
            private void apply() {
                applyFilterAndSort();
            }

            /**
             * Method insertUpdate.
             * @param e parameter
             */
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }

            /**
             * Method removeUpdate.
             * @param e parameter
             */
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }

            /**
             * Method changedUpdate.
             * @param e parameter
             */
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                apply();
            }
        });
        cbUsedOnly.addActionListener(e -> applyFilterAndSort());
        cbSort.addActionListener(e -> applyFilterAndSort());


        ok.addActionListener(e -> {
            result = list.getSelectedValue();
            setVisible(false);
        });
        cancel.addActionListener(e -> {
            result = null;
            setVisible(false);
        });
        btnUseTyped.addActionListener(e -> {
            String typed = tfFilter.getText().trim();
            if (typed.isEmpty()) {
                result = null;
                setVisible(false);
                return;
            }
            String key = typed.toUpperCase(java.util.Locale.ROOT);
            java.util.List<String> allowed;
            TextureCache tc2 = TextureCache.get();
            /**
             * Constructor for switch.
             * @param mode parameter
             */
            switch (mode) {
                case FLATS -> allowed = tc2.isIndexed() ? tc2.getAllFlatNames() : java.util.List.of();
                case WALLS -> allowed = tc2.isIndexed() ? tc2.getAllWallTextureNames() : java.util.List.of();
                default -> allowed = tc2.isIndexed() ? tc2.getAllTextureNames() : java.util.List.of();
            }
            if (!allowed.contains(key)) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "The typed name \"" + typed + "\" is not a valid " + (mode == Mode.FLATS ? "flat" : mode == Mode.WALLS ? "wall texture" : "texture") + ".",
                        "Invalid name", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            result = key;
            setVisible(false);
        });


        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    result = list.getSelectedValue();
                    setVisible(false);
                }
            }
        });

        setSize(520, 560);
        setLocationRelativeTo(owner);
    }

    /**
     * Method getResult.
     *
     * @return result
     */
    public String getResult() {
        return result;
    }

    /**
     * Method seedFromCacheOrDefaults.
     *
     * @param suggestions parameter
     */
    private void seedFromCacheOrDefaults(java.util.Collection<String> suggestions) {
        TextureCache tc = TextureCache.get();
        usage = tc.getUsageMap();

        java.util.List<String> names = java.util.List.of();
        if (tc.isIndexed()) {
            /**
             * Constructor for switch.
             * @param mode parameter
             */
            switch (mode) {
                case FLATS -> {
                    names = tc.getAllFlatNames();
                    infoLabel.setText(names.size() + " flats indexed");
                    usage = TextureCache.getLastUsage().flats;
                }
                case WALLS -> {
                    names = tc.getAllWallTextureNames();
                    infoLabel.setText(names.size() + " wall textures indexed");
                    usage = TextureCache.getLastUsage().textures;
                }
                default -> {
                    names = tc.getAllTextureNames();
                    infoLabel.setText(names.size() + " textures indexed");
                }
            }
        }
        if (names.isEmpty()) {

            if (suggestions != null && !suggestions.isEmpty()) {
                names = new java.util.ArrayList<>(suggestions);
            } else {
                names = java.util.List.of(
                        "STARTAN3", "STARGR1", "BIGBRIK1", "BRICK1", "BRONZE1", "DOOR3", "DOORTRAK",
                        "SUPPORT2", "METAL", "GSTONE1", "GSTONE2", "FLOOR0_1", "FLOOR0_2", "CEIL1_1",
                        "BROWN1", "BROWN96", "STONE2", "STONE3", "WOOD1", "WOOD5"
                );
            }
            infoLabel.setText("Texture cache empty; showing defaults");
        }

        allNames = names.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty() && !"-".equals(s))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        applyFilterAndSort();
    }

    /**
     * Method applyFilterAndSort.
     */
    private void applyFilterAndSort() {
        String q = tfFilter.getText().trim().toUpperCase(Locale.ROOT);
        boolean usedOnly = cbUsedOnly.isSelected();
        boolean sortUsage = cbSort.getSelectedIndex() == 1;


        List<String> filtered = allNames.stream()
                .filter(n -> q.isEmpty() || n.contains(q))
                .filter(n -> !usedOnly || usage.getOrDefault(n, 0) > 0)
                .collect(Collectors.toList());


        /**
         * Constructor for if.
         * @param sortUsage parameter
         */
        if (sortUsage) {
            filtered.sort((a, b) -> {
                int ua = usage.getOrDefault(a, 0);
                int ub = usage.getOrDefault(b, 0);
                if (ua != ub) return Integer.compare(ub, ua);
                return a.compareTo(b);
            });
        } else {
            Collections.sort(filtered);
        }


        model.clear();
        for (String s : filtered) model.addElement(s);

        infoLabel.setText((model.getSize()) + " shown"
                + (usedOnly ? " (used only)" : "")
                + (q.isEmpty() ? "" : (" — filter: \"" + q + "\"")));
        if (model.getSize() > 0) list.setSelectedIndex(0);
    }

    /**
     * Enum Mode.
     * <p>Auto-generated documentation stub.</p>
     */
    public enum Mode {ANY, WALLS, FLATS}
}
