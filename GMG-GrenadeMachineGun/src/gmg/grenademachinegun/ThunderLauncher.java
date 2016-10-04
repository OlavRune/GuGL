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

        System.out.println("MissileLauncher RUN()");
        int start = 0;
        int max = 255;

        for (int x = 0; x < 255; x++) {
            gugl.put(x);
            System.out.println("MissileLauncher#1 ( " + this.turret.getName() + ")(" + "put" + gugl.get() + ")");

            try {
                //sleep((int)(Math.random() * 100)); // random sleep-time (integer)
                Thread.sleep(100);  // suspend this thread for a number of millis
            } catch (InterruptedException e) {
            }
        }
        if (turret.getName().equalsIgnoreCase("Thread-1")) {  // compare
            gugl.put(-1); // stop sign for the consumer
        }
        System.out.println("MissileLauncher # 1 stopped...");
    }
}
