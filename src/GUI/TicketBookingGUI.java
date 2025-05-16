package GUI;

import Controller.*;
import DataBase.*;
import Exceptions.*;
import Interfaces.ITicketOperation;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class TicketBookingGUI extends JFrame {
    private ITicketOperation tOp;
    private Customer currentUser;
    private Manager currMgr;
    
    // Main panels
    private JPanel mainPanel;
    private CardLayout cLayout;
    private JPanel loginPanel;
    private JPanel userPanel;
    private JPanel adminPanel;
    
    // Login components
    private JTextField userField;
    private JPasswordField pwField;
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
    private JButton transferButton;
    private JButton commentButton;
    private JTextArea commentArea;
    
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
    private JButton statisticsButton;
    private JTextField startDateField, endDateField;
    
    // Add button for Edit Activity
    private JButton editActivityButton;
    
    // User management components
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deleteUserButton;  // New button for deleting users
    private JPanel userManagementPanel;
    private JTextField userIdField, userNameField, userEmailField, userPasswordField;
    
    public TicketBookingGUI() {
        System.out.println("Initializing Ticket Booking System GUI...");
        tOp = new TicketOperationImpl();
        
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
        cLayout = new CardLayout();
        mainPanel = new JPanel(cLayout);
        
        // Create panels
        createLoginPanel();
        createUserPanel();
        createAdminPanel();
        
        // Add panels to main panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(userPanel, "user");
        mainPanel.add(adminPanel, "admin");
        
        // Set login panel as default
        cLayout.show(mainPanel, "login");
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
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
        loginPanel = new JPanel(new BorderLayout(10, 10));
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create title panel (using FlowLayout)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Ticket Booking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Create form panel (using GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        
        JLabel usernameLabel = new JLabel("Username/ID (letters and numbers only):");
        JLabel passwordLabel = new JLabel("Password (letters and numbers only):");
        
        userField = new JTextField(20);
        pwField = new JPasswordField(20);
        
        formPanel.add(usernameLabel);
        formPanel.add(userField);
        formPanel.add(passwordLabel);
        formPanel.add(pwField);
        
        // Add padding cells
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        
        // Create button panel (using FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        adminLoginButton = new JButton("Admin Login");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(adminLoginButton);
        
        // Add panels to login panel
        loginPanel.add(titlePanel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminLogin();
            }
        });
    }
    
    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout(5, 5));
        
        // Create header panel (using BorderLayout)
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        JLabel titleLabel = new JLabel("User Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoutButton = new JButton("Logout");
        
        // Logout button panel (using FlowLayout)
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        // Create tables panel
        JTabbedPane tabbedPane = new JTabbedPane();
        
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
        
        // Add tables to tabbed pane
        tabbedPane.addTab("Available Activities", activitiesScrollPane);
        tabbedPane.addTab("My Tickets", ticketsScrollPane);
        
        // Create controls panel (using FlowLayout)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(5);
        buyButton = new JButton("Buy Ticket");
        cancelButton = new JButton("Cancel Ticket");
        transferButton = new JButton("Transfer Ticket");
        commentButton = new JButton("Add Comment");
        
        controlsPanel.add(quantityLabel);
        controlsPanel.add(quantityField);
        controlsPanel.add(buyButton);
        controlsPanel.add(cancelButton);
        controlsPanel.add(transferButton);
        controlsPanel.add(commentButton);
        
        // Create details panel (using GridLayout)
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        activityDetailsArea = new JTextArea(5, 30);
        activityDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(activityDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Activity Details"));
        
        commentArea = new JTextArea(5, 30);
        commentArea.setEditable(false);
        JScrollPane commentsScrollPane = new JScrollPane(commentArea);
        commentsScrollPane.setBorder(BorderFactory.createTitledBorder("Comments"));
        
        detailsPanel.add(detailsScrollPane);
        detailsPanel.add(commentsScrollPane);
        
        // Create footer panel containing controls and details (using BorderLayout)
        JPanel footerPanel = new JPanel(new BorderLayout(5, 5));
        footerPanel.add(controlsPanel, BorderLayout.NORTH);
        footerPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Add all panels to user panel
        userPanel.add(headerPanel, BorderLayout.NORTH);
        userPanel.add(tabbedPane, BorderLayout.CENTER);
        userPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyTicket();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelTicket();
            }
        });
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferTicket();
            }
        });
        commentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addComment();
            }
        });
        
        // Add table selection listener
        activitiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && activitiesTable.getSelectedRow() != -1) {
                    displayActivityDetails();
                }
            }
        });
    }
    
    private void createAdminPanel() {
        adminPanel = new JPanel(new BorderLayout(5, 5));
        
        // Create header panel (using BorderLayout)
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminLogoutButton = new JButton("Logout");
        logoutPanel.add(adminLogoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        // Create main content panel (using BorderLayout)
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        
        // Create activities and user management panels
        JPanel activitiesPanel = createActivitiesManagementPanel();
        userManagementPanel = createUserManagementPanel();
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Activities Management", activitiesPanel);
        tabbedPane.addTab("User Management", userManagementPanel);
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add all panels to admin panel
        adminPanel.add(headerPanel, BorderLayout.NORTH);
        adminPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add button listeners
        adminLogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }
    
    private JPanel createActivitiesManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Create activities table
        String[] activityColumns = {"ID", "Name", "Date", "Venue", "Price", "Available", "Total"};
        allActivitiesTableModel = new DefaultTableModel(activityColumns, 0);
        allActivitiesTable = new JTable(allActivitiesTableModel);
        JScrollPane activitiesScrollPane = new JScrollPane(allActivitiesTable);
        
        // Create activities control panel (using BorderLayout)
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        
        // Create add activity panel (using GridLayout)
        JPanel addActivityPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        addActivityPanel.setBorder(BorderFactory.createTitledBorder("Add New Activity"));
        
        JLabel idLabel = new JLabel("ID:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JLabel venueLabel = new JLabel("Venue:");
        JLabel priceLabel = new JLabel("Price:");
        JLabel totalTicketsLabel = new JLabel("Total Tickets:");
        
        idField = new JTextField(10);
        nameField = new JTextField(10);
        dateField = new JTextField(10);
        venueField = new JTextField(10);
        priceField = new JTextField(10);
        totalTicketsField = new JTextField(10);
        
        addActivityButton = new JButton("Add Activity");
        
        // Add components to panel
        addActivityPanel.add(idLabel);
        addActivityPanel.add(idField);
        addActivityPanel.add(nameLabel);
        addActivityPanel.add(nameField);
        addActivityPanel.add(dateLabel);
        addActivityPanel.add(dateField);
        addActivityPanel.add(venueLabel);
        addActivityPanel.add(venueField);
        addActivityPanel.add(priceLabel);
        addActivityPanel.add(priceField);
        addActivityPanel.add(totalTicketsLabel);
        addActivityPanel.add(totalTicketsField);
        addActivityPanel.add(new JLabel("")); // Empty for spacing
        addActivityPanel.add(addActivityButton);
        addActivityPanel.add(new JLabel("")); // Empty for spacing
        addActivityPanel.add(new JLabel("")); // Empty for spacing
        
        // Create modify activity panel
        JPanel modifyPanel = new JPanel(new BorderLayout(5, 5));
        
        // Upper panel for activity modifications (using FlowLayout)
        JPanel activityModPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        activityModPanel.setBorder(BorderFactory.createTitledBorder("Modify Selected Activity"));
        
        cancelActivityButton = new JButton("Cancel Activity");
        JLabel newPriceLabel = new JLabel("New Price:");
        newPriceField = new JTextField(5);
        updatePriceButton = new JButton("Update Price");
        editActivityButton = new JButton("Edit Activity");
        
        activityModPanel.add(cancelActivityButton);
        activityModPanel.add(newPriceLabel);
        activityModPanel.add(newPriceField);
        activityModPanel.add(updatePriceButton);
        activityModPanel.add(editActivityButton);
        
        // Statistics panel (using FlowLayout)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Ticket Statistics"));
        
        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateField = new JTextField(10);
        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateField = new JTextField(10);
        statisticsButton = new JButton("Generate Statistics");
        
        statsPanel.add(startDateLabel);
        statsPanel.add(startDateField);
        statsPanel.add(endDateLabel);
        statsPanel.add(endDateField);
        statsPanel.add(statisticsButton);
        
        // Combine modify panels (using GridLayout)
        JPanel modifyPanels = new JPanel(new GridLayout(2, 1, 5, 5));
        modifyPanels.add(activityModPanel);
        modifyPanels.add(statsPanel);
        
        modifyPanel.add(modifyPanels, BorderLayout.CENTER);
        
        // Add components to control panel
        controlPanel.add(addActivityPanel, BorderLayout.NORTH);
        controlPanel.add(modifyPanel, BorderLayout.SOUTH);
        
        // Add components to activities management panel
        panel.add(activitiesScrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addActivity();
            }
        });
        cancelActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelActivity();
            }
        });
        updatePriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double newPrice = Double.parseDouble(newPriceField.getText().trim());
                    updateActivityPrice(newPrice);
                    newPriceField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TicketBookingGUI.this, "Please enter a valid price");
                }
            }
        });
        editActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editActivity();
            }
        });
        statisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateStatistics();
            }
        });
        
        return panel;
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Create users table
        String[] userColumns = {"ID", "Name", "Email", "Admin"};
        usersTableModel = new DefaultTableModel(userColumns, 0);
        usersTable = new JTable(usersTableModel);
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        
        // Create user management control panel (using BorderLayout)
        JPanel userControlsPanel = new JPanel(new BorderLayout(5, 5));
        
        // User form panel using new layout - 4 columns in a grid
        JPanel userFormPanel = new JPanel(new GridLayout(5, 4, 10, 5));
        userFormPanel.setBorder(BorderFactory.createTitledBorder("User Management"));
        
        // Labels for form fields
        JLabel userIdLabel = new JLabel("User ID:", JLabel.RIGHT);
        JLabel userNameLabel = new JLabel("Username:", JLabel.RIGHT);
        JLabel userPasswordLabel = new JLabel("Password:", JLabel.RIGHT);
        JLabel userEmailLabel = new JLabel("Email:", JLabel.RIGHT);
        
        // Text fields
        userIdField = new JTextField(15);
        userNameField = new JTextField(15);
        userPasswordField = new JTextField(15);
        userEmailField = new JTextField(15);
        
        // Buttons
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");
        
        // First row - labels and text fields in 4 columns
        userFormPanel.add(userIdLabel);
        userFormPanel.add(userIdField);
        userFormPanel.add(userNameLabel);
        userFormPanel.add(userNameField);
        
        // Second row - labels and text fields in 4 columns
        userFormPanel.add(userPasswordLabel);
        userFormPanel.add(userPasswordField);
        userFormPanel.add(userEmailLabel);
        userFormPanel.add(userEmailField);
        
        // Third row - empty and buttons
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(addUserButton);
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(editUserButton);
        
        // Fourth row - empty and delete button
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(deleteUserButton);
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(new JLabel(""));
        
        // Fifth row - empty for spacing
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(new JLabel(""));
        userFormPanel.add(new JLabel(""));
        
        userControlsPanel.add(userFormPanel, BorderLayout.CENTER);
        
        // Add components to user management panel
        panel.add(usersScrollPane, BorderLayout.CENTER);
        panel.add(userControlsPanel, BorderLayout.SOUTH);
        
        // Add user management button listeners
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        return panel;
    }
    
    // Login methods
    private void login() {
        String id = userField.getText();
        String password = new String(pwField.getPassword());
        
        // Frontend validation
        if (!AuthService.isValidAlphanumericInput(id)) {
            JOptionPane.showMessageDialog(this, "User ID must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!AuthService.isValidAlphanumericInput(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            currentUser = AuthService.authenticateUserCredentials(id, password);
            userField.setText("");
            pwField.setText("");
            updateUserPanel();
            cLayout.show(mainPanel, "user");
        } catch (AuthException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void register() {
        String name = JOptionPane.showInputDialog(this, "Enter username (letters and numbers only):");
        if (name == null || name.trim().isEmpty()) return;
        
        // Validate username
        if (!AuthService.isValidAlphanumericInput(name)) {
            JOptionPane.showMessageDialog(this, "Username must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = JOptionPane.showInputDialog(this, "Enter user ID (letters and numbers only):");
        if (id == null || id.trim().isEmpty()) return;
        
        // Validate ID
        if (!AuthService.isValidAlphanumericInput(id)) {
            JOptionPane.showMessageDialog(this, "User ID must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String password = JOptionPane.showInputDialog(this, "Enter password (letters and numbers only):");
        if (password == null || password.trim().isEmpty()) return;
        
        // Validate password
        if (!AuthService.isValidAlphanumericInput(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String email = JOptionPane.showInputDialog(this, "Enter email:");
        if (email == null || email.trim().isEmpty()) return;
        
        try {
            currentUser = AuthService.registerNewUserAccount(name, id, password, email);
            JOptionPane.showMessageDialog(this, "Registration successful!");
            updateUserPanel();
            cLayout.show(mainPanel, "user");
        } catch (AuthException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void adminLogin() {
        String id = userField.getText();
        String password = new String(pwField.getPassword());
        
        // Frontend validation
        if (!AuthService.isValidAlphanumericInput(id)) {
            JOptionPane.showMessageDialog(this, "Admin ID must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!AuthService.isValidAlphanumericInput(password)) {
            JOptionPane.showMessageDialog(this, "Password must contain only letters and numbers", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.out.println("GUI: Attempting admin login, ID=" + id);
        
        try {
            currMgr = AuthService.authenticateAdministratorCredentials(id, password);
            System.out.println("GUI: Admin login successful, admin name=" + currMgr.getName() + ", permission=" + currMgr.isJurisdiction());
            currentUser = null;
            userField.setText("");
            pwField.setText("");
            updateAdminPanel();
            cLayout.show(mainPanel, "admin");
        } catch (AuthException e) {
            System.out.println("GUI: Admin login failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Admin Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        currentUser = null;
        currMgr = null;
        cLayout.show(mainPanel, "login");
    }
    
    // User panel methods
    private void updateUserPanel() {
        // Clear tables
        activitiesTableModel.setRowCount(0);
        ticketsTableModel.setRowCount(0);
        
        // Load activities
        List<Activity> activities = tOp.getAvailableActivities();
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
            List<Ticket> tickets = tOp.getUserTickets(currentUser);
            
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
        commentArea.setText("");
    }
    
    private void displayActivityDetails() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String activityId = (String) activitiesTable.getValueAt(selectedRow, 0);
        Activity activity = tOp.getActivityDetails(activityId);
        
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
            
            // Display comments
            List<String> comments = tOp.getActivityComments(activity);
            if (comments.isEmpty()) {
                commentArea.setText("No comments yet.");
            } else {
                StringBuilder commentSb = new StringBuilder();
                for (String comment : comments) {
                    commentSb.append(comment).append("\n");
                }
                commentArea.setText(commentSb.toString());
            }
        }
    }
    
    private void buyTicket() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must log in to buy tickets");
            return;
        }
        
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity first");
            return;
        }
        
        String quantityString = quantityField.getText().trim();
        if (quantityString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the quantity");
            return;
        }
        
        try {
            int ticketQuantity = Integer.parseInt(quantityString);
            if (ticketQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero");
                return;
            }
            
            String activityIdentifier = (String) activitiesTable.getValueAt(selectedRow, 0);
            Activity selectedActivity = tOp.getActivityDetails(activityIdentifier);
            
            if (selectedActivity != null) {
                try {
                    tOp.purchaseTicket(currentUser, selectedActivity, ticketQuantity);
                    // This code won't execute because purchaseTicket throws an exception
                } catch (PaymentRequiredException e) {
                    // Show payment prompt when purchase is successful
                    JOptionPane.showMessageDialog(this, "Purchase successful! " + e.getMessage());
                    quantityField.setText("");
                    updateUserPanel();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number");
        }
    }
    
    private void cancelTicket() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must log in to cancel tickets");
            return;
        }
        
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the ticket to cancel");
            return;
        }
        
        List<Ticket> tickets = tOp.getUserTickets(currentUser);
        if (selectedRow >= tickets.size()) {
            JOptionPane.showMessageDialog(this, "Invalid ticket selection");
            return;
        }
        
        Ticket selectedTicket = tickets.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this ticket?\n" + selectedTicket,
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (tOp.cancelTicket(currentUser, selectedTicket)) {
                JOptionPane.showMessageDialog(this, "Ticket cancellation successful!");
                updateUserPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Ticket cancellation failed");
            }
        }
    }
    
    private void transferTicket() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must log in to transfer tickets");
            return;
        }
        
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the ticket to transfer");
            return;
        }
        
        List<Ticket> tickets = tOp.getUserTickets(currentUser);
        if (selectedRow >= tickets.size()) {
            JOptionPane.showMessageDialog(this, "Invalid ticket selection");
            return;
        }
        
        Ticket selectedTicket = tickets.get(selectedRow);
        
        // Ask for recipient ID
        String recipientId = JOptionPane.showInputDialog(this, "Enter the ID of the user receiving the ticket:");
        if (recipientId == null || recipientId.trim().isEmpty()) {
            return;
        }
        
        // Find recipient
        Customer recipient = null;
        for (Customer customer : Initialize.customers) {
            if (customer.getID().equals(recipientId)) {
                recipient = customer;
                break;
            }
        }
        
        if (recipient == null) {
            JOptionPane.showMessageDialog(this, "Recipient user not found");
            return;
        }
        
        // Confirm transfer
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to transfer this ticket to " + recipient.getName() + "?\n" + selectedTicket,
            "Confirm Transfer", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (tOp.transferTicket(currentUser, recipient, selectedTicket)) {
                JOptionPane.showMessageDialog(this, "Ticket transfer successful!");
                updateUserPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Ticket transfer failed");
            }
        }
    }
    
    private void addComment() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must log in to add comments");
            return;
        }
        
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the activity to comment on");
            return;
        }
        
        String activityId = (String) activitiesTable.getValueAt(selectedRow, 0);
        Activity activity = tOp.getActivityDetails(activityId);
        
        if (activity != null) {
            String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
            if (comment != null && !comment.trim().isEmpty()) {
                if (tOp.addActivityComment(currentUser, activity, comment)) {
                    JOptionPane.showMessageDialog(this, "Comment added successfully!");
                    displayActivityDetails(); // Refresh comments
                } else {
                    JOptionPane.showMessageDialog(this, "Comment addition failed");
                }
            }
        }
    }
    
    // Admin panel methods
    private void updateAdminPanel() {
        if (currMgr == null) {
            return;
        }
        
        // 更新活动表格
        allActivitiesTableModel.setRowCount(0);
        for (Activity a : Initialize.activities) {
            Object[] row = {
                a.getId(),
                a.getName(),
                new SimpleDateFormat("yyyy-MM-dd").format(a.getDate()),
                a.getVenue(),
                a.getPrice(),
                a.getRemainingTickets(),
                a.getTotalTickets()
            };
            allActivitiesTableModel.addRow(row);
        }
        
        // 更新用户表格
        usersTableModel.setRowCount(0);
        
        // 添加普通用户
        for (Customer customer : Initialize.customers) {
            Object[] row = {
                customer.getID(),
                customer.getName(),
                customer.getMail(),
                "No"
            };
            usersTableModel.addRow(row);
        }
        
        // 添加管理员
        for (Manager manager : Initialize.managers) {
            Object[] row = {
                manager.getID(),
                manager.getName(),
                manager.getMail(),
                "Yes"
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
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            
            Activity newActivity = currMgr.createActivity(id, name, date, venue, price, totalTickets);
            Initialize.activities.add(newActivity);
            Initialize.saveAllData();
            
            // 清空表单字段
            idField.setText("");
            nameField.setText("");
            dateField.setText("");
            venueField.setText("");
            priceField.setText("");
            totalTicketsField.setText("");
            
            JOptionPane.showMessageDialog(this, "Activity added successfully!");
            updateAdminPanel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Activity addition error: " + e.getMessage());
        }
    }
    
    private void cancelActivity() {
        int selectedRow = allActivitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the activity to cancel");
            return;
        }
        
        String activityId = (String) allActivitiesTable.getValueAt(selectedRow, 0);
        String activityName = (String) allActivitiesTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this activity?\nThis will completely delete it and remove all related tickets",
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (currMgr.cancelActivity(Initialize.activities, activityId)) {
                // 取消后保存所有数据
                Initialize.saveAllData();
                JOptionPane.showMessageDialog(this, "Activity cancellation successful! All related tickets have been removed");
                
                // 更新UI以反映变化
                updateAdminPanel();
                if (currentUser != null) {
                    updateUserPanel(); // 如果当前用户拥有此活动的票，也需要更新用户面板
                }
            } else {
                JOptionPane.showMessageDialog(this, "Activity cancellation failed");
            }
        }
    }
    
    private void updateActivityPrice(double newPrice) {
        int selectedRow = allActivitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the activity to update price");
            return;
        }
        
        if (newPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Price must be greater than zero");
            return;
        }
        
        String activityId = (String) allActivitiesTable.getValueAt(selectedRow, 0);
        
        if (currMgr.updateActivityPrice(Initialize.activities, activityId, newPrice)) {
            Initialize.saveAllData();
            JOptionPane.showMessageDialog(this, "Price update successful!");
            updateAdminPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Price update failed");
        }
    }
    
    private void generateStatistics() {
        if (currMgr == null) {
            return;
        }
        
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            if (!startDateField.getText().trim().isEmpty()) {
                startDate = dateFormat.parse(startDateField.getText().trim());
            }
            
            if (!endDateField.getText().trim().isEmpty()) {
                endDate = dateFormat.parse(endDateField.getText().trim());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD");
            return;
        }
        
        // Generate ticket statistics
        Map<String, Integer> ticketStats = currMgr.generateTicketStatistics(Initialize.tickets, startDate, endDate);
        double totalRevenue = currMgr.calculateTotalRevenue(Initialize.tickets, startDate, endDate);
        
        // Display results
        StringBuilder resultText = new StringBuilder();
        resultText.append("Ticket Statistics\n\n");
        
        String dateRange = "All time";
        if (startDate != null && endDate != null) {
            dateRange = dateFormat.format(startDate) + " to " + dateFormat.format(endDate);
        } else if (startDate != null) {
            dateRange = "From " + dateFormat.format(startDate);
        } else if (endDate != null) {
            dateRange = "Until " + dateFormat.format(endDate);
        }
        
        resultText.append("Date range: ").append(dateRange).append("\n\n");
        resultText.append("Ticket sales by activity:\n");
        
        int totalTickets = 0;
        for (Map.Entry<String, Integer> entry : ticketStats.entrySet()) {
            resultText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" tickets\n");
            totalTickets += entry.getValue();
        }
        
        resultText.append("\nTotal tickets sold: ").append(totalTickets).append("\n");
        resultText.append("Total revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        
        JTextArea textArea = new JTextArea(resultText.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Ticket Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editActivity() {
        if (currMgr == null) {
            return;
        }
        
        int selectedRow = allActivitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an activity to edit");
            return;
        }
        
        String activityId = (String) allActivitiesTable.getValueAt(selectedRow, 0);
        Activity activity = null;
        
        // Find the activity
        for (Activity a : Initialize.activities) {
            if (a.getId().equals(activityId)) {
                activity = a;
                break;
            }
        }
        
        if (activity == null) {
            JOptionPane.showMessageDialog(this, "Activity not found");
            return;
        }
        
        // Create a dialog to edit activity
        JDialog editDialog = new JDialog(this, "Edit Activity", true);
        editDialog.setLayout(new BorderLayout(10, 10));
        
        // Create form panel (using GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create form fields
        JLabel idLabel = new JLabel("ID:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JLabel venueLabel = new JLabel("Venue:");
        JLabel priceLabel = new JLabel("Price:");
        JLabel totalTicketsLabel = new JLabel("Total Tickets:");
        
        JTextField idField = new JTextField(activity.getId(), 20);
        idField.setEditable(false); // ID cannot be changed
        JTextField nameField = new JTextField(activity.getName(), 20);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(sdf.format(activity.getDate()), 20);
        JTextField venueField = new JTextField(activity.getVenue(), 20);
        JTextField priceField = new JTextField(String.valueOf(activity.getPrice()), 20);
        JTextField totalTicketsField = new JTextField(String.valueOf(activity.getTotalTickets()), 20);
        
        // Add components to form panel
        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(venueLabel);
        formPanel.add(venueField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(totalTicketsLabel);
        formPanel.add(totalTicketsField);
        
        // Create button panel (using FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    Date date = sdf.parse(dateField.getText().trim());
                    String venue = venueField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int totalTickets = Integer.parseInt(totalTicketsField.getText().trim());
                    
                    if (name.isEmpty() || venue.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "Name and venue cannot be empty");
                        return;
                    }
                    
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(editDialog, "Price must be greater than zero");
                        return;
                    }
                    
                    if (totalTickets <= 0) {
                        JOptionPane.showMessageDialog(editDialog, "Total tickets must be greater than zero");
                        return;
                    }
                    
                    // Update activity
                    if (currMgr.updateActivity(Initialize.activities, activityId, name, date, venue, price, totalTickets)) {
                        Initialize.saveAllData();
                        JOptionPane.showMessageDialog(editDialog, "Activity updated successfully!");
                        editDialog.dispose();
                        updateAdminPanel();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Failed to update activity");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Invalid input: " + ex.getMessage());
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });
        
        // Show dialog
        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }
    
    // User management methods
    private void addUser() {
        if (currMgr == null) {
            return;
        }
        
        String id = userIdField.getText().trim();
        String name = userNameField.getText().trim();
        String password = userPasswordField.getText().trim();
        String email = userEmailField.getText().trim();
        
        // Validate inputs
        if (id.isEmpty() || name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }
        
        if (!AuthService.isValidAlphanumericInput(id) || !AuthService.isValidAlphanumericInput(name)) {
            JOptionPane.showMessageDialog(this, "User ID and name must contain only letters and numbers");
            return;
        }
        
        // Check if ID already exists
        for (Customer customer : Initialize.customers) {
            if (customer.getID().equals(id)) {
                JOptionPane.showMessageDialog(this, "User ID already exists");
                return;
            }
        }
        
        for (Manager manager : Initialize.managers) {
            if (manager.getID().equals(id)) {
                JOptionPane.showMessageDialog(this, "User ID already exists");
                return;
            }
        }
        
        // Create new user dialog
        String[] options = {"Regular User", "Admin"};
        int choice = JOptionPane.showOptionDialog(
            this, 
            "Select user type", 
            "User Type", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0]
        );
        
        if (choice == 0) {
            // Create regular user
            Customer newCustomer = new Customer(name, id, password, email);
            Initialize.customers.add(newCustomer);
            JOptionPane.showMessageDialog(this, "User created successfully");
        } else if (choice == 1) {
            // Create admin
            Manager newManager = new Manager(name, id, password, email);
            Initialize.managers.add(newManager);
            JOptionPane.showMessageDialog(this, "Admin created successfully");
        }
        
        // Clear fields
        userIdField.setText("");
        userNameField.setText("");
        userPasswordField.setText("");
        userEmailField.setText("");
        
        // Save data and update
        Initialize.saveAllData();
        updateAdminPanel();
    }
    
    private void editUser() {
        if (currMgr == null) {
            return;
        }
        
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the user to edit");
            return;
        }
        
        String userId = (String) usersTable.getValueAt(selectedRow, 0);
        String isAdmin = (String) usersTable.getValueAt(selectedRow, 3);
        boolean userIsAdmin = isAdmin.equals("Yes");
        
        // Find the user
        if (userIsAdmin) {
            final Manager managerToEdit = findManagerById(userId);
            
            if (managerToEdit == null) {
                JOptionPane.showMessageDialog(this, "Manager not found");
                return;
            }
            
            // Cannot edit own account
            if (managerToEdit.getID().equals(currMgr.getID())) {
                JOptionPane.showMessageDialog(this, "Cannot edit your own account");
                return;
            }
            
            // Create a dialog to edit admin
            JDialog editDialog = new JDialog(this, "Edit Admin", true);
            editDialog.setLayout(new BorderLayout(10, 10));
            
            // Create form panel (using GridLayout)
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JLabel nameLabel = new JLabel("Name:");
            JLabel passwordLabel = new JLabel("Password:");
            JLabel emailLabel = new JLabel("Email:");
            
            JTextField editNameField = new JTextField(managerToEdit.getName(), 20);
            JTextField editPasswordField = new JTextField(managerToEdit.getPassWord(), 20);
            JTextField editEmailField = new JTextField(managerToEdit.getMail(), 20);
            
            formPanel.add(nameLabel);
            formPanel.add(editNameField);
            formPanel.add(passwordLabel);
            formPanel.add(editPasswordField);
            formPanel.add(emailLabel);
            formPanel.add(editEmailField);
            
            // Create button panel (using FlowLayout)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Add panels to dialog
            editDialog.add(formPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add button listeners
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newName = editNameField.getText().trim();
                    String newPassword = editPasswordField.getText().trim();
                    String newEmail = editEmailField.getText().trim();
                    
                    if (newName.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "All fields are required");
                        return;
                    }
                    
                    if (!AuthService.isValidAlphanumericInput(newName)) {
                        JOptionPane.showMessageDialog(editDialog, "Name must contain only letters and numbers");
                        return;
                    }
                    
                    // Update manager details
                    managerToEdit.setName(newName);
                    managerToEdit.setPassWord(newPassword);
                    managerToEdit.setMail(newEmail);
                    
                    Initialize.saveAllData();
                    JOptionPane.showMessageDialog(editDialog, "Admin update successful");
                    editDialog.dispose();
                    updateAdminPanel();
                }
            });
            
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editDialog.dispose();
                }
            });
            
            // Show dialog
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
            
        } else {
            final Customer customerToEdit = findCustomerById(userId);
            
            if (customerToEdit == null) {
                JOptionPane.showMessageDialog(this, "User not found");
                return;
            }
            
            // Create a dialog to edit user
            JDialog editDialog = new JDialog(this, "Edit User", true);
            editDialog.setLayout(new BorderLayout(10, 10));
            
            // Create form panel (using GridLayout)
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JLabel nameLabel = new JLabel("Name:");
            JLabel passwordLabel = new JLabel("Password:");
            JLabel emailLabel = new JLabel("Email:");
            
            JTextField editNameField = new JTextField(customerToEdit.getName(), 20);
            JTextField editPasswordField = new JTextField(customerToEdit.getPassWord(), 20);
            JTextField editEmailField = new JTextField(customerToEdit.getMail(), 20);
            
            formPanel.add(nameLabel);
            formPanel.add(editNameField);
            formPanel.add(passwordLabel);
            formPanel.add(editPasswordField);
            formPanel.add(emailLabel);
            formPanel.add(editEmailField);
            
            // Create button panel (using FlowLayout)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Add panels to dialog
            editDialog.add(formPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add button listeners
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newName = editNameField.getText().trim();
                    String newPassword = editPasswordField.getText().trim();
                    String newEmail = editEmailField.getText().trim();
                    
                    if (newName.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "All fields are required");
                        return;
                    }
                    
                    if (!AuthService.isValidAlphanumericInput(newName)) {
                        JOptionPane.showMessageDialog(editDialog, "Name must contain only letters and numbers");
                        return;
                    }
                    
                    // Update user details
                    customerToEdit.setName(newName);
                    customerToEdit.setPassWord(newPassword);
                    customerToEdit.setMail(newEmail);
                    
                    Initialize.saveAllData();
                    JOptionPane.showMessageDialog(editDialog, "User update successful");
                    editDialog.dispose();
                    updateAdminPanel();
                }
            });
            
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editDialog.dispose();
                }
            });
            
            // Show dialog
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }
    
    private void deleteUser() {
        if (currMgr == null) {
            return;
        }
        
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the user to delete");
            return;
        }
        
        String userId = (String) usersTable.getValueAt(selectedRow, 0);
        String userName = (String) usersTable.getValueAt(selectedRow, 1);
        String isAdmin = (String) usersTable.getValueAt(selectedRow, 3);
        boolean userIsAdmin = isAdmin.equals("Yes");
        
        String message = userIsAdmin ? 
            "Are you sure you want to delete admin \"" + userName + "\" (ID: " + userId + ")?" :
            "Are you sure you want to delete user \"" + userName + "\" (ID: " + userId + ")?\nAll tickets belonging to this user will also be deleted.";
            
        int confirm = JOptionPane.showConfirmDialog(this, 
            message + "\nThis action cannot be undone",
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (userIsAdmin) {
                final Manager managerToDelete = findManagerById(userId);
                
                if (managerToDelete == null) {
                    JOptionPane.showMessageDialog(this, "Manager not found");
                    return;
                }
                
                // Cannot delete own account
                if (managerToDelete.getID().equals(currMgr.getID())) {
                    JOptionPane.showMessageDialog(this, "Cannot delete your own account");
                    return;
                }
                
                // Delete manager
                if (currMgr.deleteManager(Initialize.managers, userId)) {
                    Initialize.saveAllData();
                    JOptionPane.showMessageDialog(this, "Admin \"" + userName + "\" deleted successfully");
                    updateAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete admin. There must be at least one admin in the system.");
                }
            } else {
                final Customer customerToDelete = findCustomerById(userId);
                
                if (customerToDelete == null) {
                    JOptionPane.showMessageDialog(this, "User not found");
                    return;
                }
                
                // Delete customer and their tickets
                if (currMgr.deleteCustomer(Initialize.customers, userId)) {
                    Initialize.saveAllData();
                    JOptionPane.showMessageDialog(this, "User \"" + userName + "\" and all associated tickets deleted successfully");
                    updateAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user");
                }
            }
        }
    }
    
    // Helper method: Find manager by ID
    private Manager findManagerById(String id) {
        for (Manager manager : Initialize.managers) {
            if (manager.getID().equals(id)) {
                return manager;
            }
        }
        return null;
    }
    
    // Helper method: Find user by ID
    private Customer findCustomerById(String id) {
        for (Customer customer : Initialize.customers) {
            if (customer.getID().equals(id)) {
                return customer;
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
        // Initialize data first
        Initialize.initialize();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
} 