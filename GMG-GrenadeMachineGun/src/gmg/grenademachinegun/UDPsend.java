/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import org.opencv.core.Mat;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import sun.security.util.Length;

/**
 *
 * @author ib
 */
public class UDPsend extends Thread {
    private StorageBoxVideoStream  storageBox;
    private Semaphore   semaphore;
    private int         consumerID;

    private boolean     stop;
    private boolean     available;
    private long        sleepTime;

    private Mat image;
    private final int PORT;
    private byte[] outByte;
    private String paramString;
    private InetAddress HOST;
    private DatagramSocket s;
    private Mat mat;
    private int value;
    
    // Protocol separators..
    private static final String PARAMSTART = "//*PARAMETERET*";
    private static final String VALUESTART = "*VERDIEN*";
    private static final String END = "*SLUTT*//";
    private static final byte[] PARAMSTARTBYTE = PARAMSTART.getBytes();
    private static final byte[] VALUESTARTBYTE = VALUESTART.getBytes();
    private static final byte[] ENDBYTE = END.getBytes();
    
  
  public UDPsend(StorageBoxVideoStream storageBox, int consumerID, Semaphore semaphore, int port, String ipAddr ) {
    this.storageBox = storageBox;
    this.semaphore = semaphore;
    this.consumerID = consumerID;
    this.sleepTime = 50;
    stop = false;
    
    
    try {
            HOST = InetAddress.getByName(ipAddr);
        } catch (UnknownHostException ex) {
            System.out.println("Exeption in generating InetAddress in UDPSend...  n/" + ex.getMessage());
        }
        this.PORT = port;
        try {
            s = new DatagramSocket();
        } catch (SocketException ex) {
            System.out.println("Exeption in generating DatagramSocket in UDPSend...  n/" + ex.getMessage());
        }
 
  }
  
  public void run() {
 
 
    
          long startTime = 0;
        long endTime = 0;
        
        
    while (!stop) {
      try {
        semaphore.acquire();
        startTime = System.currentTimeMillis();

      }
      catch(InterruptedException e) {
        stop = true;
      }
      available = storageBox.getAvailable();
      if (available) {
        image = storageBox.get();
      }
      semaphore.release();
        
      
      //sendImage(image);
      
      endTime = System.currentTimeMillis();
       long time = (endTime-startTime);
          //  System.out.println("Time elapsed from consumer acquired to release " + time + "ms");
      // normally non-critical operations will be ouside semaphore:
      
      
  
      // check if all producers have terminated
    
      try {
        Thread.sleep(sleepTime);  // Consumer will slow down execution
      }
      catch (InterruptedException e) {
      }
    }
    
    System.out.println("Consumer #" + this.consumerID + " stopped...");
  }

    public boolean sendParam(byte[] bytesToSend) {
        boolean result = false;
        byte[] paramBytes = bytesToSend;
        result = send(paramBytes);
        return result;
    }

    
    
    public boolean sendImage(Mat mat) {
        boolean result = false;
        this.mat = mat;
        BufferedImage buf = matToBufferedImage(mat);
        
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(buf, "jpg", baos);
            baos.flush();
            outByte = baos.toByteArray();
        }catch(IOException e){
            System.out.println("Exception when converting BufferedImage to byte[] in UDPSend...  /n" + e.getMessage());
        }
        
        /*byte[] outByte = new byte[(int) (mat.total() * 
                                            mat.channels())];
        mat.get(0, 0, outByte);
        */
        //BufferedImage bufImg = new BufferedImage(mob.width(), mob.height(), mob.type());
        //Highgui.imencode(outString, mob, mob, params)
        //outByteTemp = [128; -127; 127; -126; 126];
        
        
        
        return send(outByte);
    }
    
    private boolean send(byte[] b){
        boolean result = false;
        DatagramPacket out = new DatagramPacket(b, b.length, HOST, PORT);
        try {
            s.send(out);
            result = true;
        } catch (IOException ex) {
            System.out.println("Exeption in sending (s.send(out)) in UDPSend...  n/" + ex.getMessage());
        }
        return result;
    }
 


    public BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int)matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3: 
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for(int i=0; i<data.length; i=i+3) {
                    b = data[i];
                    data[i] = data[i+2];
                    data[i+2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    } 
}

