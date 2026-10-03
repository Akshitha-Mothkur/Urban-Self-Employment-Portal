import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {

    // ---------- Palette ----------
    private static final Color PRIMARY       = new Color(79, 70, 229);
    private static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color GRAD_END      = new Color(124, 58, 237);
    private static final Color BG            = new Color(243, 244, 248);
    private static final Color CARD          = Color.WHITE;
    private static final Color TEXT_DARK     = new Color(17, 24, 39);
    private static final Color TEXT_MUTED    = new Color(107, 114, 128);
    private static final Color SOFT_BG       = new Color(238, 242, 255);
    private static final Color SOFT_HOVER    = new Color(224, 231, 255);
    private static final Color SUCCESS       = new Color(22, 163, 74);
    private static final Color DANGER        = new Color(220, 38, 38);

    private static final String FONT = "Segoe UI";

    private JTextField searchField;
    private JPanel resultsPanel;
    private JLabel statusLabel;

    public Main() {
        setTitle("Urban Connect");
        setSize(820, 700);
        setMinimumSize(new Dimension(640, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);

        setContentPane(root);
        showWelcome();
    }

    // ============================================================
    //  UI SECTIONS
    // ============================================================

    private JComponent buildHeader() {
        GradientPanel header = new GradientPanel(PRIMARY, GRAD_END);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("Urban Connect");
        title.setFont(new Font(FONT, Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Find trusted local service professionals, instantly.");
        subtitle.setFont(new Font(FONT, Font.PLAIN, 15));
        subtitle.setForeground(new Color(224, 231, 255));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        // --- Search card (overlaps header slightly) ---
        RoundedPanel searchCard = new RoundedPanel(new BorderLayout(10, 0), 18, CARD, true);

        JLabel icon = new JLabel(new SearchIcon(18, TEXT_MUTED));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        searchField = new HintTextField("Search for a service, e.g. Plumber, Electrician, Tutor...");
        searchField.setFont(new Font(FONT, Font.PLAIN, 15));
        searchField.setForeground(TEXT_DARK);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        searchField.setOpaque(false);
        searchField.addActionListener(e -> searchProviders());

        RoundedButton searchButton = new RoundedButton("Search", PRIMARY, PRIMARY_HOVER, Color.WHITE);
        searchButton.addActionListener(e -> searchProviders());

        searchCard.add(icon, BorderLayout.WEST);
        searchCard.add(searchField, BorderLayout.CENTER);
        searchCard.add(searchButton, BorderLayout.EAST);

        // --- Quick suggestions ---
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        quick.setOpaque(false);
        JLabel popular = new JLabel("Popular:");
        popular.setFont(new Font(FONT, Font.PLAIN, 13));
        popular.setForeground(TEXT_MUTED);
        quick.add(popular);
        for (String s : new String[]{"Plumber", "Electrician", "Carpenter", "Cleaner", "Tutor"}) {
            RoundedButton chip = new RoundedButton(s, SOFT_BG, SOFT_HOVER, PRIMARY);
            chip.setFont(new Font(FONT, Font.PLAIN, 12));
            chip.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
            chip.setArc(999);
            chip.addActionListener(e -> {
                searchField.setText(s);
                searchProviders();
            });
            quick.add(chip);
        }

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(FONT, Font.BOLD, 14));
        statusLabel.setForeground(TEXT_DARK);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));

        searchCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        quick.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        top.add(searchCard);
        top.add(Box.createVerticalStrut(8));
        top.add(quick);
        top.add(statusLabel);

        // --- Results ---
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(resultsPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(top, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
        return body;
    }

    // ============================================================
    //  SEARCH
    // ============================================================

    private void searchProviders() {
        String service = searchField.getText().trim();

        if (service.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a service.");
            return;
        }

        statusLabel.setText("Searching for \"" + service + "\"...");
        statusLabel.setForeground(TEXT_MUTED);

        new SwingWorker<List<Provider>, Void>() {
            @Override
            protected List<Provider> doInBackground() {
                return new ProviderDAO().searchProviders(service);
            }

            @Override
            protected void done() {
                try {
                    showResults(get(), service);
                } catch (Exception ex) {
                    resultsPanel.removeAll();
                    statusLabel.setText("Something went wrong while searching.");
                    statusLabel.setForeground(DANGER);
                    refreshResults();
                }
            }
        }.execute();
    }

    private void showResults(List<Provider> providers, String service) {
        resultsPanel.removeAll();

        if (providers == null || providers.isEmpty()) {
            statusLabel.setText("No results");
            statusLabel.setForeground(TEXT_DARK);
            resultsPanel.add(messagePanel(
                    "No providers found",
                    "We couldn't find anyone for \"" + service + "\". Try a different service."));
        } else {
            statusLabel.setText(providers.size() + (providers.size() == 1 ? " provider" : " providers")
                    + " found for \"" + service + "\"");
            statusLabel.setForeground(TEXT_DARK);
            for (Provider provider : providers) {
                addProviderCard(provider);
            }
        }
        refreshResults();
    }

    private void showWelcome() {
        resultsPanel.removeAll();
        resultsPanel.add(messagePanel(
                "Ready when you are",
                "Search for a service above or pick one of the popular categories."));
        refreshResults();
    }

    private void refreshResults() {
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JComponent messagePanel(String heading, String text) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        JLabel h = new JLabel(heading);
        h.setFont(new Font(FONT, Font.BOLD, 18));
        h.setForeground(TEXT_DARK);
        h.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(text);
        t.setFont(new Font(FONT, Font.PLAIN, 14));
        t.setForeground(TEXT_MUTED);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(h);
        p.add(Box.createVerticalStrut(6));
        p.add(t);
        return p;
    }

    // ============================================================
    //  PROVIDER CARD
    // ============================================================

    private void addProviderCard(Provider provider) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(16, 0), 16, CARD, true);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar
        Avatar avatar = new Avatar(String.valueOf(provider.getName()), 54);

        // Info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(String.valueOf(provider.getName()));
        name.setFont(new Font(FONT, Font.BOLD, 16));
        name.setForeground(TEXT_DARK);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel service = new JLabel(String.valueOf(provider.getService()));
        service.setFont(new Font(FONT, Font.PLAIN, 13));
        service.setForeground(TEXT_MUTED);
        service.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        chips.add(new Chip(String.valueOf(provider.getLocation()),
                new Color(224, 242, 254), new Color(3, 105, 161)));
        chips.add(new Chip(provider.getExperience() + " yrs experience",
                new Color(254, 243, 199), new Color(180, 83, 9)));

        info.add(name);
        info.add(Box.createVerticalStrut(2));
        info.add(service);
        info.add(Box.createVerticalStrut(8));
        info.add(chips);

        // Price + action
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel price = new JLabel("\u20B9" + provider.getPrice());
        price.setFont(new Font(FONT, Font.BOLD, 20));
        price.setForeground(SUCCESS);
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel perVisit = new JLabel("per visit");
        perVisit.setFont(new Font(FONT, Font.PLAIN, 12));
        perVisit.setForeground(TEXT_MUTED);
        perVisit.setAlignmentX(Component.RIGHT_ALIGNMENT);

        RoundedButton book = new RoundedButton("Book Now", PRIMARY, PRIMARY_HOVER, Color.WHITE);
        book.setAlignmentX(Component.RIGHT_ALIGNMENT);
        book.addActionListener(e -> openBookingWindow(provider));

        right.add(price);
        right.add(perVisit);
        right.add(Box.createVerticalStrut(8));
        right.add(book);

        card.add(avatar, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        resultsPanel.add(card);
        resultsPanel.add(Box.createVerticalStrut(4));
    }

    // ============================================================
    //  BOOKING DIALOG
    // ============================================================

    private void openBookingWindow(Provider provider) {
        JDialog dialog = new JDialog(this, "Confirm Booking", true);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));

        JLabel heading = new JLabel("Book " + provider.getName());
        heading.setFont(new Font(FONT, Font.BOLD, 20));
        heading.setForeground(TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel summary = new JLabel("<html>" + provider.getService() + " &nbsp;\u2022&nbsp; "
                + provider.getLocation() + " &nbsp;\u2022&nbsp; \u20B9" + provider.getPrice() + "</html>");
        summary.setFont(new Font(FONT, Font.PLAIN, 13));
        summary.setForeground(TEXT_MUTED);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Your name");
        nameLabel.setFont(new Font(FONT, Font.BOLD, 13));
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = new JTextField(22);
        nameField.setFont(new Font(FONT, Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel dateLabel = new JLabel("Date: " + java.time.LocalDate.now());
        dateLabel.setFont(new Font(FONT, Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_MUTED);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel error = new JLabel(" ");
        error.setFont(new Font(FONT, Font.PLAIN, 12));
        error.setForeground(DANGER);
        error.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton cancel = new RoundedButton("Cancel", new Color(243, 244, 246),
                new Color(229, 231, 235), TEXT_DARK);
        RoundedButton confirm = new RoundedButton("Confirm Booking", PRIMARY, PRIMARY_HOVER, Color.WHITE);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(cancel);
        buttons.add(confirm);

        content.add(heading);
        content.add(Box.createVerticalStrut(4));
        content.add(summary);
        content.add(Box.createVerticalStrut(20));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(nameField);
        content.add(Box.createVerticalStrut(8));
        content.add(dateLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(error);
        content.add(Box.createVerticalStrut(10));
        content.add(buttons);

        cancel.addActionListener(e -> dialog.dispose());

        Runnable submit = () -> {
            String customerName = nameField.getText().trim();
            if (customerName.isEmpty()) {
                error.setText("Please enter your name to continue.");
                return;
            }

            BookingDAO bookingDAO = new BookingDAO();
            boolean success = bookingDAO.createBooking(
                    provider.getId(),
                    customerName,
                    java.time.LocalDate.now());

            dialog.dispose();

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "<html><b style='font-size:13px'>Booking confirmed!</b><br><br>"
                                + "Provider: " + provider.getName() + "<br>"
                                + "Service: " + provider.getService() + "</html>",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Booking failed. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        confirm.addActionListener(e -> submit.run());
        nameField.addActionListener(e -> submit.run());

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============================================================
    //  CUSTOM COMPONENTS
    // ============================================================

    private static void aa(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** Panel with a diagonal gradient background. */
    static class GradientPanel extends JPanel {
        private final Color c1, c2;

        GradientPanel(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // soft decorative circles
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillOval(getWidth() - 220, -90, 260, 260);
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillOval(getWidth() - 340, 30, 180, 180);
            g2.dispose();
        }
    }

    /** Rounded card with optional soft drop shadow. */
    static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final boolean shadow;

        RoundedPanel(LayoutManager lm, int arc, Color fill, boolean shadow) {
            super(lm);
            this.arc = arc;
            this.fill = fill;
            this.shadow = shadow;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 20, 22, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            int w = getWidth(), h = getHeight();
            if (shadow) {
                for (int i = 0; i < 4; i++) {
                    g2.setColor(new Color(15, 23, 42, 9));
                    g2.fillRoundRect(i, i + 2, w - 2 * i, h - 2 * i - 2, arc + 4, arc + 4);
                }
            }
            g2.setColor(fill);
            g2.fillRoundRect(4, 2, w - 8, h - 8, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Flat rounded button with hover colour. */
    static class RoundedButton extends JButton {
        private final Color base, hover;
        private boolean over;
        private int arc = 12;

        RoundedButton(String text, Color base, Color hover, Color fg) {
            super(text);
            this.base = base;
            this.hover = hover;
            setForeground(fg);
            setFont(new Font(FONT, Font.BOLD, 13));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { over = false; repaint(); }
            });
        }

        void setArc(int arc) { this.arc = arc; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setColor(over ? hover : base);
            int a = Math.min(arc, getHeight());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), a, a);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Text field that shows greyed placeholder text when empty. */
    static class HintTextField extends JTextField {
        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e)   { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                aa(g2);
                g2.setColor(new Color(156, 163, 175));
                g2.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, in.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    /** Small rounded tag label. */
    static class Chip extends JLabel {
        private final Color bg;

        Chip(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            setForeground(fg);
            setFont(new Font(FONT, Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Circular avatar showing the provider's initials. */
    static class Avatar extends JComponent {
        private static final Color[] COLORS = {
                new Color(99, 102, 241), new Color(236, 72, 153), new Color(16, 185, 129),
                new Color(245, 158, 11), new Color(14, 165, 233), new Color(168, 85, 247)
        };
        private final String initials;
        private final Color color;
        private final int size;

        Avatar(String name, int size) {
            this.size = size;
            this.color = COLORS[Math.abs(name.hashCode()) % COLORS.length];
            String[] parts = name.trim().split("\\s+");
            String s = parts.length > 0 && !parts[0].isEmpty() ? parts[0].substring(0, 1) : "?";
            if (parts.length > 1 && !parts[1].isEmpty()) s += parts[1].substring(0, 1);
            this.initials = s.toUpperCase();
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            int y = (getHeight() - size) / 2;
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(0, y, size, size));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(FONT, Font.BOLD, size / 3));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initials,
                    (size - fm.stringWidth(initials)) / 2,
                    y + (size - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Drawn magnifier icon (no emoji font dependency). */
    static class SearchIcon implements Icon {
        private final int size;
        private final Color color;

        SearchIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            aa(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int d = (int) (size * 0.65);
            g2.drawOval(x, y, d, d);
            g2.drawLine(x + d - 1, y + d - 1, x + size - 1, y + size - 1);
            g2.dispose();
        }

        @Override public int getIconWidth()  { return size; }
        @Override public int getIconHeight() { return size; }
    }

    // ============================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
