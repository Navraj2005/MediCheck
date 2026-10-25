package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Main window of MediCheck. Builds the Swing user interface and connects
 * the buttons to the checking logic (InteractionChecker) and the database.
 */
public class MainFrame extends JFrame {
    private static final Color TEAL = new Color(0x0F9D8A);
    private static final Color TEAL_DARK = new Color(0x0B7A6B);
    private static final Color BG = new Color(0xF4F7F9);
    private static final Color TEXT = new Color(0x1F2D3D);
    private static final Color MUTED = new Color(0x6B7C8F);
    private static final String SAVE_FILE = "mylist.txt";

    private final Database db = new Database();
    private final InteractionChecker checker = new InteractionChecker(db);
    private final List<Medicine> medicines = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private final JTextField input = new JTextField();
    private final ResultsPanel resultsPanel = new ResultsPanel();
    private int fontSize = 15;
    private boolean resultsShown = false;

    public MainFrame() {
        super("MediCheck - Drug Interaction Checker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(16, 16, 8, 16));
        body.add(buildLeftCard(), BorderLayout.WEST);
        body.add(buildRightCard(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ---------- UI building ----------

    /** Teal title bar at the top of the window. */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TEAL);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));
        JLabel title = new JLabel("MediCheck");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Drug Interaction Checker  |  check your medicines before combining them");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0xD8F3EE));
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    /** Left card: add, list, remove, save and load medicines. */
    private JPanel buildLeftCard() {
        Card card = new Card(new BorderLayout(0, 10), Color.WHITE, null);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setPreferredSize(new Dimension(360, 0));

        input.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        input.putClientProperty("JTextField.placeholderText", "Type a medicine name...");
        JButton addBtn = primaryButton("Add");
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(input, BorderLayout.CENTER);
        top.add(addBtn, BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setOpaque(false);
        north.add(sectionLabel("My Medicines"), BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);

        list.setFont(new Font("Segoe UI", Font.PLAIN, fontSize + 2));
        list.setFixedCellHeight(34);
        list.setBorder(new EmptyBorder(4, 8, 4, 8));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE3E9EF)));

        JButton removeBtn = secondaryButton("Remove");
        JButton saveBtn = secondaryButton("Save");
        JButton loadBtn = secondaryButton("Load");
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.add(removeBtn);
        row.add(saveBtn);
        row.add(loadBtn);

