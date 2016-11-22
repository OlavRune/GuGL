package gmg.grenademachinegun;


/**
 *
 * @author Guess who
 */
public class StorageBoxSettings {

    private int contents;               // value to be stored
    private boolean available = false;  // flag

    private double[] setttings;


    /**
     * Return true if new values available
     * @return 
     */
    public boolean getAvailable() {
        return available;
    }

 /**
  * Putting new settings
  * @param Setting 
  */
    public void putSettings(double[] Setting) {
   
            this.setttings = Setting;

            available = true;
     

    }

    /**
     * Return settings
     * @return 
     */
    public double[] getSettings() {
        if (available == true) {
            available = false;
        }
        return setttings;
    }

}
