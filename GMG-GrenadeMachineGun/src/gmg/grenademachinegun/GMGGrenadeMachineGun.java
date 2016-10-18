/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;


/**
 *
 * @author Olav Rune
 */
public class GMGGrenadeMachineGun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
    // ColorTrack c = new ColorTrack();
     
     
     // Adding Semaphores
      int numberOfPermits = 1;
    int numberOfProducers = 1;
    boolean fairness = true;  // used in the Java's Semaphore

    System.out.println("Busy waiting...");

    Semaphore semaphore = new Semaphore(numberOfPermits, fairness);

    StorageBox storageBox = new StorageBox();
    
    //Producer producer[] = new Producer[numberOfProducers];

    for (int i = 0; i < numberOfProducers; i++)
    {
      //producer[i] = new Producer(storageBox, i+1, semaphore);
      // Start producer threads
     // producer[i].start();
    }
    
    Consumer c1 = new Consumer(storageBox, 1, semaphore, numberOfProducers);

    // Start consumer threads
    c1.start();
     
     
     
     
     
     
     
     
     
    }

     
     /*
     
        System.out.println("kake");
     SerialTest main = new SerialTest();
      
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                                    // create a scanner so we can read the command-line input
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                }
            }
        };
        t.start();
        System.out.println("Started");
    }
     
     
    // SerialTest s = new SerialTest();
     
     
     
    //SerialInterface s = new SerialInterface();
    //s.start();
       /* GuGl gugl = new GuGl();

        // KeyController keys = new KeyController();
        ImageAnalyzer img = new ImageAnalyzer(gugl);
        ThunderLauncher thlu = new ThunderLauncher(gugl);

        */
    


}