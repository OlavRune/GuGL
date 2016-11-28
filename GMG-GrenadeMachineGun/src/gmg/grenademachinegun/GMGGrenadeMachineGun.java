package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Olav Rune, Såklart
 */
public class GMGGrenadeMachineGun {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        int numberOfPermits = 1;
        int numberOfProducers = 1;
        boolean fairness = true;  // used in the Java's Semaphore

        System.out.println("Busy waiting...");

        // Creating all the semaphores required
        Semaphore semaphoreCoordinates = new Semaphore(numberOfPermits, fairness);
        Semaphore semaphoreSettings = new Semaphore(numberOfPermits, fairness);
        Semaphore semaphoreVideoStream = new Semaphore(numberOfPermits, fairness);
        Semaphore semaphoreLauncher = new Semaphore(numberOfPermits,fairness);

        // Creating the storageboxes required
        StorageBoxCoordinates storageBoxCoordinates = new StorageBoxCoordinates();
        StorageBoxSettings storageBoxSettings = new StorageBoxSettings();
        StorageBoxVideoStream storageBoxVideoStream = new StorageBoxVideoStream();
        
        LauncherCommands launcherCommands = new LauncherCommands();
        
        Launcher l = new Launcher(launcherCommands, semaphoreLauncher);
        l.start();
        

        ColorTrack color = new ColorTrack(storageBoxCoordinates, storageBoxSettings, storageBoxVideoStream, semaphoreCoordinates, semaphoreSettings, semaphoreVideoStream, launcherCommands, semaphoreLauncher);

        SerialCom c = new SerialCom(semaphoreCoordinates, storageBoxCoordinates);
        c.start();

        UDPrecive recive = new UDPrecive(storageBoxSettings, 1, semaphoreSettings, numberOfProducers, 4000);
        recive.start();

        //UDPsend send = new UDPsend(storageBoxVideoStream, numberOfPermits, semaphoreVideoStream, 5000, "192.6.6.6");
        //send.start();
        

        // Starting the "Main" thread
        color.start();

    }

}
