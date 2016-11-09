/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.nio.ByteBuffer;

/**
 *
 * @author ib
 */
public class StorageBoxSettings {

    private int contents;               // value to be stored
    private boolean available = false;  // flag

    private double[] setttings;
    private byte[] hsvSettingsByte;

    public boolean getAvailable() {
        return available;
    }

    public int get() {
        if (available == true) {
            // value not available, wait for producer
            available = false;
        }
        return contents;
    }

    // reading value and resetting flag, wake up other threads (producer)
    public void put(int value) {
        if (available == false) {
            contents = value; // store value
            available = true; // now available for consumer
        }
    }

    public void putSettings(double[] hsvValues) {
        if (available == false) {
            this.setttings = hsvValues;

            available = true;
        }

    }

    public double[] getSettings() {
        if (available == true) {
            available = false;
        }
        return setttings;
    }

}
