package Controller;

import DataBase.*;
import Exceptions.AuthException;
import java.util.*;
import java.util.regex.Pattern;

public class AuthService {
    // check the input whether is valid
    private static final Pattern ALPHANUMERIC_INPUT_VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    /**
     * 验证输入字符串是否只包含字母和数字
     * @param inputString 待验证的字符串
     * @return 如果字符串非空且只包含字母和数字则返回true
     */
    public static boolean isValidAlphanumericInput(String inputString) {
        return inputString != null && !inputString.isEmpty() && ALPHANUMERIC_INPUT_VALIDATION_PATTERN.matcher(inputString).matches();
    }
    
    /**
     * 用户登录验证
     * @param userIdentifier 用户ID
     * @param userPassword 用户密码
     * @return 验证成功的Customer对象
     * @throws AuthException 如果验证失败则抛出异常
     */
    public static Customer authenticateUserCredentials(String userIdentifier, String userPassword) throws AuthException {
        if (!isValidAlphanumericInput(userIdentifier)) {//检查输入
            throw new AuthException("User ID must contain only letters and numbers");
        }
        if (!isValidAlphanumericInput(userPassword)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        System.out.println("Attempting user login: ID=" + userIdentifier);
        for (Customer customerRecord : Initialize.customers) {
            if (customerRecord.getID().equals(userIdentifier)) {
                if (customerRecord.login(userIdentifier, userPassword)) {
                    return customerRecord;
                } else {
                    throw new AuthException("Incorrect password");
                }
            }
        }
        throw new AuthException("User does not exist");
    }

    /**
     * 管理员登录验证
     * @param adminIdentifier 管理员ID
     * @param adminPassword 管理员密码
     * @return 验证成功的Manager对象
     * @throws AuthException 如果验证失败则抛出异常
     */
    public static Manager authenticateAdministratorCredentials(String adminIdentifier, String adminPassword) throws AuthException {
        if (!isValidAlphanumericInput(adminIdentifier)) {
            throw new AuthException("Admin ID must contain only letters and numbers");
        }
        if (!isValidAlphanumericInput(adminPassword)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        System.out.println("Attempting admin login: ID=" + adminIdentifier);
        System.out.println("Current admin list size: " + Initialize.managers.size());
        
        for (Manager managerRecord : Initialize.managers) {
            System.out.println("Checking admin: ID=" + managerRecord.getID() + ", Name=" + managerRecord.getName() + ", Permission=" + managerRecord.isJurisdiction());
            if (managerRecord.getID().equals(adminIdentifier)) {
                System.out.println("Found matching admin ID: " + adminIdentifier);
                if (managerRecord.login(adminIdentifier, adminPassword)) {
                    System.out.println("Password verification successful, returning admin object");
                    return managerRecord;
                } else {
                    System.out.println("Password verification failed");
                    throw new AuthException("Incorrect password");
                }
            }
        }
        System.out.println("Admin ID not found: " + adminIdentifier);
        throw new AuthException("Admin account does not exist or insufficient privileges");
    }

    /**
     * 用户注册
     * @param userName 用户名
     * @param userIdentifier 用户ID
     * @param userPassword 用户密码
     * @param userEmail 用户邮箱
     * @return 注册成功的Customer对象
     * @throws AuthException 如果注册失败则抛出异常
     */
    public static Customer registerNewUserAccount(String userName, String userIdentifier, String userPassword, String userEmail) throws AuthException {
        // 验证输入
        if (!isValidAlphanumericInput(userName)) {
            throw new AuthException("Username must contain only letters and numbers");
        }
        if (!isValidAlphanumericInput(userIdentifier)) {
            throw new AuthException("User ID must contain only letters and numbers");
        }
        if (!isValidAlphanumericInput(userPassword)) {
            throw new AuthException("Password must contain only letters and numbers");
        }
        
        // 检查ID是否已存在
        for (Customer existingCustomer : Initialize.customers) {
            if (existingCustomer.getID().equals(userIdentifier)) {
                throw new AuthException("User ID already exists");
            }
        }
        
        Customer newCustomerAccount = new Customer(userName, userIdentifier, userPassword, userEmail);
        Initialize.customers.add(newCustomerAccount);
        Initialize.saveAllData();
        return newCustomerAccount;
    }
} 
