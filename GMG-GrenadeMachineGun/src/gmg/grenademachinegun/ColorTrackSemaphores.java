/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import java.awt.AWTException;
import java.awt.Graphics;
//import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
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

/**
 *
 * @author Olav Rune
 */
public class ColorTrackSemaphores extends Thread{
    
    private VideoCapture capture;
    private GUI gui;
    private Mouse mouse;
    private Mat webcam_image;
    private JFrame cameraFrame;
    private JFrame hsvFrame;
    private JFrame thresholdFrame;
    private JFrame guiFrame;
    private Panel cameraPanel;
    private Panel hsvPanel;
    private Panel thresholdPanel;
    private Mat hsv_image; 
    private Mat thresholded;
    private Mat thresholded2;
    private double[] data;
    private Mat circles;
    private double[] hsv_values;
    
    private Scalar hsv_min;
    private Scalar hsv_max;
    
    private List<Mat> lhsv;
    private Mat array255;
    private Mat distance;
    List<MatOfPoint> contours;
    
           boolean b = true;
           
  private StorageBox  storageBox;
  private Semaphore semaphore;
   private boolean available;
   
   
   int counter = 0;
 
    
    

    public ColorTrackSemaphores(StorageBox storeageBox, Semaphore semaphore){
        
        this.storageBox = storeageBox; 
 
        this.semaphore = semaphore;
        this.available = true;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        try{ 
            createMat();
            createFrames();

       
             //trackColors();
        }
        catch(AWTException e){
            e.printStackTrace();
        }
    
  
}
    
    public void run(){
        
        int start = 1;
        long startTime = 0;
        long endTime = 0;
        long startTotTime = 0;
        long endTotTime = 0;
    
    counter = start;
        
        while (counter < 10000) {
            startTotTime = System.currentTimeMillis();
            
              try {
        semaphore.acquire();
                //startTime = System.currentTimeMillis();
      }
      catch(InterruptedException e) {
      }
      available = storageBox.getAvailable();
      //available = false;
      if (!available) {
         
        storageBox.put(counter);
        
        startTime = System.currentTimeMillis();
        
        trackColors();
        
        endTime = System.currentTimeMillis();
      long time = endTime-startTime;
       System.out.println("Time in TrackColors loop: " + time + "ms");
    
      }
      semaphore.release();
      
      endTime = System.currentTimeMillis();
      //long time = endTime-startTime;
            //System.out.println("Time elapsed from producer acquired to release " + time + "ms");
     
    
      
      // normally non-critical operations will be outside semaphore:
      if (!available) {
          
        System.out.println( "Producer put: " + counter);
        counter++;
          
      }
      endTotTime = System.currentTimeMillis();
      long totTime = endTotTime-startTotTime;
            System.out.println("Total time in producer-loop: " +totTime+"ms");
        }
        
        
    }

    private void createFrames() throws AWTException {
        
                mouse = new Mouse();
		cameraFrame = new JFrame("Camera");  
		cameraFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		cameraFrame.setSize(640,480);  
		cameraFrame.setBounds(0, 0, cameraFrame.getWidth(), cameraFrame.getHeight());  
		cameraPanel = new Panel();  
		cameraFrame.setContentPane(cameraPanel);  
		cameraFrame.setVisible(true);  
		hsvFrame = new JFrame("HSV");  
		hsvFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		hsvFrame.setSize(640,480);  
		hsvFrame.setBounds(300,100, hsvFrame.getWidth()+300, 100+hsvFrame.getHeight());  
		hsvPanel = new Panel();  
		hsvFrame.setContentPane(hsvPanel);  
		hsvFrame.setVisible(true);  
                thresholdFrame = new JFrame("Threshold");  
		thresholdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		thresholdFrame.setSize(640,480);  
		thresholdFrame.setBounds(900,300, hsvFrame.getWidth()+900, 300+hsvFrame.getHeight());  
		thresholdPanel = new Panel();  
		thresholdFrame.setContentPane(thresholdPanel);      
		//  thresholdFrame.setVisible(true);  
                guiFrame = new JFrame("Slider");  
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		guiFrame.setSize(640,480);  
                gui = new GUI();  
		guiFrame.setContentPane(gui);      
		guiFrame.setVisible(true);  
                
                double[] hsv_values = new double[3];
                
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
		cameraFrame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		hsvFrame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		
		thresholdFrame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		array255 = new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
		array255.setTo(new Scalar(255));  
                Mat distance=new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
                List<Mat> lhsv = new ArrayList<>(3);      
		Mat circles = new Mat();
    }

