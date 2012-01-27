/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package come.intelius.sen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sxu
 * 
 * reads in CASS address test set
 * outputs CASS correct address     geocoded latlon
 * 
 */
public class CASS2GeocoderBing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        Address ad=new Address();
        try {
            File input=new File("NATLSTG1_withkey_google.txt");
            if (!input.exists()) {
                System.out.println("no input");
                System.exit(-1);
            }
            BufferedReader br=new BufferedReader(new FileReader(input));
            
            File output=new File("NATLSTG1_withkey_googleGeocoded.txt");
            if (!input.exists()) output.createNewFile();
            BufferedWriter bw=new BufferedWriter(new FileWriter(output,true));
            String line;
            while ((line=br.readLine())!=null){
                String key = line.split("\\t")[0]; 
                String correctAddress = line.split("\\t")[1].split("\\|")[1];
                //System.out.println(correctAddress);
                ad.setFullAddress(correctAddress);
                //change proxy to solve the limit 2500 per ip per day by google
                ArrayList<String> proxies=Proxy.getProxyList();
                int proxyid=0;
                int proxytotal=proxies.size();
                System.setProperty("http.proxyHost", proxies.get(proxyid).split("\\t")[0]);
                System.setProperty("http.proxyPort", proxies.get(proxyid).split("\\t")[1]);
                
                LatLon latlon=ad.getLatLonFromGoogle();
                int counter=0;
                while (((latlon.getLat()-0.0))<0.0000001&&(counter<10)){
                    Thread.sleep(10000);//if the response from google is invalid, wait 5 seconds then retry
                    System.out.println("No response for this address(wait 5 sec then retry):"+key+"\t"+correctAddress);
                    if (counter==2) System.out.println(GoogleGeocoder.GoogleURLContructor(correctAddress));//if there are 5 no responses, emit the URL for manual examination
                    latlon=ad.getLatLonFromGoogle();
                    counter++;//if no response, try 10 time, then move on
                    if (counter==7) {//if no response for 8 times, change proxy
                        proxyid++;
                        if (proxyid>=proxytotal) {
                            System.out.println("All proxy have been used, wait for 24 hours then start from this first proxy");
                            Thread.sleep(86400);
                            proxyid=0;
                        }
                        System.setProperty("http.proxyHost", proxies.get(proxyid).split("\\t")[0]);
                        System.setProperty("http.proxyPort", proxies.get(proxyid).split("\\t")[1]);
                    }
                }
                String latlonstr=latlon.toStringwithPrec();
                
                bw.append(key+"\t"+correctAddress+"|"+latlonstr);
                System.out.println(key+"\t"+correctAddress+"|"+latlonstr);
                bw.newLine();
                bw.flush();
                
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(CASS2GeocoderBing.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(ad.getLatLonFromYahoo().toString());
    }
}
