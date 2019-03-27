/*
 * PaymentFactory.java
 *
 * Created on January 20, 2011, 2:27 PM
 */

package bondcalculator;

import java.util.*;

/**
 *
 * @author Don
 */
public class PaymentFactory {
    
    private static PaymentFactory instance = null;
    private Bond bond;
    private ArrayList<Payment> instalment0;
    private ArrayList<Payment> instalment;
    private ArrayList<Payment> adhoc0;
    private ArrayList<Payment> adhoc;
    private boolean adhocEnabled;
    
    /** Creates a new instance of PaymentFactory */
    private PaymentFactory(Bond bond) {
        this.bond = bond;
        instalment0 = new ArrayList<Payment>();        
        instalment = new ArrayList<Payment>();        
        adhoc = new ArrayList<Payment>();        
        adhoc0 = new ArrayList<Payment>();
        adhocEnabled = true;
    }
    
    private static void copyPayments(ArrayList<Payment> source, ArrayList<Payment> dest) {
        dest.clear();
        for (Payment p : source) {
            dest.add(new Payment(p));
        }
    }

    public static PaymentFactory getInstance(Bond bond) {
        if (instance == null) {
            instance = new PaymentFactory(bond);
        }
        return instance;
    }    

    public void init() {
    	instalment.clear();
    	adhoc.clear();
        //PaymentIO.loadPaymentsXmlFile("Instalments", "./instalments.xml", this);
    	PaymentIO.loadPaymentsXmlFile("", "data/payments.xml", this);
        addInstalment(bond.getStartDate(), bond.getBasicInstalment());
        copyPayments(instalment, instalment0);
        //PaymentIO.loadPaymentsPAV("./payments.ini", this);
        //PaymentIO.loadPaymentsXmlFile("Adhoc", "data/payments.xml", this);
        copyPayments(adhoc, adhoc0);
    }
    
    public void loadInstalments(String filename)
    {
    	if (filename == null) {
    		filename = "data/payments.xml";
    	}
    	instalment.clear();
    	PaymentIO.loadPaymentsXmlFile("Instalment", filename, this);
        if (lookup(instalment, bond.getStartDate()) == null) {
        	addInstalment(bond.getStartDate(), bond.getBasicInstalment());
        }    	
    }
    
    public void loadPayments(String filename)
    {
    	if (filename == null) {
    		filename = "data/payments.xml";
    	}    	
    	adhoc.clear();
    	PaymentIO.loadPaymentsXmlFile("Adhoc", filename, this);
    }    
    
    private Payment lookup(ArrayList<Payment> payment, Date theDate) {
        for (Payment p : payment) {
            Date pd = p.getEffectiveDate();
            if (pd.getYear() == theDate.getYear() &&
                pd.getMonth() == theDate.getMonth() &&
                pd.getDate() == theDate.getDate()) {
                return p;
            }         
        }
        return null;
    }

    public Payment[] getInstalments() {
    	//return (instalment != null) ? (Payment[])(instalment.toArray(null)) : null;
    	if (instalment != null) {
    		Payment[] pay = new Payment[instalment.size()];
    		int k = 0;
    		for (Payment inst : instalment) {
    			pay[k++] = inst;
    		}
    		return pay;
    	}
    	return null;
    }
    
    public boolean getAdhocState() {
    	return adhocEnabled;
    }
    
    public Payment[] getAdhocs() {
    	//return (adhoc != null) ? (Payment[])(adhoc.toArray(null)) : null;
    	if (adhoc != null) {
    		Payment[] pay = new Payment[adhoc.size()];
    		int k = 0;
    		for (Payment inst : adhoc) {
    			pay[k++] = inst;
    		}
    		return pay;
    	}
    	return null;    	
    }    
    
    public void reset() {
        copyPayments(instalment0, instalment);
        copyPayments(adhoc0, adhoc);
        adhocEnabled = true;
    }
    
    private boolean isPaymentDate(Date theDate) {
        if (theDate.getDate() == bond.getInstalmentDayOfMonth()) {
            return true;
        }
        @SuppressWarnings("deprecation")
        Calendar cal = new GregorianCalendar(theDate.getYear(), theDate.getMonth(), theDate.getDate());
        if (theDate.getDate() == cal.getActualMaximum(Calendar.DAY_OF_MONTH) &&
            bond.getInstalmentDayOfMonth() > theDate.getDate()) {
            return true;
        }
        return false;
    }

//NB: Assumes sorted by effective date ascending    
    public double getInstalmentForDate(Date theDate)
    {
        double amount = 0.0;
        for (Payment inst : instalment) {            
            if (inst.getEffectiveDate().compareTo(theDate) > 0) {
                break;
            }
            amount = inst.getPayment(); 
        }
        return amount;      
    }
    
    public void setAdhocState(boolean state) {
    	adhocEnabled = state;
    }
    
    public double getAdhocPaymentForDate(Date theDate)
    {
        double amount = 0.0;
        if (adhocEnabled) {
            for (Payment ah : adhoc) {
        	Date ahDate = ah.getEffectiveDate();
        	if (ahDate.getYear() == theDate.getYear() &&
        	    ahDate.getMonth() == theDate.getMonth() &&
        	    ahDate.getDate() == theDate.getDate()) {
        	    amount += ah.getPayment(); ;
        	}
            }
        }
        return amount;      
    }
    
    public double getPaymentForDate(Date theDate, String type) {
        double payment = 0.0;
        if (type.contains("I") && isPaymentDate(theDate)) {
            //payment = bond.getInstalment(theDate);
            payment = getInstalmentForDate(theDate);
        }
        if (type.contains("A")) {
        	payment += getAdhocPaymentForDate(theDate);
        }
        return payment;
    }
    
    public int getInstalmentDayOfMonth() {
        return bond.getInstalmentDayOfMonth();  //1-based day
    }
    
    public boolean setInstalmentForDate(Date theDate, double amount) {
        Payment p = lookup(instalment, theDate);
        if (p != null) {
            p.setPayment(amount);
        }
        else {
            addInstalment(theDate, amount);
        }
        return true;
    }
        
    public boolean addInstalment(Date effDate, double amount) {
        if (lookup(instalment, effDate) == null) {
            instalment.add(new Payment(effDate, amount));
            Collections.sort(instalment);
            return true;
        }
        return false;
    }
    
    public boolean addAdhocPayment(Date effDate, double amount) {
        if (lookup(adhoc, effDate) == null) {
            adhoc.add(new Payment(effDate, amount));
            Collections.sort(adhoc);
            return true;
        }
        return false;
    }
}