        card.add(north, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(row, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addMedicine());
        input.addActionListener(e -> addMedicine());
        removeBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) {
                medicines.remove(idx);
                listModel.remove(idx);
            }
        });
        saveBtn.addActionListener(e -> saveList());
        loadBtn.addActionListener(e -> loadList());
        return card;
    }

    /** Right card: scrollable area that shows the result cards. */
    private JPanel buildRightCard() {
        Card card = new Card(new BorderLayout(0, 10), Color.WHITE, null);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.add(sectionLabel("Results"), BorderLayout.NORTH);

        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);
        showPlaceholder();

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /** Bottom bar: main action buttons, large-text switch and disclaimer. */
    private JPanel buildBottomBar() {
        JButton checkBtn = primaryButton("Check Interactions");
        JButton graphBtn = secondaryButton("Show Graph");
        JButton printBtn = secondaryButton("Print Report");
        JButton addIntBtn = secondaryButton("Add Interaction");
        checkBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel buttons = new JPanel(new GridLayout(1, 4, 10, 0));
        buttons.setOpaque(false);
        buttons.add(checkBtn);
        buttons.add(graphBtn);
        buttons.add(printBtn);
        buttons.add(addIntBtn);

        JCheckBox bigText = new JCheckBox("Large text");
        bigText.setOpaque(false);
        bigText.setForeground(MUTED);
        JLabel disclaimer = new JLabel("Educational use only - not medical advice.");
        disclaimer.setForeground(MUTED);
        disclaimer.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(bigText, BorderLayout.WEST);
        footer.add(disclaimer, BorderLayout.EAST);

        JPanel bar = new JPanel(new BorderLayout(0, 8));
        bar.setBackground(BG);
        bar.setBorder(new EmptyBorder(8, 16, 12, 16));
        bar.add(buttons, BorderLayout.CENTER);
        bar.add(footer, BorderLayout.SOUTH);

        checkBtn.addActionListener(e -> showResults());
        printBtn.addActionListener(e -> printReport());
        addIntBtn.addActionListener(e -> addInteractionDialog());
        graphBtn.addActionListener(e -> {
            if (medicines.size() < 2) {
                JOptionPane.showMessageDialog(this, "Add at least 2 medicines first.");
                return;
            }
            JDialog d = new JDialog(this, "Interaction Graph", false);
            d.add(new GraphPanel(new ArrayList<>(medicines), checker.check(medicines)));
            d.setSize(650, 550);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
        });
        bigText.addActionListener(e -> {
            fontSize = bigText.isSelected() ? 20 : 15;
            list.setFont(new Font("Segoe UI", Font.PLAIN, fontSize + 2));
            input.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            if (resultsShown && medicines.size() >= 2) {
                showResults();
            } else {
                showPlaceholder();
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });
        return bar;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(TEXT);
        return l;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.setBackground(TEAL);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setMargin(new Insets(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.setBackground(new Color(0xE7F6F4));
        b.setForeground(TEAL_DARK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setMargin(new Insets(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------- Result cards ----------

    private void showPlaceholder() {
        resultsPanel.removeAll();
        resultsShown = false;
        addCard("Add two or more medicines, then click <b>Check Interactions</b>.",
                new Color(0xE7F6F4), TEAL);
    }

    /** Adds one rounded card with a coloured left stripe to the results area. */
    private void addCard(String html, Color tint, Color stripe) {
        Card card = new Card(new BorderLayout(), tint, stripe);
        card.setBorder(new EmptyBorder(12, 20, 12, 14));
        JLabel label = new JLabel("<html><body style='width:480px'>" + html + "</body></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(TEXT);
        card.add(label, BorderLayout.CENTER);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        resultsPanel.add(card);
        resultsPanel.add(Box.createVerticalStrut(10));
    }

    /** Starts the interaction check on a background thread so the window never freezes. */
    private void showResults() {
        if (medicines.size() < 2) {
            JOptionPane.showMessageDialog(this, "Add at least 2 medicines to check.");
            return;
        }
        resultsPanel.removeAll();
        addCard("Checking interactions...", new Color(0xE7F6F4), TEAL);
        resultsPanel.revalidate();
        resultsPanel.repaint();

        final List<Medicine> snapshot = new ArrayList<>(medicines);
        new SwingWorker<List<Interaction>, Void>() {
            @Override
            protected List<Interaction> doInBackground() {
                return checker.check(snapshot);   // runs on a worker thread
            }

            @Override
            protected void done() {               // runs back on the UI thread
                try {
                    renderResults(get(), snapshot);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Check failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    /** Draws the result cards (always called on the UI thread). */
    private void renderResults(List<Interaction> found, List<Medicine> meds) {
        resultsPanel.removeAll();
        int n = meds.size();
        int pairs = n * (n - 1) / 2;
        addCard("<b>Checked " + n + " medicines (" + pairs + " pairs)</b> - "
                + found.size() + " interaction(s) found", new Color(0xE7F6F4), TEAL);
        if (found.isEmpty()) {
            addCard("No known interactions found in the database.",
                    new Color(0xE8F5EC), new Color(0x43A047));
        }
        for (Interaction i : found) {
            Color tint, stripe;
            String hex;
            switch (i.getSeverity()) {
                case DANGEROUS:
                    tint = new Color(0xFDECEC); stripe = new Color(0xE53935); hex = "#C62828"; break;
                case CAUTION:
                    tint = new Color(0xFFF6DD); stripe = new Color(0xFFB300); hex = "#B26A00"; break;
                default:
                    tint = new Color(0xE8F5EC); stripe = new Color(0x43A047); hex = "#2E7D32";
            }
            addCard("<b>" + i.getDrugA() + " + " + i.getDrugB() + "</b> &nbsp; "
                    + "<span style='color:" + hex + "'><b>"
                    + i.getSeverity().getLabel().toUpperCase() + "</b></span><br>"
                    + i.getDescription(), tint, stripe);
        }
        for (Medicine m : meds) {
            String w = FoodWarnings.get(m);
            if (w != null) {
                addCard("<b>Food / drink: " + m + "</b><br>" + w,
                        new Color(0xE8F1FB), new Color(0x1E88E5));
            }
        }
        resultsShown = true;
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    // ---------- Actions ----------

    /** Adds the typed medicine; warns if the name is unknown (possible typo). */
    private void addMedicine() {
        try {
            Medicine m = new Medicine(input.getText());
            if (medicines.contains(m)) {
                JOptionPane.showMessageDialog(this, m + " is already in the list.");
            } else {
                if (!checker.isKnown(m)) {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "\"" + m + "\" is not in the database. Check the spelling.\nAdd anyway?",
                            "Unknown medicine", JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        input.requestFocus();
                        return;
                    }
                }
                medicines.add(m);
                listModel.addElement(m.getName());
            }
            input.setText("");
            input.requestFocus();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    /** Builds a plain-text report and sends it to the printer (or Print to PDF). */
    private void printReport() {
        if (medicines.size() < 2) {
            JOptionPane.showMessageDialog(this, "Add at least 2 medicines first.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("MEDICHECK - DRUG INTERACTION REPORT\n");
        sb.append("Date: ").append(java.time.LocalDate.now()).append("\n\n");
        sb.append("Medicines checked: ");
        for (int i = 0; i < medicines.size(); i++) {
            sb.append(medicines.get(i).getName());
            if (i < medicines.size() - 1) sb.append(", ");
        }
        sb.append("\n\n");

        List<Interaction> found = checker.check(medicines);
        sb.append("INTERACTIONS FOUND: ").append(found.size()).append("\n\n");
        for (Interaction i : found) {
            sb.append("[").append(i.getSeverity().getLabel().toUpperCase()).append("] ")
                    .append(i.getDrugA()).append(" + ").append(i.getDrugB()).append("\n")
                    .append("   ").append(i.getDescription()).append("\n\n");
        }

        sb.append("FOOD / DRINK NOTES\n");
        for (Medicine m : medicines) {
            String w = FoodWarnings.get(m);
            if (w != null) sb.append("- ").append(m).append(": ").append(w).append("\n");
        }
        sb.append("\nEducational use only - not medical advice.\n");
        sb.append("Please show this report to a doctor or pharmacist.\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        try {
            area.print();
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Could not print: " + ex.getMessage());
        }
    }

    /** Dialog that lets the user add a new interaction to the database. */
    private void addInteractionDialog() {
        JTextField a = new JTextField(12);
        JTextField b = new JTextField(12);
        JComboBox<Severity> sev = new JComboBox<>(Severity.values());
        JTextField desc = new JTextField(25);
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Medicine A:")); p.add(a);
        p.add(new JLabel("Medicine B:")); p.add(b);
        p.add(new JLabel("Severity:")); p.add(sev);
        p.add(new JLabel("Description:")); p.add(desc);

        int r = JOptionPane.showConfirmDialog(this, p, "Add Interaction",
                JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            Medicine ma = new Medicine(a.getText());
            Medicine mb = new Medicine(b.getText());
            if (ma.equals(mb)) {
                JOptionPane.showMessageDialog(this, "Choose two different medicines.");
                return;
            }
            if (desc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add a short description.");
                return;
            }
            if (db.findInteraction(ma, mb) != null) {
                JOptionPane.showMessageDialog(this, "This pair is already in the database.");
                return;
            }
            db.addInteraction(new Interaction(ma, mb,
                    (Severity) sev.getSelectedItem(), desc.getText().trim()));
            JOptionPane.showMessageDialog(this, "Interaction saved.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    /** Saves the current medicine list to a text file (file handling). */
    private void saveList() {
        try (java.io.PrintWriter out = new java.io.PrintWriter(SAVE_FILE)) {
            for (Medicine m : medicines) out.println(m.getName());
            JOptionPane.showMessageDialog(this, "Saved " + medicines.size() + " medicine(s).");
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save: " + ex.getMessage());
        }
    }

    /** Loads a previously saved medicine list from the text file. */
    private void loadList() {
        java.io.File f = new java.io.File(SAVE_FILE);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this, "No saved list found.");
            return;
        }
        try (java.util.Scanner sc = new java.util.Scanner(f)) {
            medicines.clear();
            listModel.clear();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                Medicine m = new Medicine(line);
                if (!medicines.contains(m)) {
                    medicines.add(m);
                    listModel.addElement(m.getName());
                }
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load: " + ex.getMessage());
        }
    }

    // ---------- Helper components ----------

    /** A panel drawn as a rounded card, with an optional coloured stripe on the left. */
    private static class Card extends JPanel {
        private final Color fill;
        private final Color stripe;

        Card(LayoutManager lm, Color fill, Color stripe) {
            super(lm);
            this.fill = fill;
            this.stripe = stripe;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, 18, 18);
            if (stripe != null) {
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));
                g2.setColor(stripe);
                g2.fillRect(0, 0, 6, h);
                g2.setClip(null);
            }
            g2.setColor(new Color(0xE3E9EF));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Results panel that follows the width of the scroll pane (so text wraps correctly). */
    private static class ResultsPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}