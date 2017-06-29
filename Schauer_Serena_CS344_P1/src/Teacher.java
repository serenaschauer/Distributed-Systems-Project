import java.util.Random;
import java.util.Vector;


public class Teacher extends Thread{
	
	// Variables
	static 	int gradeBook[][] = new int[Main.numStudents][4];
	static Random random_grade = new Random();
	static Random rand = new Random();
	static int testNumber = 0;
	static int testMax = 4;
	private int id;
	
	// Constructor
	public Teacher(int id){
		this.id = id;
		setName("Teacher " + id);
	}
	
	// For Printing
	public void msg(String m){
		System.out.println("Teacher: " + "["+(System.currentTimeMillis()-Main.time)+"] "+getName()+" : " + m);
	}
	
	// Run Method
	public void run(){
		msg("is Running.");
		msg("arrives at school.");
		getReadyForDay();
		// teacher administers 4 tests a day
		while(testNumber < testMax){
			testNumber++;
			letStudentsEnterClassRoom();
			handOutExams();
			waitForExamToEnd();
			checkExams();
			gradeExams();
			prepairForNextExam();
		}	
		msg("administered all 4 tests.");
		showFinalGrades();
		msg("ended. ------------ ENDED ------------");
	}//Run
	
	synchronized public void getReadyForDay(){
		// Initialize gradeBook
		for(int r=0 ; r<Main.numStudents ; r++){
			System.out.println();
			for(int c=0 ; c<4 ; c++){
				gradeBook[r][c] = -1;
			}
		}
		System.out.println();
	}
	
	public void letStudentsEnterClassRoom(){		
		// teacher waits for timer to tell her to let the kids in
		synchronized(Main.waitingTeacher_ToOpenDoors){
			try {
				msg("Waits for timer to open doors.");
				Main.waitingTeacher_ToOpenDoors.wait();
				msg("Opens doors.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
		msg("This is for Exam Number: " + testNumber);
		Main.roomIsOpen = true;
		synchronized(Main.waitingStudents_OnLine){
			Main.waitingStudents_OnLine.notifyAll();
		}
		msg("Lets students enter the room.");
	}
	
	public void handOutExams(){
		// waits for 15 minutes to be up
		try{
			synchronized(Main.waitingTeacher_ToCloseDoors){
				Main.waitingTeacher_ToCloseDoors.wait();
				Main.roomIsOpen = false;
				msg("Closed the doors.");
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		// Students take seats
/*		synchronized (Main.studentsWaitingAtTable) {
			while(Main.tableNumber < Main.numTables ){
				// wait till last table is occupied
				try{
					synchronized(Main.waitingTeacher_LastTableOccupied){
						Main.waitingTeacher_LastTableOccupied.wait();
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!Main.studentsWaitingAtTable.isEmpty()) {
				synchronized (Main.studentsWaitingAtTable.elementAt(0)) {
					Main.studentsWaitingAtTable.elementAt(0).notify();
					msg("Has notified students at table " + Main.studentsWaitingAtTable.elementAt(0).id + ".........");
					Main.studentsWaitingAtTable.removeElementAt(0);
				}
			}
		}
*/		
		// Hands out exams by signaling students
		// signal students to start test
		synchronized(Main.waitingStudents_ToStartTest){
			Main.waitingStudents_ToStartTest.notifyAll();
			msg("Handed out exams.");
		}
	}
	
	public void waitForExamToEnd(){
		// Students and Teacher wait for exam to end
		try{
			synchronized(Main.waiting_TestToEnd){
				Main.waiting_TestToEnd.wait();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		msg("Was notified that Exam Ended.");
	}
	
	public void checkExams(){
		// waits for all students to give back the exam
		while(Main.studentsWithExams != 0 ){
			try{
				synchronized(Main.waitingTeacher_ToGetBackTests){
					Main.waitingTeacher_ToGetBackTests.wait();
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}		
		} // When all students have handed in their exam, Teacher can check exams
		// Checking Exams is Simulated By Sleep For A Random Time
		msg("Is in the process of checking and grading exams.!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		try{
			Thread.sleep(rand.nextInt(300));
		}catch(InterruptedException e){
			e.printStackTrace();
		}	
	}
	
	public void gradeExams(){
		// give the grade
		// notify the student when the test has been graded
		synchronized (Main.studentsWaitingForGrade) {
			while (!Main.studentsWaitingForGrade.isEmpty()) {
				synchronized (Main.studentsWaitingForGrade.elementAt(0)) {
					msg("Has graded test for Student " + Main.studentsWaitingForGrade.elementAt(0).id);
					Main.studentsWaitingForGrade.elementAt(0).grade = rand.nextInt(100);
					msg("Test Grade for Student " + Main.studentsWaitingForGrade.elementAt(0).id + " for test "+ testNumber + " = " + Main.studentsWaitingForGrade.elementAt(0).grade );
					gradeBook[Main.studentsWaitingForGrade.elementAt(0).id-1][testNumber-1] = Main.studentsWaitingForGrade.elementAt(0).grade;					
					Main.studentsWaitingForGrade.elementAt(0).notify();
					
				// print grades
 /*				System.out.println();
					System.out.println("--- Grades ---");
					for(int r=0 ; r<Main.numStudents ; r++){
						System.out.println();
						System.out.print("Student " + (r+1) + ": ");
						for(int c=0 ; c<4 ; c++){
							System.out.print(gradeBook[r][c] + " ");
						}
					}
					System.out.println();
					System.out.println();
*/					
				}
				Main.studentsWaitingForGrade.removeElementAt(0);
			}
		}
		msg("Graded all tests for test number " + testNumber);
	}

	synchronized public void prepairForNextExam(){
		Main.tableNumber = 1;
		Main.seatNumber = 1;
	}
	
	synchronized public void showFinalGrades(){
		// print grades
		System.out.println();
		System.out.print(" ---- Final Grades For Students ----");
		for(int r=0 ; r<Main.numStudents ; r++){
			System.out.println();
			System.out.print("Student " + (r+1) + ": ");
			if((r+1) < 10) { System.out.print(" "); }
			for(int c=0 ; c<4 ; c++){
				if(gradeBook[r][c]==-1)  {    gradeBook[r][c] = 0;    }
				System.out.print(gradeBook[r][c] + " ");
				if( gradeBook[r][c] < 10) { System.out.print(" "); }
			}
		}
		System.out.println();
		System.out.println();
		System.out.println("-------------------------------------------------------------------");
		
		int count = 0;
		for(int r=0 ; r<Main.numStudents ; r++){
			for(int c=0 ; c<4 ; c++){
				if(gradeBook[r][c]!=0)  {    count = count + 1;    }
			}
			if(count < 3){ 
				msg("Student " + (r+1) + " won't be able to finish.");
			}
			count = 0;
		}
		System.out.println("-------------------------------------------------------------------");
	}
	
	

	
}
