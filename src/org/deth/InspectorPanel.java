package org.deth;

import org.deth.wad.MapData;
import org.deth.wad.SpriteCache;
import org.deth.wad.TextureCache;
import org.deth.wad.TexturePickerDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class InspectorPanel.
 * <p>Auto-generated documentation stub.</p>
 */
public class InspectorPanel extends JPanel implements MapPanel.SelectionListener {
    private static final SpecEntry[] LINEDEF_SPECIALS = new SpecEntry[]{
            new SpecEntry(0, "None"),


            new SpecEntry(1, "DR Door"),
            new SpecEntry(2, "W1 Door: Stay Open"),
            new SpecEntry(3, "W1 Door: Close"),
            new SpecEntry(4, "W1 Door: Open/Wait/Close"),
            new SpecEntry(5, "W1 Floor → Lowest Adjacent Ceiling"),
            new SpecEntry(6, "W1 Crusher: Start (fast dmg)"),
            new SpecEntry(7, "S1 Stairs: Build 8 up"),
            new SpecEntry(8, "W1 Stairs: Build 8 up"),
            new SpecEntry(9, "S1 Floor Donut"),
            new SpecEntry(10, "W1 Lift (also monsters)"),
            new SpecEntry(11, "S1 Exit (normal)"),
            new SpecEntry(12, "W1 Light → Highest Adjacent"),
            new SpecEntry(13, "W1 Light → 255"),
            new SpecEntry(14, "S1 Floor ↑ 32 + Change Tex"),
            new SpecEntry(15, "S1 Floor ↑ 24 + Change Tex"),
            new SpecEntry(16, "W1 Door: Close then Open"),
            new SpecEntry(17, "W1 Light: Blink 1.0s"),
            new SpecEntry(18, "S1 Floor → Higher Adjacent Floor"),
            new SpecEntry(19, "W1 Floor → Highest Adjacent Floor"),
            new SpecEntry(20, "S1 Floor → Higher Floor + Change Tex"),
            new SpecEntry(21, "S1 Lift"),
            new SpecEntry(22, "W1 Floor → Higher Floor + Change Tex"),
            new SpecEntry(23, "S1 Floor → Lowest Adjacent Floor"),
            new SpecEntry(24, "G1 Floor → Lowest Adjacent Ceiling"),
            new SpecEntry(25, "W1 Crusher: Start (slow dmg)"),
            new SpecEntry(26, "DR Door (Blue key)"),
            new SpecEntry(27, "DR Door (Yellow key)"),
            new SpecEntry(28, "DR Door (Red key)"),
            new SpecEntry(29, "S1 Door: Open/Wait/Close"),
            new SpecEntry(30, "W1 Floor ↑ Shortest Lower Tex"),
            new SpecEntry(31, "D1 Door: Stay Open"),
            new SpecEntry(32, "D1 Door (Blue key) — stay open"),
            new SpecEntry(33, "D1 Door (Red key) — stay open"),
            new SpecEntry(34, "D1 Door (Yellow key) — stay open"),
            new SpecEntry(35, "W1 Light → 35"),
            new SpecEntry(36, "W1 Floor → 8 above Highest Adjacent (fast)"),
            new SpecEntry(37, "W1 Floor → Lowest Adjacent + Change Tex/Type"),
            new SpecEntry(38, "W1 Floor → Lowest Adjacent Floor"),
            new SpecEntry(39, "W1 Teleport"),
            new SpecEntry(40, "W1 Ceiling → Highest Ceiling"),
            new SpecEntry(41, "S1 Ceiling → Floor"),
            new SpecEntry(42, "SR Door: Close"),
            new SpecEntry(43, "SR Ceiling → Floor"),
            new SpecEntry(44, "W1 Ceiling → 8 above Floor"),
            new SpecEntry(45, "SR Floor → Highest Adjacent Floor"),
            new SpecEntry(46, "GR Door (also monsters)"),
            new SpecEntry(47, "G1 Floor → Higher Floor + Change Tex"),
            new SpecEntry(48, "Scrolling Wall (left)"),
            new SpecEntry(49, "S1 Crusher: Start (slow dmg)"),
            new SpecEntry(50, "S1 Door: Close"),
            new SpecEntry(51, "S1 Exit (secret)"),
            new SpecEntry(52, "W1 Exit (normal)"),
            new SpecEntry(53, "W1 Moving Floor: Start"),
            new SpecEntry(54, "W1 Moving Floor: Stop"),
            new SpecEntry(55, "S1 Floor → 8 below Lowest Adj. Ceiling + Crush"),
            new SpecEntry(56, "W1 Floor → 8 below Lowest Adj. Ceiling + Crush"),
            new SpecEntry(57, "W1 Crusher: Stop"),
            new SpecEntry(58, "W1 Floor ↑ 24"),
            new SpecEntry(59, "W1 Floor ↑ 24 + Change Tex/Type"),
            new SpecEntry(60, "SR Floor → Lowest Adjacent Floor"),
            new SpecEntry(61, "SR Door: Stay Open"),
            new SpecEntry(62, "SR Lift"),
            new SpecEntry(63, "SR Door: Open/Wait/Close"),
            new SpecEntry(64, "SR Floor → Lowest Adjacent Ceiling"),
            new SpecEntry(65, "SR Floor → 8 below Lowest Adj. Ceiling + Crush"),
            new SpecEntry(66, "SR Floor ↑ 24 + Change Tex"),
            new SpecEntry(67, "SR Floor ↑ 32 + Change Tex"),
            new SpecEntry(68, "SR Floor → Higher Floor + Change Tex"),
            new SpecEntry(69, "SR Floor → Higher Adjacent Floor"),
            new SpecEntry(70, "SR Floor → 8 above Highest Adjacent (fast)"),
            new SpecEntry(71, "S1 Floor → 8 above Highest Adjacent (fast)"),
            new SpecEntry(72, "WR Ceiling → 8 above Floor"),
            new SpecEntry(73, "WR Crusher: Start (slow dmg)"),
            new SpecEntry(74, "WR Crusher: Stop"),
            new SpecEntry(75, "WR Door: Close"),
            new SpecEntry(76, "WR Door: Close then Open"),
            new SpecEntry(77, "WR Crusher: Start (fast dmg)"),

            new SpecEntry(79, "WR Light → 35"),
            new SpecEntry(80, "WR Light → Highest Adjacent"),
            new SpecEntry(81, "WR Light → 255"),
            new SpecEntry(82, "WR Floor → Lowest Adjacent Floor"),
            new SpecEntry(83, "WR Floor → Highest Adjacent Floor"),
            new SpecEntry(84, "WR Floor → Lowest Adjacent + Change Tex/Type"),

            new SpecEntry(86, "WR Door: Stay Open"),
            new SpecEntry(87, "WR Moving Floor: Start"),
            new SpecEntry(88, "WR Lift (also monsters)"),
            new SpecEntry(89, "WR Moving Floor: Stop"),
            new SpecEntry(90, "WR Door: Open/Wait/Close"),
            new SpecEntry(91, "WR Floor → Lowest Adjacent Ceiling"),
            new SpecEntry(92, "WR Floor ↑ 24"),
            new SpecEntry(93, "WR Floor ↑ 24 + Change Tex/Type"),
            new SpecEntry(94, "WR Floor → 8 below Lowest Adj. Ceiling + Crush"),
            new SpecEntry(95, "WR Floor → Higher Floor + Change Tex"),
            new SpecEntry(96, "WR Floor ↑ Shortest Lower Tex"),
            new SpecEntry(97, "WR Teleport"),
            new SpecEntry(98, "WR Floor → 8 above Highest Adjacent (fast)"),


            new SpecEntry(99, "SR Door (Blue key, fast)"),
            new SpecEntry(100, "W1 Stairs: Build 16 + Crush"),
            new SpecEntry(101, "S1 Floor → Lowest Adjacent Ceiling"),
            new SpecEntry(102, "S1 Floor → Highest Adjacent Floor"),
            new SpecEntry(103, "S1 Door: Stay Open"),
            new SpecEntry(104, "W1 Light → Lowest Adjacent"),
            new SpecEntry(105, "WR Door (fast)"),
            new SpecEntry(106, "WR Door: Stay Open (fast)"),
            new SpecEntry(107, "WR Door: Close (fast)"),
            new SpecEntry(108, "W1 Door (fast)"),
            new SpecEntry(109, "W1 Door: Stay Open (fast)"),
            new SpecEntry(110, "W1 Door: Close (fast)"),
            new SpecEntry(111, "S1 Door (fast)"),
            new SpecEntry(112, "S1 Door: Stay Open (fast)"),
            new SpecEntry(113, "S1 Door: Close (fast)"),
            new SpecEntry(114, "SR Door (fast)"),
            new SpecEntry(115, "SR Door: Stay Open (fast)"),
            new SpecEntry(116, "SR Door: Close (fast)"),
            new SpecEntry(117, "DR Door (fast)"),
            new SpecEntry(118, "D1 Door (fast)"),
            new SpecEntry(119, "W1 Floor → Higher Adjacent Floor"),
            new SpecEntry(120, "WR Lift (fast)"),
            new SpecEntry(121, "W1 Lift (fast)"),
            new SpecEntry(122, "S1 Lift (fast)"),
            new SpecEntry(123, "SR Lift (fast)"),
            new SpecEntry(124, "W1 Exit (secret)"),
            new SpecEntry(125, "W1 Teleport (monsters only)"),
            new SpecEntry(126, "WR Teleport (monsters only)"),
            new SpecEntry(127, "S1 Stairs: Build 16 + Crush"),
            new SpecEntry(128, "WR Floor → Higher Adjacent Floor"),
            new SpecEntry(129, "WR Floor → Higher Floor (fast)"),
            new SpecEntry(130, "W1 Floor → Higher Floor (fast)"),
            new SpecEntry(131, "S1 Floor → Higher Floor (fast)"),
            new SpecEntry(132, "SR Floor → Higher Floor (fast)"),
            new SpecEntry(133, "S1 Door (Blue key, fast)"),
            new SpecEntry(134, "SR Door (Red key, fast)"),
            new SpecEntry(135, "S1 Door (Red key, fast)"),
            new SpecEntry(136, "SR Door (Yellow key, fast)"),
            new SpecEntry(137, "S1 Door (Yellow key, fast)"),
            new SpecEntry(138, "SR Light → 255"),
            new SpecEntry(139, "SR Light → 35"),
            new SpecEntry(140, "S1 Floor ↑ 512"),
            new SpecEntry(141, "W1 Crusher: Start (silent)")
    };
    private static final SpecEntry[] SECTOR_SPECIALS = new SpecEntry[]{
            new SpecEntry(0, "Normal"),
            new SpecEntry(1, "Light: Blink random"),
            new SpecEntry(2, "Light: Blink 0.5s"),
            new SpecEntry(3, "Light: Blink 1.0s"),
            new SpecEntry(4, "Blink + 20% damage"),
            new SpecEntry(5, "Damage: 10%/sec"),
            new SpecEntry(7, "Damage: 5%/sec"),
            new SpecEntry(8, "Light: Oscillates"),
            new SpecEntry(9, "Secret"),
            new SpecEntry(10, "Door: closes after 30s"),
            new SpecEntry(11, "End: 20% dmg; exit <11% HP"),
            new SpecEntry(12, "Light: Blink 1.0s (sync)"),
            new SpecEntry(13, "Light: Blink 0.5s (sync)"),
            new SpecEntry(14, "Door: opens after 300s"),
            new SpecEntry(16, "Damage: 20%/sec"),
            new SpecEntry(17, "Light: Flickers random")
    };
    private static final int F_IMPASS = 0x0001;
    private static final int F_BLOCKMON = 0x0002;
    private static final int F_TWOSIDED = 0x0004;
    private static final int F_UPPERUN = 0x0008;
    private static final int F_LOWERUN = 0x0010;
    private static final int F_SECRET = 0x0020;
    private static final int F_BLOCKSND = 0x0040;
    private static final int F_NOTMAP = 0x0080;
    private static final int F_ONMAP = 0x0100;
    private final MapPanel panel;
    private final JLabel thingPreview = new JLabel();
    private final JCheckBox cbImpassable = new JCheckBox("Impassable (0x0001)");
    private final JCheckBox cbBlockMon = new JCheckBox("Block monsters (0x0002)");
    private final JCheckBox cbTwoSided = new JCheckBox("Two-sided (0x0004)");
    private final JCheckBox cbUpperUnpeg = new JCheckBox("Upper unpegged (0x0008)");
    private final JCheckBox cbLowerUnpeg = new JCheckBox("Lower unpegged (0x0010)");
    private final JCheckBox cbSecret = new JCheckBox("Secret / don't show (0x0020)");
    private final JCheckBox cbBlockSound = new JCheckBox("Block sound (0x0040)");
    private final JCheckBox cbNotOnMap = new JCheckBox("Not on automap (0x0080)");
    private final JCheckBox cbOnMap = new JCheckBox("Already on automap (0x0100)");
    private final JComboBox<SpecEntry> tfSpecial = new JComboBox<>();
    private final JTextField tfTag = new JTextField();
    private final JLabel selSummary = new JLabel("No selection (linedefs)");
    private final JLabel thingSummary = new JLabel("No selection (things)");
    private final JTextField tfType = new JTextField();
    private final JTextField tfAngle = new JTextField();
    private final JCheckBox cbEasy = new JCheckBox("Spawn on Easy");
    private final JCheckBox cbNormal = new JCheckBox("Spawn on Normal");
    private final JCheckBox cbHard = new JCheckBox("Spawn on Hard");
    private final JCheckBox cbAmbush = new JCheckBox("Ambush");
    private final JCheckBox cbNotSingle = new JCheckBox("Not in Singleplayer");
    private final JCheckBox cbNotDM = new JCheckBox("Not in Deathmatch");
    private final JCheckBox cbNotCoop = new JCheckBox("Not in Coop");
    private final JLabel sectorSummary = new JLabel("No selection (sectors)");
    private final JTextField tfFloor = new JTextField();
    private final JTextField tfCeil = new JTextField();
    private final JTextField tfFloorTex = new JTextField();
    private final JTextField tfCeilTex = new JTextField();
    private final JTextField tfLight = new JTextField();
    private final JComboBox<SpecEntry> tfSecSpecial = new JComboBox<>();
    private final JTextField tfSecTag = new JTextField();
    private final JTextField tfUp = new JTextField();
    private final JTextField tfMid = new JTextField();
    private final JTextField tfLow = new JTextField();
    private final JTextField tfX = new JTextField();
    private final JTextField tfY = new JTextField();
    private final JLabel prevUp = createPreviewLabel();
    private final JLabel prevMid = createPreviewLabel();
    private final JLabel prevLow = createPreviewLabel();
    private final JLabel prevFloor = createPreviewLabel();
    private final JLabel prevCeil = createPreviewLabel();
    private final javax.swing.JTextArea lineInfo = createInfoArea();
    private final javax.swing.JTextArea thingInfo = createInfoArea();
    private final javax.swing.JTextArea sectorInfo = createInfoArea();
    private final JPanel sideRoot;
    private final JPanel lineRoot;
    private final JPanel thingRoot;
    private final JPanel sectorRoot;
    /**
     * Method InspectorPanel.
     *
     * @param panel parameter
     * @return result
     */
    public InspectorPanel(MapPanel panel) {
        this.panel = panel;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(360, 640));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setBackground(new Color(245, 245, 245));

