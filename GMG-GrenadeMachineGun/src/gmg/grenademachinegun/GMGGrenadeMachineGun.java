/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

/**
 *
 * @author Olav Rune
 */
public class GMGGrenadeMachineGun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        GuGl gugl = new GuGl();
        
        KeyController keys = new KeyController();
        ImageAnalyzer img = new ImageAnalyzer(gugl);
        ThunderLauncher thlu = new ThunderLauncher(gugl);
    }
    
}
