package com.zerowaste;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class RegisterFrame extends JFrame {

    private final JFrame parentFrame;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton userRadio;
    private JRadioButton hotelRadio;

    private static final Color GREEN_DARK = new Color(34, 139, 34);
    private static final Color GREEN_LIGHT = new Color(144, 238, 144);
    private static final Color WHITE = Color.WHITE;

    public RegisterFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("ZeroWaste — Create Account");
        setSize(430, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GREEN_DARK);
        headerPanel.setPreferredSize(new Dimension(430, 90));

        JLabel titleLabel = new JLabel("Create an account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        JLabel accountLabel = new JLabel("Register as:");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(accountLabel, gbc);

        userRadio = new JRadioButton("User");
        hotelRadio = new JRadioButton("Hotel Partner");
        userRadio.setSelected(true);
        userRadio.setBackground(WHITE);
        hotelRadio.setBackground(WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(userRadio);
        group.add(hotelRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setBackground(WHITE);
        radioPanel.add(userRadio);
        radioPanel.add(hotelRadio);
        gbc.gridy = 1;
        formPanel.add(radioPanel, gbc);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(260, 35));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(260, 35));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(260, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(GREEN_DARK);
        registerButton.setForeground(WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.setPreferredSize(new Dimension(280, 42));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(WHITE);
        cancelButton.setForeground(GREEN_DARK);
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(280, 36));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createLineBorder(GREEN_DARK));
        gbc.gridy = 6;
        formPanel.add(cancelButton, gbc);

        registerButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String type = userRadio.isSelected() ? "user" : "hotel";
        String normalizedEmail = email.toLowerCase(Locale.ROOT);

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields before registering.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (emailExists(conn, normalizedEmail)) {
                JOptionPane.showMessageDialog(this,
                        "An account with this email already exists.",
                        "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userSql = "INSERT INTO users (name, email, password, account_type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, name);
                ps.setString(2, normalizedEmail);
                ps.setString(3, password);
                ps.setString(4, type);
                ps.executeUpdate();
            }

            int userId = getLastInsertedId(conn);

            if (type.equals("hotel")) {
                if (userId <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Unable to create the hotel account because the new user ID could not be read.",
                            "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String hotelSql = "INSERT INTO hotels (name, contact, user_id) VALUES (?, ?, ?)";
                try (PreparedStatement hotelPs = conn.prepareStatement(hotelSql)) {
                    hotelPs.setString(1, name + " Hotel");
                    hotelPs.setString(2, "N/A");
                    hotelPs.setInt(3, userId);
                    hotelPs.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to complete registration: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getLastInsertedId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT last_insert_rowid()")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    private boolean emailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase(Locale.ROOT));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
