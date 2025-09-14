package org.deth;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;

/**
 * Class SimpleSyntaxPane.
 * <p>Auto-generated documentation stub.</p>
 */
public class SimpleSyntaxPane extends JTextPane {

    private final Scheme scheme;
    private final StyleContext sc = StyleContext.getDefaultStyleContext();
    private final AttributeSet attrBase;
    private final AttributeSet attrKW;
    private final AttributeSet attrSec;
    private final AttributeSet attrStr;
    private final AttributeSet attrCom;
    /**
     * Method SimpleSyntaxPane.
     *
     * @param scheme      parameter
     * @param initialText parameter
     * @return result
     */
    public SimpleSyntaxPane(Scheme scheme, String initialText) {
        this.scheme = scheme;
        attrBase = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, scheme.base);
        attrKW = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, scheme.kw);
        attrSec = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, scheme.sec);
        attrStr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, scheme.str);
        attrCom = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, scheme.com);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        setText(initialText == null ? "" : initialText);

        getDocument().addDocumentListener(new DocumentListener() {
            /**
             * Method insertUpdate.
             * @param e parameter
             */
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> highlight());
            }

            /**
             * Method removeUpdate.
             * @param e parameter
             */
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> highlight());
            }

            /**
             * Method changedUpdate.
             * @param e parameter
             */
            public void changedUpdate(DocumentEvent e) {
            }
        });

        highlight();
    }

    /**
     * Method isWordChar.
     *
     * @param ch parameter
     * @return result
     */
    private boolean isWordChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '-';
    }

    /**
     * Method highlight.
     */
    private void highlight() {
        StyledDocument doc = getStyledDocument();
        String text;
        try {
            text = doc.getText(0, doc.getLength());
        }
        /**
         * Constructor for catch.
         * @param e parameter
         */ catch (BadLocationException e) {
            return;
        }

        doc.setCharacterAttributes(0, text.length(), attrBase, true);

        int i = 0, n = text.length();
        /**
         * Constructor for while.
         * @param n parameter
         */
        while (i < n) {
            char c = text.charAt(i);

            /**
             * Constructor for if.
             * @param param1 parameter
             */
            if (c == ';' || c == '#') {
                int j = i;
                while (j < n && text.charAt(j) != '\n') j++;
                doc.setCharacterAttributes(i, j - i, attrCom, false);
                i = j;
                continue;
            }

            /**
             * Constructor for if.
             * @param param1 parameter
             */
            if (c == '[') {
                int j = i + 1;
                while (j < n && text.charAt(j) != ']') j++;
                /**
                 * Constructor for if.
                 * @param n parameter
                 */
                if (j < n) {
                    doc.setCharacterAttributes(i, (j + 1) - i, attrSec, false);
                    i = j + 1;
                    continue;
                }
            }

            /**
             * Constructor for if.
             * @param param1 parameter
             */
            if (c == '"') {
                int j = i + 1;
                /**
                 * Constructor for while.
                 * @param n parameter
                 */
                while (j < n) {
                    char cj = text.charAt(j);
                    if (cj == '"' && text.charAt(j - 1) != '\\') {
                        j++;
                        break;
                    }
                    j++;
                }
                doc.setCharacterAttributes(i, Math.min(j, n) - i, attrStr, false);
                i = Math.min(j, n);
                continue;
            }

            if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < n && isWordChar(text.charAt(j))) j++;
                String word = text.substring(i, j);
                String lower = word.toLowerCase();
                if (scheme.keywords.contains(lower) || scheme.sectionHeads.contains(word)) {
                    doc.setCharacterAttributes(i, j - i, scheme.keywords.contains(lower) ? attrKW : attrSec, false);
                }
                i = j;
                continue;
            }

            i++;
        }
    }

    /**
     * Method getAllText.
     *
     * @return result
     */
    public String getAllText() {
        return getText();
    }

    /**
     * Class Scheme.
     * <p>Auto-generated documentation stub.</p>
     */
    public static class Scheme {
        public final Set<String> keywords;
        public final Set<String> sectionHeads;
        public final Color kw = new Color(0, 0, 160);
        public final Color sec = new Color(128, 0, 128);
        public final Color str = new Color(0, 128, 0);
        public final Color com = new Color(120, 120, 120);
        public final Color base = Color.BLACK;

        /**
         * Method Scheme.
         *
         * @param keywords     parameter
         * @param sectionHeads parameter
         * @return result
         */
        public Scheme(Set<String> keywords, Set<String> sectionHeads) {
            this.keywords = keywords;
            this.sectionHeads = sectionHeads;
        }
    }
}
