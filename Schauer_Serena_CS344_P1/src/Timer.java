import java.util.Random;
import java.util.Vector;


public class Timer extends Thread{

	// Variables
	private int id;
	static Random rand = new Random();
	public int timer_testNum = 0;

	// Constructor
	public Timer(int id){
		this.id = id;
		setName("Timer " + id);
	}
	
	// For Printing
	public void msg(String m){
		System.out.println("Timer: " + "["+(System.currentTimeMillis()-Main.time)+"] "+getName()+" : " + m);
	}
	
	public void run(){
		waitTillDayStarts();
		// timer for 4 tests in one day
		while(timer_testNum < Teacher.testMax){
			wait15mins();
			testBeingAdministered();
			ninetyFiveMinuteWait();
		}
		msg("Ended. ------------ ENDED ------------");
	}
	
	public void waitTillDayStarts(){
		// Simulated By Sleep For A Random Time
		msg("Day starting...");
		try{
			Thread.sleep(rand.nextInt(2000));
		}catch(InterruptedException e){
			e.printStackTrace();
		}				
	}
	
	public void wait15mins(){
		// notify the teacher the kids can enter the room
		synchronized(Main.waitingTeacher_ToOpenDoors){
			msg("Tells teacher to open doors.");
			msg("Timer for exam number " + timer_testNum + ".");
			Main.waitingTeacher_ToOpenDoors.notifyAll();
		}
		//wait some time..  Simulated By Sleep For A Random Time
		try{
			Thread.sleep(rand.nextInt(2000));
		}catch(InterruptedException e){
			e.printStackTrace();
		}	
		msg("15 minutes is up.");
		// signal teacher to hand out test
		synchronized(Main.waitingTeacher_ToCloseDoors){
			msg("Tells teacher to close doors.");
			timer_testNum = timer_testNum + 1;
			Main.waitingTeacher_ToCloseDoors.notifyAll();
		}	
	}
	
	public void testBeingAdministered(){
		// test is an hour  --  Simulated By Sleep For A Random Time
		msg("Test Starts.");
		try{
			Thread.sleep(rand.nextInt(2500));
		}catch(InterruptedException e){
			e.printStackTrace();
		}			
		msg("An Hour Has Passed: Test has Ended.");
		// signal to students to end test
		synchronized(Main.waiting_TestToEnd){
			Main.waiting_TestToEnd.notifyAll();
		}	
	}
	
	public void ninetyFiveMinuteWait(){
		// Time Between Tests... Simulated By Sleep For A Random Time
		try{
			Thread.sleep(rand.nextInt(15000));
		}catch(InterruptedException e){
			e.printStackTrace();
		}	
	}
	
}
