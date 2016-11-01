/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.nio.ByteBuffer;

/**
 *
 * @author ib
 */
public class StorageBoxSettings {

    private int contents;               // value to be stored
    private boolean available = false;  // flag

    private double[] hsvSettings;
    private byte[] hsvSettingsByte;


    public boolean getAvailable() {
        return available;
    }

    public int get() {
        if (available == true) {
            // value not available, wait for producer
            available = false;
        }
        return contents;
    }

    // reading value and resetting flag, wake up other threads (producer)
    public void put(int value) {
        if (available == false) {
            contents = value; // store value
            available = true; // now available for consumer
        }
    }

    public void putHsvSettings(double[] hsvValues) {
       if(available == false){
            this.hsvSettings = hsvValues;

            available = true;
       }
     
    }
    
      public void putHsvSettingsByte(byte[] hsvValues) {
       if(available == false){
            this.hsvSettingsByte = hsvValues;

            available = true;
       }
     
    }
      public byte[] getHsvSettingsByte(){
           if(available == true){
            available = false;
        }
        return hsvSettingsByte;
      }
    

    
    



   
    public double[] getHsvSettings(){
        if(available == true){
            available = false;
        }
        return hsvSettings;
    }
    
 
    
}
