package Controller;

import DataBase.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Initialize {
    private static final String PATH = System.getProperty("user.dir") + File.separator + "src";
    private static final String USER_DATA = "customer_data.txt";
    private static final String TIX_DATA = "ticket_data.txt";
    private static final String ACT_DATA = "activity_data.txt";
    private static final String MGR_DATA = "manager_data.txt";

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
        for (Manager m : managers) {
            System.out.println("Loaded admin: ID=" + m.getID() + ", Name=" + m.getName());
        }
        
        // 打印活动加载信息
        System.out.println("Loaded activities count: " + activities.size());
        for (Activity a : activities) {
            System.out.println("Loaded activity: ID=" + a.getId() + ", Name=" + a.getName() + 
                ", Remaining/Total=" + a.getRemainingTickets() + "/" + a.getTotalTickets());
        }
    }

    private static void loadCustomers() {
        String fp = PATH + File.separator + USER_DATA;
        File df = new File(fp);
        if (!df.exists()) {
            createCustomerFile(fp);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Customer c = new Customer(parts[0], parts[1], parts[2], parts[4]);
                    c.setJurisdiction(Boolean.parseBoolean(parts[3]));
                    customers.add(c);
                }
            }
            System.out.println("Customer data loaded: " + fp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadManagers() {
        String fp = PATH + File.separator + MGR_DATA;
        File df = new File(fp);
        if (!df.exists()) {
            System.out.println("Admin data file does not exist, creating new file...");
            createManagerFile(fp);
        }
        System.out.println("Loading admin data: " + fp);
        try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                System.out.println("Reading admin data: " + line);
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    System.out.println("Creating admin: Name=" + parts[0] + ", ID=" + parts[1]);
                    Manager m = new Manager(parts[0], parts[1], parts[2], parts[3]);
                    managers.add(m);
                }
            }
            System.out.println("Admin data loaded: " + fp + ", count=" + managers.size());
        } catch (IOException e) {
            System.out.println("Error loading admin: " + e.getMessage());
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
        String filePath = PATH + File.separator + ACT_DATA;
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            System.out.println("Activity data file does not exist, creating default file...");
            createActivityFile(filePath);
        }
        
        System.out.println("Loading activities from: " + filePath);
        activities.clear(); // 确保先清空活动列表，避免重复加载
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            int lineCount = 0; // 跟踪总行数
            int loadedCount = 0; // 跟踪成功加载的活动数量
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (firstLine) {
                    firstLine = false;
                    System.out.println("Reading header line: " + line);
                    continue;
                }
                
                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line " + lineCount);
                    continue;
                }
                
                System.out.println("Reading activity line " + lineCount + ": " + line);
                String[] parts = line.split(",", 8); // Allow maximum of 8 parts to handle comments with commas
                Activity activity = null;
                
                if (parts.length >= 7) {
                    try {
                        activity = new Activity(
                            parts[0], parts[1], sdf.parse(parts[2]), parts[3], 
                            Double.parseDouble(parts[4]), Integer.parseInt(parts[5]), 
                            Integer.parseInt(parts[6])
                        );
                        
                        // Load comments if available
                        if (parts.length >= 8 && parts[7] != null && !parts[7].isEmpty()) {
                            String[] comments = parts[7].split("\\|");
                            for (String comment : comments) {
                                if (!comment.isEmpty()) {
                                    // Convert back any [comma] placeholders to real commas
                                    String originalComment = comment.replace("[comma]", ",");
                                    activity.addComment(originalComment);
                                }
                            }
                        }
                        
                        activities.add(activity);
                        loadedCount++;
                        System.out.println("Successfully loaded activity: " + activity.getName());
                    } catch (Exception e) {
                        System.err.println("Error parsing activity with 7+ fields at line " + lineCount + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (parts.length >= 6) {
                    try {
                        activity = new Activity(
                            parts[0], parts[1], sdf.parse(parts[2]), parts[3], 
                            Double.parseDouble(parts[4]), Integer.parseInt(parts[5])
                        );
                        activities.add(activity);
                        loadedCount++;
                        System.out.println("Successfully loaded activity with 6 fields: " + activity.getName());
                    } catch (Exception e) {
                        System.err.println("Error parsing activity with 6 fields at line " + lineCount + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Skipping line " + lineCount + " due to insufficient fields: " + line);
                }
            }
            System.out.println("Activity data loaded successfully: " + filePath);
            System.out.println("Total lines read: " + lineCount + ", Activities loaded: " + loadedCount);
        } catch (Exception e) {
            System.err.println("Error loading activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createActivityFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,name,date,venue,price,totalTickets,remainingTickets,comments");
            bw.newLine();
            bw.write("1,Concert A,2025-06-01,Grand Theater,100.0,100,100,");
            bw.newLine();
            bw.write("2,Theater B,2025-06-05,City Hall,80.0,200,200,");
            bw.newLine();
            bw.write("3,Opera C,2025-06-10,National Center,120.0,150,150,");
            bw.newLine();
            System.out.println("Activity data file created successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadTickets() {
        String filePath = PATH + File.separator + TIX_DATA;
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
        String filePath = PATH + File.separator + USER_DATA;
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
        String filePath = PATH + File.separator + MGR_DATA;
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
        String filePath = PATH + File.separator + ACT_DATA;
        System.out.println("Saving activities to: " + filePath + ", count: " + activities.size());
        
        // 如果activities为空，先尝试加载已有文件中的活动
        if (activities.isEmpty()) {
            System.out.println("Warning: No activities in memory to save!");
            
            // 检查文件是否存在，如果存在则保留
            File existingFile = new File(filePath);
            if (existingFile.exists() && existingFile.length() > 0) {
                System.out.println("Existing activity file found. Preserving current file content.");
                
                // 尝试从现有文件加载活动
                List<Activity> tempActivities = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    boolean firstLine = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    
                    while ((line = br.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue; // 跳过标题行
                        }
                        
                        if (line.trim().isEmpty()) {
                            continue; // 跳过空行
                        }
                        
                        String[] parts = line.split(",", 8);
                        if (parts.length >= 7) {
                            try {
                                Activity activity = new Activity(
                                    parts[0], parts[1], sdf.parse(parts[2]), parts[3], 
                                    Double.parseDouble(parts[4]), Integer.parseInt(parts[5]), 
                                    Integer.parseInt(parts[6])
                                );
                                tempActivities.add(activity);
                                System.out.println("Preserved existing activity: " + activity.getName());
                            } catch (Exception e) {
                                System.err.println("Error preserving activity: " + e.getMessage());
                            }
                        }
                    }
                    
                    // 如果成功从文件加载了活动，使用这些活动
                    if (!tempActivities.isEmpty()) {
                        activities.addAll(tempActivities);
                        System.out.println("Loaded " + tempActivities.size() + " activities from existing file.");
                        return; // 已经保留了现有活动，无需继续保存
                    }
                } catch (IOException e) {
                    System.err.println("Error reading existing activity file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // 如果文件不存在或为空，创建默认活动文件
                System.out.println("No existing activity file or file is empty. Creating default activities.");
                createActivityFile(filePath);
                return;
            }
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // 写入标题行
            bw.write("id,name,date,venue,price,totalTickets,remainingTickets,comments");
            bw.newLine();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int savedCount = 0;
            
            for (Activity activity : activities) {
                try {
                    // Serialize comments as a single string with | separator
                    StringBuilder commentsStr = new StringBuilder();
                    for (String comment : activity.getComments()) {
                        // Replace any commas in comments to avoid CSV parsing issues
                        String safeComment = comment.replace(",", "[comma]");
                        commentsStr.append(safeComment).append("|");
                    }
                    
                    String line = String.format("%s,%s,%s,%s,%.2f,%d,%d,%s",
                        activity.getId(),
                        activity.getName(),
                        sdf.format(activity.getDate()),
                        activity.getVenue(),
                        activity.getPrice(),
                        activity.getTotalTickets(),
                        activity.getRemainingTickets(),
                        commentsStr.toString()
                    );
                    
                    bw.write(line);
                    bw.newLine();
                    savedCount++;
                    System.out.println("Saved activity: " + activity.getId() + " - " + activity.getName());
                } catch (Exception e) {
                    System.err.println("Error saving activity: " + activity.getId() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Activity data saved successfully: " + filePath + ", Saved " + savedCount + " activities");
        } catch (IOException e) {
            System.err.println("Error saving activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveTickets() {
        String filePath = PATH + File.separator + TIX_DATA;
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