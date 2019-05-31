import java.io.*;
import java.util.*;

public class MyClassifier {
	// declare global vars
	public static File training_file;
	public static File testing_file;
	public static Scanner scan_tr;
	public static Scanner scan_test;
	public static ArrayList<double[]> training_data;
	public static ArrayList<double[]> testing_data;

	public static void main(String[] args) {
		// check num args
		System.out.println("*num args: " + args.length + "*");
		if (args.length != 3) {
			// do sth
			System.out.println("Too many/little args");
			return;
		}

		// parse args
		// assumes correct input format
		// think about other parsing corners & edges
		String training_path = args[0];
		String testing_path = args[1];
		String algo = args[2];

		try {
			// open files and scanners
			training_file = new File(training_path);
			testing_file = new File(testing_path);
			scan_tr = new Scanner(training_file);
			scan_test = new Scanner(testing_file);
			training_data = new ArrayList<double[]>();

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
				// other break condition?
			}

			
		} catch (FileNotFoundException e) {
			// return;
			e.printStackTrace();
		}

		if (algo.equals("NB")) {
			naiveBayes();
		} else
			try {
				// check if fits kNN format
				// have accounted for wrong String length (?)
				//System.out.println(algo);
				String[] split_str = algo.split("");
				System.out.println("The String array for kNN is: " + Arrays.toString(split_str));
				int num_neighbours = Integer.parseInt(split_str[0]);

				if (split_str[1].equals("N") && split_str[2].equals("N")) {
					kNearestNeighbour(num_neighbours);
				}

			} catch (Exception exc) {
				exc.printStackTrace();
			}

		System.out.println("*End of program*");
	}

	private static void naiveBayes() {
		System.out.println("*Got to Naive Bayes*");
	}

	private static void kNearestNeighbour(int num_neighbours) {
		// vars: Num, lists
		// for each example in testing list
		// calculate Euclidean distance to each training example 
		// save N shortest distances
		// look at class of N selected training examples
		// majority vote on class of testing example
		
		// go through all examples from file into TESTING_data list
		
		ArrayList<double[]> n_closest = new ArrayList<double[]>();
		//go through testing examples
		while (scan_test.hasNextLine()) {

			String line = scan_test.nextLine();
			String[] entry_str = line.replaceAll("ï»¿","").split(",");

			// change everything to number
			double[] test_arr = new double[entry_str.length];
			for (int i = 0; i < entry_str.length; i++) {
				double sth = Double.parseDouble(entry_str[i]);
				test_arr[i] = sth;			
				System.out.println(sth);
			}
			
			//for each test patient, go through all training examples
			int n = 0;
			System.out.println("Calculating distance from test to all training ex");
			for (double[] train : training_data) {
				//make new instance for each training example
				//each has distance to test and class of training example
				double distance = dist_Euclidean(test_arr,train);
				double[] training_ex = {distance, train[train.length - 1]};
				//first k examples added automatically
				if(n<num_neighbours) {
					n_closest.add(training_ex);
				}
				//for rest, check if closer to test example 
				else {
					int furthest_index = 0;
					//loop through list of closest to find furthest of those
					System.out.println("Finding furthest training ex");
					for(int i = 0; i < num_neighbours; i++) {
						if(n_closest.get(i)[0] > n_closest.get(furthest_index)[0]) {
							furthest_index = i;							
						}	
					}

					//now compare distance of this furthest one to distance of training example
					//what to do if the distance is the same for both?? Keep oldest or newest??
					//!!!
					System.out.println("Find N closest training ex");
					if (training_ex[0] < n_closest.get(furthest_index)[0]) {
						//see if this works with the array instances & stuff
						//if closer, add this one and delete previous
						n_closest.remove(n_closest.get(furthest_index));
						n_closest.add(training_ex);
					}
				}
			}
			//gone through all training examples
			//now count amount of yes and no
			
			int count = 0;
			for(double[] arr: n_closest) {
				if (arr[1] == 1) {
					count++;
				}					
			}	
			
			if(num_neighbours/count > 2) {
				System.out.println("no");				
			}	
			else if (num_neighbours/count <= 2) {
				System.out.println("yes");
			}
			else {
				System.out.println("Something is wrong.");
			}			
		}		
		//gone through all test examples
	}





//		for (double[] test: testing_data) {
//			//PQ
//			//a PQ of arrays(?) that store distance, class
//			PriorityQueue<double[]> n_lowest = new PriorityQueue<double[]>((a,b) -> a[0] - b[0]);
//			
//			for (double[] train : training_data) {
//				double distance = dist_Euclidean(test,train);
//				if (among N lowest distances) 
//				//i.e. is lower than all in queue 
//				//OR there are less than N in queue
//				{
//					//add test to n_lowest in defined order					
//				}
//				
//			}
//		}
		
		
	

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

}
