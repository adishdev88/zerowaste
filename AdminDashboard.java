package com.zerowaste;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {

    private static final Color GREEN_DARK = new Color(34, 139, 34);
    private static final Color GREEN_LIGHT = new Color(240, 255, 240);
    private static final Color WHITE = Color.WHITE;

    private JLabel usersLabel;
    private JLabel hotelsLabel;
    private JLabel foodsLabel;
    private JLabel ordersLabel;

    private JTable hotelTable;
    private DefaultTableModel hotelTableModel;
    private JTextField hotelNameField;
    private JTextField hotelEmailField;
    private JTextField hotelPasswordField;

    public AdminDashboard() {
        setTitle("ZeroWaste — Admin Dashboard");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        loadSummary();
        loadHotelTable();
    }

    private void buildUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GREEN_DARK);
        headerPanel.setPreferredSize(new Dimension(950, 70));

        JLabel titleLabel = new JLabel("🌿 Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(GREEN_DARK);
        logoutButton.setForeground(WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WHITE, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        logoutButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        rightPanel.setBackground(GREEN_DARK);
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout(18, 18));
        mainPanel.setBackground(GREEN_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        summaryPanel.setBackground(GREEN_LIGHT);
        usersLabel = createStatCard("Users", "0");
        hotelsLabel = createStatCard("Hotels", "0");
        foodsLabel = createStatCard("Food Items", "0");
        ordersLabel = createStatCard("Orders", "0");
        summaryPanel.add(usersLabel);
        summaryPanel.add(hotelsLabel);
        summaryPanel.add(foodsLabel);
        summaryPanel.add(ordersLabel);

        JPanel hotelPanel = new JPanel(new BorderLayout(12, 12));
        hotelPanel.setBackground(WHITE);
        hotelPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel hotelTitle = new JLabel("Hotel Management");
        hotelTitle.setFont(new Font("Arial", Font.BOLD, 20));
        hotelTitle.setForeground(GREEN_DARK);
        hotelPanel.add(hotelTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Hotel Name:"), gbc);
        hotelNameField = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(hotelNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Hotel Email:"), gbc);
        hotelEmailField = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(hotelEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        hotelPasswordField = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(hotelPasswordField, gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setBackground(WHITE);
        JButton addHotelButton = new JButton("Add Hotel");
        addHotelButton.setBackground(GREEN_DARK);
        addHotelButton.setForeground(WHITE);
        addHotelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHotelButton.addActionListener(e -> addHotel());

        JButton removeHotelButton = new JButton("Remove Selected");
        removeHotelButton.setBackground(WHITE);
        removeHotelButton.setForeground(GREEN_DARK);
        removeHotelButton.setBorder(BorderFactory.createLineBorder(GREEN_DARK, 1));
        removeHotelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeHotelButton.addActionListener(e -> removeSelectedHotel());

        buttonRow.add(addHotelButton);
        buttonRow.add(removeHotelButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonRow, gbc);

        hotelTableModel = new DefaultTableModel(new Object[]{"Hotel ID", "Hotel Name", "Hotel Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hotelTable = new JTable(hotelTableModel);
        hotelTable.setRowHeight(26);
        hotelTable.getTableHeader().setBackground(GREEN_DARK);
        hotelTable.getTableHeader().setForeground(WHITE);
        hotelTable.setFillsViewportHeight(true);

        JScrollPane hotelScroll = new JScrollPane(hotelTable);
        hotelScroll.setPreferredSize(new Dimension(350, 220));

        hotelPanel.add(formPanel, BorderLayout.NORTH);
        hotelPanel.add(hotelScroll, BorderLayout.CENTER);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(hotelPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createStatCard(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(GREEN_DARK);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 34));
        valueLabel.setForeground(GREEN_DARK);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return new JLabel() {{
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
        }};
    }

    private void loadSummary() {
        try (Connection conn = DBConnection.getConnection()) {
            usersLabel.setText("Users: " + getCount(conn, "SELECT COUNT(*) FROM users"));
            hotelsLabel.setText("Hotels: " + getCount(conn, "SELECT COUNT(*) FROM hotels"));
            foodsLabel.setText("Food Items: " + getCount(conn, "SELECT COUNT(*) FROM foods"));
            ordersLabel.setText("Orders: " + getCount(conn, "SELECT COUNT(*) FROM orders"));
        } catch (SQLException e) {
            usersLabel.setText("Users: error");
            hotelsLabel.setText("Hotels: error");
            foodsLabel.setText("Food Items: error");
            ordersLabel.setText("Orders: error");
        }
    }

    private void loadHotelTable() {
        hotelTableModel.setRowCount(0);
        String sql = "SELECT h.id, h.name, u.email FROM hotels h LEFT JOIN users u ON u.id = h.user_id ORDER BY h.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hotelTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email") == null ? "No hotel login" : rs.getString("email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading hotel list: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addHotel() {
        String name = hotelNameField.getText().trim();
        String email = hotelEmailField.getText().trim();
        String password = hotelPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter hotel name, email, and password.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (emailExists(conn, email)) {
                JOptionPane.showMessageDialog(this, "A hotel account with this email already exists.", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userSql = "INSERT INTO users (name, email, password, account_type) VALUES (?, ?, ?, 'hotel')";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, name + " Manager");
                ps.setString(2, email);
                ps.setString(3, password);
                ps.executeUpdate();
            }

            int newUserId = getLastInsertedUserId(conn);
            if (newUserId <= 0) {
                JOptionPane.showMessageDialog(this, "Unable to create hotel login account.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hotelSql = "INSERT INTO hotels (name, contact, user_id) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(hotelSql)) {
                ps.setString(1, name);
                ps.setString(2, "Added by Admin");
                ps.setInt(3, newUserId);
                ps.executeUpdate();
            }

            hotelNameField.setText("");
            hotelEmailField.setText("");
            hotelPasswordField.setText("");
            loadSummary();
            loadHotelTable();
            JOptionPane.showMessageDialog(this, "Hotel added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not add hotel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedHotel() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a hotel from the table to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int hotelId = (int) hotelTable.getValueAt(selectedRow, 0);
        String hotelName = (String) hotelTable.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete hotel '" + hotelName + "' and its hotel account?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int userId = getHotelUserId(conn, hotelId);
            String deleteFoods = "DELETE FROM foods WHERE hotel_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteFoods)) {
                ps.setInt(1, hotelId);
                ps.executeUpdate();
            }

            String deleteOrders = "DELETE FROM orders WHERE hotel_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteOrders)) {
                ps.setInt(1, hotelId);
                ps.executeUpdate();
            }

            String deleteHotel = "DELETE FROM hotels WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteHotel)) {
                ps.setInt(1, hotelId);
                ps.executeUpdate();
            }

            if (userId > 0) {
                String deleteUser = "DELETE FROM users WHERE id = ? AND account_type = 'hotel'";
                try (PreparedStatement ps = conn.prepareStatement(deleteUser)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }

            loadSummary();
            loadHotelTable();
            JOptionPane.showMessageDialog(this, "Hotel removed successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not remove hotel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getHotelUserId(Connection conn, int hotelId) throws SQLException {
        String sql = "SELECT user_id FROM hotels WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1;
    }

    private int getLastInsertedUserId(Connection conn) throws SQLException {
        String sql = "SELECT id FROM users ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        }
    }

    private boolean emailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
