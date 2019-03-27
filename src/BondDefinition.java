package bondcalculator;

public class BondDefinition {
    public String getNameStr() {
        return nameStr;
    }
    public String getPrincipalStr() {
        return principalStr;
    }
    public String getDateStr() {
        return dateStr;
    }
    public String getTermStr() {
        return termStr;
    }
    public String getPaymentStr() {
        return paymentStr;
    }
    public String getInstDayStr() {
        return instDayStr;
    }
    public String getSetupFeeStr() {
        return setupFeeStr;
    }
    public String getMonthFeeStr() {
        return monthFeeStr;
    }
    public String getRateTypeStr() {
        return rateTypeStr;
    }
    public String getRateStr() {
        return rateStr;
    }
    private String nameStr,
            principalStr,
            dateStr,
            termStr,
            paymentStr,
            instDayStr,
            setupFeeStr,
            monthFeeStr,
            rateTypeStr,
            rateStr;
    
    public void setName(String name) {
        nameStr = name; 
    }
    public void setPrincipal(String amount) {
        principalStr = amount; 
    }
    public void setDate(String date) {
        dateStr = date; 
    }
    public void setTerm(String term) {
        termStr = term; 
    }
    public void setPayment(String payment) {
        paymentStr = payment; 
    }
    public void setInstalDay(String day) {
        instDayStr = day; 
    }
    public void setInstFee(String fee) {
        setupFeeStr = fee; 
    }
    public void setMonthFee(String fee) {
        monthFeeStr = fee; 
    }
    public void setRateType(String type) {
        rateTypeStr = type; 
    }
    public void setRate(String rate) {
        rateStr = rate; 
    }    
}
