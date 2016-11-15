/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matias
 */
public class GuGl {

    private int contents;
    private boolean available = false;

    public synchronized int get() {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GuGl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        available = false;
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        while (available == true) {
            System.out.println("put()" + value);
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GuGl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        contents = value; //stores value
        available = true;
        notifyAll();
    }
}
