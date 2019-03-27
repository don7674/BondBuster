/*
 * RateIO.java
 *
 * Created on January 22, 2011, 11:58 AM
 */

package bondcalculator;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Don
 */
public class RateIO {
    
    /** Creates a new instance of RateIO */
    private RateIO() {
    }

    public static void loadRatesXmlFile(String filename, InterestCalculator calc){
      //get the factory
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
      try {
			
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
			
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new File(filename));
                
            Element docEle = dom.getDocumentElement();

          //get a nodelist of <Prime> elements
            NodeList nl = docEle.getElementsByTagName("Prime");
            if(nl != null && nl.getLength() > 0) {
                for(int i = 0 ; i < nl.getLength();i++) {
                    String dateStr = null, rateStr = null;
                    Element primeElement = (Element)nl.item(i);
                    NodeList nl2 = primeElement.getElementsByTagName("Date");                        
                    if(nl2 != null && nl2.getLength() > 0) {
                        Element dateElement = (Element)nl2.item(0);
                        dateStr = dateElement.getFirstChild().getNodeValue();
                    }

                    nl2 = primeElement.getElementsByTagName("Rate");
                    if(nl != null && nl2.getLength() > 0) {
                       Element rateElement = (Element)nl2.item(0);
                       rateStr = rateElement.getFirstChild().getNodeValue();
                    }
                    calc.addRate(BondUtils.stringToDate(dateStr, null), Double.parseDouble(rateStr));
                }
            }
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
       } catch(SAXException se) {
           se.printStackTrace();
       } catch(IOException ioe) {
           ioe.printStackTrace();
       } catch(Exception x) {
           //String s = x.toString();
           x.printStackTrace();
      }
    }

/*    
    public static boolean loadRatesPAV(String filename, InterestCalculator calc) {
        try {
            File rateFile = new File(filename);
            FileReader fr = new FileReader(rateFile);
            BufferedReader rIn = new BufferedReader(fr);
            int k;
            String dateStr = null, rateStr = null;
            String str;
            while ((str = rIn.readLine()) != null)
            {
                str = str.trim();
                if (str.equals("") || str.startsWith("#"))
                {
                    continue;
                }
                if ((k = str.indexOf('=')) > 0) {
                    String val = str.substring(k+1);
                    if (val != null) {
                         str = str.substring(0, k);
                         str = str.trim();
                         str = str.toLowerCase();
                         val = val.trim();
                         
                         if (str.equals("date")) {
                             dateStr = val;
                         }
                         else if (str.equals("rate")) {
                             rateStr = val;
                         }
                         else {
                             continue;
                         }
                         if (dateStr != null && rateStr != null) {
                             calc.addRate(DateUtils.stringToDate(dateStr, null), Double.parseDouble(rateStr));
                             dateStr = rateStr = null;
                         }
                    }
                }
            }
        } catch (java.io.IOException x) {
            System.err.println("Fatal error: No rates file (rates.ini) !");
            System.exit(1);
        }        

        return true;
    }
*/
    
}
