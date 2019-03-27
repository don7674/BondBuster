/*
 * Bond.java
 *
 * Created on January 17, 2011, 12:00 PM
 */

package bondcalculator;

import java.util.*;

/**
 *
 * @author Don
 */
public class Bond {
    
    private String bondId;
    private final GregorianCalendar calnStartDate0;
    private GregorianCalendar calnStartDate;
    private final double principal0;
    private double principal;
    private final double instalment0;
    private double instalment;
    private final double fixedRate0;
    private double fixedRate;
    private final double primeDelta0;
    private double primeDelta;
    private final int months0;
    private final int instalmentDom0;
    private int months, effectiveMonths;
    private int totalDays, effectiveDays;
    private int instalmentDom;  // day of month
    private final double initFee0;
    private double initFee;
    private final double monthlyFee0;
    private double monthlyFee;
    private BondDay[] bondDays;
    private InterestCalculatorFactory interestCalculator;
    private PaymentFactory paymentFactory;
    
    private double paidCapital;
    private double paidInterest;
    
    private void initData() {
      effectiveMonths = months;
      totalDays = months/12 * 365;
      totalDays += ((months%12) * 30);
      effectiveDays = totalDays;
      bondDays = new BondDay[totalDays];      
    }
    
    /** Restore initial bond values **/
    public void resetValues() {
      calnStartDate = (GregorianCalendar)calnStartDate0.clone();
      principal = principal0;
      fixedRate = fixedRate0;
      primeDelta = primeDelta0;
      months = months0;
      instalment = instalment0;
      instalmentDom = instalmentDom0;
      initFee = initFee0;
      monthlyFee = monthlyFee0;
     
      paidCapital = paidInterest = 0.0;
      
      paymentFactory.reset();
      
      initData();
    }    
    
    /** Creates a new instance of Bond */
    public Bond(
            String id,
            double amount,
            int nbrMonths,
            GregorianCalendar calnStart,
            double install,
            int installDom,
            double fixRate,
            double rateDelta,
            double setupFee,
            double mthlyFee,
            InterestCalculatorFactory icf) {
      bondId = id;
      calnStartDate0 = (GregorianCalendar)calnStart.clone();
      calnStartDate = (GregorianCalendar)calnStart.clone();
      principal0 = principal = amount;
      instalment0 = instalment = install;
      instalmentDom0 = instalmentDom = installDom;  // 1-based day
      fixedRate0 = fixedRate = fixRate;
      primeDelta0 = primeDelta = rateDelta;
      months0 = months = nbrMonths;
      initFee0 = initFee = setupFee;
      monthlyFee0 = monthlyFee = mthlyFee;
      interestCalculator = icf;
      paymentFactory = PaymentFactory.getInstance(this);
      paymentFactory.init();
      //paymentFactory.setInstalmentDayOfMonth(instalmentDom0, true);
      initData();
      
      calculate();
    }
    
    public String getId() {
        return bondId;
    }
    public double getPrincipal() {
        return principal;
    }
    public double getBasicInstalment() {
        return instalment;
    }    
    public int getTerm() {
        return months;
    }
    public int getEffectiveTerm() {
    	return effectiveMonths;
    }
    public double getFixedRate() {
        return fixedRate;
    }
    public double getPrimeDeltaRate() {
        return primeDelta;
    }
    public double getInitFee() {
        return initFee;
    }
    public double getMonthlyFee() {
        return monthlyFee;
    }
    public double getCapitalPaid() {
        return paidCapital;
    }
    public double getInterestPaid() {
        return paidInterest;
    }    
    public double getFeesPaid() {
        return monthlyFee * effectiveMonths;
    }    
    
    
    public GregorianCalendar getStartDateCalendar() {
        // need to use clone? otherwise we mod this calendar!
        GregorianCalendar caln = (GregorianCalendar)calnStartDate.clone();
        return caln;
    }
    
    public InterestRate[] getAllRates() {
        return interestCalculator.getAllRates();
    }

    public Payment[] getInstalments() {
    	return paymentFactory.getInstalments();
    }
    
    public void refreshInstalments() {
    	paymentFactory.loadInstalments(null);
    }
    
    public boolean getAdhocState() {
    	return paymentFactory.getAdhocState();
    }
    
    public Payment[] getAdhocs() {
    	return paymentFactory.getAdhocs();
    }    
    
    public void refreshAdhocs() {
    	paymentFactory.loadPayments(null);
    }
    
    public Date getStartDate() {
        return calnStartDate.getTime();
    }

    public double getInterestRateForDate(Date theDate) {
        if (fixedRate > 0.0) {
            return fixedRate;
        }
        return interestCalculator.getInterestRateForDate(theDate);
    }

    public int getInstalmentDayOfMonth() {
        return instalmentDom;
    }
    
