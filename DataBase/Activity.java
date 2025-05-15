package DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity {
    private String id;
    private String name;
    private Date date;
    private String venue;
    private double price;
    private int totalTickets;
    private int remainingTickets;
    
    public Activity(String id, String name, Date date, String venue, double price, int totalTickets) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.price = price;
        this.totalTickets = totalTickets;
        this.remainingTickets = totalTickets;
    }
    
    public Activity(String id, String name, Date date, String venue, double price, int totalTickets, int remainingTickets) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.price = price;
        this.totalTickets = totalTickets;
        this.remainingTickets = remainingTickets;
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
    
    public String getName() { return name; }
    public Date getDate() { return date; }
    public String getVenue() { return venue; }
    public double getPrice() { return price; }
    public int getRemainingTickets() { return remainingTickets; }
    
    public void setPrice(double price) { this.price = price; }
    
    public void setRemainingTickets(int remainingTickets) {
        this.remainingTickets = remainingTickets;
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