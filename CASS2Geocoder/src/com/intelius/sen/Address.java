/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intelius.sen;

/**
 *
 * @author sxu
 */
public class Address {


    
    private String fulladdress="";
    
    public Address(String s){
        fulladdress=s;
    }
    
    public Address(){}
    
    public void setFullAddress(String s){
        this.fulladdress=s;
    }
    
    public String getFullAddress(){
        return this.fulladdress;
    }
    
    public LatLon getLatLonFromYahoo() throws InterruptedException{
        
        if (this.fulladdress.isEmpty()) return null;
        
        LatLon latlon = new LatLon();
        latlon=YahooGeocoder.ParseLatLonFromGeocoder(YahooGeocoder.YahooURLContructor(this.fulladdress));
        return latlon;
    }

    public LatLon getLatLonFromGoogle() throws InterruptedException{
        if (this.fulladdress.isEmpty()) return null;
        
        LatLon latlon = new LatLon();
        latlon=GoogleGeocoder.ParseLatLonFromGeocoder(GoogleGeocoder.GoogleURLContructor(this.fulladdress));
        return latlon;
    }
    
    public LatLon getLatLonFromBing() throws InterruptedException{
        LatLon latlon=null;
        latlon=BingGeocoder.ParseLatLonFromGeocoder(BingGeocoder.BingURLContructor(this.fulladdress));
        return latlon;
    }
    
    public String toString(){
        return this.fulladdress.toString();
    }
    
    public static void main(String[] args) throws InterruptedException{

        Address ad=new Address();
        ad.setFullAddress("320 Vairo Blvd, Apt C, State College, PA 16803");
        
        System.out.println(ad.getLatLonFromGoogle().toString());
    }
    
}
