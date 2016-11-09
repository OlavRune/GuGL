
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bakken
 */
public class SerialCom implements SerialPortEventListener {
    
     SerialPort serialPort;
    private final Semaphore semaphore;
    private final StorageBoxCoordinates storagebox;
     
     
     
     public SerialCom(Semaphore semaphore, StorageBoxCoordinates storageBox){
         
         this.semaphore = semaphore;
         this.storagebox = storageBox;
         
     }
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyACM0", // Raspberry Pi
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
    };

    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    private InputStream inputStream;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;
    
    boolean running = true;
    
    public void initialize(){
     // the next line is for Raspberry Pi and 
     // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
     System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            
            output = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
            
            
            //Random r = new Random();
            while(running){
                long start = System.currentTimeMillis();
             byte[] sendValue = getValues();
             byte[] inputByte = new byte[2];
                
               
             //int i = r.nextInt(255);
           
             output.write(sendValue);
             inputStream.read(inputByte);
             if(sendValue == inputByte){
                 long stop = System.currentTimeMillis();
                 long timeTaken = start-stop;
                 System.out.println("Time from aquire to recive: " + timeTaken);
             }
             else
                    System.out.println("not equal");
             
             
             
             
             //System.out.println(i);
               // System.out.println("Sendt to Arduino: " + sendValue + "   Going to sleep!" + sendValue[0] + " " + sendValue[1]);
             Thread.sleep(20);
            }
            
            serialPort.addEventListener(this);
            
            serialPort.notifyOnDataAvailable(true);
            
           
            
            
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    } 
   /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */ 
   public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }  
   
  

   
   /**
     * Handle an event on the serial port. Read the data and print it.
     */
   public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    
}


    private byte[] getValues() {
       
        
                
                     byte[] b = new byte[2];
        
         try {
             semaphore.acquire();
         } catch (InterruptedException ex) {
             Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         boolean available = storagebox.getAvailable();
         
         if(available){
            
             //d = storagebox.getErrorAsByte();
             b = storagebox.getErrorAsByte();
         }
         
         semaphore.release();
         
         
         
         
         return b;
         
    }
   
   
}
