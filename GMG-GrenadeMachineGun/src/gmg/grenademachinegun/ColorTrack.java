package gmg.grenademachinegun;

import java.awt.AWTException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * Why does Java programmers wear glasses?
 * They can't see sharp. 8-)
 * hohohohohohho
 * 
 * @author Olav Rune, Head of programming
 */
public class ColorTrack extends Thread {

    private VideoCapture capture;

    private Mat webcam_image;
    private JFrame cameraFrame;
    private JFrame hsvFrame;
    private JFrame thresholdFrame;

    private Panel cameraPanel;
    private Panel hsvPanel;
    private Panel thresholdPanel;
    private Mat hsv_image;
    private Mat thresholded;
    private Mat thresholded2;
    private double[] data;
    private Mat circles;
    private double[] hsv_values;

    private boolean launcherActive = true;
    private boolean cameraFramActive = true; //Flag to activate cameraframe
    private boolean hsvFrameActive = true; //flag to activate the hsvFrame
    private boolean thresholdFrameActive = false;   //flag to activate the hsvFrame 
    private boolean timerActive = false;
    private boolean videoStreamActive;
    private boolean manualModeActive;

    private Scalar hsv_min;
    private Scalar hsv_max;

    private List<Mat> lhsv;
    private Mat array255;
    private Mat distance;
    List<MatOfPoint> contours;

    long timeAtErrorPut;

    private Launcher l;

    boolean b = true;

    private StorageBoxCoordinates storageBoxCoordinates;
    private StorageBoxSettings storageBoxSettings;
    private StorageBoxVideoStream storageBoxVideoStream;

    private Semaphore semaphoreCoordinates;
    private Semaphore semaphoreSettings;
    private Semaphore semaphoreVideoStream;
    private boolean VideoStreamBoxAvailable;    //Flag to check if the Storagebox for videostream is available
    private boolean newSettings;    //flag to check if there are new settings in StoreageboxSettings

    int counter = 0;
    private long startTime;
    private long endTime;
    private double[] newSettingsFromStorageBox;
    private int fire;
    private int shootToKill;

    public ColorTrack(StorageBoxCoordinates storageBoxCoordinates, StorageBoxSettings storageBoxSettings, StorageBoxVideoStream storageBoxVideoStream, Semaphore semaphoreCoordinates, Semaphore semaphoreSettings, Semaphore semaphoreVideoStream) {

        this.storageBoxCoordinates = storageBoxCoordinates;
        this.storageBoxSettings = storageBoxSettings;
        this.storageBoxVideoStream = storageBoxVideoStream;
        this.semaphoreCoordinates = semaphoreCoordinates;
        this.semaphoreSettings = semaphoreSettings;
        this.semaphoreVideoStream = semaphoreVideoStream;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            createMat();
            createCameraFrame();
            createHsvFrame();
            createThresholdFrame();
            addInitialValues();

        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        int start = 1;
        long startTime = 0;
        long endTime;
        long startTotTime;
        long endTotTime;

        counter = start;

        boolean stop = false;
        l = new Launcher();
        l.start();

        while (!stop) {

            if (timerActive) {
                startTimer();
            }

            // Check if there are new settings from the GUI
            TryUpdateSettings();

            if (launcherActive) {

                // If launcher is active and fire is choosed, then fire
                if (fire == 1) {
                    l.execute(Launcher.Command.FIRE);
                    fire = 0;
                }

                // If autofire activated, turn on warning LED.
                // No warning shots will be given.
                if (shootToKill == 1) {
                    l.execute(Launcher.Command.LEDON);

                } else {
                    l.execute(Launcher.Command.LEDOFF);
                }

            }

            try {
                // acquire semaphore for storeagebox
                // Release after putting error values
                semaphoreCoordinates.acquire();

            } catch (InterruptedException e) {
            }

            //track colors and get error values
            trackColors();

            
            if (timerActive) {
                stopTimer();
            }

            if (videoStreamActive) {
                // If videostream is activated. Send image over UDP.
                // Future availability, perhaps.  maybe... 
                streamVideo();
            }

        }

    }

