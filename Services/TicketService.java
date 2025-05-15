package Services;

import DataBase.*;
import Interfaces.ITicketOperation;
import Exceptions.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class TicketService implements ITicketOperation {
    private List<Activity> activities;
    private List<Ticket> tickets;
    
    public TicketService(List<Activity> activities, List<Ticket> tickets) {
        this.activities = activities;
        this.tickets = tickets;
    }
    
    @Override
    public boolean purchaseTicket(Customer customer, Activity activity, int quantity) {
        if (customer == null) {
            return false;
        }
        
        if (!activities.contains(activity)) {
            return false;
        }
        
        if (quantity <= 0) {
            return false;
        }
        
        synchronized (activity) {
            if (!activity.purchaseTickets(quantity)) {
                return false;
            }
            
            // 生成票
            for (int i = 0; i < quantity; i++) {
                String seatNumber = generateSeatNumber(activity);
                Ticket ticket = new Ticket(
                    activity.getName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()),
                    seatNumber,
                    activity.getPrice()
                );
                ticket.setCustomerID(customer.getID());
                tickets.add(ticket);
                customer.addTicket(ticket);
            }
            return true;
        }
    }
    
    private String generateSeatNumber(Activity activity) {
        int sold = activity.getTotalTickets() - activity.getRemainingTickets();
        return "R" + (sold / 10 + 1) + "C" + (sold % 10 + 1);
    }
    
    @Override
    public List<Activity> getAvailableActivities() {
        return new ArrayList<>(activities);
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
        
        Activity activity = findActivityByName(ticket.getShowName());
        if (activity == null) {
            return false;
        }
        
        synchronized (activity) {
            if (activity.cancelTickets(1)) {
                tickets.remove(ticket);
                customer.removeTicket(ticket);
                return true;
            }
            return false;
        }
    }
    
    private Activity findActivityByName(String name) {
        for (Activity activity : activities) {
            if (activity.getName().equals(name)) {
                return activity;
            }
        }
        return null;
    }
    
    @Override
    public Activity getActivityDetails(String activityId) {
        for (Activity activity : activities) {
            if (activity.getId().equals(activityId)) {
                return activity;
            }
        }
        return null;
    }
}