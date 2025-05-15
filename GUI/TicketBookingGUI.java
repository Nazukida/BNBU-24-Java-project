package GUI;

import Controller.*;
import DataBase.*;
import Exceptions.AuthException;
import Interfaces.ITicketOperation;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class TicketBookingGUI extends JFrame {
    private ITicketOperation ticketOperation;
    private Customer currentUser;
    private Manager currentManager;
    
    // Main panels
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel userPanel;
    private JPanel adminPanel;
    
    // Login components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton adminLoginButton;
    
    // User panel components
    private JTable activitiesTable;
    private DefaultTableModel activitiesTableModel;
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    private JTextField quantityField;
    private JButton buyButton;
    private JButton cancelButton;
    private JButton logoutButton;
    private JTextArea activityDetailsArea;
    
    // Admin panel components
    private JTable allActivitiesTable;
    private DefaultTableModel allActivitiesTableModel;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JButton addActivityButton;
    private JButton adminLogoutButton;
    private JTextField idField, nameField, dateField, venueField, priceField, totalTicketsField;
    private JButton cancelActivityButton;
    private JTextField newPriceField;
    private JButton updatePriceButton;
    
    public TicketBookingGUI() {
        System.out.println("Initializing TicketBookingGUI...");
        ticketOperation = new TicketOperationImpl();
        
        // Initialize window
        setTitle("Ticket Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels
        createLoginPanel();
        createUserPanel();
        createAdminPanel();
        
        // Add panels to main panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(userPanel, "user");
        mainPanel.add(adminPanel, "admin");
        
        // Set login panel as default
        cardLayout.show(mainPanel, "login");
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add window listener for saving data on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Initialize.saveAllData();
                System.exit(0);
            }
        });
        
        System.out.println("GUI initialization completed.");
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        
        // Create a panel for the login form with padding
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Set up GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create components
        JLabel titleLabel = new JLabel("Ticket Booking System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel usernameLabel = new JLabel("Username/ID (letters and numbers only):");
        JLabel passwordLabel = new JLabel("Password (letters and numbers only):");
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        adminLoginButton = new JButton("Admin Login");
        
        // Add title to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);
        
        // Add username components
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Add password components
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(adminLoginButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Add form panel to login panel
        loginPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add button listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
        adminLoginButton.addActionListener(e -> adminLogin());
    }
    
    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("User Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoutButton = new JButton("Logout");
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create activities table
        String[] activityColumns = {"ID", "Name", "Date", "Venue", "Price", "Available"};
        activitiesTableModel = new DefaultTableModel(activityColumns, 0);
        activitiesTable = new JTable(activitiesTableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane activitiesScrollPane = new JScrollPane(activitiesTable);
        
        // Create tickets table
        String[] ticketColumns = {"Show", "Date", "Seat", "Price"};
        ticketsTableModel = new DefaultTableModel(ticketColumns, 0);
        ticketsTable = new JTable(ticketsTableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ticketsScrollPane = new JScrollPane(ticketsTable);
        
        // Create controls panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(5);
        buyButton = new JButton("Buy Ticket");
        cancelButton = new JButton("Cancel Ticket");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlsPanel.add(quantityLabel, gbc);
        
        gbc.gridx = 1;
        controlsPanel.add(quantityField, gbc);
        
        gbc.gridx = 2;
        controlsPanel.add(buyButton, gbc);
        
        gbc.gridx = 3;
        controlsPanel.add(cancelButton, gbc);
        
        // Create activity details area
        activityDetailsArea = new JTextArea(5, 30);
        activityDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(activityDetailsArea);
        
        // Create tabbed pane for tables
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Available Activities", activitiesScrollPane);
        tabbedPane.addTab("My Tickets", ticketsScrollPane);
        
        // Add components to user panel
        userPanel.add(headerPanel, BorderLayout.NORTH);
        userPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(controlsPanel, BorderLayout.NORTH);
        southPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        userPanel.add(southPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        logoutButton.addActionListener(e -> logout());
        buyButton.addActionListener(e -> buyTicket());
        cancelButton.addActionListener(e -> cancelTicket());
        
        // Add selection listener to activities table
        activitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activitiesTable.getSelectedRow() != -1) {
                displayActivityDetails();
            }
        });
    }
    
    private void createAdminPanel() {
        adminPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        adminLogoutButton = new JButton("Logout");
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(adminLogoutButton, BorderLayout.EAST);
        
        // Create activity management panel
        JPanel activityMgmtPanel = new JPanel(new BorderLayout());
        
        // Create add activity panel
        JPanel addActivityPanel = new JPanel(new GridBagLayout());
        addActivityPanel.setBorder(BorderFactory.createTitledBorder("Add New Activity"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        
        // Create form fields
        JLabel idLabel = new JLabel("ID:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JLabel venueLabel = new JLabel("Venue:");
        JLabel priceLabel = new JLabel("Price:");
        JLabel totalTicketsLabel = new JLabel("Total Tickets:");
        
        idField = new JTextField(5);
        nameField = new JTextField(15);
        dateField = new JTextField(10);
        venueField = new JTextField(15);
        priceField = new JTextField(5);
        totalTicketsField = new JTextField(5);
        
        addActivityButton = new JButton("Add Activity");
        
        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        addActivityPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        addActivityPanel.add(idField, gbc);
        
        gbc.gridx = 2;
        addActivityPanel.add(nameLabel, gbc);
        
        gbc.gridx = 3;
        addActivityPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        addActivityPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        addActivityPanel.add(dateField, gbc);
        
        gbc.gridx = 2;
        addActivityPanel.add(venueLabel, gbc);
        
        gbc.gridx = 3;
        addActivityPanel.add(venueField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        addActivityPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1;
        addActivityPanel.add(priceField, gbc);
        
        gbc.gridx = 2;
        addActivityPanel.add(totalTicketsLabel, gbc);
        
        gbc.gridx = 3;
        addActivityPanel.add(totalTicketsField, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addActivityPanel.add(addActivityButton, gbc);
        
        // Create all activities table
        String[] activityColumns = {"ID", "Name", "Date", "Venue", "Price", "Available", "Total"};
        allActivitiesTableModel = new DefaultTableModel(activityColumns, 0);
        allActivitiesTable = new JTable(allActivitiesTableModel);
        JScrollPane activitiesScrollPane = new JScrollPane(allActivitiesTable);
        
        // Create users table
        String[] userColumns = {"ID", "Name", "Email", "Admin"};
        usersTableModel = new DefaultTableModel(userColumns, 0);
        usersTable = new JTable(usersTableModel);
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        
        // Create modify activity panel
        JPanel modifyActivityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        modifyActivityPanel.setBorder(BorderFactory.createTitledBorder("Modify Selected Activity"));
        
        cancelActivityButton = new JButton("Cancel Activity");
        JLabel newPriceLabel = new JLabel("New Price:");
        newPriceField = new JTextField(5);
        updatePriceButton = new JButton("Update Price");
        
        modifyActivityPanel.add(cancelActivityButton);
        modifyActivityPanel.add(newPriceLabel);
        modifyActivityPanel.add(newPriceField);
        modifyActivityPanel.add(updatePriceButton);
        
        // Add panels to activity management panel
        activityMgmtPanel.add(addActivityPanel, BorderLayout.NORTH);
        activityMgmtPanel.add(modifyActivityPanel, BorderLayout.SOUTH);
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Activities", activitiesScrollPane);
        tabbedPane.addTab("Users", usersScrollPane);
        
        // Add components to admin panel
        adminPanel.add(headerPanel, BorderLayout.NORTH);
        adminPanel.add(tabbedPane, BorderLayout.CENTER);
        adminPanel.add(activityMgmtPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        adminLogoutButton.addActionListener(e -> logout());
        addActivityButton.addActionListener(e -> addActivity());
        
        // Add listeners for new buttons
        cancelActivityButton.addActionListener(e -> cancelActivity());
        updatePriceButton.addActionListener(e -> {
            try {
                double newPrice = Double.parseDouble(newPriceField.getText().trim());
                updateActivityPrice(newPrice);
                newPriceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            }
        });
    }
    
    // Login methods
    private void login() {
        String id = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Frontend validation
        if (!AuthService.isValidAlphanumeric(id)) {
            JOptionPane.showMessageDialog(this, "User ID must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!AuthService.isValidAlphanumeric(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            currentUser = AuthService.login(id, password);
            usernameField.setText("");
            passwordField.setText("");
            updateUserPanel();
            cardLayout.show(mainPanel, "user");
        } catch (AuthException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void register() {
        String name = JOptionPane.showInputDialog(this, "Enter username (letters and numbers only):");
        if (name == null || name.trim().isEmpty()) return;
        
        // Validate username
        if (!AuthService.isValidAlphanumeric(name)) {
            JOptionPane.showMessageDialog(this, "Username must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = JOptionPane.showInputDialog(this, "Enter user ID (letters and numbers only):");
        if (id == null || id.trim().isEmpty()) return;
        
        // Validate ID
        if (!AuthService.isValidAlphanumeric(id)) {
            JOptionPane.showMessageDialog(this, "User ID must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String password = JOptionPane.showInputDialog(this, "Enter password (letters and numbers only):");
        if (password == null || password.trim().isEmpty()) return;
        
        // Validate password
        if (!AuthService.isValidAlphanumeric(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String email = JOptionPane.showInputDialog(this, "Enter email:");
        if (email == null || email.trim().isEmpty()) return;
        
        try {
            currentUser = AuthService.register(name, id, password, email);
            JOptionPane.showMessageDialog(this, "Registration successful!");
            updateUserPanel();
            cardLayout.show(mainPanel, "user");
        } catch (AuthException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adminLogin() {
        String id = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Frontend validation
        if (!AuthService.isValidAlphanumeric(id)) {
            JOptionPane.showMessageDialog(this, "Admin ID must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!AuthService.isValidAlphanumeric(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.out.println("GUI: Attempting admin login, ID=" + id);
        
        try {
            currentManager = AuthService.loginAsManager(id, password);
            System.out.println("GUI: Admin login successful, admin name=" + currentManager.getName() + ", permission=" + currentManager.isJurisdiction());
            currentUser = null;
            usernameField.setText("");
            passwordField.setText("");
            updateAdminPanel();
            cardLayout.show(mainPanel, "admin");
        } catch (AuthException e) {
            System.out.println("GUI: Admin login failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Admin Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        currentUser = null;
        currentManager = null;
        cardLayout.show(mainPanel, "login");
    }
    
    // User panel methods
    private void updateUserPanel() {
        // Clear tables
        activitiesTableModel.setRowCount(0);
        ticketsTableModel.setRowCount(0);
        
        // Load activities
        List<Activity> activities = ticketOperation.getAvailableActivities();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Activity activity : activities) {
            Object[] row = {
                activity.getId(),
                activity.getName(),
                sdf.format(activity.getDate()),
                activity.getVenue(),
                activity.getPrice(),
                activity.getRemainingTickets() + "/" + activity.getTotalTickets()
            };
            activitiesTableModel.addRow(row);
        }
        
        // Load user tickets
        if (currentUser != null) {
            List<Ticket> tickets = ticketOperation.getUserTickets(currentUser);
            
            for (Ticket ticket : tickets) {
                Object[] row = {
                    ticket.getShowName(),
                    ticket.getShowDate(),
                    ticket.getSeatNumber(),
                    ticket.getPrice()
                };
                ticketsTableModel.addRow(row);
            }
        }
        
        // Clear activity details
        activityDetailsArea.setText("");
    }
    
    private void displayActivityDetails() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String activityId = (String) activitiesTable.getValueAt(selectedRow, 0);
        Activity activity = ticketOperation.getActivityDetails(activityId);
        
        if (activity != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            StringBuilder sb = new StringBuilder();
            sb.append("Activity Details:\n");
            sb.append("ID: ").append(activity.getId()).append("\n");
            sb.append("Name: ").append(activity.getName()).append("\n");
            sb.append("Date: ").append(sdf.format(activity.getDate())).append("\n");
            sb.append("Venue: ").append(activity.getVenue()).append("\n");
            sb.append("Price: $").append(activity.getPrice()).append("\n");
            sb.append("Available tickets: ").append(activity.getRemainingTickets())
              .append("/").append(activity.getTotalTickets()).append("\n");
            
            activityDetailsArea.setText(sb.toString());
        }
    }
    
    private void buyTicket() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to buy tickets.");
            return;
        }
        
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity first.");
            return;
        }
        
        String quantityStr = quantityField.getText().trim();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                return;
            }
            
            String activityId = (String) activitiesTable.getValueAt(selectedRow, 0);
            Activity activity = ticketOperation.getActivityDetails(activityId);
            
            if (activity != null) {
                if (ticketOperation.purchaseTicket(currentUser, activity, quantity)) {
                    JOptionPane.showMessageDialog(this, "Tickets purchased successfully!");
                    quantityField.setText("");
                    updateUserPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to purchase tickets. Please check availability.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.");
        }
    }
    
    private void cancelTicket() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to cancel tickets.");
            return;
        }
        
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to cancel.");
            return;
        }
        
        List<Ticket> tickets = ticketOperation.getUserTickets(currentUser);
        if (selectedRow >= tickets.size()) {
            JOptionPane.showMessageDialog(this, "Invalid ticket selection.");
            return;
        }
        
        Ticket selectedTicket = tickets.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this ticket?\n" + selectedTicket,
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (ticketOperation.cancelTicket(currentUser, selectedTicket)) {
                JOptionPane.showMessageDialog(this, "Ticket cancelled successfully!");
                updateUserPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel ticket.");
            }
        }
    }
    
    // Admin panel methods
    private void updateAdminPanel() {
        // Clear tables
        allActivitiesTableModel.setRowCount(0);
        usersTableModel.setRowCount(0);
        
        // Load all activities
        List<Activity> activities = Initialize.activities;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Activity activity : activities) {
            Object[] row = {
                activity.getId(),
                activity.getName(),
                sdf.format(activity.getDate()),
                activity.getVenue(),
                activity.getPrice(),
                activity.getRemainingTickets(),
                activity.getTotalTickets()
            };
            allActivitiesTableModel.addRow(row);
        }
        
        // Load all users
        List<Customer> customers = Initialize.customers;
        
        for (Customer customer : customers) {
            Object[] row = {
                customer.getID(),
                customer.getName(),
                customer.getMail(),
                customer.isJurisdiction() ? "Yes" : "No"
            };
            usersTableModel.addRow(row);
        }
    }
    
    private void addActivity() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dateStr = dateField.getText().trim();
            String venue = venueField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int totalTickets = Integer.parseInt(totalTicketsField.getText().trim());
            
            if (id.isEmpty() || name.isEmpty() || dateStr.isEmpty() || venue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            
            Activity newActivity = currentManager.createActivity(id, name, date, venue, price, totalTickets);
            Initialize.activities.add(newActivity);
            Initialize.saveAllData();
            
            // Clear form fields
            idField.setText("");
            nameField.setText("");
            dateField.setText("");
            venueField.setText("");
            priceField.setText("");
            totalTicketsField.setText("");
            
            JOptionPane.showMessageDialog(this, "Activity added successfully!");
            updateAdminPanel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding activity: " + e.getMessage());
        }
    }
    
    private void cancelActivity() {
        int selectedRow = allActivitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity to cancel.");
            return;
        }
        
        String activityId = (String) allActivitiesTable.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this activity?\nThis will remove it completely.",
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (currentManager.cancelActivity(Initialize.activities, activityId)) {
                Initialize.saveAllData();
                JOptionPane.showMessageDialog(this, "Activity cancelled successfully!");
                updateAdminPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel activity.");
            }
        }
    }
    
    private void updateActivityPrice(double newPrice) {
        int selectedRow = allActivitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity to update price.");
            return;
        }
        
        if (newPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Price must be greater than zero.");
            return;
        }
        
        String activityId = (String) allActivitiesTable.getValueAt(selectedRow, 0);
        
        if (currentManager.updateActivityPrice(Initialize.activities, activityId, newPrice)) {
            Initialize.saveAllData();
            JOptionPane.showMessageDialog(this, "Price updated successfully!");
            updateAdminPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update price.");
        }
    }
    
    public static void main(String[] args) {
        // Initialize data first
        Initialize.initialize();
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating and showing GUI...");
                TicketBookingGUI gui = new TicketBookingGUI();
                gui.pack(); // Ensure components are at preferred size
                gui.setSize(800, 600); // Set size again after pack
                gui.setVisible(true);
                System.out.println("GUI should be visible now.");
            } catch (Exception e) {
                System.err.println("Error creating GUI:");
                e.printStackTrace();
            }
        });
    }
} 