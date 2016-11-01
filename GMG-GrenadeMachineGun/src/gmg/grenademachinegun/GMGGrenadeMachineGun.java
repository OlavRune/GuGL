/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;
import javax.swing.JFrame;


/**
 *
 * @author Olav Rune
 */
public class GMGGrenadeMachineGun {


    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
//    // ColorTrack c = new ColorTrack();
     
     
     // Adding Semaphores
      int numberOfPermits = 1;
    int numberOfProducers = 1;
    boolean fairness = true;  // used in the Java's Semaphore

    System.out.println("Busy waiting...");
 
    Semaphore semaphoreCoordinates = new Semaphore(numberOfPermits, fairness);
    Semaphore semaphoreSettings = new Semaphore(numberOfPermits,fairness);
    Semaphore semaphoreVideoStream = new Semaphore(numberOfPermits, fairness);

    StorageBoxCoordinates storageBoxCoordinates = new StorageBoxCoordinates();
    StorageBoxSettings storageBoxSettings = new StorageBoxSettings();
    StorageBoxVideoStream storageBoxVideoStream = new StorageBoxVideoStream();
    
  
    
    
    ColorTrackSemaphoresSplitClass color = new ColorTrackSemaphoresSplitClass(storageBoxCoordinates, storageBoxSettings, storageBoxVideoStream, semaphoreCoordinates, semaphoreSettings, semaphoreVideoStream);
    

    ArduinoSerial ArduinoSerial = new ArduinoSerial(storageBoxCoordinates, 1, semaphoreCoordinates);
    
    UDPrecive recive = new UDPrecive(storageBoxSettings, 1, semaphoreSettings, numberOfProducers);
    UDPsend send = new UDPsend(storageBoxVideoStream, numberOfPermits, semaphoreVideoStream, 5000, "192.6.6.6");
    recive.start();
    send.start();

    // Start consumer threads
    color.start();
    ArduinoSerial.start();
     
     
     
     
     
     
     
     
     
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