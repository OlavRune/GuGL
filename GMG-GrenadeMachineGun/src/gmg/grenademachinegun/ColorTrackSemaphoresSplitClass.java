/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author Olav Rune
 */
public class ColorTrackSemaphoresSplitClass extends Thread {

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

    public ColorTrackSemaphoresSplitClass(StorageBoxCoordinates storageBoxCoordinates, StorageBoxSettings storageBoxSettings, StorageBoxVideoStream storageBoxVideoStream, Semaphore semaphoreCoordinates, Semaphore semaphoreSettings, Semaphore semaphoreVideoStream) {

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

            //trackColors();
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

            TryUpdateSettings();
            
            if(launcherActive){
                
                //l = new Launcher();       KELVIN TESTER
                //l.start();
                
                if(fire == 1){
                    l.execute(Launcher.Command.FIRE);
                    fire = 0;
                }
                if(shootToKill == 1){
                    l.execute(Launcher.Command.LEDON);
                    
                }
                else {
                    l.execute(Launcher.Command.LEDOFF);
                }
                
            }
            

            try {
                semaphoreCoordinates.acquire();
              
            } catch (InterruptedException e) {
            }

            
            trackColors();
            
            storageBoxCoordinates.put(counter);
        

            semaphoreCoordinates.release();

            if (timerActive) {
                stopTimer();
            }
            
            if(videoStreamActive){
                streamVideo();
            }
     
          

        }

    }

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
        //capture.set(3,1920);
        // capture.set(4,1080);
        //capture.set(5,40);
        // capture.se(CV_CAP_PROP_,3);
        // capture.set(15, -2);
        capture.set(3, 400);
        capture.set(4, 500);
        //capture.set(3, 1366);
        //capture.set(4, 768);
        //capture.set(15, -2);

        capture.read(webcam_image);
        if (cameraFramActive == true) {
            cameraFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
        }
        //hsvFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

        //thresholdFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
        array255 = new Mat(webcam_image.height(), webcam_image.width(), CvType.CV_8UC1);
        array255.setTo(new Scalar(255));
        //Mat distance = new Mat(webcam_image.height(), webcam_image.width(), CvType.CV_8UC1);
        //  List<Mat> lhsv = new ArrayList<>(3);
        // Mat circles = new Mat();
    }

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

    public void createThresholdFrame() {

        if (thresholdFrameActive == true) {
            thresholdFrame = new JFrame("Threshold");
            thresholdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            thresholdFrame.setSize(640, 480);
            //  thresholdFrame.setBounds(900, 300, hsvFrame.getWidth() + 900, 300 + hsvFrame.getHeight());
            thresholdPanel = new Panel();
            thresholdFrame.setContentPane(thresholdPanel);
            thresholdFrame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

            thresholdFrame.setVisible(true);
        }

    }

    private void trackColors() {

        capture.read(webcam_image);
        if (!webcam_image.empty()) {

            //Adjusting brightness and contrast
            // webcam_image.convertTo(webcam_image, -1, brightness, contrast);
            //Adding blur to remove noise
            //Imgproc.blur(webcam_image, webcam_image, new Size(7, 7));
            // converting to HSV image
            Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);

            //Checking if the hsv image is in range.
            Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
            //Core.inRange(hsv_image, hsv_minByte, hsv_maxByte, thresholded);

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

            //List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 8, 200, 100, 0, 0);
            Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            Imgproc.drawContours(webcam_image, contours, -1, new Scalar(255, 0, 0), 2);
            //Imgproc.drawContours(webcam_image, contours2, -1, new Scalar(255, 0, 0), 2);

            /*
            Core.circle(webcam_image, new Point(210, 210), 10, new Scalar(100, 10, 10), 3);
            data = webcam_image.get(210, 210);
            Core.putText(webcam_image, String.format("(" + String.valueOf(data[0]) + "," + String.valueOf(data[1]) + "," + String.valueOf(data[2]) + ")"), new Point(30, 30), 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
                    , 1.0, new Scalar(100, 10, 10, 255), 3);
            
            int thickness = 2;
            int lineType = 8;
            Point start = new Point(0, 0);
            Point end = new Point(0, 0);
            Scalar black = new Scalar(100, 10, 10);

            int rows = circles.rows();
            int elemSize = (int) circles.elemSize(); // Returns 12 (3 * 4bytes in a float)  
            float[] data2 = new float[rows * elemSize / 4];
             */
            getTargetError();
            addInfoToImage();

            //Core.line(hsv_image, new Point(150, 50), new Point(202, 200), new Scalar(100, 10, 10)/*CV_BGR(100,10,10)*/, 3);
            //Core.circle(hsv_image, new Point(210, 210), 10, new Scalar(100, 10, 10), 3);
            hsv_values = hsv_image.get(210, 210);

            //Core.putText(hsv_image, String.format("x" + "(" + String.valueOf(hsv_values[0]) + "," + String.valueOf(hsv_values[1]) + "," + String.valueOf(hsv_values[2]) + ")"), new Point(30, 30), 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
            //      , 1.0, new Scalar(50, 10, 10, 255), 3);
            distance.convertTo(distance, CvType.CV_8UC1);

            // Core.line(distance, new Point(150, 50), new Point(202, 200), new Scalar(100)/*CV_BGR(100,10,10)*/, 3);
            //Core.circle(distance, new Point(210, 210), 10, new Scalar(100), 3);
            //data = (double[]) distance.get(210, 210);
            //getCoordinates(thresholded);
            //Core.putText(distance, String.format("(" + String.valueOf(data[0]) + ")"), new Point(30, 30), 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
            //      , 1.0, new Scalar(100), 3);
            updatePanels();
            /*
            cameraPanel.setimagewithMat(webcam_image);

            hsvPanel.setimagewithMat(hsv_image);  
            //panel2.setimagewithMat(S);  
            //distance.convertTo(distance, CvType.CV_8UC1);  
            //panel3.setimagewithMat(distance);  
            thresholdPanel.setimagewithMat(thresholded);

            cameraFrame.repaint();
            hsvFrame.repaint();
            // frame3.repaint();  
            thresholdFrame.repaint();
             */
        } else {

            System.out.println(" --(!) No captured frame -- Break!");

        }

    }

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

    private void getTargetError() {

        int x = getXError(contours);
        int y = getYError(contours);
        contours.clear();

        if (x > 0) {

            Core.circle(webcam_image, new Point(x, y), 4, new Scalar(50, 49, 0, 255), 4);
            //float centerX = cameraPanel.getWidth()/2;
            //int centerY = cameraPanel.getHeight()/2;
            float centerX = webcam_image.width() / 2;
            float centerY = webcam_image.height() / 2;
            // centerCirle
            Core.circle(webcam_image, new Point(centerX, centerY), 4, new Scalar(50, 49, 0, 255), 4);

            //System.out.println("centerX: " + centerX );
            float cameraAngleX = 70.42f;
            float cameraAngleY = 43.30f;

            float pixErrorX = x - centerX;
            float pixErrorY = -y + centerY;

            //System.out.println("PixError: "+pixErrorX);
            float angleErrorX = (pixErrorX / centerX) * cameraAngleX;
            float angleErrorY = (pixErrorY / centerY) * cameraAngleY;
            
            
            if(launcherActive){
            if(angleErrorX < 5 && angleErrorY < 5 && shootToKill == 1) {
                l.execute(Launcher.Command.FIRE);
            }
            }
            
            
            
            Core.line(webcam_image, new Point(x, y), new Point(centerX, centerY), new Scalar(150, 150, 100)/*CV_BGR(100,10,10)*/, 3);

            // System.out.println("angleErrorX: "+angleErrorX);
            // System.out.println("angleErrorY: "+angleErrorY);
            //  System.out.println(counter);
            if(manualModeActive == false){
            storageBoxCoordinates.putError(angleErrorX, angleErrorY);
            }
        }

    }

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

    private void addInitialValues() {

        // Add initial values to HSV min settings
        double[] d = new double[]{3, 144, 115};
        hsv_min.set(d);
        byte b = 255 - 128;
        byte[] e = new byte[]{3, b, 115};
        byte x = e[1];

        // Add initial vales to HSV max settings
        double[] m = new double[]{15, 245, 178};
        hsv_max.set(m);
        
        videoStreamActive = false;


    }

    private void updateSettings() {

        newSettingsFromStorageBox = storageBoxSettings.getSettings();
        
        String print = null;
        for(int i = 1; i < 20; i++){
            print = print + newSettingsFromStorageBox[i] + " " ;
            
        }
        System.out.println(print);
        
        double[] min = new double[]{newSettingsFromStorageBox[1], newSettingsFromStorageBox[3], newSettingsFromStorageBox[5]};
        hsv_min.set(min);

        double[] max = new double[]{newSettingsFromStorageBox[2], newSettingsFromStorageBox[4], newSettingsFromStorageBox[6]};
        hsv_max.set(max);
        
        
        //System.out.println("Value recived: " + newSettingsFromStorageBox[6]);
        if((newSettingsFromStorageBox[7]) == 1){
            videoStreamActive = true;
        }
        else
            videoStreamActive = false;
        
        if(newSettingsFromStorageBox[8]==1){
            manualModeActive = true;
            updateManualMoveValues(true);
        }
        else{
            manualModeActive = false;
            updateManualMoveValues(false);
        }
        
         fire = (int) newSettingsFromStorageBox[14];
         shootToKill = (int) newSettingsFromStorageBox[13];
        //double[] fireSettings = new double[]{newSettingsFromStorageBox[13], newSettingsFromStorageBox[14]};
        //storageBoxCoordinates.putFireSettings(fireSettings);
        
        
        
        
        //System.out.println("Value of videostreamActive from GUI: " +videoStreamActive);
        
        
        

    }

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

        //panel2.setimagewithMat(S);  
        //distance.convertTo(distance, CvType.CV_8UC1);  
        //panel3.setimagewithMat(distance);  
        //thresholdPanel.setimagewithMat(thresholded);
        // cameraFrame.repaint();
        // hsvFrame.repaint();
        // frame3.repaint();  
        // thresholdFrame.repaint();
    }

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

    private void TryUpdateSettings() {
        semaphoreSettings.tryAcquire();
        newSettings = storageBoxSettings.getAvailable();

        if (newSettings) {
            updateSettings();
        }
        semaphoreSettings.release();
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
    }

    private void stopTimer() {

        endTime = System.currentTimeMillis();
        long totTime = endTime - startTime;
        System.out.println("Time elapsed from producer acquired to release " + totTime + "ms");
    }

    private void streamVideo() {
        
        
          
            if (semaphoreVideoStream.tryAcquire()) {
                
                VideoStreamBoxAvailable = storageBoxVideoStream.getAvailable();
                if(VideoStreamBoxAvailable){
                storageBoxVideoStream.put(webcam_image);
                }
                
                semaphoreVideoStream.release();

            }
    }

    private void updateManualMoveValues(boolean active) {
       /*
        calculating the manual movement from the gui.
        input: buttonvalues up,down,left,right
        output: X and Y coordinates
        */
        if(active){
            // (up value minus down value)
            // (right value minus left value)
            float x = (float) (newSettingsFromStorageBox[12]-newSettingsFromStorageBox[11]);
            float y = (float) (newSettingsFromStorageBox[9]-newSettingsFromStorageBox[10]);
           
            storageBoxCoordinates.putError(x, y);
        }
        
        else{
           
        }
    }

   

}
