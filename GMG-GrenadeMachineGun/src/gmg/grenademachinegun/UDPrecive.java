
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
 * @author 
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
    private static final int PARAMS = 25;
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

    }

    public void run() {
    
        long startTime = 0;
        long endTime = 0;

        while (!stop) {
            byte[] b = new byte[25];
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
