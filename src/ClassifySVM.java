
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


public class ClassifySVM {
	static int accuracy=0;
	public static void main(String args[]) throws IOException {
		Integer[][] wordVectors;
		Integer[][] devVectors;
		Integer[][] testVectors;
		/*
		 * Call createVector for bag of words Model along with readVectors.
		 * readVectors reads a 2d array from file as integers.
		 */
		wordVectors = readvectors("data/train_vector.txt");
		//createVector.make("data/train.txt","data/label.txt","data/train_vector.txt");
		//devVectors = readvectors("data/dev_vector.txt");
		//createVector.make("data/dev.txt","data/label_dev.txt","data/dev_vector.txt");
		testVectors = readvectors("data/test_vector.txt");
		//createVector.make("data/test.txt","data/label_test.txt","data/test_vector.txt");
		Model _model;
		
		//int trainingSize = wordVectors
		_model = svmTrain(wordVectors);
		//accuracy = new Integer[data.size()];
		int accu;
		int ftp = 0;
		int ltp = 0;
		int ftn = 0;
		int ltn =0;
		int ffp = 0;
		int lfp = 0;
		int ffn = 0;
		int lfn =0;
		int literal_count=0;
		int fig_count=0;
		for(int j=0;j<testVectors.length;j++){	
			//System.out.println(testVectors[j][0]);
			if(testVectors[j][0] == 0)
				literal_count++;
			else if(testVectors[j][0] == 1)
				fig_count++;
			accu=evaluate(testVectors[j],_model);
			//System.out.println(accu);
			if(accu == testVectors[j][0]){
				if(accu == 0){
					ltp++;
					ftn++;
				}
				else if(accu ==1){
					ltn++;
					ftp++;
				}
			}
			else{
				if(accu == 0){
					lfp++;
					ffn++;
				}
				else if(accu ==1){
					lfn++;
					ffp++;
				}
			}
		}
		double l_accu = ltp*1.0/literal_count;
		double f_accu = ftp*1.0/fig_count;
		double accur = l_accu*0.4+f_accu*0.6;
		System.out.println("Accuracy_Metric:    "+accur);
		double f_accuracy = (accuracy*1.0)/testVectors.length;
		System.out.println("Accuracy:    "+f_accuracy);
		double lprecision = (ltp*1.0)/(ltp+lfp);
		System.out.println("Literal Precision:    "+lprecision);
		double lrecall = (ltp*1.0)/(ltp+lfn);
		System.out.println("Literal Recall:    "+lrecall);
		double lf1 = 2*lprecision*lrecall/(lprecision+lrecall);
		System.out.println("Literal F1:    "+lf1);
		double fprecision = (ftp*1.0)/(ftp+ffp);
		System.out.println("Figurative Precision:    "+fprecision);
		double frecall = (ftp*1.0)/(ffn+ftp);
		System.out.println("Figurative Recall:    "+frecall);
		double ff1 = 2*fprecision*frecall/(fprecision+frecall);
		System.out.println("Figurative F1:    "+ff1);
		//System.out.println("Precision:    "+accuracy);
		 
	}
	
private static Model svmTrain(Integer [][] wordVector) {
	    Problem prob = new Problem();
	    int dataCount = wordVector.length;
	    prob.y = new double[dataCount];
	    prob.l = dataCount;
	    prob.n = wordVector[0].length-1;
	    prob.x = new Feature[dataCount][];     
	    
	    Integer[] features;
	    for (int i = 0; i < dataCount; i++){            
	    	features = wordVector[i];
	        prob.x[i] = new Feature[features.length-1];
	        for (int j = 1; j < features.length; j++){
	            FeatureNode node = new FeatureNode(j, features[j]);
	            prob.x[i][j-1] = node;
	        }           
	        prob.y[i] = features[0];
	    }
	    double [] weights = new double[2];
	    weights[0] = 0.6;
	    weights[1] = 0.4;
	    int [] weightLabels = new int[2];
	    weightLabels[0] = 0;
	    weightLabels[1] = 1;
	    SolverType solver = SolverType.L2R_LR; // -s 0
	    double C = 1;    // cost of constraints violation
	    double eps = 0.0001; // stopping criteria
	    Parameter parameter = new Parameter(solver, C, eps);
	    parameter.setWeights(weights, weightLabels);
	    //svm_model model = svm.svm_train(prob, param);
	    Model model = Linear.train(prob, parameter);
	    return model;
	}

public static int evaluate(Integer[] wordVectors2, Model model) 
{
    Feature[] nodes = new FeatureNode[wordVectors2.length-1];
    for (int i = 1; i < wordVectors2.length; i++)
    {
        FeatureNode node = new FeatureNode(i, wordVectors2[i]);
        nodes[i-1] = node;
    }
    int totalClasses = 3;       
//    int[] labels = new int[totalClasses];
//    svm.svm_get_labels(model,labels);
    double[] prob_estimates = new double[totalClasses];
    double v = Linear.predictProbability(model, nodes,prob_estimates);
    //System.out.println(v+"*******");
    int result;
    if(v >=0.5)
    	result = 1;
    else
    	result =0;
    System.out.println("(Actual:" + wordVectors2[0] + " Prediction:" + result + ")");            
    if(wordVectors2[0]==result)
    	accuracy++;
    return result;
}

public static Integer[][] readvectors(String filename){
	Integer[][] w_vector = null;
	try{
		 String[] temp;
		  Scanner file=new Scanner (new File(filename));
		  String[] token = file.nextLine().split(" ");
		  w_vector = new Integer[Integer.parseInt(token[0])][Integer.parseInt(token[1])];
		  file.nextLine();
		    while(file.hasNextLine()){
		        String line= file.nextLine();
		        temp = line.split(" ");
		        //System.out.println(w_vector.length);
		        //System.out.println(temp.length);
		        for(int i = 0; i<w_vector.length; i++) {
	                for (int j = 0; j<temp.length; j++) {    
	                    w_vector[i][j] = Integer.parseInt(temp[j]);
	                }
	            }
		        //vector=new Integer[lines.size()][];

		    }
		}
		catch (IOException e){
		    System.out.println("IOException");
		}
	return w_vector;
}

}

