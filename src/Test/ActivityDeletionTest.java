package Test;

import Controller.Initialize;
import DataBase.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityDeletionTest {
    private List<Activity> activities;
    private List<Ticket> tickets;
    private List<Customer> customers;
    private Manager manager;
    private Activity testActivity;
    private Customer testCustomer;

    public void setUp() {
        // Initialize test data
        activities = new ArrayList<>();
        tickets = new ArrayList<>();
        customers = new ArrayList<>();
        
        // Create a test manager
        manager = new Manager("Test Manager", "manager1", "password", "manager@test.com");
        
        // Create a test customer
        testCustomer = new Customer("Test Customer", "customer1", "password", "customer@test.com");
        customers.add(testCustomer);
        
        // Create a test activity
        testActivity = new Activity("test1", "Test Activity", new Date(), "Test Venue", 100.0, 100);
        activities.add(testActivity);
        
        // Create test tickets for the activity
        for (int i = 0; i < 5; i++) {
            Ticket ticket = new Ticket(
                testActivity.getName(),
                "2025-10-10",
                "Row1-Seat" + (i + 1),
                testActivity.getPrice(),
                testCustomer.getID()
            );
            tickets.add(ticket);
            testCustomer.addTicket(ticket);
        }
        
        // Set up Initialize static lists for the test
        Initialize.activities = activities;
        Initialize.tickets = tickets;
        Initialize.customers = customers;
    }
    
    public void testCancelActivityRemovesAssociatedTickets() {
        // Verify initial state
        System.out.println("Initial state:");
        System.out.println("Activities count: " + activities.size() + " (expected 1)");
        System.out.println("Tickets count: " + tickets.size() + " (expected 5)");
        System.out.println("Customer tickets count: " + testCustomer.getTickets().size() + " (expected 5)");
        
        // Cancel the activity
        boolean result = manager.cancelActivity(activities, "test1");
        
        // Verify the activity was removed
        System.out.println("\nAfter cancellation:");
        System.out.println("Cancellation result: " + result + " (expected true)");
        System.out.println("Activities count: " + activities.size() + " (expected 0)");
        System.out.println("Tickets count: " + tickets.size() + " (expected 0)");
        System.out.println("Customer tickets count: " + testCustomer.getTickets().size() + " (expected 0)");
        
        // Show test result
        boolean testPassed = result && activities.size() == 0 && tickets.size() == 0 && testCustomer.getTickets().size() == 0;
        System.out.println("\nTest " + (testPassed ? "PASSED" : "FAILED"));
    }
    
    public static void main(String[] args) {
        ActivityDeletionTest test = new ActivityDeletionTest();
        test.setUp();
        test.testCancelActivityRemovesAssociatedTickets();
    }
} 