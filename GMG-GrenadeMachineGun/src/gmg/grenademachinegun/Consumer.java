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
public class Consumer extends Thread {
  private StorageBox  storageBox;
  private Semaphore   semaphore;
  private int         consumerID;
  private int         numberOfProducerThreads;
  private boolean     stop;
  private boolean     available;
  private long        sleepTime;

  public Consumer(StorageBox storageBox, int consumerID, Semaphore semaphore,
                  int numberOfProducers) {
    this.storageBox = storageBox;
    this.semaphore = semaphore;
    this.consumerID = consumerID;
    this.sleepTime = 10;
    stop = false;
    this.numberOfProducerThreads = numberOfProducers;
  }
  
  public void run() {
    int value = 0;
    int activeThreads = Thread.activeCount();  // init active thread counter
    while (!stop) {
      try {
        semaphore.acquire();
      }
      catch(InterruptedException e) {
        stop = true;
      }
      available = storageBox.getAvailable();
      if (available) {
        value = storageBox.get();
      }
      semaphore.release();
      // normally non-critical operations will be ouside semaphore:
      if (available) {
        System.out.println("Consumer #" + this.consumerID + " (" +
                            this.getName() + ") " + " got: " + value);
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
