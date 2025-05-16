package Exceptions;
 
public class PaymentRequiredException extends Exception {
    public PaymentRequiredException(String msg) {
        super(msg);
    }
} 