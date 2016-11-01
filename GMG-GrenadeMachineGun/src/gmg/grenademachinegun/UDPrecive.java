/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*  ------ HOW TO DOUBLE[] TO BYTE[]
Convert your doubles into a byte array using java.nio.ByteBuffer

ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
for(double d : doubles) {
   bb.putDouble(d);
}
get the byte array

byte[] bytearray = bb.array();
send it over the net and then convert it to double array on the receiving side

ByteBuffer bb = ByteBuffer.wrap(bytearray);
double[] doubles = new double(bytearray.length / 8);
for(int i = 0; i < doubles.length; i++) {
    doubles[i] = bb.getDouble();
}
*/







package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author ib
 */
public class UDPrecive extends Thread {

    private StorageBoxSettings storageBox;
    private Semaphore semaphore;
    private int consumerID;
    private int numberOfProducerThreads;
    private boolean stop;
    private boolean available;
    private long sleepTime;

    private GUIcorrected gui;
    private JFrame guiFrame;

    public UDPrecive(StorageBoxSettings storageBox, int consumerID, Semaphore semaphore,
            int numberOfProducers) {
        this.storageBox = storageBox;
        this.semaphore = semaphore;
        this.consumerID = consumerID;
        this.sleepTime = 1;
        stop = false;
        this.numberOfProducerThreads = numberOfProducers;

        createGUI();
    }

    public void run() {
        int value = 0;
        int activeThreads = Thread.activeCount();  // init active thread counter

        long startTime = 0;
        long endTime = 0;

        while (!stop) {

        }
        /**
         * while (!stop) { try { semaphore.acquire(); startTime =
         * System.currentTimeMillis();
         *
         * }
         * catch(InterruptedException e) { stop = true; } available =
         * storageBox.getAvailable(); if (available) { value = storageBox.get();
         * } semaphore.release(); endTime = System.currentTimeMillis(); long
         * time = (endTime-startTime); // System.out.println("Time elapsed from
         * consumer acquired to release " + time + "ms"); // normally
         * non-critical operations will be ouside semaphore: if (available) {
         * System.out.println("Consumer got: " + value); } // check if all
         * producers have terminated if (Thread.activeCount() == activeThreads -
         * numberOfProducerThreads) { stop = true; } try {
         * Thread.sleep(sleepTime); // Consumer will slow down execution } catch
         * (InterruptedException e) { } } System.out.println("Consumer #" +
         * this.consumerID + " stopped...");
         *
         */
    }

    private void createGUI() {

        guiFrame = new JFrame("Slider");
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setSize(640, 480);
        gui = new GUIcorrected();
        guiFrame.setContentPane(gui);
        guiFrame.setVisible(true);
        gui.importclass(this);

    }

    /**
     * Protocol:
     * 
     */
    public void updateSettings() {

        int hueMinValue = gui.getHueMinValue();
        int hueMaxValue = gui.getHueMaxValue();

        int satMinValue = gui.getSaturationMinValue();
        int satMaxValue = gui.getSaturationeMaxValue();

        int valMinValue = gui.getValueMinValue();
        int valMaxValue = gui.getValueMaxValue();

        // putting the settings in a double field
        double[] val = new double[]{hueMinValue,satMinValue,valMinValue, hueMaxValue, satMaxValue, valMaxValue};
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(UDPrecive.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] b = new byte[]{2,3,4};
        storageBox.putHsvSettings(val);
       storageBox.putHsvSettingsByte(b);
        //System.out.println("putted values");

        semaphore.release();
        System.out.println("reløease");

    }

}
