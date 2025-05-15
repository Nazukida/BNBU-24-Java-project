package DataBase;

public class Ticket {
    private String showName;
    private String showDate;
    private String seatNumber;
    private double price;
    private String customerID;
    
    public Ticket(String showName, String showDate, String seatNumber, double price) {
        this.showName = showName;
        this.showDate = showDate;
        this.seatNumber = seatNumber;
        this.price = price;
        this.customerID = "";
    }
    
    public Ticket(String showName, String showDate, String seatNumber, double price, String customerID) {
        this.showName = showName;
        this.showDate = showDate;
        this.seatNumber = seatNumber;
        this.price = price;
        this.customerID = customerID;
    }

    // Getters
    public String getShowName() { return showName; }
    public String getShowDate() { return showDate; }
    public String getSeatNumber() { return seatNumber; }
    public double getPrice() { return price; }
    public String getCustomerID() { return customerID; }
    
    // Setters
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    @Override
    public String toString() {
        return String.format("Show: %s, Date: %s, Seat: %s, Price: %.2f", 
            showName, showDate, seatNumber, price);
    }
}