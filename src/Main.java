/*
 * Main.java
 *
 * Created on January 17, 2011, 12:00 PM
 */

package bondcalculator;

import java.util.GregorianCalendar;

/**
 *
 * @author Don
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Bond bond = BondFactory.registerBond("data/bond.xml");
        if (bond == null) {
            InterestCalculatorFactory icf = InterestCalculatorFactory.getInstance();
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(2010, 1, 1);
            bond = new Bond("Test1", 500000, 24 * 10, cal, 4400, 15, 0.0, -1.0, 100, 10, icf);
        }
        BondCalculator bc = new BondCalculator(bond);
        bc.run();
    }
}
