package org.deth;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Class NodeBuilderManager.
 * <p>Auto-generated documentation stub.</p>
 */
public class NodeBuilderManager {
    private final Prefs prefs;
    private final List<Preset> presets = new ArrayList<>();
    private int activeIndex = -1;
    /**
     * Method NodeBuilderManager.
     *
     * @param prefs parameter
     * @return result
     */
    public NodeBuilderManager(Prefs prefs) {
        this.prefs = prefs;
        loadFromPrefs();
        if (presets.isEmpty()) {
            presets.add(new Preset("ZDBSP (fast)", "zdbsp", "-o {out} {in}"));
            activeIndex = 0;
            saveToPrefs();
        }
    }

    /**
     * Method quoteForParse.
     *
     * @param s parameter
     * @return result
     */
    private static String quoteForParse(String s) {
        if (s == null) return "\"\"";

        String esc = s.replace("\"", "\\\"");
        return "\"" + esc + "\"";
    }

    /**
     * Method stripSurroundingQuotes.
     *
     * @param s parameter
     * @return result
     */
    private static String stripSurroundingQuotes(String s) {
        if (s == null || s.length() < 2) return s;
        char first = s.charAt(0), last = s.charAt(s.length() - 1);
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Method tokenizeArgs.
     *
     * @param s parameter
     * @return result
     */
    private static List<String> tokenizeArgs(String s) {
        List<String> out = new ArrayList<>();
        if (s == null || s.isEmpty()) return out;
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        char quoteChar = 0;
        boolean escape = false;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            /**
             * Constructor for if.
             * @param escape parameter
             */
            if (escape) {
                cur.append(ch);
                escape = false;
                continue;
            }
            /**
             * Constructor for if.
             * @param param1 parameter
             */
            if (ch == '\\') {
                escape = true;
                continue;
            }

            /**
             * Constructor for if.
             * @param inQuote parameter
             */
            if (inQuote) {
                /**
                 * Constructor for if.
                 * @param quoteChar parameter
                 */
                if (ch == quoteChar) {
                    inQuote = false;
                    quoteChar = 0;
                } else {
                    cur.append(ch);
                }
                continue;
            }

            /**
             * Constructor for if.
             * @param param1 parameter
             */
            if (ch == '"' || ch == '\'') {
                inQuote = true;
                quoteChar = ch;
                continue;
            }
            if (Character.isWhitespace(ch)) {
                if (cur.length() > 0) {
                    out.add(cur.toString());
                    cur.setLength(0);
                }
                continue;
            }
            cur.append(ch);
        }
        if (escape) cur.append('\\');
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }

    /**
     * Method loadFromPrefs.
     */
    private void loadFromPrefs() {
        presets.clear();
        int n = prefs.getInt("nb.count", 0);
        /**
         * Constructor for for.
         * @param i0ini parameter
         */
        for (int i = 0; i < n; i++) {
            String name = prefs.get("nb." + i + ".name", "Preset " + i);
            String exe = prefs.get("nb." + i + ".exe", "zdbsp");
            String args = prefs.get("nb." + i + ".args", "-o {out} {in}");
            presets.add(new Preset(name, exe, args));
        }
        activeIndex = prefs.getInt("nb.active", presets.isEmpty() ? -1 : 0);
        if (activeIndex < 0 || activeIndex >= presets.size()) activeIndex = presets.isEmpty() ? -1 : 0;
    }

    /**
     * Method saveToPrefs.
     */
    private void saveToPrefs() {
        prefs.setInt("nb.count", presets.size());
        for (int i = 0; i < presets.size(); i++) {
            Prefs p = prefs;
            p.set("nb." + i + ".name", presets.get(i).name);
            p.set("nb." + i + ".exe", presets.get(i).exe);
            p.set("nb." + i + ".args", presets.get(i).args);
        }
        prefs.setInt("nb.active", activeIndex);
        prefs.save();
    }

