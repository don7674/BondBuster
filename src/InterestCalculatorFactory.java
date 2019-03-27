/*
 * InterestCalculatorFactory.java
 *
 * Created on January 17, 2011, 4:04 PM
 */

package bondcalculator;

import java.util.Date;

/**
 *
 * @author Don
 */
public class InterestCalculatorFactory {
    
    private static InterestCalculatorFactory instance = null;
    private InterestCalculator calc;
    
    /** Creates a new instance of InterestCalculatorFactory */
    private InterestCalculatorFactory() {
        calc = new InterestCalculator();
                
        //calc.add_test_rates();
        //RateIO.loadRatesPAV("./rates.ini", calc);
        RateIO.loadRatesXmlFile("data/rates.xml", calc);
    }
    
    public static InterestCalculatorFactory getInstance() {
        if (instance == null) {
            instance = new InterestCalculatorFactory();
        }
        return instance;
    }
    
    public InterestRate[] getAllRates() {
        return calc.getRates();
    }
    
    public double getInterestRateForDate(Date theDate)
    {
        return calc.getInterestRateForDate(theDate);
    }    
}
