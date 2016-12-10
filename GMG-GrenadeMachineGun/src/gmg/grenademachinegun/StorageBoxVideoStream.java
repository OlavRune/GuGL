package gmg.grenademachinegun;

import org.opencv.core.Mat;

/**
 * StorageBoxVideoStream Class
 * @author Olav Rune, code
 * @author Matias, javadoc
 */
public class StorageBoxVideoStream {
//Variables.....................................................
    private Mat image;               // value to be stored
    private boolean available = false;  // flag
//..............................................................
    /**
     * getAvailable(), boolan method. 
     * @return, returns true if a new image is available
     */
    public boolean getAvailable() {
        return available;
    }

    /**
     * get() method, of type Mat
     * @return, returns image matrice
     */
    public Mat get() {
        if (available == true) {
            // value not available, wait for producer
            available = false;
        }
        return image;
    }

    /**
     * put() method, puts mat, matrice to stream to GUI
     *
     * @param imageToStream
     */
    public void put(Mat imageToStream) {

        image = imageToStream; // stores value in new variable "image" of type Mat
        available = true; // now available for consumer
    }

}
