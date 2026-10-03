package second;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import BookingDAO;
import Provider;
import ProviderDAO;

public class Main extends JFrame {

    // ---------- COLORS ----------
    private static final Color PRIMARY = new Color(108, 76, 255);
    private static final Color PRIMARY_DARK = new Color(83, 55, 210);
    private static final Color BACKGROUND = new Color(247, 247, 252);
    private static final Color TEXT = new Color(35, 35, 45);
    private static final Color SECONDARY_TEXT = new Color(110, 110, 125);
    private static final Color BORDER = new Color(225, 225, 235);

    private JTextField searchField;
    private JPanel resultsPanel;

    public Main() {

        setTitle("Urban Connect");
        setSize(900, 700);
        setMinimumSize(new Dimension(750, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND);

        buildUI();
    }

    // ---------- MAIN UI ----------

    private void buildUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 45, 30, 45));

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND);

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(createSearchSection());
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(createPopularServices());
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(createResultsSection());

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    // ---------- HEADER ----------

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND);

        JLabel title = new JLabel("URBAN CONNECT");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Local services. Trusted people."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton bookingsButton = createSecondaryButton("My Bookings");

        header.add(titlePanel, BorderLayout.WEST);
        header.add(bookingsButton, BorderLayout.EAST);

        return header;
    }

    // ---------- SEARCH ----------

    private JPanel createSearchSection() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Find a service");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setForeground(TEXT);

        JLabel description = new JLabel(
                "Search for trusted professionals near you."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description.setForeground(SECONDARY_TEXT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(6));
        panel.add(description);
        panel.add(Box.createVerticalStrut(15));

        JPanel searchBox = new JPanel(new BorderLayout(12, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 15, 8, 8)
        ));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setBorder(null);
        searchField.setText("");

        JButton searchButton = createPrimaryButton("Search");

        searchButton.addActionListener(e -> searchProviders());

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchButton, BorderLayout.EAST);

        panel.add(searchBox);

        return panel;
    }

    // ---------- POPULAR SERVICES ----------

    private JPanel createPopularServices() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Popular services");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(TEXT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(12));

        JPanel buttonsPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 10, 0
        ));

        buttonsPanel.setBackground(BACKGROUND);

        String[] services = {
                "Electrician",
                "Plumber",
                "Tailor",
                "Tutor"
        };

        for (String service : services) {

            JButton button = createServiceButton(service);

            button.addActionListener(e -> {
                searchField.setText(service);
                searchProviders();
            });

            buttonsPanel.add(button);
        }

        panel.add(buttonsPanel);

        return panel;
    }

    // ---------- RESULTS ----------

    private JPanel createResultsSection() {

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("AVAILABLE PROVIDERS");
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));
        heading.setForeground(SECONDARY_TEXT);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(
                resultsPanel,
                BoxLayout.Y_AXIS
        ));
        resultsPanel.setBackground(BACKGROUND);

        JLabel message = new JLabel(
                "Search for a service to see available providers."
        );
        message.setFont(new Font("SansSerif", Font.PLAIN, 14));
        message.setForeground(SECONDARY_TEXT);

        resultsPanel.add(message);

        outerPanel.add(heading, BorderLayout.NORTH);
        outerPanel.add(Box.createVerticalStrut(12), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.add(resultsPanel, BorderLayout.NORTH);

        outerPanel.add(wrapper, BorderLayout.SOUTH);

        return outerPanel;
    }

    // ---------- SEARCH LOGIC ----------

    private void searchProviders() {

        String service = searchField.getText().trim();

        if (service.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a service.",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        ProviderDAO dao = new ProviderDAO();

        List<Provider> providers =
                dao.searchProviders(service);

        resultsPanel.removeAll();

        if (providers.isEmpty()) {

            JLabel noResult = new JLabel(
                    "No providers found for \"" + service + "\"."
            );

            noResult.setFont(
                    new Font("SansSerif", Font.PLAIN, 14)
            );

            noResult.setForeground(SECONDARY_TEXT);

            resultsPanel.add(noResult);

        } else {

            for (Provider provider : providers) {
                resultsPanel.add(createProviderCard(provider));
                resultsPanel.add(Box.createVerticalStrut(12));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    // ---------- PROVIDER CARD ----------

    private JPanel createProviderCard(Provider provider) {

        JPanel card = new JPanel(new BorderLayout(20, 0));

        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)
        ));

        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 115)
        );

        // Provider information
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel name = new JLabel(provider.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 18));
        name.setForeground(TEXT);

        JLabel service = new JLabel(provider.getService());
        service.setFont(new Font("SansSerif", Font.BOLD, 13));
        service.setForeground(PRIMARY);

        JLabel details = new JLabel(
                provider.getLocation()
                        + "   •   "
                        + provider.getExperience()
                        + " years experience"
        );

        details.setFont(new Font("SansSerif", Font.PLAIN, 13));
        details.setForeground(SECONDARY_TEXT);

        info.add(name);
        info.add(Box.createVerticalStrut(5));
        info.add(service);
        info.add(Box.createVerticalStrut(8));
        info.add(details);

        // Right section
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);

        JLabel price = new JLabel(
                "₹" + String.format("%.0f", provider.getPrice())
        );

        price.setFont(new Font("SansSerif", Font.BOLD, 19));
        price.setForeground(TEXT);

        JButton bookButton = createPrimaryButton("Book Now");

        bookButton.addActionListener(
                e -> openBookingWindow(provider)
        );

        right.add(price);
        right.add(Box.createVerticalStrut(12));
        right.add(bookButton);

        card.add(info, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    // ---------- BOOKING ----------

    private void openBookingWindow(Provider provider) {

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        JTextField nameField = new JTextField();

        panel.add(new JLabel("Your Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Book " + provider.getService(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String customerName = nameField.getText().trim();

        if (customerName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your name."
            );

            return;
        }

        BookingDAO dao = new BookingDAO();

        boolean success = dao.createBooking(
                provider.getId(),
                customerName,
                java.time.LocalDate.now()
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking confirmed!\n\n"
                            + "Provider: "
                            + provider.getName()
                            + "\nService: "
                            + provider.getService()
                            + "\nDate: "
                            + java.time.LocalDate.now(),
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking failed. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------- BUTTON STYLES ----------

    private JButton createPrimaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(
                new EmptyBorder(10, 18, 10, 18)
        );
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    private JButton createSecondaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY),
                new EmptyBorder(8, 14, 8, 14)
        ));

        return button;
    }

    private JButton createServiceButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 16, 9, 16)
        ));

        return button;
    }

    // ---------- MAIN ----------

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Main app = new Main();
            app.setVisible(true);

        });
    }
}