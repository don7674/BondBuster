/*
 * Payment.java
 *
 * Created on January 20, 2011, 3:05 PM
 */

package bondcalculator;

import java.util.Date;
/**
 *
 * @author Don
 */
public class Payment implements Comparable<Payment> {

    private Date effectiveDate;
    private double amount;
    private boolean enabled;
    
    /** Creates a new instance of Payment */
    public Payment(Date theDate, double theAmount) {
        amount = theAmount;
        effectiveDate = theDate;
        enabled = true;
    }
    
    public Payment(final Payment p) {
        amount = p.amount;
        effectiveDate = (Date)p.effectiveDate.clone();
        enabled = p.enabled;
    }
    
    public String toString() {
    	return BondUtils.dateToString(getEffectiveDate(), null) +
    		"          " +
    		getPayment() +
    		"          " +
    		(isEnabled() ? "" : "Disabled");
    }
    
    public int compareTo(final Payment payment) {
    	return getEffectiveDate().compareTo(payment.getEffectiveDate());
    }
    
    public double getPayment()
    {
        return amount;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    
    public boolean isEnabled() {
    	return enabled;
    }
    
    public void setPayment(double amnt) {
        amount = amnt;
    }
    
    public void setEffectiveDate(Date date) {
        effectiveDate = date;
    }
    
    public void setEnabled(boolean state) {
    	enabled = state;
    }
      
}
