/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intelius.sen;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author sxu
 */
public class BingGeocoder {
    
    
    public static void main(String[] args) throws InterruptedException{
        String fulladdress="9 BROADWAY PMB 66349, SOMERVILLE MA 02145";
        URL bingurl=BingURLContructor(fulladdress);
        //System.out.println(bingurl);
        LatLon latlon=ParseLatLonFromGeocoder(bingurl);
        //System.out.println(latlon.toStringwithPrec());
        
    }

    /*
     * construct URL with full address
     * example:
     * input:   320 vairo Blvd, Apt C, State College, PA 16803 
     * output:  http://dev.virtualearth.net/REST/v1/Locations/US/PA/16803/State%20College/320%20vairo%20Blvd%20Apt%20C?o=xml&key=ArHg1cMweFLBZD6p0TMspBnCfW7kG2_PdNd4xaE6CxgZbbqNFPj9OCglIpTLSpeL
     */
    public static URL BingURLContructor(String fulladdress) {
        
        if (fulladdress.isEmpty()) return null;
        String constructed="";
        String[] sp=fulladdress.split(", ");
        String s=fulladdress.split(", ")[sp.length-1];//first, get the last part of address: "State College PA 16803"
        String[] citystatezip=s.split("\\s");
        String last=citystatezip[citystatezip.length-1];//last: 16803
        if (last.length()==2)//sometimes last is not zip, it's the state
        {
            constructed=last+"/"+citystatezip[0];
            for (int i=1; i<citystatezip.length-1;i++){
                constructed=constructed+"%20"+citystatezip[i];
            }
            constructed=constructed.concat("/");//constructed after this step: PA/16803/State College/
        }else{//when there is zipcode
            constructed=citystatezip[citystatezip.length-2]+"/";//PA/
            constructed=constructed+last+"/"+citystatezip[0];//PA/16803/State
            for (int i=1; i<citystatezip.length-2;i++){
                constructed=constructed+"%20"+citystatezip[i];
            }
        }
        constructed=constructed+"/";
        
        constructed=constructed+fulladdress.split(", ")[0].replaceAll("\\s", "%20");//second, get the second part of address: "320 Varo Blvd Apt C"
        
        //System.out.println(constructed);
        

        
        URL url=null;
        try {
            //example url: http://dev.virtualearth.net/REST/v1/Locations/US/WA/98052/Redmond/1%20Microsoft%20Way?o=xml&key=ArHg1cMweFLBZD6p0TMspBnCfW7kG2_PdNd4xaE6CxgZbbqNFPj9OCglIpTLSpeL
            
            
            
            url = new URL("http://dev.virtualearth.net/REST/v1/Locations/US/"+constructed+"?o=xml&key=ArHg1cMweFLBZD6p0TMspBnCfW7kG2_PdNd4xaE6CxgZbbqNFPj9OCglIpTLSpeL");
            System.out.println(url.toString());
        } catch (MalformedURLException ex) {
            return null;
        }
        return url;
    }

    public static LatLon ParseLatLonFromGeocoder(URL bingurl) throws InterruptedException {
        
        if (bingurl==null) return null;
        LatLon latlon=new LatLon();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            //System.out.println("start fetching result from:" + bing.toString());
            db = dbf.newDocumentBuilder();
//set proxi
//            System.setProperty("http.proxyHost", "118.97.117.138");
//            System.setProperty("http.proxyPort", "8080");
            Document doc = db.parse(bingurl.openStream());
            
            Element rootEle = doc.getDocumentElement();
            NodeList nd= rootEle.getElementsByTagName("Resources");
            if (nd!=null && nd.getLength()>0){
                Element el = (Element)nd.item(0);
                //here needs to add another layer, nodelist "location" 
                if (getTagValue(el,"Latitude")!=null) {
                    String lats = getTagValue(el,"Latitude");
                    String lons = getTagValue(el,"Longitude");
                    //get location_type as precision
                String precs = getTagValue(el, "CalculationMethod");
                precs=precs.concat("|Confidence_"+getTagValue(el, "Confidence"));
                precs=precs.concat("|MatchCode_"+getTagValue(el, "MatchCode"));
                
                //System.out.println(lats+"  "+lons);
                latlon.setLatLon(Double.parseDouble(lats), Double.parseDouble(lons));
                latlon.setPrecision(precs);
                }else{
                    latlon.setLatLon(0.0, 0.0);
                    latlon.setPrecision("failed");
                   }
                
                
                
            }
            
        }catch (SAXException ex) {
            Logger.getLogger(BingGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //System.out.println("reached daily limit for yahoo crawling, now will wait for 12 hours before continue");
            //Thread.sleep(43200);
            Logger.getLogger(BingGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BingGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return latlon;
    }

    private static String getTagValue(Element el,String s) {
        NodeList nl=el.getElementsByTagName(s);
        if (nl!=null && nl.getLength()>0){
            Element e=(Element)nl.item(0);
            return e.getFirstChild().getNodeValue();
        }
        return null;
    }
    
}