        JTabbedPane tabs = new JTabbedPane();


        lineRoot = new JPanel();
        lineRoot.setLayout(new BoxLayout(lineRoot, BoxLayout.Y_AXIS));
        lineRoot.setOpaque(false);

        JPanel summary = new JPanel(new BorderLayout());
        summary.setOpaque(false);
        selSummary.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        summary.add(selSummary, BorderLayout.CENTER);
        lineRoot.add(summary);

        JPanel flags = new JPanel(new GridLayout(0, 1, 4, 2));
        flags.setOpaque(false);
        flags.setBorder(new TitledBorder("Linedef Flags"));
        flags.add(cbImpassable);
        flags.add(cbBlockMon);
        flags.add(cbTwoSided);
        flags.add(cbUpperUnpeg);
        flags.add(cbLowerUnpeg);
        flags.add(cbSecret);
        flags.add(cbBlockSound);
        flags.add(cbNotOnMap);
        flags.add(cbOnMap);
        lineRoot.add(flags);

        JPanel st = new JPanel(new GridBagLayout());
        st.setOpaque(false);
        st.setBorder(new TitledBorder("Special & Tag"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        st.add(new JLabel("Special:"), c);
        c.gridx = 1;
        c.weightx = 1;
        st.add(tfSpecial, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        st.add(new JLabel("Tag:"), c);
        c.gridx = 1;
        c.weightx = 1;
        st.add(tfTag, c);
        lineRoot.add(st);

        fillSpecialCombos(tfSpecial, LINEDEF_SPECIALS);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton splitDonut = new JButton("Split Donut");

        JButton flip = new JButton("Flip Direction");
        JButton splitMid = new JButton("Split at Midpoint");
        JButton apply = new JButton("Apply to selection");
        splitDonut.setToolTipText("Connect two linedefs that face the same sector (Ctrl+Shift+D)");
        splitDonut.addActionListener(e -> {
            try {
                panel.connectSelectedLinedefsSplitDonut();
            } finally {
                refreshFromSelection();
            }
        });

        JButton refresh = new JButton("Refresh");
        flip.addActionListener(e -> panel.flipSelectedLinedefs());
        splitMid.addActionListener(e -> panel.splitSelectedLinedefsMidpoint());
        btns.add(flip);
        btns.add(splitMid);
        btns.add(splitDonut);
        btns.add(refresh);
        btns.add(apply);
        lineRoot.add(Box.createVerticalStrut(8));
        lineRoot.add(btns);

        JPanel ldet = new JPanel(new BorderLayout());
        ldet.setOpaque(false);
        ldet.setBorder(new TitledBorder("Details"));
        ldet.add(new JScrollPane(lineInfo), BorderLayout.CENTER);
        lineRoot.add(ldet);

        tabs.addTab("Linedefs", new JScrollPane(lineRoot));


        thingRoot = new JPanel();
        thingRoot.setLayout(new BoxLayout(thingRoot, BoxLayout.Y_AXIS));
        thingRoot.setOpaque(false);

        thingSummary.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        thingRoot.add(thingSummary);

        JPanel tprops = new JPanel(new GridBagLayout());
        tprops.setOpaque(false);
        tprops.setBorder(new TitledBorder("Thing Properties"));
        GridBagConstraints tc = new GridBagConstraints();
        tc.insets = new Insets(2, 2, 2, 2);
        tc.fill = GridBagConstraints.HORIZONTAL;
        tc.gridx = 0;
        tc.gridy = 0;
        tprops.add(new JLabel("Type:"), tc);
        tc.gridx = 1;
        tc.weightx = 1;
        tprops.add(tfType, tc);


        tc.gridx = 2;
        tc.weightx = 0;
        JButton btnThingPalette = new JButton("Palette…");
        tprops.add(btnThingPalette, tc);
        tc.gridx = 0;
        tc.gridy = 1;
        tc.weightx = 0;
        tprops.add(new JLabel("Angle:"), tc);
        tc.gridx = 1;
        tc.weightx = 1;
        tprops.add(tfAngle, tc);
        thingRoot.add(tprops);

        JPanel tflags = new JPanel(new GridLayout(0, 1, 4, 2));
        tflags.setOpaque(false);
        tflags.setBorder(new TitledBorder("Spawn Flags"));
        tflags.add(cbEasy);
        tflags.add(cbNormal);
        tflags.add(cbHard);
        tflags.add(cbAmbush);
        tflags.add(cbNotSingle);
        tflags.add(cbNotDM);
        tflags.add(cbNotCoop);
        thingRoot.add(tflags);


        JPanel tprevBox = new JPanel(new BorderLayout());
        tprevBox.setOpaque(false);
        tprevBox.setBorder(new TitledBorder("Preview"));

        thingPreview.setHorizontalAlignment(SwingConstants.CENTER);
        thingPreview.setVerticalAlignment(SwingConstants.CENTER);
        thingPreview.setPreferredSize(new Dimension(128, 128));
        thingPreview.setOpaque(true);
        thingPreview.setBackground(Color.WHITE);

        tprevBox.add(thingPreview, BorderLayout.CENTER);
        thingRoot.add(tprevBox);

        JPanel tbtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton tapplies = new JButton("Apply to selection");
        JButton tref = new JButton("Refresh");
        tbtns.add(tref);
        tbtns.add(tapplies);
        JPanel tdet = new JPanel(new BorderLayout());
        tdet.setOpaque(false);
        tdet.setBorder(new TitledBorder("Details"));
        tdet.add(new JScrollPane(thingInfo), BorderLayout.CENTER);
        thingRoot.add(tdet);
        thingRoot.add(Box.createVerticalStrut(8));
        thingRoot.add(tbtns);

        btnThingPalette.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            ThingPaletteDialog dlg = new ThingPaletteDialog(owner, panel.getThingTemplateType(), panel.getThingTemplateFlags());
            dlg.setLocationRelativeTo(owner);
            dlg.setVisible(true);
            Integer selType = dlg.getResultType();
            Integer selFlags = dlg.getResultFlags();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (selType != null) {

                if (!panel.getSelectedThings().isEmpty()) {
                    panel.updateSelectedThings(selType, null, selFlags);
                }
                panel.setThingTemplate(selType, selFlags != null ? selFlags : panel.getThingTemplateFlags());
            }
        });


        JPanel previewBox = new JPanel(new BorderLayout());
        previewBox.setBorder(new TitledBorder("Thing Preview"));
        thingPreview.setHorizontalAlignment(SwingConstants.CENTER);
        thingPreview.setVerticalAlignment(SwingConstants.CENTER);
        thingPreview.setPreferredSize(new Dimension(128, 128));
        thingPreview.setOpaque(true);
        thingPreview.setBackground(new Color(32, 32, 36));
        thingPreview.setForeground(new Color(220, 220, 220));
        thingPreview.setText("(no sprite)");
        previewBox.add(thingPreview, BorderLayout.CENTER);


        JPanel thingTab = new JPanel(new BorderLayout());
        thingTab.add(new JScrollPane(thingRoot), BorderLayout.CENTER);
        thingTab.add(previewBox, BorderLayout.EAST);


        sideRoot = new JPanel();
        sideRoot.setLayout(new BoxLayout(sideRoot, BoxLayout.Y_AXIS));
        sideRoot.setOpaque(false);

        JPanel sdx = new JPanel(new GridBagLayout());
        sdx.setOpaque(false);
        sdx.setBorder(new TitledBorder("Sidedef Textures"));
        GridBagConstraints xc = new GridBagConstraints();
        xc.insets = new Insets(2, 2, 2, 2);
        xc.fill = GridBagConstraints.HORIZONTAL;
        JButton pickUp = new JButton("…");
        JButton pickMid = new JButton("…");
        JButton pickLow = new JButton("…");
        xc.gridx = 0;
        xc.gridy = 0;
        sdx.add(new JLabel("Upper:"), xc);
        xc.gridx = 1;
        xc.weightx = 1;
        sdx.add(tfUp, xc);
        xc.gridx = 2;
        xc.weightx = 0;
        sdx.add(pickUp, xc);
        xc.gridx = 0;
        xc.gridy = 1;
        sdx.add(new JLabel("Middle:"), xc);
        xc.gridx = 1;
        xc.weightx = 1;
        sdx.add(tfMid, xc);
        xc.gridx = 2;
        xc.weightx = 0;
        sdx.add(pickMid, xc);
        xc.gridx = 0;
        xc.gridy = 2;
        sdx.add(new JLabel("Lower:"), xc);
        xc.gridx = 1;
        xc.weightx = 1;
        sdx.add(tfLow, xc);
        xc.gridx = 2;
        xc.weightx = 0;
        sdx.add(pickLow, xc);
        sideRoot.add(sdx);


        JPanel previews = new JPanel(new GridLayout(1, 3, 6, 6));
        previews.setBorder(new TitledBorder("Preview"));
        previews.setOpaque(false);
        previews.add(wrapPreview("Upper", prevUp));
        previews.add(wrapPreview("Middle", prevMid));
        previews.add(wrapPreview("Lower", prevLow));
        sideRoot.add(previews);

        JPanel offp = new JPanel(new GridBagLayout());
        offp.setOpaque(false);
        offp.setBorder(new TitledBorder("Offsets"));
        GridBagConstraints oc = new GridBagConstraints();
        oc.insets = new Insets(2, 2, 2, 2);
        oc.fill = GridBagConstraints.HORIZONTAL;
        oc.gridx = 0;
        oc.gridy = 0;
        offp.add(new JLabel("X offset:"), oc);
        oc.gridx = 1;
        oc.weightx = 1;
        offp.add(tfX, oc);
        oc.gridx = 0;
        oc.gridy = 1;
        oc.weightx = 0;
        offp.add(new JLabel("Y offset:"), oc);
        oc.gridx = 1;
        oc.weightx = 1;
        offp.add(tfY, oc);
        sideRoot.add(offp);

        JPanel sdBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sdApply = new JButton("Apply");
        JButton sdSwap = new JButton("Swap sidedefs");
        JButton sdAlignX = new JButton("Align X");
        JButton sdAlignY = new JButton("Align Y");
        sdBtns.add(sdSwap);
        sdBtns.add(sdAlignX);
        sdBtns.add(sdAlignY);
        sdBtns.add(sdApply);
        sideRoot.add(sdBtns);


        tabs.addTab("Things", thingTab);
        tabs.addTab("Sidedefs", new JScrollPane(sideRoot));


        pickUp.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (r != null) {
                tfUp.setText(r);
                refreshPreviews();
            }
        });
        pickMid.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (r != null) {
                tfMid.setText(r);
                refreshPreviews();
            }
        });
        pickLow.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.WALLS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (r != null) {
                tfLow.setText(r);
                refreshPreviews();
            }
        });


        tfUp.getDocument().addDocumentListener((SimpleDocListener) e -> refreshPreviews());
        tfMid.getDocument().addDocumentListener((SimpleDocListener) e -> refreshPreviews());
        tfLow.getDocument().addDocumentListener((SimpleDocListener) e -> refreshPreviews());
        tfFloorTex.getDocument().addDocumentListener((SimpleDocListener) e -> refreshSectorPreviews());
        tfCeilTex.getDocument().addDocumentListener((SimpleDocListener) e -> refreshSectorPreviews());


        tfType.getDocument().addDocumentListener((SimpleDocListener) e -> setThingPreview(parseIntOrNull(tfType.getText().trim()), parseIntOrNull(tfAngle.getText().trim())));
        tfAngle.getDocument().addDocumentListener((SimpleDocListener) e -> setThingPreview(parseIntOrNull(tfType.getText().trim()), parseIntOrNull(tfAngle.getText().trim())));

        sdApply.addActionListener(e -> {
            Integer x = parseIntOrNull(tfX.getText().trim());
            Integer y = parseIntOrNull(tfY.getText().trim());
            String up = tfUp.getText().trim();
            if (up.isEmpty()) up = null;
            String mid = tfMid.getText().trim();
            if (mid.isEmpty()) mid = null;
            String low = tfLow.getText().trim();
            if (low.isEmpty()) low = null;
            panel.updateSelectedSidedefs(up, mid, low, x, y);
        });
        sdSwap.addActionListener(e -> panel.swapSelectedSidedefs());
        sdAlignX.addActionListener(e -> panel.alignSelectedSidedefs(true));
        sdAlignY.addActionListener(e -> panel.alignSelectedSidedefs(false));


        sectorRoot = new JPanel();
        sectorRoot.setLayout(new BoxLayout(sectorRoot, BoxLayout.Y_AXIS));
        sectorRoot.setOpaque(false);

        sectorSummary.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        sectorRoot.add(sectorSummary);

        JPanel sprops = new JPanel(new GridBagLayout());
        sprops.setOpaque(false);
        sprops.setBorder(new TitledBorder("Sector Properties"));
        GridBagConstraints sc = new GridBagConstraints();
        sc.insets = new Insets(2, 2, 2, 2);
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.gridx = 0;
        sc.gridy = 0;
        sprops.add(new JLabel("Floor height:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfFloor, sc);
        sc.gridx = 0;
        sc.gridy = 1;
        sc.weightx = 0;
        sprops.add(new JLabel("Ceiling height:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfCeil, sc);
        sc.gridx = 0;
        sc.gridy = 2;
        sc.weightx = 0;
        sprops.add(new JLabel("Floor texture:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfFloorTex, sc);
        sc.gridx = 2;
        sc.weightx = 0;
        JButton pickFloor = new JButton("…");
        sprops.add(pickFloor, sc);
        sc.gridx = 0;
        sc.gridy = 3;
        sc.weightx = 0;
        sprops.add(new JLabel("Ceiling texture:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfCeilTex, sc);
        sc.gridx = 2;
        sc.weightx = 0;
        JButton pickCeil = new JButton("…");
        sprops.add(pickCeil, sc);

        pickFloor.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.FLATS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (r != null) {
                tfFloorTex.setText(r);
                refreshSectorPreviews();
            }
        });
        pickCeil.addActionListener(e -> {
            TexturePickerDialog dlg = new TexturePickerDialog(SwingUtilities.getWindowAncestor(this), null, TexturePickerDialog.Mode.FLATS);
            dlg.setVisible(true);
            String r = dlg.getResult();
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (r != null) {
                tfCeilTex.setText(r);
                refreshSectorPreviews();
            }
        });

        sc.gridx = 0;
        sc.gridy = 4;
        sc.weightx = 0;
        sprops.add(new JLabel("Light level:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfLight, sc);
        sc.gridx = 0;
        sc.gridy = 5;
        sc.weightx = 0;
        sprops.add(new JLabel("Special:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfSecSpecial, sc);
        sc.gridx = 0;
        sc.gridy = 6;
        sc.weightx = 0;
        sprops.add(new JLabel("Tag:"), sc);
        sc.gridx = 1;
        sc.weightx = 1;
        sprops.add(tfSecTag, sc);
        sectorRoot.add(sprops);

        JPanel secPrev = new JPanel(new GridLayout(1, 2, 6, 6));
        secPrev.setBorder(new TitledBorder("Preview"));
        secPrev.setOpaque(false);
        secPrev.add(wrapPreview("Floor", prevFloor));
        secPrev.add(wrapPreview("Ceiling", prevCeil));
        sectorRoot.add(secPrev);

        fillSpecialCombos(tfSecSpecial, SECTOR_SPECIALS);

        JButton sApply = new JButton("Apply to selection");
        JButton sRefresh = new JButton("Refresh");
        JButton sFront = new JButton("Select Front from Lines");
        JButton sBack = new JButton("Select Back from Lines");
        JButton sClear = new JButton("Clear Sector Selection");
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(sFront);
        left.add(sBack);
        left.add(sClear);
        left.add(sRefresh);
        JPanel top = new JPanel(new BorderLayout());
        top.add(left, BorderLayout.WEST);
        sectorRoot.add(top);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(sApply);
        JPanel sdet = new JPanel(new BorderLayout());
        sdet.setOpaque(false);
        sdet.setBorder(new TitledBorder("Details"));
        sdet.add(new JScrollPane(sectorInfo), BorderLayout.CENTER);
        sectorRoot.add(sdet);
        sectorRoot.add(bottom);

        tabs.addTab("Sectors", new JScrollPane(sectorRoot));

        add(tabs, BorderLayout.CENTER);

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke("ctrl shift D"), "splitDonut");
        am.put("splitDonut", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    panel.connectSelectedLinedefsSplitDonut();
                } finally {
                    refreshFromSelection();
                }
            }
        });


        apply.addActionListener(e -> applyLinedefs());
        refresh.addActionListener(e -> refreshFromSelection());
        tapplies.addActionListener(e -> applyThings());
        tref.addActionListener(e -> refreshFromSelection());
        sApply.addActionListener(e -> applySectors());
        sRefresh.addActionListener(e -> refreshFromSelection());
        sFront.addActionListener(e -> {
            panel.selectFrontSectorsOfSelectedLines();
            refreshFromSelection();
        });
        sBack.addActionListener(e -> {
            panel.selectBackSectorsOfSelectedLines();
            refreshFromSelection();
        });
        sClear.addActionListener(e -> {
            panel.clearSectorSelection();
            refreshFromSelection();
        });

        panel.addSelectionListener(this);
        refreshFromSelection();
    }

    /**
     * Method fillSpecialCombos.
     *
     * @param combo parameter
     * @param data  parameter
     */
    private static void fillSpecialCombos(JComboBox<SpecEntry> combo, SpecEntry[] data) {
        combo.removeAllItems();
        for (SpecEntry e : data) combo.addItem(e);
    }

    /**
     * Method getSelectedCode.
     *
     * @param combo parameter
     * @return result
     */
    private static Integer getSelectedCode(JComboBox<SpecEntry> combo) {
        Object o = combo.getSelectedItem();
        if (o instanceof SpecEntry se) return se.code;
        return null;
    }

    /**
     * Method selectByCode.
     *
     * @param combo parameter
     * @param code  parameter
     */
    private static void selectByCode(JComboBox<SpecEntry> combo, Integer code) {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (code == null) {
            combo.setSelectedItem(null);
            return;
        }
        ComboBoxModel<SpecEntry> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            SpecEntry it = m.getElementAt(i);
            /**
             * Constructor for if.
             * @param code parameter
             */
            if (it != null && it.code == code) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.setSelectedItem(null);
    }

    /**
     * Method equalsOrEmpty.
     *
     * @param a parameter
     * @param b parameter
     * @return result
     */
    private static boolean equalsOrEmpty(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    /**
     * Method createPreviewLabel.
     *
     * @return result
     */
    private static JLabel createPreviewLabel() {
        JLabel l = new JLabel("—", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(96, 96));
        l.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        return l;
    }

    /**
     * Method wrapPreview.
     *
     * @param title parameter
     * @param label parameter
     * @return result
     */
    private static JPanel wrapPreview(String title, JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new TitledBorder(title));
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    /**
     * Method allHave.
     *
     * @param sel  parameter
     * @param mask parameter
     * @return result
     */
    private static boolean allHave(List<MapData.Linedef> sel, int mask) {
        for (var l : sel) if ((l.flags & mask) == 0) return false;
        return true;
    }

    /**
     * Method commonValue.
     *
     * @param sel     parameter
     * @param special parameter
     * @return result
     */
    private static String commonValue(List<MapData.Linedef> sel, boolean special) {
        Integer v = null;
        /**
         * Constructor for for.
         * @param sel parameter
         */
        for (var l : sel) {
            int cur = special ? l.special : l.tag;
            if (v == null) v = cur;
            else if (!v.equals(cur)) return "";
        }
        return v == null ? "" : Integer.toString(v);
    }

    /**
     * Method commonThing.
     *
     * @param sel   parameter
     * @param which parameter
     * @return result
     */
    private static String commonThing(List<MapData.Thing> sel, int which) {
        Integer v = null;
        /**
         * Constructor for for.
         * @param sel parameter
         */
        for (var t : sel) {
            int cur = (which == 0) ? t.type() : t.angle();
            if (v == null) v = cur;
            else if (!v.equals(cur)) return "";
        }
        return v == null ? "" : Integer.toString(v);
    }

    /**
     * Method commonThingFlags.
     *
     * @param sel parameter
     * @return result
     */
    private static int commonThingFlags(List<MapData.Thing> sel) {
        if (sel.isEmpty()) return 0;
        int mask = sel.get(0).flags();
        for (var t : sel) mask &= t.flags();
        return mask;
    }

    /**
     * Method commonSectorInt.
     *
     * @param sel   parameter
     * @param which parameter
     * @return result
     */
    private static String commonSectorInt(List<MapData.Sector> sel, int which) {
        Integer v = null;
        /**
         * Constructor for for.
         * @param sel parameter
         */
        for (var s : sel) {
            int cur = switch (which) {
                case 0 -> s.floorHeight();
                case 1 -> s.ceilingHeight();
                case 2 -> s.lightLevel();
                case 3 -> s.special();
                case 4 -> s.tag();
                default -> 0;
            };
            if (v == null) v = cur;
            else if (!v.equals(cur)) return "";
        }
        return v == null ? "" : Integer.toString(v);
    }

    /**
     * Method commonSectorStr.
     *
     * @param sel   parameter
     * @param which parameter
     * @return result
     */
    private static String commonSectorStr(List<MapData.Sector> sel, int which) {
        String v = null;
        /**
         * Constructor for for.
         * @param sel parameter
         */
        for (var s : sel) {
            String cur = which == 0 ? s.floorTex() : s.ceilingTex();
            if (v == null) v = cur;
            else if (!v.equals(cur)) return "";
        }
        return v == null ? "" : v;
    }

    /**
     * Method parseIntOrNull.
     *
     * @param s parameter
     * @return result
     */
    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Method setEnabledTree.
     *
     * @param root    parameter
     * @param enabled parameter
     */
    private static void setEnabledTree(Component root, boolean enabled) {
        if (root == null) return;
        root.setEnabled(enabled);
        /**
         * Constructor for if.
         * @param cont parameter
         */
        if (root instanceof Container cont) {
            for (Component child : cont.getComponents()) setEnabledTree(child, enabled);
        }
    }

    /**
     * Method createInfoArea.
     *
     * @return result
     */
    private static javax.swing.JTextArea createInfoArea() {
        javax.swing.JTextArea ta = new javax.swing.JTextArea(6, 20);
        ta.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16));
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setLineWrap(false);
        ta.setWrapStyleWord(false);
        ta.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 4, 4));
        return ta;
    }

    /**
     * Method fmt.
     *
     * @param v parameter
     * @return result
     */
    private static String fmt(double v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }

    @Override
    public void selectionChanged() {
        refreshFromSelection();
    }

    /**
     * Method refreshFromSelection.
     */
    private void refreshFromSelection() {

        List<MapData.Linedef> sel = panel.getSelectedLinedefs();
        int n = sel.size();
        selSummary.setText(n == 0 ? "No selection (linedefs)" : (n == 1 ? "1 linedef selected" : (n + " linedefs selected")));
        setEnabledTree(lineRoot, n > 0);

        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (n > 0) {
            cbImpassable.setSelected(allHave(sel, F_IMPASS));
            cbBlockMon.setSelected(allHave(sel, F_BLOCKMON));
            cbTwoSided.setSelected(allHave(sel, F_TWOSIDED));
            cbUpperUnpeg.setSelected(allHave(sel, F_UPPERUN));
            cbLowerUnpeg.setSelected(allHave(sel, F_LOWERUN));
            cbSecret.setSelected(allHave(sel, F_SECRET));
            cbBlockSound.setSelected(allHave(sel, F_BLOCKSND));
            cbNotOnMap.setSelected(allHave(sel, F_NOTMAP));
            cbOnMap.setSelected(allHave(sel, F_ONMAP));

            selectByCode(tfSpecial, parseIntOrNull(commonValue(sel, true)));
            tfTag.setText(commonValue(sel, false));
        } else {
            cbImpassable.setSelected(false);
            cbBlockMon.setSelected(false);
            cbTwoSided.setSelected(false);
            cbUpperUnpeg.setSelected(false);
            cbLowerUnpeg.setSelected(false);
            cbSecret.setSelected(false);
            cbBlockSound.setSelected(false);
            cbNotOnMap.setSelected(false);
            cbOnMap.setSelected(false);
            tfSpecial.setSelectedItem(null);
            tfTag.setText("");
        }


        refreshSidedefsTab(sel);
        updateLineDetails(sel);


        List<MapData.Thing> tsel = panel.getSelectedThings();
        int nt = tsel.size();
        thingSummary.setText(nt == 0 ? "No selection (things)" : (nt == 1 ? "1 thing selected" : (nt + " things selected")));
        setEnabledTree(thingRoot, nt > 0);

        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (nt > 0) {
            tfType.setText(commonThing(tsel, 0));
            tfAngle.setText(commonThing(tsel, 1));
            int maskAll = commonThingFlags(tsel);
            cbEasy.setSelected((maskAll & 0x0001) != 0);
            cbNormal.setSelected((maskAll & 0x0002) != 0);
            cbHard.setSelected((maskAll & 0x0004) != 0);
            cbAmbush.setSelected((maskAll & 0x0008) != 0);
            cbNotSingle.setSelected((maskAll & 0x0010) != 0);
            cbNotDM.setSelected((maskAll & 0x0020) != 0);
            cbNotCoop.setSelected((maskAll & 0x0040) != 0);
        } else {
            tfType.setText("");
            tfAngle.setText("");
            cbEasy.setSelected(false);
            cbNormal.setSelected(false);
            cbHard.setSelected(false);
            cbAmbush.setSelected(false);
            cbNotSingle.setSelected(false);
            cbNotDM.setSelected(false);
            cbNotCoop.setSelected(false);
        }


        setThingPreview(parseIntOrNull(tfType.getText().trim()), parseIntOrNull(tfAngle.getText().trim()));
        updateThingPreviewFromSelection();
        updateThingDetails(tsel);


        List<MapData.Sector> ssel = panel.getSelectedSectors();
        int ns = ssel.size();
        sectorSummary.setText(ns == 0 ? "No selection (sectors)" : (ns == 1 ? "1 sector selected" : (ns + " sectors selected")));
        setEnabledTree(sectorRoot, ns > 0);

        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (ns > 0) {
            tfFloor.setText(commonSectorInt(ssel, 0));
            tfCeil.setText(commonSectorInt(ssel, 1));
            tfFloorTex.setText(commonSectorStr(ssel, 0));
            tfCeilTex.setText(commonSectorStr(ssel, 1));
            tfLight.setText(commonSectorInt(ssel, 2));
            selectByCode(tfSecSpecial, parseIntOrNull(commonSectorInt(ssel, 3)));
            tfSecTag.setText(commonSectorInt(ssel, 4));
        } else {
            tfFloor.setText("");
            tfCeil.setText("");
            tfFloorTex.setText("");
            tfCeilTex.setText("");
            tfLight.setText("");
            tfSecSpecial.setSelectedItem(null);
            tfSecTag.setText("");
        }
        refreshSectorPreviews();
        updateSectorDetails(ssel);
    }

    /**
     * Method updateThingPreview.
     *
     * @param selThings parameter
     */
    private void updateThingPreview(java.util.Set<Integer> selThings) {
        thingPreview.setIcon(null);
        thingPreview.setText("—");
        thingPreview.setToolTipText(null);

        var md = panel.getMapData();
        if (md == null || selThings == null || selThings.size() != 1) {
            return;
        }
        try {
            int idx = selThings.iterator().next();
            if (idx < 0 || idx >= md.things.size()) return;

            var t = md.things.get(idx);
            java.awt.image.BufferedImage im =
                    org.deth.wad.SpriteCache.get().getThingSprite(t.type(), t.angle());

            /**
             * Constructor for if.
             * @param null parameter
             */
            if (im != null) {
                int w = im.getWidth(), h = im.getHeight();
                int max = 128;
                double s = (double) max / Math.max(w, h);
                java.awt.Image scaled = im.getScaledInstance(
                        (int) Math.round(w * s),
                        (int) Math.round(h * s),
                        java.awt.Image.SCALE_SMOOTH);
                thingPreview.setIcon(new javax.swing.ImageIcon(scaled));
                thingPreview.setText(null);
                thingPreview.setToolTipText("Type " + t.type() + " @ " + t.angle() + "°");
            } else {
                thingPreview.setText("?");
                thingPreview.setToolTipText("No sprite found for type " + t.type() +
                        " (check resource WAD and mapping)");
            }
        } catch (Throwable ex) {
            thingPreview.setText("!");
            thingPreview.setToolTipText("Failed to load sprite: " + ex.getClass().getSimpleName());
        }
    }

    /**
     * Method updateThingPreviewFromSelection.
     */
    private void updateThingPreviewFromSelection() {
        int[] idxs = panel.getSelectedThingIndices();
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int i : idxs) set.add(i);
        updateThingPreview(set);
    }

    /**
     * Method refreshSidedefsTab.
     *
     * @param selLines parameter
     */
    private void refreshSidedefsTab(List<MapData.Linedef> selLines) {

        List<MapData.Sidedef> sides = new ArrayList<>();
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (selLines != null) {
            var map = panel.getMapData();
            /**
             * Constructor for for.
             * @param selLines parameter
             */
            for (var l : selLines) {
                if (l.rightSidedef >= 0 && l.rightSidedef != 0xFFFF && l.rightSidedef < map.sidedefs.size())
                    sides.add(map.sidedefs.get(l.rightSidedef));
                if (l.leftSidedef >= 0 && l.leftSidedef != 0xFFFF && l.leftSidedef < map.sidedefs.size())
                    sides.add(map.sidedefs.get(l.leftSidedef));
            }
        }
        boolean enable = !sides.isEmpty();
        setEnabledTree(sideRoot, enable);
        /**
         * Constructor for if.
         * @param enable parameter
         */
        if (!enable) {
            tfUp.setText("");
            tfMid.setText("");
            tfLow.setText("");
            tfX.setText("");
            tfY.setText("");
            clearPreviews();
            return;
        }

        String up = null, mid = null, low = null;
        Integer xo = null, yo = null;

        /**
         * Constructor for for.
         * @param sides parameter
         */
        for (var sd : sides) {

            up = (up == null) ? sd.upperTex() : (equalsOrEmpty(up, sd.upperTex()) ? up : "");
            mid = (mid == null) ? sd.middleTex() : (equalsOrEmpty(mid, sd.middleTex()) ? mid : "");
            low = (low == null) ? sd.lowerTex() : (equalsOrEmpty(low, sd.lowerTex()) ? low : "");


            Integer sxo = sd.xOffset();
            Integer syo = sd.yOffset();

            xo = (xo == null) ? sxo : (java.util.Objects.equals(xo, sxo) ? xo : null);
            yo = (yo == null) ? syo : (java.util.Objects.equals(yo, syo) ? yo : null);
        }

        tfUp.setText(up == null ? "" : up);
        tfMid.setText(mid == null ? "" : mid);
        tfLow.setText(low == null ? "" : low);
        tfX.setText(xo == null ? "" : String.valueOf(xo));
        tfY.setText(yo == null ? "" : String.valueOf(yo));

        refreshPreviews();
    }

    /**
     * Method refreshPreviews.
     */
    private void refreshPreviews() {
        setTexturePreview(prevUp, tfUp.getText().trim());
        setTexturePreview(prevMid, tfMid.getText().trim());
        setTexturePreview(prevLow, tfLow.getText().trim());
    }

    /**
     * Method clearPreviews.
     */
    private void clearPreviews() {
        setTexturePreview(prevUp, null);
        setTexturePreview(prevMid, null);
        setTexturePreview(prevLow, null);
    }

    /**
     * Method setTexturePreview.
     *
     * @param target parameter
     * @param name   parameter
     */
    private void setTexturePreview(JLabel target, String name) {
        if (name == null || name.isEmpty() || "-".equals(name)) {
            target.setIcon(null);
            target.setText("—");
            target.setToolTipText(null);
            return;
        }
        var img = TextureCache.get().getTextureImage(name);
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (img == null) {
            target.setIcon(null);
            target.setText("?");
            target.setToolTipText(name + " (not found)");
            return;
        }
        Image thumb = TextureCache.makeThumb(img, 96, 96);
        target.setText(null);
        target.setIcon(new ImageIcon(thumb));
        target.setToolTipText(name);
    }

    /**
     * Method refreshSectorPreviews.
     */
    private void refreshSectorPreviews() {
        setTexturePreview(prevFloor, tfFloorTex.getText().trim());
        setTexturePreview(prevCeil, tfCeilTex.getText().trim());
    }

    /**
     * Method setThingPreview.
     *
     * @param type  parameter
     * @param angle parameter
     */
    private void setThingPreview(Integer type, Integer angle) {
        /**
         * Constructor for if.
         * @param null parameter
         */
        if (type == null) {
            thingPreview.setIcon(null);
            thingPreview.setText("(no sprite)");
            thingPreview.setToolTipText(null);
            return;
        }
        try {
            BufferedImage im = SpriteCache.get().getThingSprite(type, angle != null ? angle : 0);
            /**
             * Constructor for if.
             * @param null parameter
             */
            if (im == null) {
                thingPreview.setIcon(null);
                thingPreview.setText("(missing)");
                thingPreview.setToolTipText("Type " + type + (angle != null ? ", angle " + angle : ""));
                return;
            }
            int w = im.getWidth(), h = im.getHeight();
            int max = 128;
            double s = (double) max / Math.max(w, h);
            Image scaled = im.getScaledInstance(
                    Math.max(1, (int) Math.round(w * s)),
                    Math.max(1, (int) Math.round(h * s)),
                    Image.SCALE_SMOOTH);
            thingPreview.setText(null);
            thingPreview.setIcon(new ImageIcon(scaled));
            thingPreview.setToolTipText("Type " + type + (angle != null ? ", angle " + angle : ""));
        } catch (Throwable t) {
            thingPreview.setIcon(null);
            thingPreview.setText("(error)");
            thingPreview.setToolTipText(t.getMessage());
        }
    }

    /**
     * Method applyLinedefs.
     */
    private void applyLinedefs() {
        int flags = 0;
        if (cbImpassable.isSelected()) flags |= F_IMPASS;
        if (cbBlockMon.isSelected()) flags |= F_BLOCKMON;
        if (cbTwoSided.isSelected()) flags |= F_TWOSIDED;
        if (cbUpperUnpeg.isSelected()) flags |= F_UPPERUN;
        if (cbLowerUnpeg.isSelected()) flags |= F_LOWERUN;
        if (cbSecret.isSelected()) flags |= F_SECRET;
        if (cbBlockSound.isSelected()) flags |= F_BLOCKSND;
        if (cbNotOnMap.isSelected()) flags |= F_NOTMAP;
        if (cbOnMap.isSelected()) flags |= F_ONMAP;

        Integer special = getSelectedCode(tfSpecial);
        Integer tag = parseIntOrNull(tfTag.getText().trim());

        panel.updateSelectedLinedefs(flags, special, tag);
    }

    /**
     * Method applyThings.
     */
    private void applyThings() {
        Integer type = parseIntOrNull(tfType.getText().trim());
        Integer angle = parseIntOrNull(tfAngle.getText().trim());
        int flags = 0;
        if (cbEasy.isSelected()) flags |= 0x0001;
        if (cbNormal.isSelected()) flags |= 0x0002;
        if (cbHard.isSelected()) flags |= 0x0004;
        if (cbAmbush.isSelected()) flags |= 0x0008;
        if (cbNotSingle.isSelected()) flags |= 0x0010;
        if (cbNotDM.isSelected()) flags |= 0x0020;
        if (cbNotCoop.isSelected()) flags |= 0x0040;
        panel.updateSelectedThings(type, angle, flags);
    }

    /**
     * Method applySectors.
     */
    private void applySectors() {
        Integer floor = parseIntOrNull(tfFloor.getText().trim());
        Integer ceil = parseIntOrNull(tfCeil.getText().trim());
        String ftex = tfFloorTex.getText().trim();
        String ctex = tfCeilTex.getText().trim();
        if (ftex.isEmpty()) ftex = null;
        if (ctex.isEmpty()) ctex = null;
        Integer light = parseIntOrNull(tfLight.getText().trim());
        Integer spec = getSelectedCode(tfSecSpecial);
        Integer tag = parseIntOrNull(tfSecTag.getText().trim());
        panel.updateSelectedSectors(floor, ceil, ftex, ctex, light, spec, tag);
    }

    /**
     * Method updateLineDetails.
     *
     * @param sel parameter
     */
    private void updateLineDetails(java.util.List<org.deth.wad.MapData.Linedef> sel) {
        var md = panel.getMapData();
        if (md == null || sel == null || sel.isEmpty()) {
            lineInfo.setText("");
            return;
        }
        int[] ids = panel.getSelectedLinedefIndices();
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Linedefs: ").append(ids.length).append(' ');
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (ids.length > 0) {
            sb.append("IDs: ");
            int maxList = Math.min(12, ids.length);
            /**
             * Constructor for for.
             * @param i0imaxListi parameter
             */
            for (int i = 0; i < maxList; i++) {
                if (i > 0) sb.append(',');
                sb.append(ids[i]);
            }
            if (ids.length > maxList) sb.append(", …");
            sb.append('\n');
        }
        if (sel.size() == 1) {
            int lid = ids.length > 0 ? ids[0] : -1;
            sb.append("Selected Linedef (#").append(lid).append(")\n");
            var l = sel.get(0);
            var v1 = md.vertices.get(l.v1);
            var v2 = md.vertices.get(l.v2);
            int dx = v2.x() - v1.x(), dy = v2.y() - v1.y();
            double len = Math.hypot(dx, dy);
            double ang = Math.toDegrees(Math.atan2(dy, dx));
            if (ang < 0) ang += 360.0;
            sb.append("V1: (").append(v1.x()).append(',').append(v1.y()).append(")  V2: (").append(v2.x()).append(',').append(v2.y()).append(")\n");
            sb.append("Length: ").append(fmt(len)).append("  ang: ").append(fmt(ang)).append("°\n");
            sb.append(String.format(java.util.Locale.ROOT, "Flags: 0x%04X  Special: %d  Tag: %d\n", l.flags, l.special, l.tag));
            int rs = l.rightSidedef, ls = l.leftSidedef;
            Integer rSec = (rs >= 0 && rs != 0xFFFF && rs < md.sidedefs.size()) ? md.sidedefs.get(rs).sectorIndex() : null;
            Integer lSec = (ls >= 0 && ls != 0xFFFF && ls < md.sidedefs.size()) ? md.sidedefs.get(ls).sectorIndex() : null;
            sb.append("Right side: ").append(rs).append(" sec:").append(rSec == null ? "-" : rSec.toString())
                    .append("   Left side: ").append(ls).append(" sec:").append(lSec == null ? "-" : lSec.toString()).append('\n');
        }
        lineInfo.setText(sb.toString());
    }

    /**
     * Method updateThingDetails.
     *
     * @param sel parameter
     */
    private void updateThingDetails(java.util.List<org.deth.wad.MapData.Thing> sel) {
        var md = panel.getMapData();
        if (md == null || sel == null || sel.isEmpty()) {
            thingInfo.setText("");
            return;
        }
        int[] ids = panel.getSelectedThingIndices();
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Things: ").append(ids.length).append('\n');
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (ids.length > 0) {
            sb.append("IDs: ");
            int maxList = Math.min(12, ids.length);
            /**
             * Constructor for for.
             * @param i0imaxListi parameter
             */
            for (int i = 0; i < maxList; i++) {
                if (i > 0) sb.append(',');
                sb.append(ids[i]);
            }
            if (ids.length > maxList) sb.append(", …");
            sb.append('\n');
        }
        if (sel.size() == 1) {
            int tid = ids.length > 0 ? ids[0] : -1;
            sb.append("Selected Thing (#").append(tid).append(")\n");
            var t = sel.get(0);
            sb.append("Position: (").append(t.x()).append(',').append(t.y()).append(")  Angle: ").append(t.angle()).append("°  Type: ").append(t.type()).append('\n');
            sb.append(String.format(java.util.Locale.ROOT, "Flags: 0x%04X\n", t.flags()));
        }
        thingInfo.setText(sb.toString());
    }

    /**
     * Method updateSectorDetails.
     *
     * @param sel parameter
     */
    private void updateSectorDetails(java.util.List<org.deth.wad.MapData.Sector> sel) {
        var md = panel.getMapData();
        if (md == null || sel == null || sel.isEmpty()) {
            sectorInfo.setText("");
            return;
        }
        int[] ids = panel.getSelectedSectorIndices();
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Sectors: ").append(ids.length).append('\n');
        /**
         * Constructor for if.
         * @param 0 parameter
         */
        if (ids.length > 0) {
            sb.append("IDs: ");
            int maxList = Math.min(12, ids.length);
            /**
             * Constructor for for.
             * @param i0imaxListi parameter
             */
            for (int i = 0; i < maxList; i++) {
                if (i > 0) sb.append(',');
                sb.append(ids[i]);
            }
            if (ids.length > maxList) sb.append(", …");
            sb.append('\n');
        }
        if (sel.size() == 1) {
            int secIdx = ids.length > 0 ? ids[0] : -1;
            sb.append("Selected Sector (#").append(secIdx).append(")\n");
            var s = sel.get(0);
            sb.append("Floor height: ").append(s.floorHeight()).append("  Ceiling height: ").append(s.ceilingHeight()).append("\nLight level: ").append(s.lightLevel()).append('\n');
            sb.append("Floor texture: ").append(s.floorTex()).append("  Ceiling texture: ").append(s.ceilingTex()).append("  Special: ").append(s.special()).append("  Tag: ").append(s.tag()).append('\n');
            int refSides = 0;
            Integer minx = null, miny = null, maxx = null, maxy = null;
            for (int i = 0; i < md.sidedefs.size(); i++) {
                var sd = md.sidedefs.get(i);
                /**
                 * Constructor for if.
                 * @param secIdx parameter
                 */
                if (sd.sectorIndex() == secIdx) {
                    refSides++;
                    for (int li = 0; li < md.linedefs.size(); li++) {
                        var ld = md.linedefs.get(li);
                        /**
                         * Constructor for if.
                         * @param i parameter
                         */
                        if (ld.rightSidedef == i || ld.leftSidedef == i) {
                            var v1 = md.vertices.get(ld.v1);
                            var v2 = md.vertices.get(ld.v2);
                            /**
                             * Constructor for if.
                             * @param minxnull parameter
                             */
                            if (minx == null) {
                                minx = maxx = v1.x();
                                miny = maxy = v1.y();
                            }
                            minx = Math.min(minx, Math.min(v1.x(), v2.x()));
                            miny = Math.min(miny, Math.min(v1.y(), v2.y()));
                            maxx = Math.max(maxx, Math.max(v1.x(), v2.x()));
                            maxy = Math.max(maxy, Math.max(v1.y(), v2.y()));
                        }
                    }
                }
            }
            sb.append("sidedefs: ").append(refSides);
            /**
             * Constructor for if.
             * @param minxnull parameter
             */
            if (minx != null) {
                sb.append("  bbox: [").append(minx).append(',').append(miny).append("]–[").append(maxx).append(',').append(maxy).append("]\n");
            } else {
                sb.append('\n');
            }
        }
        sectorInfo.setText(sb.toString());
    }

    /**
     * Interface SimpleDocListener.
     * <p>Auto-generated documentation stub.</p>
     */
    private interface SimpleDocListener extends javax.swing.event.DocumentListener {
        void change(javax.swing.event.DocumentEvent e);

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            change(e);
        }

        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            change(e);
        }

        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            change(e);
        }
    }

    /**
         * Class SpecEntry.
         * <p>Auto-generated documentation stub.</p>
         */
        private record SpecEntry(int code, String label) {
        /**
         * Constructor for SpecEntry.
         *
         * @param code  parameter
         * @param label parameter
         */
        private SpecEntry {
        }

            @Override
            public String toString() {
                return code + " — " + label;
            }
        }

}