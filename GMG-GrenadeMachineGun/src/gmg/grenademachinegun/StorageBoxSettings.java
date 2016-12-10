package gmg.grenademachinegun;

/**
 *
 * @author Guess who
 * @author Matias Javadoc
 */
public class StorageBoxSettings {

    private int contents;               // value to be stored
    private boolean available = false;  // flag

    private double[] setttings;

    /**
     * Return true if new values available
     *
     * @return type bolean
     */
    public boolean getAvailable() {
        return available;
    }

    /**
     * Putting new settings
     *
     * @param Setting type double array
     */
    public void putSettings(double[] Setting) {

        this.setttings = Setting;

        available = true;

    }

    /**
     * Return settings
     *
     * @return settings of type double array
     */
    public double[] getSettings() {
        if (available == true) {
            available = false;
        }
        return setttings;
    }

}