    /**
     * Method showManagerDialog.
     *
     * @param parent parameter
     */
    public void showManagerDialog(Component parent) {
        DefaultListModel<Preset> model = new DefaultListModel<>();
        for (Preset pr : presets) model.addElement(pr);
        JList<Preset> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (activeIndex >= 0 && activeIndex < model.size()) list.setSelectedIndex(activeIndex);

        JTextField tfName = new JTextField(20);
        JTextField tfExe = new JTextField(20);
        JTextField tfArgs = new JTextField(30);

        JButton add = new JButton("Add");
        JButton del = new JButton("Delete");
        JButton set = new JButton("Set Active");
        JButton save = new JButton("Save All");

        JPanel edit = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        edit.add(new JLabel("Name:"), c);
        c.gridx = 1;
        edit.add(tfName, c);
        c.gridx = 0;
        c.gridy = 1;
        edit.add(new JLabel("Executable:"), c);
        c.gridx = 1;
        edit.add(tfExe, c);
        c.gridx = 0;
        c.gridy = 2;
        edit.add(new JLabel("Args ({in} {out} {map}):"), c);
        c.gridx = 1;
        edit.add(tfArgs, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(add);
        buttons.add(del);
        buttons.add(set);
        buttons.add(save);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(edit, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.add(new JScrollPane(list), BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(parent), "Node Builders", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setContentPane(main);
        dlg.setSize(700, 320);
        dlg.setLocationRelativeTo(parent);

        list.addListSelectionListener(ev -> {
            int i = list.getSelectedIndex();
            /**
             * Constructor for if.
             * @param i0 parameter
             */
            if (i >= 0) {
                Preset pr = model.get(i);
                tfName.setText(pr.name);
                tfExe.setText(pr.exe);
                tfArgs.setText(pr.args);
            }
        });

        add.addActionListener(e -> {
            Preset pr = new Preset("New Preset", "zdbsp", "-o {out} {in}");
            model.addElement(pr);
            presets.add(pr);
            list.setSelectedIndex(model.size() - 1);
        });
        del.addActionListener(e -> {
            int i = list.getSelectedIndex();
            if (i < 0) return;
            presets.remove(i);
            model.remove(i);
            if (activeIndex == i) activeIndex = -1;
            if (activeIndex > i) activeIndex--;
        });
        set.addActionListener(e -> {
            int i = list.getSelectedIndex();
            if (i < 0) return;
            activeIndex = i;
            JOptionPane.showMessageDialog(dlg, "Active preset set to: " + presets.get(i).name);
        });
        save.addActionListener(e -> {
            int i = list.getSelectedIndex();
            if (i >= 0) {
                Preset pr = presets.get(i);
                pr.name = tfName.getText().trim();
                pr.exe = tfExe.getText().trim();
                pr.args = tfArgs.getText().trim();
            }
            saveToPrefs();
            JOptionPane.showMessageDialog(dlg, "Saved.");
        });
        dlg.setVisible(true);
    }

    /**
     * Method runActive.
     *
     * @param inWad   parameter
     * @param mapName parameter
     * @param parent  parameter
     * @return result
     */
    public boolean runActive(File inWad, String mapName, Component parent) {
        if (activeIndex < 0 || activeIndex >= presets.size()) {
            JOptionPane.showMessageDialog(parent, "No active node builder preset. Use Tools → Node Builders…");
            return false;
        }
        Preset pr = presets.get(activeIndex);
        try {

            String name = inWad.getName();
            String outName = name.toLowerCase().endsWith(".wad")
                    ? name.substring(0, name.length() - 4) + "_nodes.wad"
                    : name + "_nodes.wad";
            File out = new File(inWad.getParentFile(), outName);


            String argsTemplate = pr.args;
            argsTemplate = argsTemplate.replace("{in}", quoteForParse(inWad.getAbsolutePath()));
            argsTemplate = argsTemplate.replace("{out}", quoteForParse(out.getAbsolutePath()));
            argsTemplate = argsTemplate.replace("{map}", quoteForParse(mapName != null ? mapName : ""));

            List<String> argv = new ArrayList<>();
            argv.add(stripSurroundingQuotes(pr.exe.trim()));
            argv.addAll(tokenizeArgs(argsTemplate));

            ProcessBuilder pb = new ProcessBuilder(argv);
            pb.directory(inWad.getParentFile());
            pb.redirectErrorStream(true);

            Process proc = pb.start();
            StringBuilder log = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) log.append(line).append('\n');
            }
            int code = proc.waitFor();
            /**
             * Constructor for if.
             * @param 0 parameter
             */
            if (code != 0) {
                JOptionPane.showMessageDialog(parent,
                        "Node builder exited with code " + code + "\n\n" + log);
            } else {
                JOptionPane.showMessageDialog(parent, "Nodes built OK.\nOutput: " + out.getAbsolutePath());
            }
            return code == 0;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Failed to run node builder: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Class Preset.
     * <p>Auto-generated documentation stub.</p>
     */
    public static class Preset {
        public String name;
        public String exe;
        public String args;

        /**
         * Method Preset.
         *
         * @param name parameter
         * @param exe  parameter
         * @param args parameter
         * @return result
         */
        public Preset(String name, String exe, String args) {
            this.name = name;
            this.exe = exe;
            this.args = args;
        }

        /**
         * Method toString.
         *
         * @return result
         */
        public String toString() {
            return name + " — " + exe;
        }
    }
}
