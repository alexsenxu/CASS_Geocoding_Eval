/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intelius.sen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sxu
 */
public class Proxy {
    
    public Proxy(){}
    
    public static ArrayList<String> getProxyList(){
        ArrayList<String> proxies=new ArrayList<String>();
        File proxyfile=new File("proxies.txt");
        try {
            BufferedReader br=new BufferedReader(new FileReader(proxyfile));
            String line;
            while ((line=br.readLine())!=null){
                proxies.add(line);
            }
            br.close();
            return proxies;
        } catch (IOException ex) {
            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public static ArrayList<String> getProxyListFromFile(File f){
        ArrayList<String> proxies=new ArrayList<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(f));
            String line;
            while ((line=br.readLine())!=null){
                proxies.add(line);
            }
            br.close();
            return proxies;
        } catch (IOException ex) {
            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    public static void main(String[] args){
        String ip;
        String port;
        ArrayList<String> ps=Proxy.getProxyList();
        for (int i=0;i<ps.size();i++){
            ip=ps.get(i).split("\\t")[0];
            port=ps.get(i).split("\\t")[1];
            System.out.println(ip+":"+port);
 
            
        }
        System.out.println("size:"+ps.size());
    }
    
}
