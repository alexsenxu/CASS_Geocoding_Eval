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
public class GoogleGeocoder {
    
    
    public static void main(String[] args) throws InterruptedException{
        String fulladdress="320 vairo Blvd, Apt C, State College, PA 16803";
        URL googleurl=GoogleURLContructor(fulladdress);
        System.out.println(googleurl);
        LatLon latlon=ParseLatLonFromGeocoder(googleurl);
        System.out.println(latlon.toStringwithPrec());
        
    }

    /*
     * construct URL with full address
     * example:
     * input:   320 vairo Blvd, Apt C, State College, PA 16803 
     * output:  http://where.yahooapis.com/geocode?q=320+vairo+Blvd,+Apt+C,+State+College,+PA+16803
     */
    public static URL GoogleURLContructor(String fulladdress) {
        
        if (fulladdress.isEmpty()) return null;
        String s=fulladdress.replaceAll("\\s", "+");
        URL url=null;
        try {
            url = new URL("http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&address="+s);
        } catch (MalformedURLException ex) {
            return null;
        }
        return url;
    }

    public static LatLon ParseLatLonFromGeocoder(URL googleurl) throws InterruptedException {
        
        if (googleurl==null) return null;
        LatLon latlon=new LatLon();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            //System.out.println("start fetching result from:" + yahoourl.toString());
            db = dbf.newDocumentBuilder();
//set proxi
//            System.setProperty("http.proxyHost", "118.97.117.138");
//            System.setProperty("http.proxyPort", "8080");
            Document doc = db.parse(googleurl.openStream());
            
            Element rootEle = doc.getDocumentElement();
            NodeList nd= rootEle.getElementsByTagName("geometry");
            if (nd!=null && nd.getLength()>0){
                Element el = (Element)nd.item(0);
                //here needs to add another layer, nodelist "location" 
                String lats = getTagValue(el,"lat");
                String lons = getTagValue(el,"lng");
                
                //get location_type as precision
                String precs = getTagValue(el, "location_type");
                //System.out.println(lats+"  "+lons);
                latlon.setLatLon(Double.parseDouble(lats), Double.parseDouble(lons));
                latlon.setPrecision(precs);
            }
            
        }catch (SAXException ex) {
            Logger.getLogger(GoogleGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //System.out.println("reached daily limit for yahoo crawling, now will wait for 12 hours before continue");
            //Thread.sleep(43200);
            Logger.getLogger(GoogleGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GoogleGeocoder.class.getName()).log(Level.SEVERE, null, ex);
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
