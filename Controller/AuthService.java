package Controller;

import DataBase.*;
import Exceptions.AuthException;
import java.util.List;
import java.util.regex.Pattern;

public class AuthService {
    // Regular expression pattern to validate alphanumeric input
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    public static boolean isValidAlphanumeric(String input) {
        return input != null && !input.isEmpty() && ALPHANUMERIC_PATTERN.matcher(input).matches();
    }
    
    public static Customer login(String ID, String password) throws AuthException {
        if (!isValidAlphanumeric(ID)) {//check the imput
            throw new AuthException("User ID must contain only letters and numbers");
        }
        if (!isValidAlphanumeric(password)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        System.out.println("Attempting user login: ID=" + ID);
        for (Customer customer : Initialize.customers) {
            if (customer.getID().equals(ID)) {
                if (customer.login(ID, password)) {
                    return customer;
                } else {
                    throw new AuthException("Incorrect password");
                }
            }
        }
        throw new AuthException("User does not exist");
    }

    public static Manager loginAsManager(String ID, String password) throws AuthException {
        if (!isValidAlphanumeric(ID)) {
            throw new AuthException("Admin ID must contain only letters and numbers");
        }
        if (!isValidAlphanumeric(password)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        System.out.println("Attempting admin login: ID=" + ID);
        System.out.println("Current admin list size: " + Initialize.managers.size());
        
        for (Manager manager : Initialize.managers) {
            System.out.println("Checking admin: ID=" + manager.getID() + ", Name=" + manager.getName() + ", Permission=" + manager.isJurisdiction());
            if (manager.getID().equals(ID)) {
                System.out.println("Found matching admin ID: " + ID);
                if (manager.login(ID, password)) {
                    System.out.println("Password verification successful, returning admin object");
                    return manager;
                } else {
                    System.out.println("Password verification failed");
                    throw new AuthException("Incorrect password");
                }
            }
        }
        System.out.println("Admin ID not found: " + ID);
        throw new AuthException("Admin account does not exist or insufficient privileges");
    }

    public static Customer register(String name, String ID, String password, String mail) throws AuthException {
        // Validate input
        if (!isValidAlphanumeric(name)) {
            throw new AuthException("Username must contain only letters and numbers");
        }
        if (!isValidAlphanumeric(ID)) {
            throw new AuthException("User ID must contain only letters and numbers");
        }
        if (!isValidAlphanumeric(password)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        // Check if ID already exists
        for (Customer customer : Initialize.customers) {
            if (customer.getID().equals(ID)) {
                throw new AuthException("User ID already exists");
            }
        }
        
        Customer newCustomer = new Customer(name, ID, password, mail);
        Initialize.customers.add(newCustomer);
        Initialize.saveAllData();
        return newCustomer;
    }
} 
