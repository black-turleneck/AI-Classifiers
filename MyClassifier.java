import java.io.*;
import java.util.*;

public class MyClassifier {

	public static void main(String[] args) {
		File training_file;
		File testing_file;
		Scanner scan_tr;
		Scanner scan_test;
		ArrayList<double[]> training_list;
		ArrayList<double[]> testing_list;		
		
	
		// check num args
		if (args.length != 3) {
			// do sth
			System.out.println("*Too many/little args" + args.length);
			return;
		}

		// parse args
		// assumes correct input format
		// think about other parsing corners & edges
		String training_path = args[0];
		String testing_path = args[1];
		String algo = args[2];

		// open files and scanners
		try {
			training_file = new File(training_path);
			testing_file = new File(testing_path);
			scan_tr = new Scanner(training_file);
			scan_test = new Scanner(testing_file);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		training_list = makeTrainingList(scan_tr);
		testing_list = makeTestingList(scan_test);	
			
		//implement one of the 2 classifiers
		if (algo.equals("NB")) {
			naiveBayes();
		} 
		else if (get_k(algo) != 0) {
			kNearestNeighbour(get_k(algo), training_list, testing_list);
		}
		else {
			System.out.println("*Something went wrong: kNN format*");
		}
		System.out.println("*End of program*");
	}
	//end of Main method
	//supporting methods:
	

	private static void kNearestNeighbour(int k, ArrayList<double[]> training, ArrayList<double[]> testing) {
		
		System.out.println("Starting kNN with k = " + k);
		// vars: Num, lists
		// for each example in testing list
		// calculate Euclidean distance to each training example 
		// save N shortest distances
		// look at class of N selected training examples
		// majority vote on class of testing example
		
		// go through all TESTING examples
		for(double[] testing_ex : testing) {
			//this arraylist stores arrays of doubles, of the form [distance, class], with class 0 or 1
			ArrayList<double[]> k_nearest = new ArrayList<double[]>();
			System.out.println("New test");
			
			//for each test line, go through all TRAINING examples
			int n = 0;
			for (double[] training_ex : training) {
				//make new [,] instance
				double distance = dist_Euclidean(testing_ex, training_ex);
				double classs = training_ex[training_ex.length - 1]; //[training_ex.length - 1] should be 8
				double[] combined = {distance, classs}; 
				
				if (n < k) {
					k_nearest.add(combined);
					System.out.println("Adding to nearest: " + Arrays.toString(combined));
				}
				else {
					int furthest = find_furthest_index(k_nearest);
					if (combined[0] < k_nearest.get(furthest)[0]) {
						//replaces furthest element in k_nearest by current var
						System.out.println("Adding to nearest: " + Arrays.toString(combined));
						System.out.println("Removing from nearest: " + Arrays.toString(k_nearest.get(furthest)));
						k_nearest.set(furthest, combined);
						//display length of AList?
					}					
				}
			n++;	
			}
			
			//at this point we have the k nearest training examples to the given test
			//count num of yes & no
			int num_yes = 0;
			int num_no = 0;
			for (double[] neighbour : k_nearest) {
				if (neighbour[1] == 1) {
					num_yes++;
				}
				else if (neighbour[1] == 0) {
					num_no++;
				}
				else {
					System.out.println("Something is wrong with yes/no counting");
				}				
			}
			
			//display class yes or no, decided by majority vote (tiebreaker is yes)
			if (num_yes >= num_no) {
				System.out.println("yes");				
			}
			else {
				System.out.println("no");
			}			
		}
		System.out.println("*End of kNN*");		
	}


		private static void naiveBayes() {
			System.out.println("*Got to Naive Bayes*");
		}

	

	public static ArrayList<double[]> makeTrainingList(Scanner scan_tr) {
		ArrayList<double[]> training_data = new ArrayList<double[]>();
		// add all examples from file into TRAINING_data list
		while (scan_tr.hasNextLine()) {
			String line = scan_tr.nextLine();
			String[] entry_str = line.replaceAll("ï»¿","").split(",");
			// change class vars to non-numeric
			if (entry_str[entry_str.length - 1].contentEquals("yes")) {
				entry_str[entry_str.length - 1] = Integer.toString(1);
			} else {
				// test for case where isn't no?
				entry_str[entry_str.length - 1] = Integer.toString(0);
			}
			//testing
			//System.out.println("String form: " + Arrays.toString(entry_str));
			// change everything to number
			double[] entry = new double[entry_str.length];
			for (int i = 0; i < entry_str.length; i++) {
				double sth = Double.parseDouble(entry_str[i]);
				entry[i] = sth;			
			}
			System.out.println("Digit form (training file): " + Arrays.toString(entry));
			training_data.add(entry);
		}
		return training_data;
	}
	
	public static ArrayList<double[]> makeTestingList(Scanner scan_test) {
		ArrayList<double[]> testing_data = new ArrayList<double[]>();
		// add all examples from file into TRAINING_data list
		
		while (scan_test.hasNextLine()) {
			//read in line, convert to String array
			String line = scan_test.nextLine();
			String[] entry_str = line.replaceAll("ï»¿","").split(",");
			
			//System.out.println("String form: " + Arrays.toString(entry_str));
			// convert to number
			double[] entry = new double[entry_str.length];			
			for (int i = 0; i < entry_str.length; i++) {
				double sth = Double.parseDouble(entry_str[i]);
				entry[i] = sth;			
			}
			
			System.out.println("Digit form (testing file): " + Arrays.toString(entry));
			testing_data.add(entry);
		}
		return testing_data;
	}

	
	private static double dist_Euclidean(double[] a, double[] b) {
		// for first 8 attributes:
		double dist = 0;
		// assumes correct num of attributes
		System.out.println("Calculating Euclidean distance...");
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(b));
		for (int i = 0; i < 8; i++) {
			dist += Math.abs(a[i] - b[i]);
		}
		System.out.println("Distance is " + dist);
		
		return dist;
	}
	
	
	// check if fits kNN format
	// have accounted for wrong String length?
	private static int get_k(String algo) {
		try {			
			//System.out.println(algo);
			String[] split_str = algo.split("");
			int num_neighbours = Integer.parseInt(split_str[0]);

			if (split_str[1].equals("N") && split_str[2].equals("N")) {
				return num_neighbours;
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		//doesn't fit format
		return 0;
	}
	
	private static int find_furthest_index(ArrayList<double[]> k_nearest) {
		int furthest_index = 0;
		int curr_index = 0;
		for (double[] neighbour : k_nearest) {
			if(neighbour[0] > k_nearest.get(furthest_index)[0]) {
				furthest_index = curr_index;							
			}	
			curr_index++;
		}		
		return furthest_index;
	}
	
	
}
