//test for the method

package Main;

import Controller.*;
import DataBase.*;
import Exceptions.AuthException;
import Exceptions.PaymentRequiredException;
import Interfaces.ITicketOperation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class test {
    private static Scanner scanner = new Scanner(System.in);
    private static ITicketOperation ticketOperation = new TicketOperationImpl();
    private static Customer currentUser = null;
    private static Manager currentManager = null;

    public static void main(String[] args) {
        Initialize.initialize();
        
        while (true) {
            if (currentUser == null && currentManager == null) {
                showMainMenu();
            } else if (currentManager != null) {
                showManagerMenu();
            } else {
                showCustomerMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Ticket Booking System Main Menu ===");
        System.out.println("1. User Login");
        System.out.println("2. User Registration");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit System");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                userLogin();
                break;
            case 2:
                userRegister();
                break;
            case 3:
                managerLogin();
                break;
            case 4:
                System.out.println("Thank you for using the system. Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice, please try again.");
        }
    }

    private static void userLogin() {
        System.out.print("Enter user ID: ");
        String ID = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        try {
            currentUser = AuthService.login(ID, password);
            System.out.println("Login successful, welcome " + currentUser.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void managerLogin() {
        System.out.print("Enter admin ID: ");
        String ID = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        try {
            currentManager = AuthService.loginAsManager(ID, password);
            System.out.println("Admin login successful, welcome " + currentManager.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void userRegister() {
        System.out.print("Enter username: ");
        String name = scanner.nextLine();
        System.out.print("Enter user ID: ");
        String ID = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String mail = scanner.nextLine();
        
        try {
            Customer newCustomer = AuthService.register(name, ID, password, mail);
            System.out.println("Registration successful! Welcome " + newCustomer.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void showCustomerMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. View Available Activities");
        System.out.println("2. View Activity Details");
        System.out.println("3. Buy Tickets");
        System.out.println("4. View My Tickets");
        System.out.println("5. Cancel Ticket");
        System.out.println("6. Logout");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                viewAvailableActivities();
                break;
            case 2:
                viewActivityDetails();
                break;
            case 3:
                purchaseTicket();
                break;
            case 4:
                viewMyTickets();
                break;
            case 5:
                cancelTicket();
                break;
            case 6:
                currentUser = null;
                System.out.println("Logged out successfully");
                break;
            default:
                System.out.println("Invalid choice, please try again.");
        }
    }

    private static void viewAvailableActivities() {
        List<Activity> activities = ticketOperation.getAvailableActivities();
        if (activities.isEmpty()) {
            System.out.println("No available activities at the moment.");
        } else {
            System.out.println("\n=== Available Activities ===");
            for (int i = 0; i < activities.size(); i++) {
                System.out.println((i + 1) + ". " + activities.get(i));
            }
        }
    }

    private static void viewActivityDetails() {
        List<Activity> activities = ticketOperation.getAvailableActivities();
        if (activities.isEmpty()) {
            System.out.println("No available activities at the moment.");
            return;
        }
        
        viewAvailableActivities();
        System.out.print("Enter activity number to view details: ");
        int activityIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline
        
        if (activityIndex < 0 || activityIndex >= activities.size()) {
            System.out.println("Invalid activity number.");
            return;
        }
        
        Activity selectedActivity = activities.get(activityIndex);
        System.out.println("\n=== Activity Details ===");
        System.out.println("ID: " + selectedActivity.getId());
        System.out.println("Name: " + selectedActivity.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Date: " + sdf.format(selectedActivity.getDate()));
        System.out.println("Venue: " + selectedActivity.getVenue());
        System.out.println("Price: " + selectedActivity.getPrice());
        System.out.println("Available tickets: " + selectedActivity.getRemainingTickets() + "/" + selectedActivity.getTotalTickets());
    }

    private static void purchaseTicket() {
        List<Activity> acts = ticketOperation.getAvailableActivities();
        if (acts.isEmpty()) {
            System.out.println("无活动可购票");
            return;
        }
        
        viewAvailableActivities();
        System.out.print("输入活动编号: ");
        int idx = scanner.nextInt() - 1;
        scanner.nextLine(); // 消耗换行符
        
        if (idx < 0 || idx >= acts.size()) {
            System.out.println("无效的活动编号");
            return;
        }
        
        Activity act = acts.get(idx);
        System.out.print("输入票数: ");
        int qty = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符
        
        try {
            ticketOperation.purchaseTicket(currentUser, act, qty);
            // 如果没有异常被抛出（这不应该发生），显示默认消息
            System.out.println("购票成功!");
        } catch (PaymentRequiredException e) {
            // 捕获并显示付款提示消息
            System.out.println("购票成功! " + e.getMessage());
        }
    }

    private static void viewMyTickets() {
        List<Ticket> tickets = ticketOperation.getUserTickets(currentUser);
        if (tickets.isEmpty()) {
            System.out.println("You currently have no tickets.");
            return;
        }
        
        System.out.println("\n=== My Tickets ===");
        for (int i = 0; i < tickets.size(); i++) {
            System.out.println((i + 1) + ". " + tickets.get(i));
        }
    }

    private static void cancelTicket() {
        List<Ticket> tickets = ticketOperation.getUserTickets(currentUser);
        if (tickets.isEmpty()) {
            System.out.println("You currently have no tickets to cancel.");
            return;
        }
        
        System.out.println("\n=== My Tickets ===");
        for (int i = 0; i < tickets.size(); i++) {
            System.out.println((i + 1) + ". " + tickets.get(i));
        }
        
        System.out.print("Enter ticket number to cancel: ");
        int ticketIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline
        
        if (ticketIndex < 0 || ticketIndex >= tickets.size()) {
            System.out.println("Invalid ticket number.");
            return;
        }
        
        Ticket selectedTicket = tickets.get(ticketIndex);
        if (ticketOperation.cancelTicket(currentUser, selectedTicket)) {
            System.out.println("Ticket cancellation successful!");
        } else {
            System.out.println("Ticket cancellation failed.");
        }
    }

    private static void showManagerMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Add Activity");
        System.out.println("2. View All Activities");
        System.out.println("3. View All Users");
        System.out.println("4. Logout");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                addActivity();
                break;
            case 2:
                viewAllActivities();
                break;
            case 3:
                viewAllUsers();
                break;
            case 4:
                currentManager = null;
                System.out.println("Logged out successfully");
                break;
            default:
                System.out.println("Invalid choice, please try again.");
        }
    }

    private static void addActivity() {
        System.out.print("Enter activity ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter activity name: ");
        String name = scanner.nextLine();
        System.out.print("Enter activity date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter activity venue: ");
        String venue = scanner.nextLine();
        System.out.print("Enter ticket price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter total tickets: ");
        int totalTickets = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            Activity newActivity = new Activity(id, name, date, venue, price, totalTickets);
            Initialize.activities.add(newActivity);
            Initialize.saveAllData();
            System.out.println("Activity added successfully!");
        } catch (Exception e) {
            System.out.println("Activity addition failed: " + e.getMessage());
        }
    }

    private static void viewAllActivities() {
        List<Activity> activities = Initialize.activities;
        if (activities.isEmpty()) {
            System.out.println("No activities at the moment.");
        } else {
            System.out.println("\n=== All Activities ===");
            for (Activity activity : activities) {
                System.out.println(activity);
            }
        }
    }

    private static void viewAllUsers() {
        List<Customer> customers = Initialize.customers;
        if (customers.isEmpty()) {
            System.out.println("No users at the moment.");
        } else {
            System.out.println("\n=== All Users ===");
            for (Customer customer : customers) {
                System.out.println("ID: " + customer.getID() + ", Name: " + customer.getName() + 
                    ", Email: " + customer.getMail() + ", Jurisdiction: " + (customer.isJurisdiction() ? "Admin" : "Normal User"));
            }
        }
    }
} 