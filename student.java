// Daniel Schroeder
// “I pledge on my honor that I have not given or received any unauthorized 
// assistance on this assignment/examination.”
package spss;

import java.util.ArrayList;
import java.util.List;

// The Student class allows for Student objects to be initialized in the SPSS 
// class. The Student class stores information for each Student that is created 
// from the SPSS class. Each Student has a name, an ArrayList of scores for each 
// submission, and an integer representing the number of successful submissions 
// they have made. 
public class Student {

	private String studentName;
	private List<Integer> studentScore;
	private int numSubmissions;

	// Student constructor that receives a String for the Student's name
	public Student(String studentName) {
		this.studentName = studentName;
		// ArrayList with Student score list initialized
		studentScore = new ArrayList<>();
	}

	// Method to be called by the SPSS class to set the Student score list if
	// valid submission is made
	public void setStudentScores(List<Integer> list) {
		studentScore = list;
	}

	public String getName() {
		return studentName;
	}

	// Returns the number of successful submissions made by student, returns int
	public int numSubmissions() {
		return numSubmissions;
	}

	// Method to be called by the SPSS class to determine if Student's score is
	// "satisfactory", or passing at least half of the tests.
	public boolean isSatisfactory() {
		// Finds the number of tests needed by dividing by 2
		int testsNeeded = studentScore.size() / 2;
		int counter = 0;
		// Iterates through the student score to see if the required
		// number of passed tests are fulfilled through a counter
		for (int i : studentScore) {
			if (i > 0) {
				counter++;
			}
		}
		// Counter must reach the required number of tests to return true
		if (counter >= testsNeeded) {
			return true;
		}
		return false;
	}

	// Method used by the getExtraCredit method in the SPSS class that tests
	// if all tests in the students score List passed, return true if they did
	// and false otherwise
	public boolean passAllTests() {
		// Iterates through the student scores list and checks that all tests
		// are greater than 0, returns false if not
		for (int i : studentScore) {
			if (i <= 0) {
				return false;
			}
		}
		return true;
	}

	// Method used to compare scores of submissions made to the SPSS class to
	// scores preset in the Student class, takes a list and returns true if
	// SPSS class submissions hold a greater score accumulation than the
	// current score in Student class and false otherwise
	public boolean isScoreGreater(List<Integer> list) {
		numSubmissions++;
		if (list.isEmpty()) {
			return false;
		}
		// Uses intCount private method to retrieve accumulation of scores in
		// the parameter score list and field list
		int currentValue = intCount(list);
		int studentValue = intCount(studentScore);
		// returns true if parameter list is greater than field list
		if (studentValue == 0 || currentValue > studentValue) {
			return true;
		}
		return false;
	}

	// Returns the sum of the score list of Student as an integer
	public int getScore() {
		// Uses intCount private method to add the scores in the score list
		return intCount(studentScore);
	}

	// Helper method which gets the sum of numbers in a List and returns the
	// value as an int
	private int intCount(List<Integer> list) {
		int result = 0;
		// Iterates through list and adds integers to return value
		for (int i : list) {
			result += i;
		}
		return result;
	}

}
