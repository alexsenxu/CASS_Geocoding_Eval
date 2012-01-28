/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intelius.sen.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sxu
 */
public class List2Stat {
    
    public static void main(String[] args){
        File input=new File("NATLSTG1_withkey_bing.txt");
        try {
            BufferedReader br=new BufferedReader(new FileReader(input));
            String s;
            int count3=0;
            int count2=0;
            int count1=0;
            
            while ((s=br.readLine())!=null){
                String correctAddress=s.split("\\t")[1].split("\\|")[1];
                int seg=correctAddress.split(",").length;
                if (seg==3) count3++;
                if (seg==1) count1++;
                if (seg==2) count2++;
            }
            System.out.println("Segment Count is 3: "+count3);
            System.out.println("Segment Count is 1: "+count1);
            System.out.println("Segment Count is 2: "+count2);
            
        } catch (IOException ex) {
            Logger.getLogger(List2Stat.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
