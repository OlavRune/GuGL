package gmg.grenademachinegun;

import org.opencv.core.Mat;

/**
 *
 * @author NummeNum
 */
public class StorageBoxVideoStream {

    private Mat image;               // value to be stored
    private boolean available = false;  // flag

    /**
     * Return true if a new image is available
     *
     * @return
     */
    public boolean getAvailable() {
        return available;
    }

    /**
     * Return image
     *
     * @return
     */
    public Mat get() {
        if (available == true) {
            // value not available, wait for producer
            available = false;
        }
        return image;
    }

    /**
     * put mat
     *
     * @param imageToStream
     */
    public void put(Mat imageToStream) {

        image = imageToStream; // store value
        available = true; // now available for consumer
    }

}
