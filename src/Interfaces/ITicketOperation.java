package Interfaces;

import DataBase.*;
import Exceptions.PaymentRequiredException;
import java.util.List;

public interface ITicketOperation {
    boolean purchaseTicket(Customer customer, Activity activity, int quantity) throws PaymentRequiredException;
    List<Activity> getAvailableActivities();
    List<Ticket> getUserTickets(Customer customer);
    boolean cancelTicket(Customer customer, Ticket ticket);
    Activity getActivityDetails(String activityId);
    
    // New methods for ticket transfer and activity comments
    boolean transferTicket(Customer fromCustomer, Customer toCustomer, Ticket ticket);
    boolean addActivityComment(Customer customer, Activity activity, String comment);
    List<String> getActivityComments(Activity activity);
}