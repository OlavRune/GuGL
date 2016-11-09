package gmg.grenademachinegun;

import gmg.grenademachinegun.SerialCom;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;




 class Runner extends Thread{

    private final Semaphore semaphore;
    private final StorageBoxCoordinates storageBox;
     
    
     public Runner(Semaphore semaphore, StorageBoxCoordinates storageBox) {
        this.sc = new SerialCom(semaphore,storageBox);
         
         this.semaphore = semaphore;
         this.storageBox = storageBox;
     }
    
  
   
   private boolean running = true;
   
   SerialCom sc;
   
   
   public void run(){
     
       while (running){
           sc.initialize();
           
         
           try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       
   }
      
}