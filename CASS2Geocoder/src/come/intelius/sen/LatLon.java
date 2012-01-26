/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package come.intelius.sen;

/**
 *
 * @author sxu
 */
public class LatLon{
        
        private double lat;
        private double lon;
        private String precision="";
        
        public LatLon(double la,double lo){
            lat=la;
            lon=lo;
        }
        
        public LatLon(){}
        
        public LatLon(double la, double lo, String prec){
            lat=la;
            lon=lo;
            precision=prec;
        }

        public void setLatLon(double la, double lo) {
            this.lat=la;
            this.lon=lo;
        }
        
        public LatLon getLatLon(){
            return this;
        }
        
        public void setPrecision(String prec){
            this.precision=prec;
        }
        
        public String getPrecision(){
            return this.precision;
        }
        
        public double getLat(){
            return this.lat;
        }
        
        public double getLon(){
            return this.lon;
        }
        
        public String toString(){
            return Double.toString(lat)+", "+Double.toString(lon);
        }
        
        public String toStringwithPrec(){
            return Double.toString(lat)+", "+Double.toString(lon)+"|"+this.precision;
        }
        
}
