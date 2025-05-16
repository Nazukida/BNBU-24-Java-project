package DataBase;

public class Ticket {
    private String showName;
    private String showDate;
    private String seatNumber;
    private double price;
    private String customerID;
    private String transferHistory; // To track ticket transfers
    
    public Ticket(String showName, String showDate, String seatNumber, double price) {
        this.showName = showName;
        this.showDate = showDate;
        this.seatNumber = seatNumber;
        this.price = price;
        this.customerID = "";
        this.transferHistory = "";
    }
    
    public Ticket(String showName, String showDate, String seatNumber, double price, String customerID) {
        this.showName = showName;
        this.showDate = showDate;
        this.seatNumber = seatNumber;
        this.price = price;
        this.customerID = customerID;
        this.transferHistory = "";
    }

    // Getters
    public String getShowName() { return showName; }
    public String getShowDate() { return showDate; }
    public String getSeatNumber() { return seatNumber; }
    public double getPrice() { return price; }
    public String getCustomerID() { return customerID; }
    public String getTransferHistory() { return transferHistory; }
    
    // Setters
    public void setCustomerID(String customerID) { this.customerID = customerID; }
    
    // Method to record ticket transfer
    public void recordTransfer(String fromCustomerId, String toCustomerId) {
        String transferRecord = fromCustomerId + " -> " + toCustomerId + " | ";
        this.transferHistory += transferRecord;
    }

    @Override
    public String toString() {
        return String.format("Show: %s, Date: %s, Seat: %s, Price: %.2f", 
            showName, showDate, seatNumber, price);
    }
}