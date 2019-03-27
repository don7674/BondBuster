/*
 * BondCalculator.java
 *
 * Created on January 18, 2011, 12:52 PM
 */

package bondcalculator;

/**
 *
 * @author Don
 */
public class BondCalculator {
    
    private Bond bond;
    private BondCalculatorGui gui;
    
    /** Creates a new instance of BondCalculator */
    public BondCalculator(Bond b) {
        bond = b;
        gui = new BondCalculatorGui(bond);
    }
    
    public void run() {
        gui.show();
    }
    
}
