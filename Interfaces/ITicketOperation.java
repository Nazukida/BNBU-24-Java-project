package Interfaces;

import DataBase.*;
import java.util.List;

public interface ITicketOperation {
    boolean purchaseTicket(Customer customer, Activity activity, int quantity);
    List<Activity> getAvailableActivities();
    List<Ticket> getUserTickets(Customer customer);
    boolean cancelTicket(Customer customer, Ticket ticket);
    Activity getActivityDetails(String activityId);
}