//Daniel Schroeder
package spss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileNotFoundException;

//The SPSS class creates a submit server that stores Student objects. The class  
// may add students to the submission server and add submissions for each 
// student. The SPSS class stores the Student objects along with the maximum 
// number of submissions each student can have, as well as the number of tests 
// each submission will have and the number of submissions made in total by 
// all students. 

public class SPSS {

	private int numTests;
	private ArrayList<Student> students;
	private int numSubmissions;

	// The myThread inner class extends the thread class. It is used to
	// instantiate threads that override the run method. It contains a field
	// with a scanner object.

	class MyThread extends Thread {

		// scanner object. sits inside the class so that no data race occurs
		Scanner scanner;

		// constructor that has a scanner as a parameter
		MyThread(Scanner scanner) {
			this.scanner = scanner;
		}

		@Override
		// override run method. adds the submission
		public void run() {
			
			// checks if the scanner contains a next available string
			while (scanner.hasNext()) {

				// sets the variable s equal to next string in the scanner
				String s = scanner.next();

				// checks if the student doesnt already exist, if it doesnt
				// skips through to next line
				if (!isStudentNew(s)) {

					// new arrayList initialized to hold the scores the name
					// holds
					List<Integer> scores = new ArrayList<>();

					// checks if scanner has next available int
					while (scanner.hasNextInt()) {

						// add the next int to the list of scores
						scores.add(scanner.nextInt());

					}
					// adds a submission using the string s to identify student
					// and the list of scores
					addSubmission(s, scores);
				}

				// goes to the next line once current line is finished
				scanner.nextLine();

			}
			scanner.close();
		}

	}

	// SPSS constructor that receives two integer values, one representing the
	// maximum number of submissions the server should receive and the other
	// representing the number of tests each submission into the server should
	// carry.

	public SPSS(int numTests) {
		// If numTests is greater to 0, the field is assigned the parameter
		// value but otherwise is made equal to 1
		if (numTests > 0) {
			this.numTests = numTests;
		} else {
			this.numTests = 1;
		}
		// Student objects ArrayList initialized
		students = new ArrayList<>();
	}

	// The addStudent method receives a String parameter with a student name and
	// adds the student to the Student object ArrayList if the student does not
	// already exist and it receives a valid name. The method returns true if
	// a student is successfully added and false otherwise.
	public boolean addStudent(String newStudentName) {
		// Uses a helper method to check if the name input is valid. Returns
		// false if not valid
		if (!isNewStudentValid(newStudentName)) {
			return false;
		}
		// If false was not returned, new Student object initialized with the
		// name of the String parameter.
		Student student = new Student(newStudentName);
		// Student added to Student objects ArrayList
		students.add(student);
		return true;
	}

	// Returns number of students in the Student objects ArrayList as an integer
	public int numStudents() {
		return students.size();
	}

	// The addSubmission method uses a String input and List input to add a
	// submission to a Student in the Student objects ArrayList if the
	// submission is valid. The method returns true if a submission is added
	// and false otherwise.
	public boolean addSubmission(String name, List<Integer> testResults) {
		// Uses a helper method to check if the String and List input are valid
		if (isSubmissionValid(name, testResults)) {
			// A getStudent helper method is called to make a copy of Student
			Student student = getStudent(name);
			numSubmissions++;
			// Uses a method in the Student class to check if the submission is
			// the Student's best score
			if (student.isScoreGreater(testResults)) {
				// Uses a method in the Student class to replace their current
				// score
				student.setStudentScores(testResults);
			}
			return true;
		}
		return false;
	}

