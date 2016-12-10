package gmg.grenademachinegun;

/**
 *
 * @author Olav Rune, code
 * @author Matias, Javadoc
 */
public class StorageBoxCoordinates {

    private int contents;               // value to be stored
    private boolean available = false;  // flag

    private float xError;
    private float yError;

    private double[] fireSettings;

    public boolean getAvailable() {
        return available;
    }

    /**
     * putError() method, puts error in x and y direction,
     * where x is horizontal and y is vertical
     * @param x
     * @param y
     */
    public void putError(float x, float y) {

        xError = x;
        yError = y;

        available = true;

    }

   /**
     * getError() method of type double array with flag change
     * @return e, double array containing error in x and y direction, 
     * where x is horizontal and y is vertical
     */
    public double[] getError() {

        if (available == true) {
            available = false;

        }
        double[] e = new double[]{xError, yError};
        return e;
        // FACK

    }

 /**
     * getError() method of type double array without flag change
     * @return e, double array containing error in x and y direction, 
     * where x is horizontal and y is vertical
     */
    public double[] getErrorWithoutFlagChange() {

        double[] e = new double[]{xError, yError};
        return e;
        // FACK

    }

    /**
     * getErrorAsByte() method of type byte array with flag change
     * @return e, double array containing error in x and y direction, 
     * where x is horizontal and y is vertical
     */
    public byte[] getErrorAsByte() {

        if (available == true) {
            available = false;
        }
        byte[] e;
        e = new byte[]{(byte) xError, (byte) yError};
        return e;

    }

    /**
     * putfireSettings() method, puts fire setting
     *
     * @param settings, of type double array
     */
    public void putFireSettings(double[] settings) {

        this.fireSettings = settings;
    }

    /**
     * getFireSettings() method, gets the firesetting
     * @return firesettings, as a double array of 1 or 0
     */
    public double[] getFireSettings() {

        return fireSettings;
    }
}
