/*
 * BondDay.java
 *
 * Created on January 17, 2011, 12:05 PM
 */

package bondcalculator;

import java.util.Date;
/**
 *
 * @author Don
 */
public class BondDay {
    
    private int dayNumber;
    private double interest, payment;
    private double balance;
    private Date dayDate;
    
    /** Creates a new instance of BondDay */
    public BondDay(int dayNum) {
        dayNumber = dayNum;
    }
    
    public BondDay(int dayNum, Date date) {
        dayNumber = dayNum;
        dayDate = date;
    }    
    
    public int getDayNumber()
    {
        return dayNumber;
    }
    
    public Date getDayDate() {
        return dayDate;
    }
        
    public double getPayment()
    {
        return payment;
    }
    
    public double getInterest()
    {
        return interest;
    }
    
    public double getBalance()
    {
        return balance;
    }    
    
    public void reset()
    {
        payment = 0.0;
        interest = 0.0;
        balance = 0.0;
    }
    
    public void makePayment(double amount)
    {
       payment += amount; 
    }
    
    public void addInterest(double amount)
    {
       interest += amount;
    }
    
    public void setBalance(double bal) {
        balance = bal;
    }
}