	// method to read all the submissions in a file concurrently. uses a list
	// string with the file names and returns true/false if the method
	// can/cant run
	public boolean readSubmissionsConcurrently(List<String> fileNames) {
		Scanner scanner;
		boolean result = false;
		// if fileNames is equal to null, nothing happens and false is returned
		if (!fileNames.contains(null)){
			result = true;
			// stores the threads in a list to be later used
			List<Thread> threads = new ArrayList<>();
			// iterates through the files
			for (String s : fileNames) {

				try {
					// new scanner object created for each file
					scanner = new Scanner(new FileReader(s));
					// new thread initialized and started with a scanner 
					MyThread thread = new MyThread(scanner);
					thread.start();
					// add thread to list
					threads.add(thread);
				} catch (FileNotFoundException e) {
					
				}
			} 
			// wait for threads to finish before returning result
			for (Thread i : threads) {
				try {
					i.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return result;
	}

	// Uses a String input to find the Student with the corresponding name
	// and returns the sum of the best submission of said Student as an int
	public int score(String name) {
		// checks for invalid names and for a non-existing Student
		if (name == null || name.isEmpty() || isStudentNew(name)) {
			return -1;
		}
		// Sets the result value by using the getStudent method to find the
		// Student and getScore method in the Student class to retrieve score
		int result = getStudent(name).getScore();
		return result;
	}

	// Finds the number of submissions made by said student in a String input
	// and returns the quantity as an integer
	public int numSubmissions(String name) {
		// Checks for invalid names and a non-existing student
		if (name == null || isStudentNew(name) || name.isEmpty()) {
			return -1;
		}
		// Uses the getStudent method to find the student and then calls the
		// numSubmissions method from the Student class on said Student
		return getStudent(name).numSubmissions();
	}

	public int numSubmissions() {
		return numSubmissions;
	}

	// Determines whether Student named in String input has passed at least half
	// their tests and returns true if they do, false if otherwise
	public boolean satisfactory(String name) {
		// Checks for a non-existing student
		if (!isStudentValid(name)) {
			return false;
		}
		// Finds the Student using getStudent method and calls isSatisfactory
		// method from the Student class to check if half of tests are passed
		return getStudent(name).isSatisfactory();
	}

	// Checks if the Student named in the String input receives extra credit,
	// returns true if they do, false if otherwise
	public boolean gotExtraCredit(String name) {
		// Checks if Student is valid using helper method
		if (!isStudentValid(name)) {
			return false;
		}
		// Retrieves Student from getStudent method and uses the passAllTests
		// method in the Student class to check if Student passes all tests and
		// the numSubmissions method to check if Student made 1 submission
		if (getStudent(name).passAllTests() 
			&& getStudent(name).numSubmissions() == 1) {
			return true;
		}
		return false;
	}

	// Helper method to check if student given in String input is not already
	// in Student objects ArrayList, returns true if they are, false otherwise
	private boolean isStudentNew(String name) {
		// Iterates through Student Object ArrayList
		for (Student i : students) {
			// Uses the getName method in the Student class to check if String
			// parameter is equal to a student in the Student Object ArrayList,
			// returns false if true
			if (name.equals(i.getName())) {
				return false;
			}
		}
		return true;
	}

	// Helper method to find Student object in the Student objects ArrayList
	// given students name in a String; returns the Student as an object
	private Student getStudent(String name) {
		// Iterates through Student objects ArrayList
		for (Student i : students) {
			// Uses the getName method in the Student class to check if the
			// String parameter is equal to a student in the Student Object
			// ArrayList, returns Student object if true
			if (i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}

	// Helper method to check if submission made through the addSubmission
	// method is valid using student's name and the list that was submitted;
	// returns true if submission is valid, false otherwise
	private boolean isSubmissionValid(String name, List<Integer> list) {
		// Makes copy of Student by retrieving Student from getStudent method
		Student student = getStudent(name);
		// Calls auxiliary method to check for other conditions as well as if
		// Student is not new, has less submissions than the maximum # of
		// submissions, and that the number of tests in the list is equal to the
		// numTests field; all conditions must satisfy to return true
		if (isSubmissionValidAux(name, list) 
			&& !isStudentNew(name) && list.size() == numTests) {
			return true;
		}
		return false;
	}

	// Auxiliary method for the isSubmissionValid method to check if Submission
	// is valid; uses the same parameters as isSubmissionValid but checks for
	// more conditions; returns true if valid, false otherwise
	private boolean isSubmissionValidAux(String name, List<Integer> list) {
		// Checks for nulls and empty parameters, returns false if found
		if (name == null || list == null || list.isEmpty() || name.isEmpty()) {
			return false;
		}
		// iterates through list parameter, returns false if negative # is found
		for (int i : list) {
			if (i < 0) {
				return false;
			}
		}
		return true;
	}

	// Helper method to check if new students added in addStudent method are
	// valid, uses String as a parameter and returns true or false if student
	// valid or not respectively
	private boolean isNewStudentValid(String name) {
		// Checks if name is null, empty, or if the student isn't new returns
		// false if either apply
		if (name == null || name.isEmpty() || !isStudentNew(name)) {
			return false;
		}
		return true;
	}

	// Helper method to check if student input for the gotExtraCredit method is
	// valid using String parameter, returns true if valid and false otherwise
	private boolean isStudentValid(String name) {
		// Checks if name is null or empty, and if Student is new or hasn't made
		// a submission, returns false if either are true
		if (name == null || name.isEmpty() || isStudentNew(name) 
			|| getStudent(name).numSubmissions() < 1) {
			return false;
		}
		return true;
	}

}
