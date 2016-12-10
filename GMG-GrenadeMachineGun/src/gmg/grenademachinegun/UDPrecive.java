package gmg.grenademachinegun;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author Olav Runde, Code
 * @author Matias, Javadoc
 */
public class UDPrecive extends Thread {
//Variables...............................
    private StorageBoxSettings storageBox;
    private Semaphore semaphore;
    private int consumerID;
    private int numberOfProducerThreads;
    private boolean stop;
    private boolean available;
    private long sleepTime;

    private final int PORT;
    private static final int PARAMS = 25;
    private DatagramSocket socket;
    private DatagramPacket datagram;
//........................................
    
    /**
     * Constructor of the Class UDPrecive
     * 
     * @param storageBox
     * @param consumerID
     * @param semaphore
     * @param numberOfProducers
     * @param PORT
     * @throws SocketException
     */
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

    }

    /**
     * run() method, Overrides the run() method in the Thread Class
     */
    @Override
    public void run() {

        long startTime = 0;
        long endTime = 0;

        while (!stop) {
            byte[] b = new byte[25];    // size of byte to be received
            try {
                b = receiveParam();
            } catch (IOException ex) {
                Logger.getLogger(UDPrecive.class.getName()).log(Level.SEVERE, null, ex);
            }

            // convert values from byte to double
            double[] d = byteToDouble(b);

            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(UDPrecive.class.getName()).log(Level.SEVERE, null, ex);
            }
            storageBox.putSettings(d);
            semaphore.release();

        }

    }

    /**
     * recieveParam(), recieves a datagram 
     * @return datagramData, byte array
     * @throws IOException
     */
    public byte[] receiveParam() throws IOException {
        socket.receive(datagram);
        byte[] datagramData = datagram.getData();
        return datagramData;
    }

    /**
     * Private method, does not require javadoc, but to ease learning it's added.
     * Converts byte array to array of double
     * @param b byte array
     * @return d, array of type double
     */
    private double[] byteToDouble(byte[] b) {

        double[] d = new double[b.length];

        for (int i = 0; i < b.length; i++) {
            d[i] = b[i] & (0xff);
        }

        return d;
    }

}
