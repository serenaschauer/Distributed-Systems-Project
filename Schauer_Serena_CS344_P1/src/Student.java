import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Student extends Thread{
	
	//public static ArrayList waitingStudents_OnLine = new ArrayList();
	
	// Variables
	static Random rand = new Random();
	int examCountOfStudent = 0;
	int examStudentLimit = 3;
	Boolean roomEmpty = true;
	private int id;
	
	// Constructor
	public Student(int id){  
		this.id = id;
		setName("Student " + id); 
	}
	
	
	// For Printing
	public void msg(String m){
		System.out.println("Student: " + "["+(System.currentTimeMillis()-Main.time)+"] "+getName()+" : " + m);
	}
	
	// Run Method
	public void run(){
		msg("is Running.");
		walkToSchool();
		// students only end when they reach 3 tests
		while(examCountOfStudent != examStudentLimit){
			waitInClassRoomLine();   // waits for instructor to Arrive
			takeSeats();       // enter class room 15 mins before test starts
			takesTest();
			examCountOfStudent++;
			checkNotes();
			returnExam();
			leaveClassRoom();
			prepareForNextExam();
			// get ready for next exam
		}
		msg("ended. Reached Test Limit.. took examCountOfStudent tests. ------------ ENDED ------------");
	}// Run
	
	public void walkToSchool(){
		// Simulated By Sleep For A Random Time
		try{
			Thread.sleep(rand.nextInt(100));
			msg("walked to school. Now arrives at school.");
		}catch(InterruptedException e){
			e.printStackTrace();
		}	
	}
	
	synchronized public void waitInClassRoomLine(){
		try{
			synchronized(Main.waitingStudents_OnLine){
				// only wait if door is not open or if capacity is full
				while(Main.roomIsOpen == false || Main.studentsInRoom == Main.capacity){
					if(Main.roomIsOpen == false ){
					msg("---- waiting in class room line. ----  room door is closed so wait....");
					}
					if(Main.studentsInRoom == Main.capacity){
						msg("---- waiting in class room line. ---- capacity over so wait.... studentsInRoom = " + Main.studentsInRoom);
					}
					Main.waitingStudents_OnLine.wait();
				}
				// if door is open and capacity is not full enter the room
				msg("Now enters the room."); 
				Main.studentsInRoom = Main.studentsInRoom + 1;
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}	
	
	}
	
	public void takeSeats(){		
		// Students wait for test to start 
		try{
			synchronized(Main.waitingStudents_ToStartTest){
				msg("is waiting to start test.");
				// wait until teacher notifies students to take exam
				Main.waitingStudents_ToStartTest.wait();
				// Teacher gives exams
				Main.studentsWithExams = Main.studentsWithExams + 1;
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		msg("was notified by teacher to start the test.");
	}
	
	public void takesTest(){
		// students and instructor wait until exam ends
		try{
			synchronized(Main.waiting_TestToEnd){
				// wait until teacher notifies students the exam has ended
				Main.waiting_TestToEnd.wait();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}		
		msg("Notified that Exam Ended.");
	}
	
	public void checkNotes(){
		// Simulated By Sleep For A Random Time
		try{
			Thread.sleep(rand.nextInt(100));
			msg("checks notes.");
		}catch(InterruptedException e){
			e.printStackTrace();
		}	
	}
	
	public void returnExam(){
		// students signal instructor 
		synchronized(Main.waitingTeacher_ToGetBackTests){
			msg("Handed teacher back exam");
			Main.studentsWithExams = Main.studentsWithExams - 1;
			Main.waitingTeacher_ToGetBackTests.notifyAll();
		}
		// Students wait for grade
		Test t = new Test(id);
		synchronized (t) {
			Main.studentsWaitingForGrade.addElement(t);
			try{
				msg("is waiting for grade.");
				t.wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}			
		}
		msg("has been given a grade.");		
	}

	
	synchronized public void leaveClassRoom(){
		msg("leaves the class room.");
		Main.studentsInRoom = Main.studentsInRoom - 1;	
	}

	public void prepareForNextExam(){
		//  -- Simulated By Sleep For A Random Time
		try{
			Thread.sleep(rand.nextInt(500));
			msg("preparing for next exam.");
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	

	
}
