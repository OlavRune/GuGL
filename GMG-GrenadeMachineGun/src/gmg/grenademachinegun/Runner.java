package gmg.grenademachinegun;

import gmg.grenademachinegun.SerialCom;
import java.util.logging.Level;
import java.util.logging.Logger;




 class Runner extends Thread{
    
  
   
   private boolean running = true;
   
   SerialCom sc = new SerialCom();
   
   
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