    private void trackColors() {
   
   
			capture.read(webcam_image);  
				if( !webcam_image.empty() ) { 
                                    
                                    getHsvMinSettings();
                                    getHsvMaxSettings();
                                    
                                    double brightness = gui.getBrightnessValue();                                   
                                    double contrast = gui.getContrastValue();
                                    
                                    
                                    //Adjusting brightness and contrast
                                     webcam_image.convertTo(webcam_image,-1, brightness, contrast);
                                     
                                     
                                     //Adding blur to remove noise
                                   //Imgproc.blur(webcam_image, webcam_image, new Size(7, 7));
                                    
                                    // converting to HSV image
                                    Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);  
                                    
                                    //Checking if the hsv image is in range.
                                    Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);         
                                    
                                    Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8,8)));
					Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
                                        Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats  
					Mat S = lhsv.get(1);  
					Mat V = lhsv.get(2);  
					Core.subtract(array255, S, S);  
					Core.subtract(array255, V, V);  
					S.convertTo(S, CvType.CV_32F);  
					V.convertTo(V, CvType.CV_32F);  
					Core.magnitude(S, V, distance);  
					Core.inRange(distance,new Scalar(0.0), new Scalar(200.0), thresholded2);  
                                        
                                        Imgproc.GaussianBlur(thresholded, thresholded, new Size(9,9),0,0);  
                                        
                                        
                                        
					//List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
					Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height()/8, 200, 100, 0, 0);   
					Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
                                      
					Imgproc.drawContours(webcam_image, contours, -1, new Scalar(255, 0, 0), 2);   
                                        //Imgproc.drawContours(webcam_image, contours2, -1, new Scalar(255, 0, 0), 2);
                                       
                                        
                                        
                                        Core.circle(webcam_image, new Point(210,210), 10, new Scalar(100,10,10),3);  
					data=webcam_image.get(210, 210);  
					Core.putText(webcam_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
							,1.0,new Scalar(100,10,10,255),3); 
					int thickness = 2;
					int lineType = 8;
					Point start = new Point(0,0);
					Point end=new Point(0,0);
					Scalar black = new Scalar( 100, 10, 10 );
                                        
                                        int rows = circles.rows();  
					int elemSize = (int)circles.elemSize(); // Returns 12 (3 * 4bytes in a float)  
					float[] data2 = new float[rows * elemSize/4];  
                                        
                                        
                                        getTargetError();
                                        
                                        
                                      
                                                
                                                
                                         Core.line(hsv_image, new Point(150,50), new Point(202,200), new Scalar(100,10,10)/*CV_BGR(100,10,10)*/, 3);  
					Core.circle(hsv_image, new Point(210,210), 10, new Scalar(100,10,10),3);  
					 hsv_values = hsv_image.get(210, 210);  
                                        //int hue = (int)hsv_values[0];
                                        gui.hueVal = (int)hsv_values[0];
                                        gui.satVal= (int)hsv_values[1];
                                        gui.valVal= (int)hsv_values[2];
                                        
                                        Core.putText(hsv_image,String.format("x"+"("+String.valueOf(hsv_values[0])+","+String.valueOf(hsv_values[1])+","+String.valueOf(hsv_values[2])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
							,1.0,new Scalar(50,10,10,255),3);  

					distance.convertTo(distance, CvType.CV_8UC1);  
					Core.line(distance, new Point(150,50), new Point(202,200), new Scalar(100)/*CV_BGR(100,10,10)*/, 3);  
					Core.circle(distance, new Point(210,210), 10, new Scalar(100),3);  
					data=(double[])distance.get(210, 210);  
					//getCoordinates(thresholded);
					Core.putText(distance,String.format("("+String.valueOf(data[0])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
							,1.0,new Scalar(100),3);  
                                                
                                                cameraPanel.setimagewithMat(webcam_image);  
					
                                                //hsvPanel.setimagewithMat(hsv_image);  
                                                
                                                
					//panel2.setimagewithMat(S);  
					//distance.convertTo(distance, CvType.CV_8UC1);  
					//panel3.setimagewithMat(distance);  
					thresholdPanel.setimagewithMat(thresholded);  
                                        
					cameraFrame.repaint();  
					hsvFrame.repaint();  
					// frame3.repaint();  
					thresholdFrame.repaint(); 
                                }
                                                
                            else  {
				  
					System.out.println(" --(!) No captured frame -- Break!");  
					 
				}                                         
                                        
               
                                    
               
    }


    private void createMat() {
        webcam_image = new Mat();
        hsv_image = new Mat();  
        thresholded =new Mat();  
        thresholded2 =new Mat(); 
        data = new double[3]; 
        circles = new Mat();
        
        lhsv = new ArrayList<Mat>(3);  
        
        array255 = new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
    
        
	array255.setTo(new Scalar(255));  
        
        distance = new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);
        
       hsv_min = new Scalar(1,1,1);
       hsv_max = new Scalar(1,1,1);
        
       contours = new ArrayList<MatOfPoint>();
    }

    private void getHsvMinSettings() {
                                    
                                    double[] d = gui.getHsvMinValues();
                                    hsv_min.set(d);
                                            
    }
    private void getHsvMaxSettings() {
                                    
                                    double[] d = gui.getHsvMaxValues();
                                    hsv_max.set(d);
                                            
    }

  

    private void getTargetError() {
       
                                        int x = mouse.getX(contours);
						int y = mouse.getY(contours);
                                                contours.clear();
                                                
                                                if(x>0){
						mouse.moveMouse(x, y);
                                                
                                                
                                                  Core.circle(webcam_image, new Point(x, y), 4, new Scalar(50,49,0,255), 4);
                                                  float centerX = cameraPanel.getWidth()/2;
                                                  int centerY = cameraPanel.getHeight()/2;
                                                  // centerCirle
                                                  Core.circle(webcam_image, new Point(centerX, centerY), 4, new Scalar(50,49,0,255), 4);
                                                  
                                                  //System.out.println("centerX: " + centerX );
                                                  float cameraAngleX = 70.42f;
                                                  float cameraAngleY = 43.30f;
                                                  
                                                  float pixErrorX = x - centerX;
                                                  float pixErrorY = -y + centerY;
                                                  
                                                  
                                                  
                                                  //System.out.println("PixError: "+pixErrorX);
                                                 float angleErrorX = (pixErrorX/centerX)*cameraAngleX;
                                                 float angleErrorY = (pixErrorY/centerY)*cameraAngleY;
                                                 Core.line(webcam_image, new Point(x,y), new Point(centerX,centerY), new Scalar(150,150,100)/*CV_BGR(100,10,10)*/, 3);  
                                                 
                                                   
                                                // System.out.println("angleErrorX: "+angleErrorX);
                                                // System.out.println("angleErrorY: "+angleErrorY);
                                                  //  System.out.println(counter);
                                                  
                                                  storageBox.putError(angleErrorX, angleErrorY);
                                                  
                                                
                                                 
                                                }
                                                 
                                                 
                                               
                                                  
                                                
                                                
    }

}

  
    
