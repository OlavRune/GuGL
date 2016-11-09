/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

/**
 *
 * @author ib
 */
public class StorageBoxCoordinates {
  private int contents;               // value to be stored
  private boolean available = false;  // flag
  
  private float xError;
  private float yError;
  
  private double[] fireSettings;
 

  public boolean getAvailable() {
    return available;
  }
  
  public synchronized int get() {
    if (available == true) {
      // value not available, wait for producer
      available = false;
    }
    return contents;
  }

  // reading value and resetting flag, wake up other threads (producer)
  public void put(int value) {
      
      contents = value; // store value
      available = true; // now available for consumer
  }
  
  
  
    public void putError(float x, float y){
      
      xError = x;
      yError = y;
      
      available = true;
      
  }
    
    /**
     * 
     * @return getError as a double containing xError, Yerror
     */
    public  double[] getError(){
        
        if(available == true){
            available = false;                  
        }
        double[] e = new double[]{xError,yError};
        return e;
    }
    
    public void putFireSettings(double[] settings){
        
        this.fireSettings = settings;
    }
    
    public double[] getFireSettings(){
        
        return fireSettings;
    }
}
