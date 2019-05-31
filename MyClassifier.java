//if standard deviation is  0, set prob dens fctn to 1!!!


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
			naiveBayes(training_list, testing_list);
		} 
		else if (get_k(algo) != 0) {
			kNearestNeighbour(get_k(algo), training_list, testing_list);
		}
		else {
			System.out.println("*Something went wrong: kNN format*");
		}
		//System.out.println("*End of program*");//!
	}
	//end of Main method
	//supporting methods:
	

	private static void kNearestNeighbour(int k, ArrayList<double[]> training, ArrayList<double[]> testing) {
		
		//!System.out.println("Starting kNN with k = " + k);//!
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
			//!System.out.println("New test");//!
			
			//for each test line, go through all TRAINING examples
			int n = 0;
			for (double[] training_ex : training) {
				//make new [,] instance
				double distance = dist_Euclidean(testing_ex, training_ex);
				double classs = training_ex[training_ex.length - 1]; //[training_ex.length - 1] should be 8
				double[] combined = {distance, classs}; 
				
				if (n < k) {
					k_nearest.add(combined);
					//!System.out.println("Adding to nearest: " + Arrays.toString(combined));//!
				}
				else {
					int furthest = find_furthest_index(k_nearest);
					if (combined[0] < k_nearest.get(furthest)[0]) {
						//replaces furthest element in k_nearest by current var
						//!System.out.println("Adding to nearest: " + Arrays.toString(combined));//!
						//!System.out.println("Removing from nearest: " + Arrays.toString(k_nearest.get(furthest)));//!
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
		//!System.out.println("*End of kNN*");//!		
	}


	private static void naiveBayes(ArrayList<double[]> training, ArrayList<double[]> testing) {
		//find mean and stdev for each attribute (1 to 8 --i.e. 0 to 7), for yes & no respectively		
		ArrayList<double[]> stats = getStats(training);
		double[] meanyes = stats.get(0);
		double[] meanno = stats.get(1);
		double[] sdyes = stats.get(2);
		double[] sdno = stats.get(3);
		double n_yes = stats.get(4)[0];
		double n_no = stats.get(4)[1];
		double n_total = n_yes + n_no;
		//check if these 2 are right!
		double probyes = n_yes/n_total;
		double probno = n_no/n_total;
		
		double yes_fctn = 1;
//		System.out.println("Should work");
//		System.out.println(yes_fctn);
		double no_fctn = 1;	
		
		double var_test = 0;
		double meanyes_test = 0;
		double sdyes_test = 0;
		
		//for each test, compute Bayes method maths with prob dens function
		for (double[] testing_ex : testing) {
			for (int i = 0; i < 8; i++) {
				if (sdyes[i] != 0) {
					//all sds are Nan??
					yes_fctn = yes_fctn*probFunction(testing_ex[i], meanyes[i], sdyes[i]);
//					System.out.println("Not sure if works");
//					System.out.println(testing_ex[i]);
//					System.out.println(meanyes[i]);
//					System.out.println(sdyes[i]);
//					System.out.println(yes_fctn);
				}
				if (sdno[i] != 0) {
					no_fctn = no_fctn*probFunction(testing_ex[i], meanno[i], sdno[i]);
				}
			}
//			System.out.println(yes_fctn);
//			System.out.println(no_fctn);
			
			yes_fctn = yes_fctn*probyes;
			no_fctn = no_fctn*probno;
			
			if (yes_fctn >= no_fctn) {
				System.out.println("yes");
			}
			else if (no_fctn > yes_fctn) {
				System.out.println("no");
			}
			else {
				System.out.println("Something is wrong with yes/no choosing");
			}
		}
		//System.out.println("End of NB. Starting test...");
		//System.out.println(var_test + meanyes_test + sdyes_test);
		//System.out.println(probFunction(var_test, meanyes_test, sdyes_test));
		
	}	

	public static double probFunction(double val, double mean, double std) {
		double function = 0;
		double sub_fctn = -Math.pow(val-mean,2)/(2*Math.pow(std, 2));
		function = 1/(std*Math.sqrt(2*Math.PI))*Math.pow(Math.E,sub_fctn);
		//System.out.println("Calculated function is: " + function);
		return function;
	}
	
	
	public static ArrayList<double[]> getStats(ArrayList<double[]> training) {
		ArrayList<double[]> stats = new ArrayList<double[]>();
		double[] sumyes = new double[8];
		double[] sumno = new double[8];
		double[] meanyes = new double[8];
		double[] meanno = new double[8];
		double[] diffyes = new double[8];
		double[] diffno = new double[8];
		double[] sdyes = new double[8];
		double[] sdno = new double[8];
		double n_yes = 0;
		double n_no = 0;
		double[] nums = {n_yes, n_no};
		for (double[] training_ex : training) {
			//if example is class yes
			if (training_ex[8] == 1) {
				for (int i = 0; i < 8; i++) {
					//do I have to initialise this to all 0 for sum?
					sumyes[i] += training_ex[i];
				}
				n_yes++;
			} 
			//if class no
			else if (training_ex[8] == 0) { 
				for (int i = 0; i < 8; i++) {
					sumno[i] += training_ex[i];
				}
				n_no++;
			}
			else {
				System.out.println("Sometihng went wrong: Not class yes or no");
			}				
		}

		//divide each index by n
		for (int i = 0; i < 8; i++) {
			meanyes[i] = sumyes[i] / n_yes;
			meanno[i] = sumno[i] / n_no;
			//do some testing
		}

//		System.out.println("Calc stdev");
		//standard deviation
		for (double[] training_ex : training) {
			//if example is class yes
			if (training_ex[8] == 1) {
				for (int i = 0; i < 8; i++) {
					diffyes[i] += Math.sqrt(training_ex[i] - meanyes[i]); 
					if (i == 0) {
//					System.out.println("Calc stdev for: training_ex[i], meanyes[i], diffyes[i] is: ");
//					System.out.println(training_ex[i]);
//					System.out.println(meanyes[i]);
//					System.out.println(diffyes[i]);
					}
				}
			}
			//if class no
			else if (training_ex[8] == 0) {
				for (int i = 0; i < 8; i++) {
					diffno[i] = diffno[i] + Math.sqrt(training_ex[i] - meanno[i]); 
				}
			}
			else {
				System.out.println("Something went wrong: Not class yes or no");
			}				
//			System.out.println(Arrays.toString(diffyes));
		}

		for (int i = 0; i < 8; i++) {
			if (diffyes[i] == 0) {
				sdyes[i] = 0;
			}
			else {
			sdyes[i] = Math.sqrt(diffyes[i]) / (n_yes - 1);
			sdno[i] = Math.sqrt(diffno[i]) / (n_no - 1);
			}
			//do some testing
			if (i == 0) {
//				System.out.println("sdyes[i] for: diffyes[i], n_yes is: ");
//				System.out.println(diffyes[i]);
//				System.out.println(n_yes);
//				System.out.println(sdyes[i]);
				}
		}
		
		
		nums[0] = n_yes;
		nums[1] = n_no;
		
		stats.add(meanyes);
		stats.add(meanno);
		stats.add(sdyes);
		stats.add(sdno);
		stats.add(nums);
		return stats;	
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
			//!System.out.println("Digit form (training file): " + Arrays.toString(entry));//!
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
			
			//!System.out.println("Digit form (testing file): " + Arrays.toString(entry));//!
			testing_data.add(entry);
		}
		return testing_data;
	}

	
	private static double dist_Euclidean(double[] a, double[] b) {
		// for first 8 attributes:
		double dist = 0;
		// assumes correct num of attributes
		//!System.out.println("Calculating Euclidean distance...");//!
		//!System.out.println(Arrays.toString(a));//!
		//!System.out.println(Arrays.toString(b));//!
		for (int i = 0; i < 8; i++) {
			//difference squared of each dimension
			dist += Math.pow(Math.abs(a[i] - b[i]),2);
		}
		dist = Math.sqrt(dist);
		//!System.out.println("Distance is " + dist);//!
		
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
