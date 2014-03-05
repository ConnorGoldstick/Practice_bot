/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author pronto2
 */
public class Drive extends Thread {

    Jaguar jagleft1, jagright1, jagleft2, jagright2;
    Joystick xBox;
    //Solenoid gear1, gear2;
    boolean running = false;
    double speed = 0, turn = 0, leftspeed = 0, rightspeed = 0;

    public Drive(Joystick x, Jaguar jL1, Jaguar jR1
            //, Jaguar jL2, Jaguar jR2, Solenoid g1, Solenoid g2
            ) {
        jagleft1 = jL1;
        jagright1 = jR1;
        //jagleft2 = jL2;
        //jagright2 = jR2;
        xBox = x;
        //gear1 = g1;
        //gear2 = g2;
    }

    public void setRun(boolean run) {
        running = run;
    }

    public void run() {
        while (true) {
            //gear1.set(false);
            //gear2.set(true);
            while (running) {
                if (xBox.getRawAxis(2) >= 0) {
                    speed = Math.sqrt(xBox.getRawAxis(2) * xBox.getRawAxis(2) * xBox.getRawAxis(2));
                } else {
                    speed = -Math.sqrt(-xBox.getRawAxis(2) * (xBox.getRawAxis(2) * xBox.getRawAxis(2)));
                }
                turn = 0.5 * xBox.getRawAxis(4); //maybe need to adjust this
                if (turn < 0.1 && turn > -0.1) { //maybe need to adjust this
                    turn = 0;
                }
                
                /*if (xBox.getRawButton(1)) {
                    gear1.set(false);
                    gear2.set(true);
                }
                if (xBox.getRawButton(2)) {
                    gear1.set(true);
                    gear2.set(false);
                } */
                
                if (Math.abs((speed - turn) - leftspeed) < .075/*
                         * .075
                         */) { //don't skip target
                    leftspeed = speed - turn;
                }
                if (leftspeed < speed - turn) { //ramp up
                    leftspeed = leftspeed + .1;
                }
                if (leftspeed > speed - turn) { //ramp down
                    leftspeed = leftspeed - .1;
                }
                if (Math.abs((speed + turn) - leftspeed) < .075/*
                         * 0.075
                         */) { //don't skip target
                    rightspeed = speed + turn;
                }
                if (rightspeed < speed + turn) { //ramp up
                    rightspeed = rightspeed + .1;
                }
                if (rightspeed > speed + turn) {//ramp down
                    rightspeed = rightspeed - .1;
                }

                if (Math.abs(rightspeed) < .05) { //dead zone
                    rightspeed = 0;
                }
                if (Math.abs(leftspeed) < .05) { //dead zone
                    leftspeed = 0;
                }
                jagleft1.set(leftspeed);
                jagright1.set(-(rightspeed));
                //jagleft2.set(leftspeed);
                //jagright2.set(-(rightspeed));
            }
        }
    }
}
