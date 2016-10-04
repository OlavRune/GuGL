/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

/**
 *
 * @author Matias
 */
public class ThunderLauncher implements Runnable {

    private Launcher launcher;
    private GuGl gugl;
    private Thread turret;

    public ThunderLauncher(GuGl ggl) {
        gugl = ggl;
        turret = new Thread(this); // creates a thread
        turret.start();
    }

    @Override
    public void run() {
        
      for (int i = 0; i<255; i++){
        gugl.put(i);
            System.out.println("MissileLauncher#1 ( "+this.turret.getName()+ ")"+"("+"put"+i+")");
        
        
        }  
    }

}
