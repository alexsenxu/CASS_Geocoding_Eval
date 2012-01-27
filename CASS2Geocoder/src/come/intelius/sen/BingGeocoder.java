/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package come.intelius.sen;

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
        String fulladdress="320 vairo Blvd, Apt C, State College, PA 16803";
        URL bingurl=BingURLContructor(fulladdress);
        System.out.println(bingurl);
        LatLon latlon=ParseLatLonFromGeocoder(bingurl);
        System.out.println(latlon.toStringwithPrec());
        
    }

    /*
     * construct URL with full address
     * example:
     * input:   320 vairo Blvd, Apt C, State College, PA 16803 
     * output:  http://where.yahooapis.com/geocode?q=320+vairo+Blvd,+Apt+C,+State+College,+PA+16803
     */
    public static URL BingURLContructor(String fulladdress) {
        
        if (fulladdress.isEmpty()) return null;
        String s=fulladdress.replaceAll("\\s", "+");
        URL url=null;
        try {
            //example url: http://dev.virtualearth.net/REST/v1/Locations/US/WA/98052/Redmond/1%20Microsoft%20Way?o=xml&key=ArHg1cMweFLBZD6p0TMspBnCfW7kG2_PdNd4xaE6CxgZbbqNFPj9OCglIpTLSpeL
            
            url = new URL("http://dev.virtualearth.net/REST/v1/Locations/US/"+s+"?o=xml&key=ArHg1cMweFLBZD6p0TMspBnCfW7kG2_PdNd4xaE6CxgZbbqNFPj9OCglIpTLSpeL");
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
            NodeList nd= rootEle.getElementsByTagName("Resources");
            if (nd!=null && nd.getLength()>0){
                Element el = (Element)nd.item(0);
                //here needs to add another layer, nodelist "location" 
                String lats = getTagValue(el,"Latitude");
                String lons = getTagValue(el,"Longitude");
                
                //get location_type as precision
                String precs = getTagValue(el, "CalculationMethod");
                precs=precs.concat("|Confidence_"+getTagValue(el, "Confidence"));
                precs=precs.concat("|MatchCode_"+getTagValue(el, "Good"));
                
                //System.out.println(lats+"  "+lons);
                latlon.setLatLon(Double.parseDouble(lats), Double.parseDouble(lons));
                latlon.setPrecision(precs);
            }
            
        }catch (SAXException ex) {
            Logger.getLogger(BingGeocoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("reached daily limit for yahoo crawling, now will wait for 12 hours before continue");
            Thread.sleep(43200);
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
