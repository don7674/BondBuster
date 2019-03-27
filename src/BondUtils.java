/*
 * DateUtils.java
 *
 * Created on January 19, 2011, 9:03 AM
 */

package bondcalculator;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Don
 * see file:///C:/My%20Books/java%20sdk6/api/java/text/SimpleDateFormat.html
 */
public class BondUtils {
    
    /** Creates a new instance of DateUtils */
    private BondUtils() {
    }
    
    public static String dateToString(Date date, String format) {
        String fmt = format != null ? format : "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        String strDate = sdf.format(date);
        return strDate;
    }
    
    public static Date stringToDate(String str, String format) {
        String fmt = format != null ? format : "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        Date date = null;
        try {
            date = sdf.parse(str);    
        }
        catch (java.text.ParseException x)
        {
            System.out.println("Error: Invalid date format: " + str);
        }
        
        return date;
    }
    
    public static String commaFormatted(final String amount, final int minLength) {
        String formatted = "";
        if (amount != null && !amount.isEmpty()) {
            char buff[] = amount.toCharArray();
            int end = amount.length()-1;
            int dot = amount.indexOf('.');            
            int k = (dot > 0) ? dot : end+1;
            while (k <= end) {
                formatted += buff[k];
                ++k;
            }
            int count = 0;                
            k = (dot >= 0) ? dot-1 : end;
            while (k >= 0) {
                if (++count % 4 == 0) {
                    formatted = ',' + formatted;
                    ++count;
                }
                formatted = buff[k] + formatted;
                --k;
            }
            // left padding
            k = minLength - formatted.length(); 
            while (k-- > 0) {
                formatted = ' ' + formatted;
            }
        }        
        return formatted;
    }
    
}
