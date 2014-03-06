/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.networktables.NetworkTable;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    NetworkTable server = NetworkTable.getTable("smartDashboard");
    SmartDashboard smart;
    Gyro gyro;
    Joystick xBox;
    Jaguar jagleft1, jagright1 /*, jagleft2, jagright2 */;
    Solenoid /*gear1, gear2,*/ fireOn, fireOff; //963-2568 Connor Phone Number
    Relay light, compressor;
    double speed, turn, leftspeed, rightspeed, conf;
    int i = 0, endTimer = 0, noWait = 0, e = 0, gyroTimer = 0;
    boolean shooting = false, atShoot, afterShoot, checkGyro;
    Drive drive;
    AnalogChannel ultrasonic;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        
        xBox = new Joystick(1);
        
        jagleft1 = new Jaguar(2, 1);
        jagright1 = new Jaguar(2, 2);
        //jagleft2 = new Jaguar(3);
        //jagright2 = new Jaguar(4);
        
        //gear1 = new Solenoid(1);
        //gear2 = new Solenoid(2);
        
        fireOff = new Solenoid(1);
        fireOn = new Solenoid(2);
        
        gyro = new Gyro(1);
        ultrasonic = new AnalogChannel(2);
        
        light = new Relay(2, 1);
        compressor = new Relay(2, 2);
        
        drive = new Drive(xBox, jagleft1, jagright1
                //, jagleft2, jagright2, gear1, gear2
                );

    }

    /**
     * This function is called periodically during autonomous
     */
   public void autonomousInit() {
        compressor.set(Relay.Value.kOn);
        
        gyro.reset();
        endTimer = 0;
        conf = 0;
        light.set(Relay.Value.kOn);
        noWait = 0;

        gyroTimer = 0;

        fireOff.set(true);
        fireOn.set(false);

        atShoot = false;
        afterShoot = false;
        checkGyro = false;
    }

    public void autonomousPeriodic() {
        compressor.set(Relay.Value.kOn);
        light.set(Relay.Value.kOn);
        System.out.println(ultrasonic.getVoltage());
        if (!checkGyro && !atShoot) { //if program does not know it's in range, do the following
            if (ultrasonic.getVoltage() > 0.43) { //if not in range, do the following
                conf = conf + SmartDashboard.getNumber("Confidence") - 70; //add to the total confidence
                jagleft1.set(-0.648); //move towards the goal
                jagright1.set(0.6);
                System.out.println("Driving forwards.");
            } else { //once in range, do the follwing
                jagleft1.set(0); //stop moving forwards
                jagright1.set(0);
                checkGyro = true; // tell the program to check the gyro
                System.out.println("Checking gyro.");
            }
        }
        if (checkGyro) {
            gyroTimer++;
            if (gyro.getAngle() < -2) { //if the robot is pointed to the left, do the following
                jagleft1.set(-0.108); //turn right
                jagright1.set(-0.1);
                System.out.println("Orienting right.");
            }
            if (gyro.getAngle() > 2) { //if the robot is pointed to the right, do the following
                jagleft1.set(0.108); //turn left
                jagright1.set(0.1);
                System.out.println("Orienting left.");
            }
            if (gyro.getAngle() > -2 && gyro.getAngle() < 2) { // if the robot is pointed towards the goal, do the following
                jagleft1.set(0); //stop the motion of the robot
                jagright1.set(0);
                checkGyro = false; //stop looking at the gyro
                atShoot = true; //tell the program that the robot is in position
                System.out.println("Oriented.");
            }
            if (gyroTimer == 30) { //after three fifths second of checking the gyro, do the following
                jagleft1.set(0); //stop the motion of the robot
                jagright1.set(0);
                checkGyro = false; //stop looking at the gyro
                atShoot = true; //tell the program that the robot is in position
                System.out.println("Gyro check timed out.");
            }
        }
        if (atShoot && !afterShoot) { //once in position, do the following
            if (conf >= 40) { //if the target has been seen, do the following
                System.out.println("Saw Target.");
                fireOff.set(true); //launch the catapult, switched these??????????????????????
                fireOn.set(false);
                afterShoot = true; //tell the program it has fired
                System.out.println("Launching.");
            }
            if (conf < 40) { //if the target has not been seen, do the following
                if (noWait == 0) { //reset the timer for this occasion
                    System.out.println("Did not see target.");
                }
                noWait++; //count the timer up
                if (noWait == 200) { //once the rimer reaches 4 seconds, do the following
                    fireOff.set(true); //launch the catapult, switched these??????????????????
                    fireOn.set(false);
                    afterShoot = true; //tell the program it has fired
                    System.out.println("Launching.");
                }
            }
        }
        if (afterShoot) { //once the program knows it has fired, do the following
            if (endTimer < 100) { // for two seconds after firing, do the following
                endTimer++; //run the ending timer
                jagleft1.set(0); //stop any motion of the robot
                jagright1.set(0);
                if (endTimer == 100) { //at end of autonomous, do the following
                    System.out.println("Autonomous Complete.");
                }
            }
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopInit() {
        compressor.set(Relay.Value.kOn);
        light.set(Relay.Value.kOff);
        drive.start();
        drive.setRun(true);
        fireOff.set(true);
        fireOn.set(false);
    }
    public void teleopPeriodic() {
        light.set(Relay.Value.kOff);
        compressor.set(Relay.Value.kOn);
        light.set(Relay.Value.kOff);
        if (xBox.getRawAxis(3) <= -0.9){
            shooting = true;
        }
        if (shooting){
            i++;
            fireOff.set(false);
            fireOn.set(true);
            if (i >= 100){
                fireOff.set(true);
                fireOn.set(false);
                i = 0;
                shooting = false;
            }
        }
    }
    public void disabledInit(){
        drive.setRun(false);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        compressor.set(Relay.Value.kOn);
    }
}
