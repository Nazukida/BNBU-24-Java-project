package DataBase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Manager extends User {
    public Manager(String name, String ID, String passWord, String mail) {
        super(name, ID, passWord, mail);
        this.jurisdiction = true;
        System.out.println("Created admin: " + name + ", ID=" + ID + ", jurisdiction=" + this.jurisdiction);
    }

    @Override
    public boolean login(String ID, String passWord) {
        boolean result = this.ID.equals(ID) && this.passWord.equals(passWord);
        System.out.println("Admin login verification: ID=" + ID + ", match result=" + result + ", permission=" + this.jurisdiction);
        return result;
    }
    
    // Admin specific feature - Create activity
    public Activity createActivity(String id, String name, Date date, String venue, double price, int totalTickets) {
        return new Activity(id, name, date, venue, price, totalTickets);
    }
    
    // Admin specific feature - Cancel activity
    public boolean cancelActivity(List<Activity> activities, String activityId) {
        for (int i = 0; i < activities.size(); i++) {
            Activity activityToRemove = activities.get(i);
            if (activityToRemove.getId().equals(activityId)) {
                // Get the activity name to match with tickets
                String activityName = activityToRemove.getName();
                
                // Remove all tickets associated with this activity
                removeActivityTickets(activityName);
                
                // Remove the activity
                activities.remove(i);
                return true;
            }
        }
        return false;
    }
    
    // Helper method to remove all tickets for a canceled activity
    private void removeActivityTickets(String activityName) {
        // Create a list to store tickets to be removed
        List<Ticket> ticketsToRemove = new ArrayList<>();
        
        // Find all tickets for this activity
        for (Ticket ticket : Controller.Initialize.tickets) {
            if (ticket.getShowName().equals(activityName)) {
                ticketsToRemove.add(ticket);
                
                // Also remove the ticket from the customer's tickets list
                for (Customer customer : Controller.Initialize.customers) {
                    if (customer.getID().equals(ticket.getCustomerID())) {
                        customer.removeTicket(ticket);
                        break;
                    }
                }
            }
        }
        
        // Remove all identified tickets
        Controller.Initialize.tickets.removeAll(ticketsToRemove);
        
        System.out.println("Removed " + ticketsToRemove.size() + " tickets for canceled activity: " + activityName);
    }
    
    // Admin specific feature - Update activity price
    public boolean updateActivityPrice(List<Activity> activities, String activityId, double newPrice) {
        for (Activity activity : activities) {
            if (activity.getId().equals(activityId)) {
                activity.setPrice(newPrice);
                return true;
            }
        }
        return false;
    }
    
    // Admin specific feature - Update activity details
    public boolean updateActivity(List<Activity> activities, String activityId, String name, 
                                Date date, String venue, double price, int totalTickets) {
        for (Activity activity : activities) {
            if (activity.getId().equals(activityId)) {
                activity.setName(name);
                activity.setDate(date);
                activity.setVenue(venue);
                activity.setPrice(price);
                activity.setTotalTickets(totalTickets);
                return true;
            }
        }
        return false;
    }
    
    // Admin specific feature - Delete customer
    public boolean deleteCustomer(List<Customer> customers, String customerId) {
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            if (customer.getID().equals(customerId)) {
                // First, remove all tickets associated with this customer
                List<Ticket> ticketsToRemove = new ArrayList<>();
                
                for (Ticket ticket : Controller.Initialize.tickets) {
                    if (ticket.getCustomerID().equals(customerId)) {
                        ticketsToRemove.add(ticket);
                    }
                }
                
                // Remove tickets from the system
                Controller.Initialize.tickets.removeAll(ticketsToRemove);
                
                // Remove the customer
                customers.remove(i);
                System.out.println("Customer deleted successfully: " + customerId);
                return true;
            }
        }
        System.out.println("Customer not found for deletion: " + customerId);
        return false;
    }
    
    // Admin specific feature - Delete manager
    public boolean deleteManager(List<Manager> managers, String managerId) {
        // Prevent deleting the last manager
        if (managers.size() <= 1) {
            System.out.println("Cannot delete the last manager");
            return false;
        }
        
        // Prevent self-deletion
        if (this.ID.equals(managerId)) {
            System.out.println("Cannot delete own account");
            return false;
        }
        
        for (int i = 0; i < managers.size(); i++) {
            Manager manager = managers.get(i);
            if (manager.getID().equals(managerId)) {
                managers.remove(i);
                System.out.println("Manager deleted successfully: " + managerId);
                return true;
            }
        }
        System.out.println("Manager not found for deletion: " + managerId);
        return false;
    }
    
    // Admin specific feature - Generate ticket statistics 
    public Map<String, Integer> generateTicketStatistics(List<Ticket> tickets, Date startDate, Date endDate) {
        Map<String, Integer> activityTicketCounts = new HashMap<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Ticket ticket : tickets) {
            try {
                Date ticketDate = dateFormat.parse(ticket.getShowDate());
                
                // Check if ticket date is in the specified range
                if ((startDate == null || !ticketDate.before(startDate)) && 
                    (endDate == null || !ticketDate.after(endDate))) {
                    
                    String activityName = ticket.getShowName();
                    activityTicketCounts.put(activityName, 
                        activityTicketCounts.getOrDefault(activityName, 0) + 1);
                }
            } catch (Exception e) {
                System.out.println("Error parsing date: " + ticket.getShowDate());
            }
        }
        
        return activityTicketCounts;
    }
    
    // Generate total revenue from ticket sales
    public double calculateTotalRevenue(List<Ticket> tickets, Date startDate, Date endDate) {
        double totalRevenue = 0.0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Ticket ticket : tickets) {
            try {
                Date ticketDate = dateFormat.parse(ticket.getShowDate());
                
                // Check if ticket date is in the specified range
                if ((startDate == null || !ticketDate.before(startDate)) && 
                    (endDate == null || !ticketDate.after(endDate))) {
                    totalRevenue += ticket.getPrice();
                }
            } catch (Exception e) {
                System.out.println("Error parsing date: " + ticket.getShowDate());
            }
        }
        
        return totalRevenue;
    }
}
