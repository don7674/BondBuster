/*
 * InterestRate.java
 *
 * Created on January 17, 2011, 12:47 PM
 */

package bondcalculator;

import java.util.Date;

/**
 *
 * @author Don
 */
public class InterestRate {
    
    private Date effectiveDate;
    private double rate;
    
    /** Creates a new instance of InterestRate */
    public InterestRate(Date theDate, double theRate) {
        rate = theRate;  // rate in %
        effectiveDate = theDate;
    }
    
    public double getInterestRate()
    {
        return rate;
    }
    public Date getEffectiveDate() {
        return effectiveDate;
    }
}
