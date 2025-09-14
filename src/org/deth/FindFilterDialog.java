package org.deth;

import javax.swing.*;
import java.awt.*;

/**
 * Class FindFilterDialog.
 * <p>Auto-generated documentation stub.</p>
 */
public class FindFilterDialog extends JDialog {
    private final JComboBox<String> entity = new JComboBox<>(new String[]{"Linedefs", "Things", "Sectors"});
    private final JTextField a = new JTextField();
    private final JTextField b = new JTextField();
    private final JCheckBox filter = new JCheckBox("Filter view to selection");
    /**
     * Method FindFilterDialog.
     *
     * @param owner parameter
     * @param cb    parameter
     * @return result
     */
    public FindFilterDialog(Window owner, Callback cb) {
        super(owner, "Find / Filter", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(8, 8));

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        center.add(new JLabel("Entity:"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        center.add(entity, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        center.add(new JLabel("A:"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        center.add(a, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        center.add(new JLabel("B:"), c);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        center.add(b, c);

        center.setBorder(BorderFactory.createTitledBorder("Criteria"));
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(filter);
        JButton selectAll = new JButton("Select All");
        JButton findNext = new JButton("Find Next");
        JButton close = new JButton("Close");
        south.add(selectAll);
        south.add(findNext);
        south.add(close);
        add(south, BorderLayout.SOUTH);

        entity.addActionListener(e -> updateLabels());
        updateLabels();

        selectAll.addActionListener(e -> {
            String ent = (String) entity.getSelectedItem();
            if ("Linedefs".equals(ent)) {
                Integer sp = parseInt(a.getText());
                Integer tg = parseInt(b.getText());
                cb.selectLinedefs(sp, tg);
            } else if ("Things".equals(ent)) {
                Integer type = parseInt(a.getText());
                cb.selectThings(type);
            } else {
                Integer lmin = parseInt(a.getText());
                Integer lmax = parseInt(b.getText());
                cb.selectSectors(lmin, lmax);
            }
            cb.setFilterMode(filter.isSelected());
        });

        findNext.addActionListener(e -> {

            selectAll.doClick();
        });

        close.addActionListener(e -> setVisible(false));

        setSize(480, 260);
        setLocationRelativeTo(owner);
    }

    /**
     * Method parseInt.
     *
     * @param s parameter
     * @return result
     */
    private static Integer parseInt(String s) {
        try {
            return s == null || s.isBlank() ? null : Integer.parseInt(s.trim());
        }
        /**
         * Constructor for catch.
         * @param ex parameter
         */ catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Method updateLabels.
     */
    private void updateLabels() {
        String ent = (String) entity.getSelectedItem();
        if ("Linedefs".equals(ent)) {
            a.setToolTipText("Special (optional)");
            b.setToolTipText("Tag (optional)");
        } else if ("Things".equals(ent)) {
            a.setToolTipText("Type (optional)");
            b.setToolTipText("");
        } else {
            a.setToolTipText("Light min (optional)");
            b.setToolTipText("Light max (optional)");
        }
    }

    /**
     * Interface Callback.
     * <p>Auto-generated documentation stub.</p>
     */
    public interface Callback {
        void selectLinedefs(Integer special, Integer tag);

        void selectThings(Integer type);

        void selectSectors(Integer lightMin, Integer lightMax);

        void setFilterMode(boolean filter);
    }
}
