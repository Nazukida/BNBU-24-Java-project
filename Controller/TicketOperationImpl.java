package Controller;

import DataBase.*;
import Interfaces.ITicketOperation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TicketOperationImpl implements ITicketOperation {
    @Override
    public boolean purchaseTicket(Customer customer, Activity activity, int quantity) {
        if (activity.getRemainingTickets() >= quantity) {
            if (activity.purchaseTickets(quantity)) {
                // 为每个票创建Ticket对象并添加到客户和系统
                for (int i = 0; i < quantity; i++) {
                    String seatNumber = generateSeatNumber(activity);
                    Ticket ticket = new Ticket(
                        activity.getName(),
                        new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()),
                        seatNumber,
                        activity.getPrice()
                    );
                    ticket.setCustomerID(customer.getID());
                    customer.addTicket(ticket);
                    Initialize.tickets.add(ticket);
                }
                Initialize.saveAllData();
                return true;
            }
        }
        return false;
    }

    private String generateSeatNumber(Activity activity) {
        // 简单的座位号生成逻辑
        int seatNum = activity.getTotalTickets() - activity.getRemainingTickets() + 1;
        return "S" + seatNum;
    }

    @Override
    public List<Activity> getAvailableActivities() {
        return Initialize.activities;
    }

    @Override
    public List<Ticket> getUserTickets(Customer customer) {
        return customer.getTickets();
    }

    @Override
    public boolean cancelTicket(Customer customer, Ticket ticket) {
        // 找到对应的活动
        for (Activity activity : Initialize.activities) {
            if (activity.getName().equals(ticket.getShowName())) {
                if (customer.getTickets().remove(ticket) && Initialize.tickets.remove(ticket)) {
                    activity.cancelTickets(1);
                    Initialize.saveAllData();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Activity getActivityDetails(String activityId) {
        for (Activity activity : Initialize.activities) {
            if (activity.getId().equals(activityId)) {
                return activity;
            }
        }
        return null;
    }
}