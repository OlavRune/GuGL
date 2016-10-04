/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

/**
 *
 * @author Matias
 */
public class ImageAnalyzer implements Runnable {

    private     CameraReader camRead;
    private     GuGl         gugl;
    private     Thread       camera;

    public ImageAnalyzer(GuGl ggl) {
        gugl = ggl;
        camera = new Thread(this); // creates a thread
        camera.start();

    }

    @Override
    public void run() {
        for (int i = 0; i<10; i++){
        gugl.put(i);
            System.out.println("Camera#1 ( "+this.camera.getName()+ ")"+"("+"put"+i+")");
        
        
        }
        
    }

}
