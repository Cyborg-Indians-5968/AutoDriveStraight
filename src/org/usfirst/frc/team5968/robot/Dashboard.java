package org.usfirst.frc.team5968.robot;

import java.util.concurrent.LinkedBlockingQueue;

import org.usfirst.frc.team5968.robot.AutoManager.AutoMode;
import org.usfirst.frc.team5968.robot.Robot.StartPoint;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Used for sending data to the dashboard. Just add the data to the NetworkTable
 * and the dashboard will have it.
 * 
 * @author BeijingStrongbow
 */
public class Dashboard {
	
	/**
	 * Whether the connection has been initialized
	 */
	private static boolean initialized = false;
	
	/**
	 * Table storing the data
	 */
	private static NetworkTable table;
	
	/**
	 * Table for storing warnings
	 */
	private static NetworkTable warnings;
	
	/**
	 * Valid automodes for the user to select. Obviously it's still WIP
	 */
	private static String[] autoModes = {"Dummy1", "Dummy2", "Dummy3"};
	
	/**
	 * Initializes the network table
	 */
	public static void init(){
		if(!initialized){
			table = NetworkTable.getTable("SmartDashboard");
			warnings = (NetworkTable) table.getSubTable("warnings");
			initialized = true;
		}
	}
	
	/*(
	 * Add the valid automodes to the network table
	 */
	public static void addModes() {
		if(!initialized){
			init();
		}
		table.putStringArray("options", autoModes);
		table.putStringArray("options", autoModes);
	}
	
	/**
	 * Update the diagnostics on the dashboard. 
	 */
	public static void updateDashboardValues(){
		if(!initialized){
			init();
		}
		table.putNumber("timeRemaining", Timer.getMatchTime());
		putWarnings();
		putRopeClimbData();
		putPneumaticsUpOrDown();
		//putPressureLabel(Pneumatics.getPressureLabel());
	}
	
	/**
	 * Send the robot's current location to the dashboard, so it can be drawn in on the
	 * field diagram.
	 * 
	 * @param loc The robot's current location on the field
	 * @param angle The robot's current angle, from 0 to 2 pi radians
	 */
	public static void putLocation(Point loc, double angle){
		if(!initialized){
			init();
		}
		
		table.putNumber("currentX", loc.getX());
		table.putNumber("currentY", loc.getY());
		table.putNumber("currentAngle", angle);
	}
	
	/**
	 * Updates the list of points along the drive path
	 * 
	 * @param q The current list of drive paths to add the point to
	 */
	public static void updateDrivePoints(LinkedBlockingQueue<Point> q){
		if(table.getNumber("targetX", -1) != -1 && table.getNumber("targetY", -1) != -1){
			try{
				q.put(new Point(table.getNumber("fieldX", -1), table.getNumber("fieldY", -1)));
			}
			catch(InterruptedException ex){
				DriverStation.reportError("Uhh... that wasn't supposed to happen", true);
			}
		}
	}
	
	/**
	 * Gets the starting point for auto
	 * 
	 * @return The starting point on the field
	 */
	public static StartPoint getStartingPoint(){
		int point = (int) table.getNumber("startPosition", -1);
		switch(point){
			case 1:
			case 4:
				return StartPoint.RETRIEVAL_ZONE;
			case 2:
			case 5:
				return StartPoint.MIDLINE;
			case 3:
			case 6:
				return StartPoint.KEY;
			default:
				return null;
		}
	}
	
	/**
	 * Get the auto option chosen by the drivers
	 * 
	 * @return The auto option chosen by the drivers
	 */
	public static AutoMode getAutoMode(){
		int routine = (int) table.getNumber("autoMode", -1);
		
		switch(routine){
			case 1:
				return AutoMode.HOPPER_BOILER;
			case 2:
				return AutoMode.HOPPER;
			case 6:
				return AutoMode.GEAR;
			case 5:
				return AutoMode.CROSS;
			default:
				return AutoMode.BE_USELESS;
		}
	}
	
	/**
	 * The hopper to empty, if it's part of the selected auto routine
	 * 
	 * @return The hopper number. Starts from the blue alliance retrieval chute at 1 and goes counter clockwise
	 */
	public static int getHopper(){
		return (int) table.getNumber("hopperChoice", 0);
	}
	
	/**
	 * Send warnings to the dashboard
	 */
	private static void putWarnings(){
		warnings.putBoolean("collision", NavXMXP.getCollisionHappened());
		warnings.putBoolean("temperature", DriveBase.isAMotorTooHot());
	}
	
	/**
	 * Put the direction the rope climber is spinning
	 */
	public static void putRopeClimbData(){
		table.putNumber("climbingRope", RopeClimber.getDirection());
		table.putNumber("climbHeight", RopeClimber.getClimbHeight());
	}
	
	/**
	 * Put whether the pneumatics are up or down (the color that should show on the dashboard).
	 */
	public static void putPneumaticsUpOrDown(){
		if(false){
			table.putString("pneumatics", "green");
		}
		else{
			table.putString("pneumatics", "red");
		}
	}
	
	public static void sendControlsReversed(boolean reversed){
		table.putBoolean("reversed", reversed);
	}
	
	public static void putPressureLabel(double pressure) {
		table.putNumber("pressureLabel", pressure);
	}
}
