package gmg.grenademachinegun;

/**
 *
 * @author Olav Rune
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
     * Putting the x and y error
     *
     * @param x
     * @param y
     */
    public void putError(float x, float y) {

        xError = x;
        yError = y;

        available = true;

    }

    /**
     *
     * @return getError as a double containing xError, Yerror
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
     * Return the error as a double array containing x,y
     *
     * @return
     */
    public double[] getErrorWithoutFlagChange() {

        double[] e = new double[]{xError, yError};
        return e;
        // FACK

    }

    /**
     * Return the error as a byte array
     *
     * @return
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
     * Putting the fire setting
     *
     * @param settings
     */
    public void putFireSettings(double[] settings) {

        this.fireSettings = settings;
    }

    /**
     * Returning the fire setting. 1 or 0
     *
     * @return
     */
    public double[] getFireSettings() {

        return fireSettings;
    }
}