    /**
     * Create the cameraframe and set the correct values
     */
    private void createCameraFrame() throws AWTException {

        if (cameraFramActive == true) {
            cameraFrame = new JFrame("Camera");
            cameraFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cameraFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);

                }
            });
            cameraFrame.setSize(640, 480);
            cameraFrame.setBounds(0, 0, cameraFrame.getWidth(), cameraFrame.getHeight());
            cameraPanel = new Panel();
            cameraFrame.setContentPane(cameraPanel);
            cameraFrame.setVisible(true);

        }

        capture = new VideoCapture(0);

        // Choosing the best resolution for image processing
        capture.set(3, 1366);
        capture.set(4, 768);

        capture.read(webcam_image);

        if (cameraFramActive == true) {
            // Adjusting the frame to the image.
            cameraFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
        }
        array255 = new Mat(webcam_image.height(), webcam_image.width(), CvType.CV_8UC1);
        array255.setTo(new Scalar(255));

    }

    /**
     * Create the HSV frame if choosen in setup
     */
    private void createHsvFrame() {

        if (hsvFrameActive == true) {

            hsvFrame = new JFrame("HSV");
            hsvFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            hsvFrame.setSize(640, 480);
            hsvFrame.setBounds(300, 100, hsvFrame.getWidth() + 300, 100 + hsvFrame.getHeight());
            hsvPanel = new Panel();
            hsvFrame.setContentPane(hsvPanel);
            hsvFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
            hsvFrame.setVisible(true);
        }

    }

    /**
     * Creating the threshold frame if choosen in setup
     */
    public void createThresholdFrame() {

        if (thresholdFrameActive == true) {
            thresholdFrame = new JFrame("Threshold");
            thresholdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            thresholdFrame.setSize(640, 480);
            thresholdPanel = new Panel();
            thresholdFrame.setContentPane(thresholdPanel);
            thresholdFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

            thresholdFrame.setVisible(true);
        }

    }

    /**
     * Reading the next frame and tracking a given color in it. Calculates the
     * angle from the center of the object to the center of the screen and puts
     * the value in the storagebox for coordinates
     */
    private void trackColors() {

        long currentTime = System.currentTimeMillis();
        capture.read(webcam_image);

        /**
         * Making sure that it is a delay from the last error put before the net
         * frame is processed. That way you don't read the same errorvalue
         * several times.
         */
        if (currentTime - timeAtErrorPut > 800) {

            if (!webcam_image.empty()) {

                //Adding blur to remove noise
                //Imgproc.blur(webcam_image, webcam_image, new Size(7, 7));
                // converting to HSV image for better processing
                Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);

                //Checking if the hsv image is in range.
                Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);

                // Lots of processing...
                Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
                Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
                Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats  
                Mat S = lhsv.get(1);
                Mat V = lhsv.get(2);
                Core.subtract(array255, S, S);
                Core.subtract(array255, V, V);
                S.convertTo(S, CvType.CV_32F);
                V.convertTo(V, CvType.CV_32F);
                Core.magnitude(S, V, distance);
                Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
                Imgproc.GaussianBlur(thresholded, thresholded, new Size(9, 9), 0, 0);
                Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 8, 200, 100, 0, 0);
                Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                Imgproc.drawContours(webcam_image, contours, -1, new Scalar(255, 0, 0), 2);

                getTargetError();
                addInfoToImage();

                distance.convertTo(distance, CvType.CV_8UC1);

                updatePanels();

            } else {

                System.out.println(" --(!) No captured frame -- Break!");

            }
        }

    }

    /**
     * Creating the different matrix and scalars needed
     */
    private void createMat() {
        webcam_image = new Mat();
        hsv_image = new Mat();
        thresholded = new Mat();
        thresholded2 = new Mat();
        data = new double[3];
        circles = new Mat();

        lhsv = new ArrayList<Mat>(3);

        array255 = new Mat(webcam_image.height(), webcam_image.width(), CvType.CV_8UC1);

        array255.setTo(new Scalar(255));

        distance = new Mat(webcam_image.height(), webcam_image.width(), CvType.CV_8UC1);

        hsv_min = new Scalar(1, 1, 1);
        hsv_max = new Scalar(1, 1, 1);

        contours = new ArrayList<MatOfPoint>();
    }

    /**
     * calculates the error value from the image and puts the value in
     * storagebox
     */
    private void getTargetError() {

        int x = getXError(contours);
        int y = getYError(contours);
        contours.clear();

        if (x > 0) {

            Core.circle(webcam_image, new Point(x, y), 4, new Scalar(50, 49, 0, 255), 4);
            float centerX = webcam_image.width() / 2;   // getting the centerpoint
            float centerY = webcam_image.height() / 2;

            // centerCirle
            Core.circle(webcam_image, new Point(centerX, centerY), 4, new Scalar(50, 49, 0, 255), 4);

            // Field of view for the current camera
            float cameraAngleX = 70.42f;
            float cameraAngleY = 43.30f;

            // Calculating the pixel error
            float pixErrorX = x - centerX;
            float pixErrorY = -y + centerY;

            // Calculating the error angle
            float angleErrorX = (pixErrorX / centerX) * cameraAngleX / 2;
            float angleErrorY = (pixErrorY / centerY) * cameraAngleY / 2;

            // If the error exceeds a given value, then put the values in storagebox for correction
            if (((angleErrorX > 3 || angleErrorX < -3) || (angleErrorY > 3 || angleErrorY < -3)) && manualModeActive == false) {
                if (angleErrorX > 8 || angleErrorX < -8 ) {
                    // Adding a factor for a more smooth movement
                    angleErrorX = angleErrorX*0.4f;
                 
                }
                if (angleErrorY > 8 || angleErrorY < -8 ) {
                    // Adding a factor for a more smooth movement
               
                    angleErrorY = angleErrorY*0.4f;
                }
                calculateAngleAndPutToStorageBox(angleErrorX, angleErrorY);
            
                timeAtErrorPut = System.currentTimeMillis();

            } else if (shootToKill == 1) {
                // If shoot to kill activated, then fire
                l.execute(Launcher.Command.FIRE);
            }

            Core.line(webcam_image, new Point(x, y), new Point(centerX, centerY), new Scalar(150, 150, 100)/*CV_BGR(100,10,10)*/, 3);

        }

    }

    /**
     * calculates the x value
     *
     * @param contours
     * @return
     */
    public int getXError(List<MatOfPoint> contours) {
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        int x = 0;
        for (int i = 0; i < contours.size(); i++) {
            mu.add(i, Imgproc.moments(contours.get(i), false));
            Moments p = mu.get(i);
            x = (int) (p.get_m10() / p.get_m00());
        }
        return x;
    }

    /**
     * calculates the Y value
     *
     * @param contours
     * @return
     */
    public int getYError(List<MatOfPoint> contours) {
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        int y = 0;
        for (int i = 0; i < contours.size(); i++) {
            mu.add(i, Imgproc.moments(contours.get(i), false));
            Moments p = mu.get(i);
            y = (int) (p.get_m01() / p.get_m00());
        }
        return y;
    }

    /**
     * Adding some initial start up values
     */
    private void addInitialValues() {

        // Add initial values to HSV min settings
        double[] d = new double[]{3, 144, 115};
        hsv_min.set(d);
        byte b = 255 - 128;
        byte[] e = new byte[]{3, b, 115};

        // Add initial vales to HSV max settings
        double[] m = new double[]{3, 245, 178};
        hsv_max.set(m);

        timeAtErrorPut = System.currentTimeMillis() + 150;
        videoStreamActive = false;

    }

    /**
     * Udate the settings from storagebox
     */
    private void updateSettings() {

        // Retrive the settings from storagebox
        newSettingsFromStorageBox = storageBoxSettings.getSettings();

        // Printing the values retrived, for debugging only
        String print = null;
        for (int i = 1; i < 20; i++) {
            print = print + newSettingsFromStorageBox[i] + " ";

        }
        System.out.println(print);

        // Adding the hsv minimun setting
        double[] min = new double[]{newSettingsFromStorageBox[1], newSettingsFromStorageBox[3], newSettingsFromStorageBox[5]};
        hsv_min.set(min);

        // Adding the maximum setting for HSV processing
        double[] max = new double[]{newSettingsFromStorageBox[2], newSettingsFromStorageBox[4], newSettingsFromStorageBox[6]};
        hsv_max.set(max);

        // Checking if videostream is activated
        if ((newSettingsFromStorageBox[7]) == 1) {
            videoStreamActive = true;
        } else {
            videoStreamActive = false;
        }

        // Checking if the grenade launcher is in manual or automatic mode
        if (newSettingsFromStorageBox[8] == 1) {
            manualModeActive = true;
            updateManualMoveValues(true);
        } else {
            manualModeActive = false;
            updateManualMoveValues(false);
        }

        // Setting Fire and Shoot to kill flags
        fire = (int) newSettingsFromStorageBox[14];
        shootToKill = (int) newSettingsFromStorageBox[13];

    }

    /**
     * Updating the panels with a new image
     */
    private void updatePanels() {

        if (cameraFramActive == true) {
            cameraPanel.setimagewithMat(webcam_image);
            cameraFrame.repaint();
        }
        if (hsvFrameActive == true) {
            hsvPanel.setimagewithMat(hsv_image);
            hsvFrame.repaint();
        }
        if (thresholdFrameActive == true) {
            thresholdPanel.setimagewithMat(thresholded);
            thresholdFrame.repaint();
        }

    }

    /**
     * Adding the RGB and HSV values to the images
     */
    private void addInfoToImage() {

        if (cameraFramActive == true) {
            Core.circle(webcam_image, new Point(210, 210), 10, new Scalar(100, 10, 10), 3);
            data = webcam_image.get(210, 210);
            Core.putText(webcam_image, String.format("(" + String.valueOf(data[0]) + "," + String.valueOf(data[1]) + "," + String.valueOf(data[2]) + ")"), new Point(30, 30), 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
                    , 1.0, new Scalar(100, 10, 10, 255), 3);
        }
        if (hsvFrameActive == true) {
            Core.line(hsv_image, new Point(150, 50), new Point(202, 200), new Scalar(100, 10, 10)/*CV_BGR(100,10,10)*/, 3);
            Core.circle(hsv_image, new Point(210, 210), 10, new Scalar(100, 10, 10), 3);
            hsv_values = hsv_image.get(210, 210);

            Core.putText(hsv_image, String.format("x" + "(" + String.valueOf(hsv_values[0]) + "," + String.valueOf(hsv_values[1]) + "," + String.valueOf(hsv_values[2]) + ")"), new Point(30, 30), 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
                    , 1.0, new Scalar(50, 10, 10, 255), 3);

        }
    }

    /**
     * Checking if there are new settings available. If yes, then retrieve the
     * new settings and apply them
     */
    private void TryUpdateSettings() {
        semaphoreSettings.tryAcquire();
        newSettings = storageBoxSettings.getAvailable();

        if (newSettings) {
            // apply settings if there are new available
            updateSettings();
        }
        semaphoreSettings.release();
    }

    /**
     * Add a timestamp from the current time
     */
    private void startTimer() {

        startTime = System.currentTimeMillis();
    }

    /**
     * Stop the timer and calculate the total time. Print the elapsed time if
     * uncommented system.print
     */
    private void stopTimer() {

        endTime = System.currentTimeMillis();
        long totTime = endTime - startTime;
        // System.out.println("Time elapsed from producer acquired to release " + totTime + "ms");
    }

    /**
     * Put the current image to the storagebox for videostream
     */
    private void streamVideo() {

        if (semaphoreVideoStream.tryAcquire()) {

            VideoStreamBoxAvailable = storageBoxVideoStream.getAvailable();
            if (VideoStreamBoxAvailable) {
                storageBoxVideoStream.put(webcam_image);
            }

            semaphoreVideoStream.release();

        }
    }

    /**
     * Getting x and y values from storagebox settings and putting them in
     * storagebox for coordinates.
     *
     * @param active
     */
    private void updateManualMoveValues(boolean active) {

        if (active) {
            float x = (float) newSettingsFromStorageBox[15];
            float y = (float) newSettingsFromStorageBox[16];

            storageBoxCoordinates.putError(x, y);
            
            notifyAll();

        }
    }

    /**
     * Calculating the new x and y angle by adding the error to the current
     * angle.
     *
     * @param x
     * @param y
     */
    private void calculateAngleAndPutToStorageBox(float x, float y) {

        double[] d = storageBoxCoordinates.getErrorWithoutFlagChange();

        float newX = (float) (d[0] + x);
        float newY = (float) (d[1] + y);
        storageBoxCoordinates.putError(newX, newY);
        semaphoreCoordinates.release();
        System.out.println("error x: " + x + " error Y: " + y + " new angle x: " + newX + " y: " + newY);
    }

}
