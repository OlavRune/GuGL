/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import gmg.grenademachinegun.LauncherBackup.Command;


/**
 *
 * @author root
 */
public class LauncherCommands {

    private int command;
    
    boolean flag = false;
    boolean fireCommand = false;
    
    
    
    public void putCommands(int c){
        
        this.command = c;
        flag = true;
    }
    public void putFireCommands(boolean fire){
        fireCommand = fire;
        
    }
    public boolean getFireCommand(){
        return fireCommand;
    }
    
    public int getCommand(){
        
        
        flag = false;
        
        
        return command;
      
        
    }
    
    public boolean getAvailable(){
        return flag;
    }
    
}
