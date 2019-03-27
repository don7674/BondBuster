/*
 * Interest.java
 *
 * Created on January 17, 2011, 12:37 PM
 */

package bondcalculator;

import java.util.*;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 *
 * @author Don
 */
public class InterestCalculator {
    
    private ArrayList<InterestRate> rates;
            
    /** Creates a new instance of Interest */
    public InterestCalculator() {
        rates = new ArrayList<InterestRate>();
    }
    
    public InterestRate[] getRates() {
        //InterestRate[] arr = (InterestRate[])(rates.toArray());
        InterestRate[] arr = new InterestRate[rates.size()];
        for (int k = 0; k < arr.length; ++k) {
            arr[k] = rates.get(k);
        }
        return arr;
    }

//NB: Assumes sorted by effective date ascending
    public double getInterestRateForDate(Date theDate)
    {
        double interestRate = 0.0;
        for (InterestRate rate : rates) {            
            if (rate.getEffectiveDate().compareTo(theDate) > 0) {
                break;
            }
            interestRate = rate.getInterestRate(); 
        }
        return interestRate;
    }
    
    public boolean addRate(Date effDate, double rate) {
        //if (rates.contains(elem))
        rates.add(new InterestRate(effDate, rate));
        return true;
    }
/*    
    public void add_test_rates()
    {
        GregorianCalendar g = new GregorianCalendar();
        g.set(2000,0,1);
        rates.add(new InterestRate(g.getTime(), 10));
        g = new GregorianCalendar();
        g.set(2001,3,31);
        rates.add(new InterestRate(g.getTime(), 9.5));
        g = new GregorianCalendar();
        g.set(2004,9,30);
        rates.add(new InterestRate(g.getTime(), 9.75));
        g = new GregorianCalendar();
        g.set(2010,6,31);
        rates.add(new InterestRate(g.getTime(), 8));        
    }
*/    
}
