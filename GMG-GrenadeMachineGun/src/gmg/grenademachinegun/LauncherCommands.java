/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmg.grenademachinegun;

import gmg.grenademachinegun.LauncherBackup.Command;

/**
 * Class Launcher Commands
 * 
 * @author root
 * @author Matias, Javadoc
 */
public class LauncherCommands {
//fields
    private int command;

    boolean flag = false;
    boolean fireCommand = false;

    /**
     * putCommands() put the commands as a type of int, to reach an ENUM
     * @param c  of type int
     */
    public void putCommands(int c) {

        this.command = c;
        flag = true;
    }

    /**
     * putFireCommands(), puts the command when to fire, and not to fire
     * @param fire of type boolean, TRUE or FALSE
     */
    public void putFireCommands(boolean fire) {
        fireCommand = fire;

    }

    /**
     * getFireCommand(), gets the fire command (TRUE or FALSE)
     * @return fireCommand, true or false
     */
    public boolean getFireCommand() {
        return fireCommand;
    }

    /**
     * getCommand() returns an int for the specified command, and set flag = false
     * @return command int c
     */
    public int getCommand() {

        flag = false;

        return command;

    }

    /**
     * getAvailable() chechs if available.
     * @return flag, TRUE or FALSE
     */
    public boolean getAvailable() {
        return flag;
    }

}
