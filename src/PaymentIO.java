/*
 * PaymentIO.java
 *
 * Created on January 22, 2011, 1:26 PM
 */

package bondcalculator;

import java.io.*;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Don
 */
public class PaymentIO {
    
    /** Creates a new instance of PaymentIO */
    private PaymentIO() {
    }

    public static boolean loadPaymentsXmlFile(String type, String filename, PaymentFactory pf) {
      //get the factory
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      boolean doInstalment = type.isEmpty() || type.equalsIgnoreCase("Instalment");
      boolean doAdhoc = type.isEmpty() || type.equalsIgnoreCase("Adhoc");
      try {
			
        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();
			
        //parse using builder to get DOM representation of the XML file
        Document dom = db.parse(new File(filename));
                
        Element docEle = dom.getDocumentElement();

        boolean isInstalment;
        
        //get a nodelist of <payment> elements
        NodeList nl = docEle.getElementsByTagName("Payment");
        if (nl != null && nl.getLength() > 0) {

        	for (int i = 0 ; i < nl.getLength();i++) {
        		String dateStr = null,	amountStr = null;
        		Element primeElement = (Element)nl.item(i);
        		NodeList nl2 = primeElement.getElementsByTagName("Date");                        
        		if(nl2 != null && nl2.getLength() > 0) {
        			Element dateElement = (Element)nl2.item(0);
        			dateStr = dateElement.getFirstChild().getNodeValue();
        		}

        		nl2 = primeElement.getElementsByTagName("Amount");
        		if(nl != null && nl2.getLength() > 0) {
        			Element rateElement = (Element)nl2.item(0);
        			amountStr = rateElement.getFirstChild().getNodeValue();
        		}
                        
        		isInstalment = true;
        		nl2 = primeElement.getElementsByTagName("Type");
                        if(nl != null && nl2.getLength() > 0) {
                            isInstalment = false;
                        }
                        
                        if (doInstalment && isInstalment) {
        			pf.addInstalment(BondUtils.stringToDate(dateStr, null), Double.parseDouble(amountStr));
        		}
                        else if (doAdhoc && !isInstalment) {
                            pf.addAdhocPayment(BondUtils.stringToDate(dateStr, null), Double.parseDouble(amountStr));
                        }
        	}
        }
      } catch(ParserConfigurationException pce) {
    	  pce.printStackTrace();
      } catch(SAXException se) {
    	  se.printStackTrace();
      } catch(IOException ioe) {
    	  ioe.printStackTrace();
      } catch(Exception x) {
    	  x.printStackTrace();
      }
    return true;
  }    
    
    public static boolean loadPaymentsPAV(String filename, PaymentFactory pf) {
        try {
            File rateFile = new File(filename);
            FileReader fr = new FileReader(rateFile);
            BufferedReader rIn = new BufferedReader(fr);
            int k;
            String dateStr = null, amountStr = null;
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
                         else if (str.equals("amount")) {
                             amountStr = val;
                         }
                         else {
                             continue;
                         }
                         if (dateStr != null && amountStr != null) {
                             pf.addAdhocPayment(BondUtils.stringToDate(dateStr, null), Double.parseDouble(amountStr));
                             dateStr = amountStr = null;
                         }
                    }
                }
            }
        } catch (java.io.IOException x) {
            //System.err.println("Fatal error: No payments file (rates.ini) !");
            //System.exit(1);
        }        

        return true;
    }
}
