// Serena Schauer
// CSCI 344 - Distributed Systems
// Project 1

import java.util.*;


public class Main {

	public static long time = System.currentTimeMillis();
	public static int numStudents;
	public static int capacity;
	public static int numSeats;
	public static int numTables;
	
	public static int tableNumber = 1;
	public static int seatNumber = 1;
	
	public static ArrayList waitingStudents_OnLine = new ArrayList();
	public static ArrayList waiting_TestToEnd = new ArrayList();
	public static ArrayList waitingStudents_Grading = new ArrayList();
	public static ArrayList waitingTeacher_ToOpenDoors = new ArrayList();
	public static ArrayList waitingTeacher_ToCloseDoors = new ArrayList();
	public static ArrayList waitingStudents_ToStartTest = new ArrayList();
	public static ArrayList waitingTeacher_ToGetBackTests = new ArrayList();
	public static ArrayList waitingTeacher_LastTableOccupied = new ArrayList();


	public static Vector <Test> studentsWaitingForGrade;
	public static Vector <Table> studentsWaitingAtTable;
	public static Vector <Test> studentsWaitingToEnter;


	public static int studentsWithExams = 0;
	public static int examCountOfDay = 0;
	public static int studentsInRoom = 0;
	public static boolean roomIsOpen = false;
	

	
	public static void main(String[] args) {
		
		System.out.println("Start Main");

		// Initialize Variables
		numStudents = Integer.parseInt(args[0]);
		capacity = Integer.parseInt(args[1]);
		numSeats = Integer.parseInt(args[2]);
		numTables = Integer.parseInt(args[3]);
		
		// Initialize Vectors
		studentsWaitingForGrade =  new Vector();
		studentsWaitingToEnter = new Vector();
		studentsWaitingAtTable = new Vector();
		
		// Initialize Student Threads
 		for (int i=1; i<=numStudents; i++){
			new Student(i).start();
			System.out.println("Student " + i + " was created." );
		}
 		
		// Initialize Teacher Thread (One Teacher)
 		new Teacher(1).start();
		System.out.println("Teacher " + "1" + " was created." );
				
		// Initialize Timer Thread (One Timer)
		new Timer(1).start();
		System.out.println("Timer " + "1" + " was created." );

		
		System.out.println("END MAIN");
		System.out.println("------------------------------------------------------------------");
		
	}

}
