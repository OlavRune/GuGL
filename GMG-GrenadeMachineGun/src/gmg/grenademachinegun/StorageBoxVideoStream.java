/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import org.opencv.core.Mat;

/**
 *
 * @author ib
 */
public class StorageBoxVideoStream {
  private Mat image;               // value to be stored
  private boolean available = false;  // flag
  

 

  public boolean getAvailable() {
    return available;
  }
  
  public Mat get() {
    if (available == true) {
      // value not available, wait for producer
      available = false;
    }
    return image;
  }

  // reading value and resetting flag, wake up other threads (producer)
  public void put(Mat imageToStream) {
    
      image = imageToStream; // store value
      available = true; // now available for consumer
    }
  
  
  }
  
   
      
  

