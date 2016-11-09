/*
package gmg.grenademachinegun;


import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;



public class Launcher extends Thread{

    private Device dev = null;
    private final byte[][] COMMANDS = {{0x02, 0x20, 0x00,0x00,0x00,0x00,0x00,0x00},{0x03, 0x01, 0x00,0x00,0x00,0x00,0x00,0x00},{0x03, 0x00, 0x00,0x00,0x00,0x00,0x00,0x00},{0x02, 0x10, 0x00,0x00,0x00,0x00,0x00,0x00}};
    public enum Command {STOP,LEDON,LEDOFF,FIRE}

    public Launcher(){
        usbSetup();
    }
    @Override
    public void run(){
        
        
    }

    
    private void usbSetup(){ 
        dev = USB.getDevice((short) 0x2123, (short) 0x1010); // Vendor ID, Product ID
        try {
            System.out.println("Trying to open device");
            dev.open(1, 0, -1); // Open the device (Configuration(default), Interface(Control), AlternativeConfig(None))
        } catch (USBException ex) {
            System.out.println("Please check the driver for device VID:0x2123, PID:0x1010");
            ex.printStackTrace();
        }
        //zero();
    }


    public void execute(Command c){
        long duration = 1;
        try{
        if(dev.isOpen()){
            switch(c){
                case LEDON:
                    dev.controlMsg(0x21, 0x09, 0,0, COMMANDS[1], COMMANDS[1].length, 2000, false);
                    break;
                case LEDOFF:
                    dev.controlMsg(0x21, 0x09, 0,0, COMMANDS[2], COMMANDS[2].length, 2000, false);
                    break;   
                case FIRE:
                    dev.controlMsg(0x21, 0x09, 0,0, COMMANDS[3], COMMANDS[3].length, 2000, false);
                    duration = 3300; //3300 milis to execute???
                    break;          
            } 
            long wait = System.currentTimeMillis()+duration;
            while(wait > System.currentTimeMillis()){
                
                // WAITING!
            }
            
            dev.controlMsg(0x21, 0x09, 0,0, COMMANDS[0], COMMANDS[0].length, 2000, false);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

*/