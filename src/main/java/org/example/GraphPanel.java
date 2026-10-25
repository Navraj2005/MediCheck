package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel {
    /** Draws the interaction graph: medicines are circles (nodes), interactions are coloured lines (edges). */
    private final List<Medicine> medicines;
    private final List<Interaction> interactions;

    public GraphPanel(List<Medicine> medicines, List<Interaction> interactions) {
        this.medicines = medicines;
        this.interactions = interactions;
        setBackground(Color.WHITE);
    }

    private Color colorFor(Severity s) {
        switch (s) {
            case DANGEROUS: return new Color(0xE53935);
            case CAUTION:   return new Color(0xFFB300);
            default:        return new Color(0x43A047);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = medicines.size();
        int cx = getWidth() / 2, cy = getHeight() / 2;
        int radius = Math.min(cx, cy) - 80;

        // Place each medicine (node) on a circle
        Point[] pos = new Point[n];
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            pos[i] = new Point(cx + (int) (radius * Math.cos(angle)),
                    cy + (int) (radius * Math.sin(angle)));
        }

        // Draw interactions (edges)
        g2.setStroke(new BasicStroke(4));
        for (Interaction in : interactions) {
            int a = medicines.indexOf(in.getDrugA());
            int b = medicines.indexOf(in.getDrugB());
            if (a < 0 || b < 0) continue;
            g2.setColor(colorFor(in.getSeverity()));
            g2.drawLine(pos[a].x, pos[a].y, pos[b].x, pos[b].y);
        }

        // Draw medicines (nodes)
        for (int i = 0; i < n; i++) {
            g2.setColor(new Color(0x0F9D8A));
            g2.fillOval(pos[i].x - 28, pos[i].y - 28, 56, 56);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String name = medicines.get(i).getName();
            int w = g2.getFontMetrics().stringWidth(name);
            g2.drawString(name, pos[i].x - w / 2, pos[i].y + 48);
        }

        // Legend
        int y = 20;
        String[] labels = {"Dangerous", "Caution", "Safe"};
        Severity[] sev = {Severity.DANGEROUS, Severity.CAUTION, Severity.SAFE};
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        for (int i = 0; i < 3; i++) {
            g2.setColor(colorFor(sev[i]));
            g2.fillRect(15, y, 18, 18);
            g2.setColor(Color.BLACK);
            g2.drawString(labels[i], 40, y + 14);
            y += 26;
        }
    }
}