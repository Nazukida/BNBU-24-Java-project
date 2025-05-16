package DataBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity {
    private String id;
    private String name;
    private Date date;
    private String venue;
    private double price;
    private int totalTickets;
    private int remainingTickets;
    private List<String> comments;
    
    public Activity(String id, String name, Date date, String venue, double price, int totalTickets) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.price = price;
        this.totalTickets = totalTickets;
        this.remainingTickets = totalTickets;
        this.comments = new ArrayList<>();
    }
    
    public Activity(String id, String name, Date date, String venue, double price, int totalTickets, int remainingTickets) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.price = price;
        this.totalTickets = totalTickets;
        this.remainingTickets = remainingTickets;
        this.comments = new ArrayList<>();
    }
    
    public synchronized boolean purchaseTickets(int quantity) {
        if (remainingTickets >= quantity) {
            remainingTickets -= quantity;
            return true;
        }
        return false;
    }
    
    public synchronized boolean cancelTickets(int quantity) {
        if (remainingTickets + quantity <= totalTickets) {
            remainingTickets += quantity;
            return true;
        }
        return false;
    }
    
    // Comment functionality
    public void addComment(String comment) {
        comments.add(comment);
    }
    
    public List<String> getComments() {
        return comments;
    }
    
    public String getName() { return name; }
    public Date getDate() { return date; }
    public String getVenue() { return venue; }
    public double getPrice() { return price; }
    public int getRemainingTickets() { return remainingTickets; }
    
    // Setters for all properties
    public void setName(String name) { this.name = name; }
    public void setDate(Date date) { this.date = date; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setPrice(double price) { this.price = price; }
    
    public void setRemainingTickets(int remainingTickets) {
        this.remainingTickets = remainingTickets;
    }
    
    public void setTotalTickets(int totalTickets) {
        // Ensure we're not reducing below the number of sold tickets
        int soldTickets = this.totalTickets - this.remainingTickets;
        if (totalTickets < soldTickets) {
            // Cannot reduce total tickets below sold tickets count
            return;
        }
        
        this.remainingTickets = totalTickets - soldTickets;
        this.totalTickets = totalTickets;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("%s - %s (%s) Price: %.2f Tickets: %d/%d", 
                name, venue, sdf.format(date), price, remainingTickets, totalTickets);
    }
    public String getId() { return id; }
    public int getTotalTickets() { return totalTickets; }
}