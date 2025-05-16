package Services;

import DataBase.*;
import Interfaces.ITicketOperation;
import Exceptions.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class TicketService implements ITicketOperation {
    private List<Activity> availableActivities;
    private List<Ticket> ticketDatabase;
    private static final String COMMENT_FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "comments_%s.txt";
    
    public TicketService(List<Activity> availableActivities, List<Ticket> ticketDatabase) {
        this.availableActivities = availableActivities;
        this.ticketDatabase = ticketDatabase;
    }
    
    @Override
    public boolean purchaseTicket(Customer customer, Activity activity, int quantity) throws PaymentRequiredException {
        if (customer == null) {
            return false;
        }
        
        if (!availableActivities.contains(activity)) {
            return false;
        }
        
        if (quantity <= 0) {
            return false;
        }
        
        synchronized (activity) {
            if (!activity.purchaseTickets(quantity)) {
                return false;
            }
            
            // Generate tickets
            int startSeatNumber = activity.getTotalTickets() - activity.getRemainingTickets() - quantity + 1;
            
            for (int i = 0; i < quantity; i++) {
                String seatNumber = generateSeatNumberForActivity(startSeatNumber + i);
                Ticket ticket = new Ticket(
                    activity.getName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()),
                    seatNumber,
                    activity.getPrice()
                );
                ticket.setCustomerID(customer.getID());
                ticketDatabase.add(ticket);
                customer.addTicket(ticket);
            }
            throw new PaymentRequiredException("Please complete payment at the offline ticket counter");
        }
    }
    
    private String generateSeatNumberForActivity(int seatNumber) {
        // Row 1-10, Column 1-10 format
        int row = (seatNumber - 1) / 10 + 1;
        int column = (seatNumber - 1) % 10 + 1;
        return "Row" + row + "-Seat" + column;
    }
    
    @Override
    public List<Activity> getAvailableActivities() {
        return new ArrayList<>(availableActivities);
    }
    
    @Override
    public List<Ticket> getUserTickets(Customer customer) {
        return new ArrayList<>(customer.getTickets());
    }
    
    @Override
    public boolean cancelTicket(Customer customer, Ticket ticket) {
        if (!customer.getTickets().contains(ticket)) {
            return false;
        }
        
        Activity activity = findActivityByActivityName(ticket.getShowName());
        if (activity == null) {
            return false;
        }
        
        synchronized (activity) {
            if (activity.cancelTickets(1)) {
                ticketDatabase.remove(ticket);
                customer.removeTicket(ticket);
                return true;
            }
            return false;
        }
    }
    
    private Activity findActivityByActivityName(String activityName) {
        for (Activity activity : availableActivities) {
            if (activity.getName().equals(activityName)) {
                return activity;
            }
        }
        return null;
    }
    
    @Override
    public Activity getActivityDetails(String activityId) {
        for (Activity activity : availableActivities) {
            if (activity.getId().equals(activityId)) {
                return activity;
            }
        }
        return null;
    }
    
    @Override
    public boolean transferTicket(Customer fromCustomer, Customer toCustomer, Ticket ticket) {
        // Verify ticket ownership
        if (!fromCustomer.getTickets().contains(ticket)) {
            return false;
        }
        
        // Remove ticket from current owner
        fromCustomer.removeTicket(ticket);
        
        // Update ticket ownership
        ticket.recordTransfer(fromCustomer.getID(), toCustomer.getID());
        ticket.setCustomerID(toCustomer.getID());
        
        // Add ticket to new owner
        toCustomer.addTicket(ticket);
        
        return true;
    }
    
    @Override
    public boolean addActivityComment(Customer customer, Activity activity, String comment) {
        if (customer == null || activity == null || comment == null || comment.trim().isEmpty()) {
            return false;
        }
        
        // Add comment to activity object
        activity.addComment(customer.getName() + ": " + comment);
        
        // Save comment to file
        String commentFilePath = String.format(COMMENT_FILE_PATH, activity.getId());
        try (PrintWriter writer = new PrintWriter(new FileWriter(commentFilePath, true))) {
            writer.println(customer.getName() + "," + customer.getID() + "," + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "," + comment);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> getActivityComments(Activity activity) {
        if (activity == null) {
            return new ArrayList<>();
        }
        
        // Try to load comments from file
        List<String> comments = new ArrayList<>();
        String commentFilePath = String.format(COMMENT_FILE_PATH, activity.getId());
        File commentFile = new File(commentFilePath);
        
        if (commentFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(commentFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Format is: name,id,timestamp,comment
                    String[] parts = line.split(",", 4);
                    if (parts.length >= 4) {
                        comments.add(parts[0] + " (" + parts[2] + "): " + parts[3]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Combine with comments in the activity object
        comments.addAll(activity.getComments());
        return comments;
    }
}