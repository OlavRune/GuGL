package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;

/**
 * Main Class of the project; GMGgrenageMachineGun
 *
 * @author Olav Rune, Såklart
 * @author Olav Rune og Matias, Javadoc
 */
public class GMGGrenadeMachineGun {

    /**
     * Main method, Creating instances of Semaphores, storage boxes and staring threads
     *
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
        Semaphore semaphoreLauncher = new Semaphore(numberOfPermits, fairness);

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
        color.start();

    }

}
