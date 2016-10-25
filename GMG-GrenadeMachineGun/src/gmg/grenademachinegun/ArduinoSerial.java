/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;

/**
 *
 * @author ib
 */
public class ArduinoSerial extends Thread {
  private StorageBoxCoordinates  storageBox;
  private Semaphore   semaphore;
  private int         consumerID;

  private boolean     stop;
  private boolean     available;
  private long        sleepTime;
  
  private double[] error;

  public ArduinoSerial(StorageBoxCoordinates storageBox, int consumerID, Semaphore semaphore) {
    this.storageBox = storageBox;
    this.semaphore = semaphore;
    this.consumerID = consumerID;
    this.sleepTime = 1;
    stop = false;
   
  }
  
  public void run() {
 
    
          long startTime = 0;
        long endTime = 0;
        int counter = 0;
        
        
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
        error = storageBox.getError();
        counter = storageBox.get();
        
      }
      semaphore.release();
      endTime = System.currentTimeMillis();
       long time = (endTime-startTime);
           // System.out.println("Time elapsed from consumer acquired to release " + time + "ms");
      // normally non-critical operations will be ouside semaphore:
      if (available) {
       // System.out.println("ErrorX: " + error[0] + " ErrorY: " + error[1]);
         // System.out.println("Consumer got: " + counter);
        
          
      }
   
      try {
        Thread.sleep(sleepTime);  // ArduinoSerial will slow down execution
      }
      catch (InterruptedException e) {
      }
    }
    System.out.println("Consumer #" + this.consumerID + " stopped...");
  }
 
}
