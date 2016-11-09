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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

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

    private final int PORT;
    private static final int PARAMS = 20;
    private DatagramSocket socket;
    private DatagramPacket datagram;

    public UDPrecive(StorageBoxSettings storageBox, int consumerID, Semaphore semaphore,
            int numberOfProducers, int PORT) throws SocketException {
        this.storageBox = storageBox;
        this.semaphore = semaphore;
        this.consumerID = consumerID;
        this.sleepTime = 1;
        stop = false;
        this.numberOfProducerThreads = numberOfProducers;
        this.PORT = PORT;
       byte[] buf = new byte[PARAMS];
        datagram = new DatagramPacket(buf, buf.length);
        socket = new DatagramSocket(this.PORT);

       // createGUI();
    }

    public void run() {
        int value = 0;
        int activeThreads = Thread.activeCount();  // init active thread counter

        long startTime = 0;
        long endTime = 0;

        while (!stop) {
            byte[] b = new byte[20];
            try {
                b = receiveParam();
            } catch (IOException ex) {
                Logger.getLogger(UDPrecive.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           // convert values from byte to double
           
           // System.out.println("length of byte array recived: " + b.length);
            double[] d = byteToDouble(b);
           
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPrecive.class.getName()).log(Level.SEVERE, null, ex);
            }
            storageBox.putSettings(d);
            semaphore.release();
           
            
            

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

    
        public byte[] receiveParam() throws IOException{
        socket.receive(datagram);
        byte[] datagramData = datagram.getData();
            //System.out.println("datagramData length: " + datagramData.length);
        return datagramData;
    }

    private double[] byteToDouble(byte[] b) {
       
       
         double[] d = new double[b.length];
      //  System.out.println(b.length);
         
        for (int i = 0; i < b.length; i++ ){
            d[i] = b[i] & (0xff);
          //  System.out.println(i + " " + d[i]);
        }
        
     
        return d;
    }

}
