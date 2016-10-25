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
public class UDPsend extends Thread {
  private StorageBoxCoordinates  storageBox;
  private Semaphore   semaphore;
  private int         consumerID;
  private int         numberOfProducerThreads;
  private boolean     stop;
  private boolean     available;
  private long        sleepTime;

  public UDPsend(StorageBoxCoordinates storageBox, int consumerID, Semaphore semaphore,
                  int numberOfProducers) {
    this.storageBox = storageBox;
    this.semaphore = semaphore;
    this.consumerID = consumerID;
    this.sleepTime = 1;
    stop = false;
    this.numberOfProducerThreads = numberOfProducers;
  }
  
  public void run() {
    int value = 0;
    int activeThreads = Thread.activeCount();  // init active thread counter
    
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
        value = storageBox.get();
      }
      semaphore.release();
      endTime = System.currentTimeMillis();
       long time = (endTime-startTime);
            System.out.println("Time elapsed from consumer acquired to release " + time + "ms");
      // normally non-critical operations will be ouside semaphore:
      if (available) {
        System.out.println("Consumer got: " + value);
      }
      // check if all producers have terminated
      if (Thread.activeCount() == activeThreads - numberOfProducerThreads)
      {
        stop = true;
      }
      try {
        Thread.sleep(sleepTime);  // Consumer will slow down execution
      }
      catch (InterruptedException e) {
      }
    }
    System.out.println("Consumer #" + this.consumerID + " stopped...");
  }
 
}
