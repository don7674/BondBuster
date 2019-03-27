package bondcalculator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.KeyToGroupMap;

public class BondChart {
	
	private final static String[] monthName = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	public static enum ChartType{VALUE, PERCENT};
	
	private BondChart() {}
	
	@SuppressWarnings("deprecation")
	public static ChartPanel createChart(ChartType type, final Bond bond) {
	    BondDay[] bd = bond.getBondDates('M');
	    if (bd != null) {
		int nbrMonths = bond.getEffectiveTerm();
		//int year0 = bond.getStartDate().getYear()-1900;
		int year0 = bond.getStartDate().getYear() + 1900;
		int month0 = bond.getStartDate().getMonth();
		//final CategoryDataset dataset = createDataset(year0, bd);
		final CategoryDataset dataset = createDatasetYY(type, year0, month0, nbrMonths/12 + 1, bd);
		//nbrMonths = dataset.getRowCount();
		final JFreeChart chart = createFreeChart(type, year0, nbrMonths, dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		//chartPanel.setPreferredSize(new java.awt.Dimension(590, 350));
		return chartPanel;
	    }
	    return null;
	}
		
	private static CategoryDataset createDatasetYY(
	    final ChartType type,
	    final int year0,
    	    final int month0,
    	    final int nbrYears,
    	    final BondDay[] bd) {
	    DefaultCategoryDataset result = new DefaultCategoryDataset();
	    int bdIdx = 0;
	    double interestTot = 0;
	    double capitalTot = 0;
	    double paymentTot = 0;
	    String ymStr = "";
	    for (int year = year0; bdIdx < bd.length && year < year0 + nbrYears; ++year) {
			ymStr = "" + year;
			// reset year totals
			paymentTot = interestTot = capitalTot = 0;
	        for (int month = (year == year0) ? month0 : 0; month < 12; ++month) {
	            double payment = bd[bdIdx].getPayment(); 
	            paymentTot += payment; 
	            double interest = bd[bdIdx].getInterest();
	            interestTot += interest;
	            double capital = (payment - interest);
	            capitalTot += capital;
	            if (++bdIdx >= bd.length) {
	            	break;
	            }
	        }
	        if (type == BondChart.ChartType.VALUE) {
	        	result.addValue(interestTot, "Interest", ymStr);
	        	result.addValue(capitalTot, "Payment", ymStr);
	        }
	        else if (paymentTot > 0){
	        	result.addValue(100 * interestTot/paymentTot, "Interest", ymStr);
	        	result.addValue(100 * capitalTot/paymentTot, "Payment", ymStr);            	
	        }
    	}
	    // add any left over portion
	    if (capitalTot * interestTot > 0.0) {
	        if (type == BondChart.ChartType.VALUE) {
	            result.addValue(interestTot, "Interest", ymStr);
                result.addValue(capitalTot, "Payment", ymStr);
            }
            else {
                result.addValue(100 * interestTot/paymentTot, "Interest", ymStr);
                result.addValue(100 * capitalTot/paymentTot, "Payment", ymStr);            	
            }
	    }
	    return result;
	}	
	
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset for the chart.
     * 
     * @return A sample chart.
     */
    private static JFreeChart createFreeChart(
    	final ChartType type,
    	final int year0,
    	final int nbrMonths,
    	final CategoryDataset dataset) {
    
        final JFreeChart chart =
                ChartFactory.createStackedBarChart(
                    type == BondChart.ChartType.VALUE ?
                    "Payment vs Interest" : "Payment vs Interest %",	// chart title
                    "Year",                      // domain axis label
                    type == BondChart.ChartType.VALUE ?
                    "Payment" : "% Payment",     // range axis label
                    dataset,                     // data
                    PlotOrientation.VERTICAL,    // the plot orientation
                    true,                        // legend
                    true,                        // tooltips
                    false                        // urls
        );
        
        int year = year0;
        String key = "" + year;
        KeyToGroupMap map = new KeyToGroupMap(key);
        for (int m = 0; m < nbrMonths; ++m) {
            if (m%12 == 0) {
                ++year;
                key = "" + year;
                map = new KeyToGroupMap(key);
            }
            map.mapKeyToGroup(monthName[m%12].substring(0,1), key);
            //map.mapKeyToGroup(monthName[m%12].substring(0,3) + (year%year0), key);
        }        
        
        return chart;
    }
}
