/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intelius.sen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class CASS2GeocoderYahoo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        Address ad=new Address();
        try {
            File input=new File("NATLSTG1_withkey_yahoo_from100001.txt");
            if (!input.exists()) {
                System.out.println("no input");
                System.exit(-1);
            }
            BufferedReader br=new BufferedReader(new FileReader(input));
            
            File output=new File("NATLSTG1_withkey_yahooGeocoded_from100001.txt");
            if (!input.exists()) output.createNewFile();
            BufferedWriter bw=new BufferedWriter(new FileWriter(output,true));
            String line;
            while ((line=br.readLine())!=null){
                String key = line.split("\\t")[0];
                String correctAddress = line.split("\\t")[1].split("\\|")[1];
                //System.out.println(correctAddress);
                ad.setFullAddress(correctAddress);
                LatLon latlon=ad.getLatLonFromYahoo();
                int counter=0;
                while (((latlon.getLat()-0.0))<0.0000001&&(counter<10)){
                    Thread.sleep(5000);//if the response from yahoo is invalid, wait 5 seconds then retry
                    System.out.println("No response for this address(wait 5 sec then retry):"+key+"\t"+correctAddress);
                    if (counter==5) System.out.println(YahooGeocoder.YahooURLContructor(correctAddress));//if there are 5 no responses, emit the URL for manual examination
                    latlon=ad.getLatLonFromYahoo();
                    counter++;//if no response, try 10 time, then move on
                }
                String latlonstr=latlon.toStringwithPrec();
                bw.append(key+"\t"+correctAddress+"|"+latlonstr);
                System.out.println(key+"\t"+correctAddress+"|"+latlonstr);
                bw.newLine();
                bw.flush();
            
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(CASS2GeocoderYahoo.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(ad.getLatLonFromYahoo().toString());
    }
}
