package bondcalculator;

import java.util.Date;
import java.util.GregorianCalendar;

public class BondFactory {

    //private static BondFactory instance = null;
    private static Bond bond = null;
    
    private BondFactory() {
    }
    
    public static Bond registerBond(String xmlFilename) {
        BondDefinition bd = BondIO.loadBondXmlFile(xmlFilename);
        if (bd != null) {
            InterestCalculatorFactory icf = InterestCalculatorFactory.getInstance();
            GregorianCalendar cal = new GregorianCalendar();
            Date date = BondUtils.stringToDate(bd.getDateStr(), "yyyy-MM-dd");
            cal.setTime(date);
            double fixedRate = 0.0, deltaRate = 0.0;
            if (bd.getRateTypeStr().equalsIgnoreCase("FIXED")) {
                fixedRate = Double.parseDouble(bd.getRateStr());
            } else {
                deltaRate = Double.parseDouble(bd.getRateStr());
            }
            bond = new Bond(
                        bd.getNameStr(),
                        Double.parseDouble(bd.getPrincipalStr()),
                        Integer.parseInt(bd.getTermStr()),
                        cal,
                        Double.parseDouble(bd.getPaymentStr()),
                        Integer.parseInt(bd.getInstDayStr()),
                        fixedRate,
                        deltaRate,
                        Double.parseDouble(bd.getSetupFeeStr()),
                        Double.parseDouble(bd.getMonthFeeStr()),
                        icf);                        
        }
        return bond;
    }
    
}
