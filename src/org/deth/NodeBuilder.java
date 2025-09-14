package org.deth;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Class NodeBuilder.
 * <p>Auto-generated documentation stub.</p>
 */
public final class NodeBuilder {
    private static final String PREF_NODE_CMD = "nodebuilder.command";
    private static final String PREF_NODE_ARGS = "nodebuilder.args";

    /**
     * Method NodeBuilder.
     *
     * @return result
     */
    private NodeBuilder() {
    }

    /**
     * Method load.
     *
     * @return result
     */
    public static Config load() {
        Preferences p = Preferences.userNodeForPackage(NodeBuilder.class);
        String cmd = p.get(PREF_NODE_CMD, "");
        String args = p.get(PREF_NODE_ARGS, "%WAD%");
        return new Config(cmd, args);
    }

    /**
     * Method save.
     *
     * @param c parameter
     */
    public static void save(Config c) {
        Preferences p = Preferences.userNodeForPackage(NodeBuilder.class);
        p.put(PREF_NODE_CMD, c.command == null ? "" : c.command);
        p.put(PREF_NODE_ARGS, c.args == null ? "" : c.args);
    }

    /**
     * Method showPreferences.
     *
     * @param parent parameter
     */
    public static void showPreferences(Component parent) {
        Config c = load();
        PreferencesDialog dlg = new PreferencesDialog(SwingUtilities.getWindowAncestor(parent));
        dlg.setNodeBuilderCommand(c.command);
        dlg.setNodeBuilderArgs(c.args);
        dlg.setVisible(true);
        if (dlg.isOk()) {
            save(new Config(dlg.getNodeBuilderCommand(), dlg.getNodeBuilderArgs()));
        }
    }


    /**
     * Method runBuilder.
     *
     * @param wadFile parameter
     * @param parent  parameter
     * @return result
     * @throws IOException          on error
     * @throws InterruptedException on error
     */
    public static int runBuilder(File wadFile, Component parent) throws IOException, InterruptedException {
        Config c = load();
        if (c.command == null || c.command.isBlank()) {
            JOptionPane.showMessageDialog(parent, "Node builder is not configured. Set it in Tools → Preferences…", "Node Builder", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        String wadPath = wadFile.getAbsolutePath();
        String args = (c.args == null ? "" : c.args);
        args = args.replace("%WAD%", wadPath)
                .replace("%DIR%", wadFile.getParentFile() != null ? wadFile.getParentFile().getAbsolutePath() : "")
                .replace("%NAME%", wadFile.getName());
        List<String> cmd = new ArrayList<>();
        cmd.add(c.command);

        for (String part : splitArgs(args)) {
            if (!part.isEmpty()) cmd.add(part);
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(wadFile.getParentFile());
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        JTextArea outArea = new JTextArea(20, 80);
        outArea.setEditable(false);
        JScrollPane sp = new JScrollPane(outArea);
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Node Builder Output", Dialog.ModalityType.MODELESS);
        dialog.add(sp);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);


        try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                outArea.append(line + "\n");
                outArea.setCaretPosition(outArea.getDocument().getLength());
            }
        }

        int exit = proc.waitFor();
        outArea.append("\nExit code: " + exit + "\n");
        return exit;
    }

    /**
     * Method splitArgs.
     *
     * @param s parameter
     * @return result
     */
    private static List<String> splitArgs(String s) {
        List<String> out = new ArrayList<>();
        if (s == null) return out;
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        char quote = '"';
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            /**
             * Constructor for if.
             * @param ch parameter
             */
            if (ch == '"' || ch == '\'') {
                /**
                 * Constructor for if.
                 * @param inQuotes parameter
                 */
                if (!inQuotes) {
                    inQuotes = true;
                    quote = ch;
                }
                /**
                 * Method if.
                 * @param quotech parameter
                 * @return result
                 */
                else if (quote == ch) {
                    inQuotes = false;
                } else {
                    cur.append(ch);
                }
            } else if (Character.isWhitespace(ch) && !inQuotes) {
                if (cur.length() > 0) {
                    out.add(cur.toString());
                    cur.setLength(0);
                }
            } else {
                cur.append(ch);
            }
        }
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }

    /**
     * Class Config.
     * <p>Auto-generated documentation stub.</p>
     */
    public static class Config {
        public String command;
        public String args;

        /**
         * Method Config.
         *
         * @param c parameter
         * @param a parameter
         * @return result
         */
        public Config(String c, String a) {
            this.command = c;
            this.args = a;
        }
    }
}
