package Controller;

import DataBase.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Initialize {
    private static final String PROJECT_PATH = System.getProperty("user.dir") + File.separator + "src";
    private static final String CUSTOMER_DATA_FILE = "customer_data.txt";
    private static final String TICKET_DATA_FILE = "ticket_data.txt";
    private static final String ACTIVITY_DATA_FILE = "activity_data.txt";
    private static final String MANAGER_DATA_FILE = "manager_data.txt";

    public static List<Customer> customers = new ArrayList<>();
    public static List<Activity> activities = new ArrayList<>();
    public static List<Ticket> tickets = new ArrayList<>();
    public static List<Manager> managers = new ArrayList<>();

    public static void initialize() {
        System.out.println("Starting system initialization...");
        loadCustomers();
        loadManagers();
        loadActivities();
        loadTickets();
        System.out.println("Initialization complete, loaded admin count: " + managers.size());
        for (Manager manager : managers) {
            System.out.println("Loaded admin: ID=" + manager.getID() + ", Name=" + manager.getName());
        }
    }

    private static void loadCustomers() {
        String filePath = PROJECT_PATH + File.separator + CUSTOMER_DATA_FILE;
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            createCustomerFile(filePath);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Customer customer = new Customer(parts[0], parts[1], parts[2], parts[4]);
                    customer.setJurisdiction(Boolean.parseBoolean(parts[3]));
                    customers.add(customer);
                }
            }
            System.out.println("Customer data loaded successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadManagers() {
        String filePath = PROJECT_PATH + File.separator + MANAGER_DATA_FILE;
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            System.out.println("Admin data file does not exist, creating new file...");
            createManagerFile(filePath);
        }
        System.out.println("Loading admin data, file path: " + filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                System.out.println("Reading admin data line: " + line);
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    System.out.println("Creating admin: Name=" + parts[0] + ", ID=" + parts[1]);
                    Manager manager = new Manager(parts[0], parts[1], parts[2], parts[3]);
                    managers.add(manager);
                }
            }
            System.out.println("Admin data loaded successfully: " + filePath + ", loaded " + managers.size() + " admins");
        } catch (IOException e) {
            System.out.println("Error loading admin data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createCustomerFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("name,ID,password,jurisdiction,mail");
            bw.newLine();
            bw.write("user1,user1,password1,false,user1@example.com");
            bw.newLine();
            System.out.println("Customer data file created successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createManagerFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("name,ID,password,mail");
            bw.newLine();
            bw.write("nazuki,1145,10086,12345@qq.com");
            bw.newLine();
            bw.write("admin,admin,admin,admin@system.com");
            bw.newLine();
            System.out.println("Manager data file created successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadActivities() {
        String filePath = PROJECT_PATH + File.separator + ACTIVITY_DATA_FILE;
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            createActivityFile(filePath);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Activity activity = new Activity(
                        parts[0], parts[1], sdf.parse(parts[2]), parts[3], 
                        Double.parseDouble(parts[4]), Integer.parseInt(parts[5]), 
                        Integer.parseInt(parts[6])
                    );
                    activities.add(activity);
                } else if (parts.length >= 6) {
                    Activity activity = new Activity(
                        parts[0], parts[1], sdf.parse(parts[2]), parts[3], 
                        Double.parseDouble(parts[4]), Integer.parseInt(parts[5])
                    );
                    activities.add(activity);
                }
            }
            System.out.println("Activity data loaded successfully: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createActivityFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,name,date,venue,price,totalTickets,remainingTickets");
            bw.newLine();
            bw.write("1,Concert A,2025-06-01,Grand Theater,100.0,100,100");
            bw.newLine();
            bw.write("2,Theater B,2025-06-05,City Hall,80.0,200,200");
            bw.newLine();
            bw.write("3,Opera C,2025-06-10,National Center,120.0,150,150");
            bw.newLine();
            System.out.println("Activity data file created successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadTickets() {
        String filePath = PROJECT_PATH + File.separator + TICKET_DATA_FILE;
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            createTicketFile(filePath);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Ticket ticket = new Ticket(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4]);
                    tickets.add(ticket);
                    
                    // Assign tickets to corresponding customers
                    for (Customer customer : customers) {
                        if (customer.getID().equals(parts[4])) {
                            customer.addTicket(ticket);
                            break;
                        }
                    }
                } else if (parts.length >= 4) {
                    Ticket ticket = new Ticket(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    tickets.add(ticket);
                }
            }
            System.out.println("Ticket data loaded successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createTicketFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("showName,showDate,seatNumber,price,customerID");
            bw.newLine();
            System.out.println("Ticket data file created successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllData() {
        saveCustomers();
        saveManagers();
        saveActivities();
        saveTickets();
    }

    private static void saveCustomers() {
        String filePath = PROJECT_PATH + File.separator + CUSTOMER_DATA_FILE;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("name,ID,password,jurisdiction,mail");
            bw.newLine();
            for (Customer customer : customers) {
                bw.write(String.format("%s,%s,%s,%s,%s",
                    customer.getName(),
                    customer.getID(),
                    customer.getPassWord(),
                    customer.isJurisdiction(),
                    customer.getMail()
                ));
                bw.newLine();
            }
            System.out.println("Customer data saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveManagers() {
        String filePath = PROJECT_PATH + File.separator + MANAGER_DATA_FILE;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("name,ID,password,mail");
            bw.newLine();
            for (Manager manager : managers) {
                bw.write(String.format("%s,%s,%s,%s",
                    manager.getName(),
                    manager.getID(),
                    manager.getPassWord(),
                    manager.getMail()
                ));
                bw.newLine();
            }
            System.out.println("Manager data saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveActivities() {
        String filePath = PROJECT_PATH + File.separator + ACTIVITY_DATA_FILE;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,name,date,venue,price,totalTickets,remainingTickets");
            bw.newLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Activity activity : activities) {
                bw.write(String.format("%s,%s,%s,%s,%.2f,%d,%d",
                    activity.getId(),
                    activity.getName(),
                    sdf.format(activity.getDate()),
                    activity.getVenue(),
                    activity.getPrice(),
                    activity.getTotalTickets(),
                    activity.getRemainingTickets()
                ));
                bw.newLine();
            }
            System.out.println("Activity data saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveTickets() {
        String filePath = PROJECT_PATH + File.separator + TICKET_DATA_FILE;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("showName,showDate,seatNumber,price,customerID");
            bw.newLine();
            for (Ticket ticket : tickets) {
                bw.write(String.format("%s,%s,%s,%.2f,%s",
                    ticket.getShowName(),
                    ticket.getShowDate(),
                    ticket.getSeatNumber(),
                    ticket.getPrice(),
                    ticket.getCustomerID()
                ));
                bw.newLine();
            }
            System.out.println("Ticket data saved successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}