package gmg.grenademachinegun;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Launcer, extends Thread
 * @author Olav Rune, James Green: http://coffeefueledcreations.com/blog/?p=131
 * @author James Green, Matias, Javadoc
 */
public class Launcher extends Thread {

    short vid = 0x2123;
    short pid = 0x1010;
    private Device dev = null;

    // Launcher commands according to protocol. Values to be sent over USB.
    private final byte[][] COMMANDS = {{0x02, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, {0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, {0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, {0x02, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}};
    private final LauncherCommands launcherCommands;
    private final Semaphore semaphoreLauncher;

    /**
     * Enum commands for the Launcer class
     * 
     */
    public enum Command {
        STOP, LEDON, LEDOFF, FIRE   // Written commands for easier user interface.
    }

    /**
     * Constructor of the Launcher class
     * @param launcherCommands LauncerCommands
     * @param semaphoreLauncher SemaphoreLauncher
     */
    public Launcher(LauncherCommands launcherCommands, Semaphore semaphoreLauncher) {
        this.launcherCommands = launcherCommands;
        this.semaphoreLauncher = semaphoreLauncher;
        usbSetup();
    }

    /**
     * run() method, overrides the run() method in Thread Class
     */
    @Override
    public void run() {
        boolean stop = false;
        int command = 0;
        boolean fire = false;
        while (!stop) {

            try {
                semaphoreLauncher.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean available = launcherCommands.getAvailable();
            if (available) {
                command = launcherCommands.getCommand();
            }
            fire = launcherCommands.getFireCommand();
            semaphoreLauncher.release();

            execute(command);
            if (fire) {
                execute(3);

            }
            command = 0;

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Javadoc for this method might not be needed, but can ease the learning process
     * usbSetup() opens the Thunder Launcer
     */
    private void usbSetup() {
        dev = USB.getDevice((short) 0x2123, (short) 0x1010); // Vendor ID, Product ID
        try {
            System.out.println("Trying to open device");
            dev.open(1, 0, -1); // Connect to device (Configuration(default), Interface(Control), AlternativeConfig(None))
            System.out.println("DEVICE CONNECTED!");
        } catch (USBException ex) {
            System.out.println("Please check the driver for device VID: " + (short) 0x2123 + ", PID: " + (short) 0x1010); // Didn't work
            ex.printStackTrace();
        }
        //zero();
    }

    

    /**
     * Primary function to use launcher. Example: launcher.execute(launcer.COMMANDS.FIRE);
     * @param c use the public "Command" enum to control the turret
     *
     * The fire command takes about 3300 milliseconds
     */
    public void execute(int c) {
        long duration = 1;
        try {
            if (dev.isOpen()) {
                switch (c) {
                    case 1: // LED_ON
                        dev.controlMsg(0x21, 0x09, 0, 0, COMMANDS[1], COMMANDS[1].length, 2000, false);
                        break;
                    case 2: // LED_OFF
                        dev.controlMsg(0x21, 0x09, 0, 0, COMMANDS[2], COMMANDS[2].length, 2000, false);
                        break;
                    case 3:     // FIAAAA
                        dev.controlMsg(0x21, 0x09, 0, 0, COMMANDS[3], COMMANDS[3].length, 2000, false);
                        duration = 3300; //3300 milis to execute???

                        break;
                }
                long wait = System.currentTimeMillis() + duration;
                while (wait > System.currentTimeMillis()) {

                    // WAITING!
                }

                dev.controlMsg(0x21, 0x09, 0, 0, COMMANDS[0], COMMANDS[0].length, 2000, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
