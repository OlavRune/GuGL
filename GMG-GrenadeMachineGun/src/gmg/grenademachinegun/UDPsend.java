/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;
import org.opencv.core.Mat;

/**
 *
 * @author ib
 */
public class UDPsend extends Thread {
  private StorageBoxVideoStream  storageBox;
  private Semaphore   semaphore;
  private int         consumerID;
 
  private boolean     stop;
  private boolean     available;
  private long        sleepTime;
  
  private Mat image;
  
  public UDPsend(StorageBoxVideoStream storageBox, int consumerID, Semaphore semaphore ) {
    this.storageBox = storageBox;
    this.semaphore = semaphore;
    this.consumerID = consumerID;
    this.sleepTime = 50;
    stop = false;
 
  }
  
  public void run() {
 
 
    
          long startTime = 0;
        long endTime = 0;
        
        
    while (!stop) {
      try {
        semaphore.acquire();
        startTime = System.currentTimeMillis();

      }
      catch(InterruptedException e) {
        stop = true;
      }
      available = storageBox.getAvailable();
      if (available) {
        image = storageBox.get();
      }
      semaphore.release();
      
      SendImageOverUDP();
      
      endTime = System.currentTimeMillis();
       long time = (endTime-startTime);
          //  System.out.println("Time elapsed from consumer acquired to release " + time + "ms");
      // normally non-critical operations will be ouside semaphore:
      
      
  
      // check if all producers have terminated
    
      try {
        Thread.sleep(sleepTime);  // Consumer will slow down execution
      }
      catch (InterruptedException e) {
      }
    }
    
    System.out.println("Consumer #" + this.consumerID + " stopped...");
  }

    private void SendImageOverUDP() {
     
        // Code to come
    }
 
}
