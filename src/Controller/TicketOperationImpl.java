package Controller;

import DataBase.*;
import Exceptions.*;
import Interfaces.ITicketOperation;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TicketOperationImpl implements ITicketOperation {
    private static final String COMMENT_FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "comments_%s.txt";
    
    @Override
    public boolean purchaseTicket(Customer customerInformation, Activity activitySelected, int ticketQuantity) throws PaymentRequiredException {
        if (activitySelected.getRemainingTickets() >= ticketQuantity) {
            if (activitySelected.purchaseTickets(ticketQuantity)) {
                // Create Ticket objects for each ticket and add to customer and system
                int startSeatNumber = activitySelected.getTotalTickets() - activitySelected.getRemainingTickets() - ticketQuantity + 1;
                
                for (int i = 0; i < ticketQuantity; i++) {
                    String seatAssignment = generateSeatAssignmentNumber(startSeatNumber + i);
                    Ticket ticketCreated = new Ticket(
                        activitySelected.getName(),
                        new SimpleDateFormat("yyyy-MM-dd").format(activitySelected.getDate()),
                        seatAssignment,
                        activitySelected.getPrice()
                    );
                    ticketCreated.setCustomerID(customerInformation.getID());
                    customerInformation.addTicket(ticketCreated);
                    Initialize.tickets.add(ticketCreated);
                }
                Initialize.saveAllData();
                throw new PaymentRequiredException("Please complete payment at the offline ticket counter");
            }
        }
        return false;
    }

    private String generateSeatAssignmentNumber(int seatNumber) {
        // Row 1-10, Column 1-10 format
        int row = (seatNumber - 1) / 10 + 1;
        int column = (seatNumber - 1) % 10 + 1;
        return "Row" + row + "-Seat" + column;
    }

    @Override
    public List<Activity> getAvailableActivities() {
        return Initialize.activities;
    }

    @Override
    public List<Ticket> getUserTickets(Customer customerInformation) {
        return customerInformation.getTickets();
    }

    @Override
    public boolean cancelTicket(Customer customerInformation, Ticket ticketToCancel) {
        // Find the corresponding activity
        for (Activity activityInformation : Initialize.activities) {
            if (activityInformation.getName().equals(ticketToCancel.getShowName())) {
                if (customerInformation.getTickets().remove(ticketToCancel) && Initialize.tickets.remove(ticketToCancel)) {
                    activityInformation.cancelTickets(1);
                    Initialize.saveAllData();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Activity getActivityDetails(String activityIdentifier) {
        for (Activity activityInformation : Initialize.activities) {
            if (activityInformation.getId().equals(activityIdentifier)) {
                return activityInformation;
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
        
        // Save changes
        Initialize.saveAllData();
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