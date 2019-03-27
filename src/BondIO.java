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

public class BondIO {
    
    private BondIO() {        
    }

    public static BondDefinition loadBondXmlFile(String filename){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                  
        try {
                          
              //Using factory get an instance of document builder
              DocumentBuilder db = dbf.newDocumentBuilder();
                          
              //parse using builder to get DOM representation of the XML file
              Document dom = db.parse(new File(filename));
                  
              Element docEle = dom.getDocumentElement();

            //get a nodelist of <Prime> elements
              NodeList nl = docEle.getElementsByTagName("Bond");
              if(nl != null && nl.getLength() > 0) {
                  String nameStr = null,
                          principalStr = null,
                          dateStr = null,
                          termStr = null,
                          paymentStr = null,
                          instDayStr = null,
                          setupFeeStr = null,
                          monthFeeStr = null,
                          rateTypeStr = null,
                          rateStr = null;                  
                  for(int i = 0 ; i < nl.getLength();i++) {
                      Element bondElement = (Element)nl.item(i);
                      NodeList nl2 = bondElement.getElementsByTagName("Name");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          nameStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Principal");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          principalStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Term");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          termStr = em.getFirstChild().getNodeValue();
                      }                      
                      nl2 = bondElement.getElementsByTagName("Start-Date");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          dateStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Payment");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          paymentStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Instalment-Day");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          instDayStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Setup-Fee");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          setupFeeStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Monthly-Fee");                        
                      if(nl2 != null && nl2.getLength() > 0) {
                          Element em = (Element)nl2.item(0);
                          monthFeeStr = em.getFirstChild().getNodeValue();
                      }                      
                      nl2 = bondElement.getElementsByTagName("Rate-Type");
                      if(nl != null && nl2.getLength() > 0) {
                         Element em = (Element)nl2.item(0);
                         rateTypeStr = em.getFirstChild().getNodeValue();
                      }
                      nl2 = bondElement.getElementsByTagName("Rate");
                      if(nl != null && nl2.getLength() > 0) {
                         Element em = (Element)nl2.item(0);
                         rateStr = em.getFirstChild().getNodeValue();
                      }                      
                  }
                  BondDefinition bd = new BondDefinition();
                  bd.setName(nameStr);
                  bd.setPrincipal(principalStr);
                  bd.setTerm(termStr);
                  bd.setDate(dateStr);
                  bd.setInstalDay(instDayStr);
                  bd.setPayment(paymentStr);
                  bd.setInstFee(setupFeeStr);
                  bd.setMonthFee(monthFeeStr);
                  bd.setRateType(rateTypeStr);
                  bd.setRate(rateStr);
                  return bd;
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
        
        return null;
      }    
}