    public void setValues(
            double amount,
            int nbrMonths,
            GregorianCalendar calnStart,
            double install,
            int installDom,
            double fixRate,
            double deltaRate) {
      if (calnStart != null) {
          calnStartDate = (GregorianCalendar)calnStart.clone();
      }
      if (amount > 0.0) {
          principal = amount;
      }
      if (install > 0.0) {
    	  instalment = install;
          paymentFactory.setInstalmentForDate(calnStart.getTime(), install);
      }
      if (installDom > 0 && installDom <= 31) {
          instalmentDom = installDom;
          //paymentFactory.setInstalmentDayOfMonth(instalmentDom, true);
      }
      if (nbrMonths > 0) {
          months = nbrMonths;
      }
      if (fixRate > 0.0) {
          fixedRate = fixRate;
      } else {
          primeDelta = deltaRate;
      }
      initData();
      //paymentFactory.resync();
    }
    
    public void setFees(double setupFee, double mthlyFee) {
        initFee = setupFee;
        monthlyFee = mthlyFee;
    }
       
    public void setFixedRate(double rate) {
      if (rate >= 0.0) {
          fixedRate = rate;
      }         
    }
            
    public void reset() {
        resetValues();
    }
    
    public void setAdhocState(boolean state) {
    	paymentFactory.setAdhocState(state);
    }
    
    public void calculate()
    {
    	resize(false);
    	
    	paidCapital = paidInterest = 0.0;
    	effectiveMonths = 0;  // updated by side effect
    	
        double balance = principal + initFee;
                
        GregorianCalendar cal = getStartDateCalendar();         
        
        int day, month;
        boolean done = false;
        for (day = 0, month = -1; day < totalDays; ++day, cal.add(Calendar.DATE, 1)) {
            Date date = cal.getTime();
            if (date.getMonth() != month) {
                if (done)
                    break;
                ++effectiveMonths;
                month = date.getMonth();
            }

            // allocate on demand
            if (bondDays[day] == null) {
                bondDays[day] = new BondDay(day);
            }
            bondDays[day].reset(); 
            
            double dailyPayment = paymentFactory.getPaymentForDate(date, "I+A");
                        
/*            
            // deduct monthly fee on 1st of month            
            if (day == 0 || date.getDate() == 1) {
                fees += monthlyFee;
            }            
*/
            
            double dailyRate = fixedRate;
            if (dailyRate == 0.0) {
                dailyRate = interestCalculator.getInterestRateForDate(date) + primeDelta;
            }
            dailyRate = dailyRate/100.0/364.25;
            
            double dayBalance = balance;
            
            double interest = 0.0;
            if (true) {
                // assume the banker schmucks do it this way                
                interest = (dailyRate * dayBalance); 
                dayBalance += interest;       // + daily interest
                
                dailyPayment = Math.min(dailyPayment, dayBalance);                
                dayBalance -= dailyPayment;   // - daily payment
            } else {
                dailyPayment = Math.min(dailyPayment, dayBalance);
                dayBalance -= dailyPayment;   // - daily payment                
            
                interest = (dailyRate * dayBalance); 
                dayBalance += interest;       // + daily interest
            }                        
            
            bondDays[day].addInterest(interest);
            bondDays[day].makePayment(dailyPayment);

            paidCapital += dailyPayment;            
            paidInterest += interest;
            
            bondDays[day].setBalance(dayBalance);            
            
            balance = dayBalance;
            
            if (balance <= 1.0) {  // rounding error                
                done = true;
            }
        }
        
        // resize for actual #days
        effectiveDays = day;
        resize(true);
    }
    
    public BondDay[] getBondDates(char dmInd)
    {
        int count = dmInd == 'D' ? effectiveDays : months;
        BondDay[] result = new BondDay[count];
        if (dmInd == 'D') {
            System.arraycopy(bondDays, 0, result, 0, bondDays.length);
        } else {
          GregorianCalendar cal = getStartDateCalendar();
          Date date = cal.getTime();
          int month = date.getMonth();
          int monthIdx = 0;
          BondDay monthTotals = new BondDay(0);
          // accumulate totals for each day per month
          for (int day = 0; day < effectiveDays; ++day) {
              monthTotals.addInterest(bondDays[day].getInterest());
              monthTotals.makePayment(bondDays[day].getPayment());
              double balance = bondDays[day].getBalance();
              monthTotals.setBalance(balance);
              cal.add(Calendar.DATE, 1);
              Date nextDate = cal.getTime();
              // next month?
              if (nextDate.getMonth() != month) {
                  result[monthIdx++] = monthTotals;
                  month = nextDate.getMonth();
                  monthTotals = new BondDay(day, nextDate);
              }
          }
          //Initialise any left over month
          while (monthIdx < count) {
              result[monthIdx++] = monthTotals;
          }
        }        
        return result;
    }
    
    private void resize(boolean shrink)
    {
        // resize for actual #days
        if (effectiveDays < totalDays) {
            BondDay[] temp;
            if (shrink) {
                temp = new BondDay[effectiveDays];
            }
            else {
                temp = new BondDay[totalDays];
            }
            System.arraycopy(bondDays, 0, temp, 0, effectiveDays);
            bondDays = temp;
        }
    }
}
