package DataBase;
import java.util.*;

public class Customer extends User {
    private ArrayList<Ticket> tickets;

    public Customer(String name, String ID, String passWord, String mail) {
        super(name, ID, passWord, mail);
        this.tickets = new ArrayList<>();
    }

    public void setJurisdiction(boolean jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    @Override
    public boolean login(String ID, String passWord) {
        return this.ID.equals(ID) && this.passWord.equals(passWord);
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }
}
