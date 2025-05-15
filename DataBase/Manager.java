package DataBase;

import java.util.Date;
import java.util.List;

public class Manager extends User {
    public Manager(String name, String ID, String passWord, String mail) {
        super(name, ID, passWord, mail);
        this.jurisdiction = true;
        System.out.println("Created admin: " + name + ", ID=" + ID + ", jurisdiction=" + this.jurisdiction);
    }

    @Override
    public boolean login(String ID, String passWord) {
        boolean result = this.ID.equals(ID) && this.passWord.equals(passWord);
        System.out.println("Admin login verification: ID=" + ID + ", match result=" + result + ", permission=" + this.jurisdiction);
        return result;
    }
    
    // Admin specific feature - Create activity
    public Activity createActivity(String id, String name, Date date, String venue, double price, int totalTickets) {
        return new Activity(id, name, date, venue, price, totalTickets);
    }
    
    // Admin specific feature - Cancel activity
    public boolean cancelActivity(List<Activity> activities, String activityId) {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getId().equals(activityId)) {
                activities.remove(i);
                return true;
            }
        }
        return false;
    }
    
    // Admin specific feature - Update activity price
    public boolean updateActivityPrice(List<Activity> activities, String activityId, double newPrice) {
        for (Activity activity : activities) {
            if (activity.getId().equals(activityId)) {
                activity.setPrice(newPrice);
                return true;
            }
        }
        return false;
    }
}
